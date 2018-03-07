/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.CsvListWriter;

import com.b2international.commons.FileUtils;
import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.exceptions.AlreadyExistsException;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.SnomedConstants;
import com.b2international.snowowl.snomed.common.ContentSubType;
import com.b2international.snowowl.snomed.importer.ImportException;
import com.b2international.snowowl.snomed.importer.net4j.DefectType;
import com.b2international.snowowl.snomed.importer.net4j.ImportConfiguration;
import com.b2international.snowowl.snomed.importer.net4j.SnomedValidationDefect;
import com.b2international.snowowl.snomed.importer.release.ReleaseFileSet.ReleaseComponentType;
import com.b2international.snowowl.snomed.importer.rf2.CsvConstants;
import com.b2international.snowowl.snomed.importer.rf2.model.ComponentImportType;
import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Closeables;
import com.google.common.io.Closer;

/**
 * Represents a release file validator that validates a single release file.
 */
public abstract class AbstractSnomedValidator {
	
	public static final String SPECIAL_EFFECTIVE_TIME_KEY = "";
	
	private static final Splitter TAB_SPLITTER = Splitter.on('\t');
	private static final Collection<ComponentCategory> CORE_COMPONENT_CATEGORIES = ImmutableList.of(ComponentCategory.CONCEPT, ComponentCategory.DESCRIPTION, ComponentCategory.RELATIONSHIP);
	
	private File componentStagingDirectory;
	private final URL releaseUrl;
	private final File stagingDirectoryRoot;
	private final ComponentImportType importType;
	
	private Set<String> moduleIdNotExist = newHashSet();
	private Set<String> invalidEffectiveTimeFormat = newHashSet();
	/**Set containing all visited SNOMED CT module concept IDs. Consider this as a cache to avoid excessive module concept existence check.*/
	private final Set<String> visitedModuleIds = Sets.newHashSet();

	protected final String releaseFileName;
	protected final ImportConfiguration configuration;
	
	private final SnomedValidationContext validationContext;
	private final String[] expectedHeader;
	private final Collection<String> effectiveTimes = newHashSet();
	
	public AbstractSnomedValidator(final ImportConfiguration configuration, 
			final URL releaseUrl,
			final ComponentImportType importType, 
			final SnomedValidationContext validationContext, 
			final String[] expectedHeader) {
		this.configuration = configuration;
		this.releaseUrl = releaseUrl;
		this.releaseFileName = configuration.getMappedName(releaseUrl.getPath());
		this.importType = importType;
		this.validationContext = validationContext;
		this.expectedHeader = expectedHeader;
		this.stagingDirectoryRoot = new File(System.getProperty("java.io.tmpdir"));
	}
	
	/**
	 * Release file specific validator method, subclass has to override it.
	 * 
	 * @param row the row which contains the release file specific elements
	 * @param lineNumber the number of the given row
	 */
	protected abstract void doValidate(List<String> row);
	
	/**
	 * Performs any one-time initialization necessary for the validation.
	 * 
	 * @param monitor the SubMonitor instance to report progress on
	 * @return the seen effective times
	 */
	protected Collection<String> preValidate(final SubMonitor monitor) {
		monitor.beginTask(MessageFormat.format("Preparing {0}s validation", importType.getDisplayName()), 1);
		
		final Map<String, CsvListWriter> writers = newHashMap();
		
		final Closer closer = Closer.create();
		try {
			final InputStreamReader releaseFileReader = closer.register(new InputStreamReader(releaseUrl.openStream(), CsvConstants.IHTSDO_CHARSET));
			final CsvListReader releaseFileListReader = closer.register(new CsvListReader(releaseFileReader, CsvConstants.IHTSDO_CSV_PREFERENCE));
			
			componentStagingDirectory = createStagingDirectory();
			
			final String[] header = releaseFileListReader.getCSVHeader(true);
			
			if (!StringUtils.equalsIgnoreCase(header, expectedHeader)) {
				addDefect(DefectType.HEADER_DIFFERENCES, String.format("Invalid header in '%s'", releaseFileName));
			}

			while (true) {
				final List<String> row = releaseFileListReader.read();

				if (null == row) {
					break;
				}
				
				final String effectiveTimeKey = getEffectiveTimeKey(row.get(1));
				
				if (!effectiveTimes.contains(effectiveTimeKey)) {
					effectiveTimes.add(effectiveTimeKey);
					
					// Use the original effective time field instead of the key
					validateEffectiveTime(row.get(1), releaseFileListReader.getLineNumber());
					
					final Path effectiveTimeFile = getEffectiveTimeFile(effectiveTimeKey);
					final BufferedWriter bw = closer.register(Files.newBufferedWriter(effectiveTimeFile, Charsets.UTF_8, StandardOpenOption.CREATE));
					final CsvListWriter lw = closer.register(new CsvListWriter(bw, CsvConstants.IHTSDO_CSV_PREFERENCE));
					writers.put(effectiveTimeKey, lw);
				}
				
				writers.get(effectiveTimeKey).write(row);
			}

			return ImmutableList.copyOf(effectiveTimes);
		} catch (final IOException e) {
			throw new ImportException(MessageFormat.format("Couldn''t read row from {0} release file.", releaseFileName), e);
		} finally {
			try {
				Closeables.close(closer, true);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			monitor.worked(1);
		}
	}
	
	private String getEffectiveTimeKey(final String effectiveTime) {
		return ContentSubType.SNAPSHOT.equals(configuration.getContentSubType()) ? SPECIAL_EFFECTIVE_TIME_KEY : effectiveTime;
	}
	
	private Path getEffectiveTimeFile(String effectiveTimeKey) {
		return componentStagingDirectory.toPath().resolve(effectiveTimeKey + "_"+ releaseFileName);
	}

	/**
	 * Validates a release file. If the release file does not have the specified effective time, then it skips execution.
	 * 
	 * @param monitor the SubMonitor instance to report progress on
	 */
	protected void doValidate(final String effectiveTime, IProgressMonitor monitor) {
		if (!effectiveTimes.contains(effectiveTime)) {
			return;	
		}
		final Stopwatch watch = Stopwatch.createStarted();
		final String effectiveTimeMessage = effectiveTime.length() == 0 ? "Unpublished" : effectiveTime;
		final String message = String.format("Validating %s file in '%s'...", importType.getDisplayName(), effectiveTimeMessage);
		monitor.beginTask(message, 1);
		validationContext.getLogger().trace(message);
		
		final int expectedNumberOfColumns = this.expectedHeader.length;
		
		try (final BufferedReader reader = getReleaseFileReader(effectiveTime)) {
			int lineNumber = 0;
			
			while (true) {
				final String line = reader.readLine();
				
				if (null == line) {
					break;
				}
				
				lineNumber++;
				
				final List<String> row = TAB_SPLITTER.splitToList(line);
				
				// skip not current effective times, also skips the first line
				if (!effectiveTime.equals(row.get(1))) {
					continue;
				}
				
				if (row.size() != expectedNumberOfColumns) {
					addDefect(DefectType.INCORRECT_COLUMN_NUMBER, String.format("Expected '%s' number of columns, but got '%s' in file %s",
							expectedNumberOfColumns, row.size(), releaseFileName));
					continue;
				}
				
				// we handle the concept file module validation in a different way
				if (!importType.equals(ComponentImportType.CONCEPT)) {
					validateModuleId(row, lineNumber);
				}
				
				doValidate(row);
			}
		} catch (final IOException e) {
			throw new ImportException(MessageFormat.format("Exception when reading {0}s for validating.", importType.getDisplayName()), e);
		} finally {
			monitor.worked(1);
			validationContext.getLogger().trace("Validated {} file in '{}' [{}]", importType.getDisplayName(), effectiveTimeMessage, watch);
		}
		// add additional defects after validation
		addDefect(DefectType.MODULE_CONCEPT_NOT_EXIST, moduleIdNotExist);
		addDefect(DefectType.INVALID_EFFECTIVE_TIME_FORMAT, invalidEffectiveTimeFormat);
		
		moduleIdNotExist.clear();
		invalidEffectiveTimeFormat.clear();
	}
	
	protected void postValidate(final SubMonitor monitor) {
		monitor.beginTask(MessageFormat.format("Finishing {0}s validation", importType.getDisplayName()), 1);
		
		if (!FileUtils.deleteDirectory(componentStagingDirectory)) {
			validationContext.getLogger().error(MessageFormat.format("Couldn''t remove {0} staging directory ''{1}''.",
					importType.getDisplayName(), componentStagingDirectory.getAbsolutePath()));
		}
	}
	
	protected void addDefect(final DefectType type, String...defects) {
		this.validationContext.addDefect(releaseFileName, type, defects);
	}
	
	/**
	 * Adds the {@link SnomedValidationDefect} to the set of defects.
	 * 
	 * @param validationDefect the defect to be added
	 */
	protected void addDefect(final DefectType type, Iterable<String> defects) {
		this.validationContext.addDefect(releaseFileName, type, defects);
	}
	
	/**
	 * Create a new {@link CsvListReader} for the release file.
	 * 
	 * @return the created reader
	 */
	protected BufferedReader getReleaseFileReader(String effectiveTime) {
		try {
			return Files.newBufferedReader(getEffectiveTimeFile(effectiveTime), Charsets.UTF_8);
		} catch (final IOException e) {
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
	protected boolean isComponentExists(final String componentId) {
		try {
			Long.parseLong(componentId);
		} catch (final NumberFormatException e) {
			//cannot be a valid core component ID
			return true;
		}
		
		return validationContext.isComponentExists(componentId, getComponentCategory(componentId));
	}
	
	private ComponentCategory getComponentCategory(String componentId) {
		for (final ComponentCategory nature : CORE_COMPONENT_CATEGORIES) {
			if (isNatureId(nature, componentId)) {
				return nature;
			}
		}
		return null;
	}

	/**
	 * Checks if the specified component identifier corresponds to this component nature (determined by its last-but-one digit).
	 * 
	 * @param componentId
	 *            the component identifier to check
	 * 
	 * @return {@code true} if the specified identifier is of this nature, {@code false} otherwise
	 */
	private boolean isNatureId(ComponentCategory category, String componentId) {

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
	protected boolean isComponentExists(final String componentId, final ReleaseComponentType componentType) {
		try {
			Long.parseLong(componentId);
		} catch (final NumberFormatException e) {
			return true;
		}
		
		if (componentType.equals(ReleaseComponentType.CONCEPT)) {
			return isConceptExists(componentId);
		} else  if (componentType.equals(ReleaseComponentType.DESCRIPTION)) {
			return isDescriptionExists(componentId);
		} else  if (componentType.equals(ReleaseComponentType.RELATIONSHIP) || componentType.equals(ReleaseComponentType.STATED_RELATIONSHIP)) {
			return isRelationshipExists(componentId);
		} else {
			return false;
		}
	}
	
	protected boolean isComponentActive(final String componentId) {
		return validationContext.isComponentActive(componentId, getComponentCategory(componentId));
	}
	
	protected void registerComponent(ComponentCategory category, String componentId, boolean status) throws AlreadyExistsException {
		validationContext.registerComponent(releaseFileName, category, componentId, status);
	}
	
	/**
	 * Validates the given component ID if it is unique or not
	 * 
	 * @param row the row where the ID can be found
	 * @param componentIds the previously processed IDs
	 * @param messages a collection where the not unique validation messages are stored (may not be {@code null})
	 * @param lineNumber the number of the line
	 */
	public void validateComponentUnique(final List<String> row, final Map<String, List<String>> componentIds, final Collection<String> messages) {
		final String id = row.get(0);
		if (componentIds.containsKey(id)) {
			// if the id is for the same component as before
			String conceptId = row.get(4);
			if (componentIds.get(id).get(0).equals(conceptId)) {
				// we set the new status
				componentIds.get(id).set(1, row.get(2));
			} else if (!componentIds.get(id).get(1).equals("0")) {
				messages.add(String.format("Component ID '%s' is not unique in file '%s'", id, releaseFileName));
			}
		} else {
			componentIds.put(id, createConceptIdStatusList(row));
		}
	}
	
	protected boolean validateComponentExists(final String effectiveTime, final String componentId, final String partOfComponentId, final ReleaseComponentType componentType, final Set<String> messages) {
		if (!isComponentExists(componentId, componentType)) {
			if (componentId.equals(partOfComponentId)) {
				messages.add(String.format("Missing component '%s' in effective time '%s'", componentId, effectiveTime));
			} else {
				messages.add(String.format("Component '%s' references missing component '%s' in effective time '%s'", partOfComponentId, componentId, effectiveTime));
			}
			return false;
		}
		return true;
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
	
	private void validateModuleId(final List<String> row, final int lineNumber) {
		final String conceptId = row.get(3);
		if (!visitedModuleIds.contains(conceptId)) {
			if (!isComponentExists(conceptId, ReleaseComponentType.CONCEPT)) {
				moduleIdNotExist.add(MessageFormat.format("Line number {0} in the ''{1}'' file with concept ID {2}.", lineNumber, releaseFileName, row.get(3)));
			} else {
				// cache module concept ID as an existing visited one
				visitedModuleIds.add(conceptId);
			}
		}
	}
	
	private void validateEffectiveTime(String effectiveTime, final int lineNumber) {
		if (effectiveTime.isEmpty()) {
			return;
		}
		try {
			EffectiveTimes.parse(effectiveTime, SnomedConstants.RF2_EFFECTIVE_TIME_FORMAT);
		} catch (final SnowowlRuntimeException e) {
			invalidEffectiveTimeFormat.add(MessageFormat.format("Line number {0} in the ''{1}'' file with effective time {2}.", lineNumber, releaseFileName, effectiveTime));
		}
	}
	
	private boolean isConceptExists(final String componentId) {
		return validationContext.isComponentExists(componentId, ComponentCategory.CONCEPT);
	}

	private boolean isDescriptionExists(final String componentId) {
		return validationContext.isComponentExists(componentId, ComponentCategory.DESCRIPTION);
	}

	private boolean isRelationshipExists(final String componentId) {
		return validationContext.isComponentExists(componentId, ComponentCategory.RELATIONSHIP);
	}
	
	private File createStagingDirectory() {
		final File componentStagingDirectory = new File(stagingDirectoryRoot, MessageFormat.format("{0}_{1}", importType.getDisplayName(), UUID.randomUUID()));
		validationContext.getLogger().info(MessageFormat.format("Creating staging directory ''{0}'' for {1} validation.", 
				componentStagingDirectory.getAbsolutePath(), importType.getDisplayName()));
		if (!componentStagingDirectory.mkdirs()) {
			throw new ImportException(MessageFormat.format("Couldn''t create staging directory for {0} validation.", importType.getDisplayName()));
		}
		return componentStagingDirectory;
	}

}
