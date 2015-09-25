/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.snowowl.snomed.importer.rf2.validation;

import static com.google.common.collect.Lists.newArrayList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.eclipse.core.runtime.SubMonitor;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.FileUtils;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.importer.ImportException;
import com.b2international.snowowl.snomed.SnomedConstants;
import com.b2international.snowowl.snomed.common.ContentSubType;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedConceptLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedDescriptionLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedRelationshipLookupService;
import com.b2international.snowowl.snomed.importer.net4j.ImportConfiguration;
import com.b2international.snowowl.snomed.importer.net4j.SnomedValidationDefect;
import com.b2international.snowowl.snomed.importer.net4j.SnomedValidationDefect.DefectType;
import com.b2international.snowowl.snomed.importer.release.ReleaseFileSet.ReleaseComponentType;
import com.b2international.snowowl.snomed.importer.rf2.CsvConstants;
import com.b2international.snowowl.snomed.importer.rf2.model.ComponentImportType;
import com.b2international.snowowl.snomed.importer.rf2.util.ValidationUtil;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Closeables;

/**
 * Represents a release file validator that validates a single release file.
 */
public abstract class AbstractSnomedValidator {
	
	private File componentStagingDirectory;
	private final URL releaseUrl;
	private final File stagingDirectoryRoot;
	private final ComponentImportType importType;
	private final int columnNumber;
	
	private Set<String> incorrectColumnNumbers;
	private Set<String> moduleIdNotExist;
	private Set<String> invalidEffectiveTimeFormat;
	/**Set containing all visited SNOMED CT module concept IDs. Consider this as a cache to avoid excessive module concept existence check.*/
	private final Set<String> visitedModuleIds;
	private final Set<String> invalidIds;

	protected final SnomedConceptLookupService conceptLookupService;
	protected final SnomedDescriptionLookupService descriptionLookupService;
	protected final SnomedRelationshipLookupService relationshipLookupService;
	
	protected final String releaseFileName;
	protected final ImportConfiguration configuration;
	protected final Set<SnomedValidationDefect> defects;
	protected final ValidationUtil validationUtil;
	
	public AbstractSnomedValidator(final ImportConfiguration configuration, 
			final URL releaseUrl,
			final ComponentImportType importType, 
			final Set<SnomedValidationDefect> defects, 
			final ValidationUtil validationUtil, 
			final int columnNumber) {
		
		this.configuration = configuration;
		this.releaseUrl = releaseUrl;
		this.releaseFileName = configuration.getMappedName(releaseUrl.getPath());
		this.importType = importType;
		this.validationUtil = validationUtil;
		this.columnNumber = columnNumber;
		this.defects = defects;

		stagingDirectoryRoot = new File(System.getProperty("java.io.tmpdir"));
		
		conceptLookupService = new SnomedConceptLookupService();
		descriptionLookupService = new SnomedDescriptionLookupService();
		relationshipLookupService = new SnomedRelationshipLookupService();
		
		visitedModuleIds = Sets.newHashSet();
		invalidIds = Sets.newHashSet();
	}
	
	/**
	 * Checks if the actual header is equal to the standard header.
	 * 
	 * @param actualHeader the header to be checked
	 */
	protected abstract void checkReleaseFileHeader(String[] actualHeader);
	
	/**
	 * Release file specific validator method, subclass has to override it.
	 * 
	 * @param row the row which contains the release file specific elements
	 * @param lineNumber the number of the given row
	 */
	protected abstract void doValidate(List<String> row, int lineNumber);
	
	/**
	 * Release type specific method to gather the defects.
	 */
	protected void addDefects() {
		addDefects(new SnomedValidationDefect(DefectType.INVALID_ID, invalidIds));
	}
	
	/**
	 * Performs any one-time initialization necessary for the validation.
	 * 
	 * @param monitor the SubMonitor instance to report progress on
	 */
	public void preValidate(final SubMonitor monitor) {
		monitor.beginTask(MessageFormat.format("Preparing {0}s validation", importType.getDisplayName()), 1);
		
		FileOutputStream outputStream = null;
		InputStreamReader releaseFileReader = null;
		CsvListReader releaseFileListReader = null;
		OutputStreamWriter streamWriter = null;
		CsvListWriter listWriter = null;
		
		try {
			releaseFileReader = new InputStreamReader(releaseUrl.openStream(), CsvConstants.IHTSDO_CHARSET);
			releaseFileListReader = new CsvListReader(releaseFileReader, CsvConstants.IHTSDO_CSV_PREFERENCE);
			
			componentStagingDirectory = createStagingDirectory();

			final File tempFile = new File((componentStagingDirectory), releaseFileName);

			outputStream = new FileOutputStream(tempFile);
			streamWriter = new OutputStreamWriter(outputStream, Charsets.UTF_8);
			listWriter = new CsvListWriter(streamWriter, CsvConstants.IHTSDO_CSV_PREFERENCE);

			checkReleaseFileHeader(releaseFileListReader.getCSVHeader(true));

			while (true) {
				final List<String> row = releaseFileListReader.read();

				if (null == row) {
					break;
				}

				listWriter.write(row);
			}
			
		} catch (final IOException e) {
			throw new ImportException(MessageFormat.format("Couldn''t read row from {0} release file.", releaseFileName), e);
		} finally {
			Closeables.closeQuietly(listWriter);
			Closeables.closeQuietly(streamWriter);
			Closeables.closeQuietly(outputStream);
			Closeables.closeQuietly(releaseFileListReader);
			Closeables.closeQuietly(releaseFileReader);
			
			monitor.worked(1);
		}
	}
	
	/**
	 * Validates a release file.
	 * 
	 * @param monitor the SubMonitor instance to report progress on
	 */
	public void doValidate(final SubMonitor monitor) {
		monitor.beginTask(MessageFormat.format("Validating {0}s...", importType.getDisplayName()), 1);
		
		final CsvListReader listReader = getReleaseFileReader();
		
		try {
			int lineNumber = 2;
			
			while (true) {
				final List<String> row = listReader.read();
				
				if (null == row) {
					break;
				}
				
				if (row.size() != columnNumber) {
					if (null == incorrectColumnNumbers) {
						incorrectColumnNumbers = Sets.newHashSet();
					}
					
					incorrectColumnNumbers.add(MessageFormat.format("Line number {0} in the ''{1}'' file, got {2}, expected {3}",
							lineNumber, releaseFileName, row.size(), columnNumber));
					continue;
				}

				// we handle the concept file module validation in a different way
				if (!importType.equals(ComponentImportType.CONCEPT)) {
					validateModuleId(row, lineNumber);
				}
				
				// validate effective time format from 3.1 as 'Unpublished' value can be present
				validateEffectiveTime(row, lineNumber);
				
				
				doValidate(row, lineNumber);
				
				lineNumber++;
			}
		} catch (final IOException e) {
			throw new ImportException(MessageFormat.format("Exception when reading {0}s for validating.", importType.getDisplayName()), e);
		} finally {
			
			Closeables.closeQuietly(listReader);
			monitor.worked(1);
		}
		
		postConceptValidation();
		
		addDefects(new SnomedValidationDefect(DefectType.INCORRECT_COLUMN_NUMBER, incorrectColumnNumbers),
				new SnomedValidationDefect(DefectType.MODULE_CONCEPT_NOT_EXIST, moduleIdNotExist),
				new SnomedValidationDefect(DefectType.INVALID_EFFECTIVE_TIME_FORMAT, invalidEffectiveTimeFormat));
		
		addDefects();
	}
	
	public void postValidate(final SubMonitor monitor) {
		monitor.beginTask(MessageFormat.format("Finishing {0}s validation", importType.getDisplayName()), 1);
		
		if (!FileUtils.deleteDirectory(componentStagingDirectory)) {
			validationUtil.getLogger().error(MessageFormat.format("Couldn''t remove {0} staging directory ''{1}''.",
					importType.getDisplayName(), componentStagingDirectory.getAbsolutePath()));
		}
	}
	
	/**
	 * Adds the {@link SnomedValidationDefect} to the set of defects.
	 * 
	 * @param validationDefects the validation defects to be added
	 * 
	 */
	public void addDefects(final SnomedValidationDefect... validationDefects) {
		for (final SnomedValidationDefect snomedValidationDefect : validationDefects) {
			if (!CompareUtils.isEmpty(snomedValidationDefect.getDefects())) {
				final Iterator<SnomedValidationDefect> iterator = defects.iterator();
				while (iterator.hasNext()) {
					final SnomedValidationDefect defect = iterator.next();
					if (defect.getDefectType().equals(snomedValidationDefect.getDefectType())) {
						defect.getDefects().addAll(snomedValidationDefect.getDefects());
						
						return;
					}
				}
				
				defects.add(snomedValidationDefect);
			}
		}
	}
	
	/**
	 * Create a new {@link CsvListReader} for the release file.
	 * 
	 * @return the created reader
	 */
	public CsvListReader getReleaseFileReader() {
		try {
			final File file = new File(componentStagingDirectory, releaseFileName);
			final InputStream fileStream = new FileInputStream(file);
			
			return new CsvListReader(new InputStreamReader(fileStream), new CsvPreference('\0', "\t".charAt(0), ""));
		} catch (final FileNotFoundException e) {
			throw new ImportException(MessageFormat.format("Couldn''t find {0} staging directory ''{1}''.",
					importType.getDisplayName(), componentStagingDirectory.getAbsolutePath()), e);
		}
	}
	
	/**
	 * Checks if a SNOMED&nbsp;CT component is present in the release files or exists in the database.
	 * 
	 * @param componentId the ID of the component
	 * @return {@code true} if the component is present or exists
	 */
	public boolean isComponentNotExist(final String componentId) {

		try {
			Long.parseLong(componentId);
		} catch (final NumberFormatException e) {
			//cannot be a valid core component ID
			return true;
		}
		
		for (final ComponentCategory nature : newArrayList(ComponentCategory.CONCEPT, ComponentCategory.DESCRIPTION, ComponentCategory.RELATIONSHIP)) {
			if (isNatureId(nature, componentId)) {
				switch (nature) {
					case CONCEPT:
						return isConceptNotExist(componentId);
					case DESCRIPTION:
						return isDescriptionNotExist(componentId);
					case RELATIONSHIP:
						return isRelationshipNotExist(componentId);
					default:
						throw new IllegalStateException(MessageFormat.format("Unhandled component nature ''{0}''.", nature));
				} 
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if the specified component identifier corresponds to this component nature (determined by its last-but-one digit).
	 * 
	 * @param componentId
	 *            the component identifier to check
	 * 
	 * @return {@code true} if the specified identifier is of this nature, {@code false} otherwise
	 */
	public boolean isNatureId(ComponentCategory category, String componentId) {

		if (componentId == null || componentId.length() < 6 || componentId.length() > 18) {
			return false;
		}

		int natureDigit = componentId.charAt(componentId.length() - 2) - '0';
		return (natureDigit == category.ordinal());
	}
	
	/**
	 * Checks if the given type SNOMED&nbsp;CT component is present in the release files or exists in the database.
	 * 
	 * @param componentId the ID of the component
	 * @return {@code true} if the component is present or exists
	 */
	public boolean isComponentNotExist(final String componentId, final ReleaseComponentType componentType) {
		
		try {
			Long.parseLong(componentId);
		} catch (final NumberFormatException e) {
			return true;
		}
		
		if (componentType.equals(ReleaseComponentType.CONCEPT)) {
			return isConceptNotExist(componentId);
		} else  if (componentType.equals(ReleaseComponentType.DESCRIPTION)) {
			return isDescriptionNotExist(componentId);
		} else  if (componentType.equals(ReleaseComponentType.RELATIONSHIP)) {
			return isRelationshipNotExist(componentId);
		} else {
			return false;
		}
	}
	
	/**
	 * Validates the given component ID if it is unique or not
	 * 
	 * @param row the row where the ID can be found
	 * @param componentIds the previously processed IDs
	 * @param messages the {@code Set} where the not unique IDs are stored (may not be {@code null})
	 * @param lineNumber the number of the line
	 */
	public void validateComponentUnique(final List<String> row, final Map<String, List<String>> componentIds, final Set<String> messages, final int lineNumber) {
		if (componentIds.containsKey(row.get(0))) {
			// if the id is for the same component as before
			String conceptId = row.get(4);
			if (componentIds.get(row.get(0)).get(0).equals(conceptId)) {
				// we set the new status
				componentIds.get(row.get(0)).set(1, row.get(2));
			} else if (!componentIds.get(row.get(0)).get(1).equals("0")) {
				messages.add(MessageFormat.format("Line number {0} in the ''{1}'' file part of concept ID {2}", lineNumber, releaseFileName, conceptId));
			}
		} else {
			componentIds.put(row.get(0), createConceptIdStatusList(row));
		}
	}
	

	/**
	 * @param componentId
	 * @param componentType
	 * @param messages
	 * @param lineNumber
	 */
	public void validateComponentExists(final String componentId, final String partOfConceptId, final ReleaseComponentType componentType, final Set<String> messages, final int lineNumber) {
		if (isComponentNotExist(componentId, componentType)) {
			if (componentId.equals(partOfConceptId)) {
				messages.add(MessageFormat.format("Line number {0} in the ''{1}'' file with concept ID {2}", lineNumber, releaseFileName, componentId));
			} else {
				messages.add(MessageFormat.format("Line number {0} in the ''{1}'' file, part of concept ID {2}, missing concept ID {3}", lineNumber, releaseFileName, partOfConceptId, componentId));
			}
		}
	}

	/**
	 * Creates a {@code List} with a concept ID and the status of that concept.
	 * 
	 * @param row which contains the ID and the status
	 * @return the newly created {@code List}
	 */
	public List<String> createConceptIdStatusList(final List<String> row) {
		final List<String> conceptIdDescriptionStatus = Lists.newArrayList();
		conceptIdDescriptionStatus.add(row.get(4)); // component ID
		conceptIdDescriptionStatus.add(row.get(2)); // status
		
		return conceptIdDescriptionStatus;
	}
	
	/**
	 * Returns with the RF2 release file name.
	 * @return the release file name.
	 */
	public String getReleaseFileName() {
		return releaseFileName;
	}
	
	/**
	 * Post validation of the concepts after all the element was read.
	 */
	protected void postConceptValidation() {
	}
	
	private void validateModuleId(final List<String> row, final int lineNumber) {

		final String conceptId = row.get(3);
		
		if (visitedModuleIds.contains(conceptId)) {
			return;
		}
		
		if (isComponentNotExist(conceptId, ReleaseComponentType.CONCEPT)) {
			if (null == moduleIdNotExist) {
				moduleIdNotExist = Sets.newHashSet();
			}
			
			moduleIdNotExist.add(MessageFormat.format("Line number {0} in the ''{1}'' file with concept ID {2}.", lineNumber, releaseFileName, row.get(3)));
		} else {
			
			//cache module concept ID as an existing visited one
			visitedModuleIds.add(conceptId);
			
		}
	}
	
	private void validateEffectiveTime(final List<String> row, final int lineNumber) {
		final String effectiveTime = row.get(1);
		
		if (ContentSubType.DELTA.equals(configuration.getVersion()) && effectiveTime.isEmpty()) {
			return;
		}
		
		try {
			EffectiveTimes.parse(effectiveTime, SnomedConstants.RF2_EFFECTIVE_TIME_FORMAT);
		} catch (final SnowowlRuntimeException e) {
			if (null == invalidEffectiveTimeFormat) {
				invalidEffectiveTimeFormat = Sets.newHashSet();
			}
			
			invalidEffectiveTimeFormat.add(MessageFormat.format("Line number {0} in the ''{1}'' file with effective time {2}.", lineNumber, releaseFileName, effectiveTime));
		}
	}
	
	private boolean isConceptNotExist(final String componentId) {
		if (validationUtil.getActiveConceptIds().contains(componentId) || validationUtil.getInactiveConceptIds().contains(componentId)) {
			return false;
		}
		
		if (conceptLookupService.exists(createActivePath(), componentId)) {
			return false;
		}
		
		return true;
	}

	private boolean isDescriptionNotExist(final String componentId) {
		if (validationUtil.getDescriptionIds().contains(componentId)) {
			return false;
		}
		
		if (descriptionLookupService.exists(createActivePath(), componentId)) {
			return false;
		}
		
		return true;
	}

	private boolean isRelationshipNotExist(final String componentId) {
		if (validationUtil.getRelationshipIds().contains(componentId)) {
			return false;
		}
		
		if (relationshipLookupService.exists(createActivePath(), componentId)) {
			return false;
		}
		
		return true;
	}
	
	protected void collectIfInvalid(final String conceptId, final short expectedComponentType) {
		if (SnomedTerminologyComponentConstants.getTerminologyComponentIdValueSafe(conceptId) != expectedComponentType) {
			invalidIds.add(conceptId);
		}
	}

	protected IBranchPath createActivePath() {
		return BranchPathUtils.createPath(configuration.getBranchPath());
	}

	private File createStagingDirectory() {
		File componentStagingDirectory;
		
		componentStagingDirectory = new File(stagingDirectoryRoot, MessageFormat.format("{0}_{1}", importType.getDisplayName(), UUID.randomUUID()));
		
		validationUtil.getLogger().info(MessageFormat.format("Creating staging directory ''{0}'' for {1} validation.", 
				componentStagingDirectory.getAbsolutePath(), importType.getDisplayName()));
		
		if (!componentStagingDirectory.mkdirs()) {
			throw new ImportException(MessageFormat.format("Couldn''t create staging directory for {0} validation.", importType.getDisplayName()));
		}
		
		return componentStagingDirectory;
	}

}
