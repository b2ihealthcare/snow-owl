/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.importer.rf2.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

import com.b2international.commons.csv.CsvLexer.EOL;
import com.b2international.commons.csv.CsvParser;
import com.b2international.commons.csv.CsvSettings;
import com.b2international.commons.csv.RecordParserCallback;
import com.b2international.snowowl.core.SnowOwlApplication;
import com.b2international.snowowl.snomed.SnomedConstants;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.snomed.importer.net4j.ImportConfiguration;
import com.b2international.snowowl.snomed.importer.rf2.refset.ErroneousAustralianReleaseFileNames;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Closeables;

/**
 * Class for getting textual representation of a SNOMED&nbsp;CT reference set (based on
 * the reference set ID) at import time.
 * 
 * <ul>
 * <li>First it reads the passed reference set to collect the different reference set
 * IDs that occur in the file.</li>
 * <li>Then it parses the description file for possible labels.</li>
 * <li>After getting all the possible labels read the language reference set to be able
 * to choose the most suitable label.</li>
 * <li>If there is any remaining unresolved reference set ID, falls back to the
 * database (if there is one) and tries to get the label from there (this is the
 * only way if only reference sets, or a delta release has to be imported, which does
 * not contain the text for the reference sets).</li>
 * <li>Populates an error dialog if a reference set cannot be resolved.</li>
 * </ul>
 * 
 */
public class SnomedRefSetNameCollector {
	
	private static final int LARGE_WORK_REMAINING = 10000;

	private static final Map<String, String> URL_TO_ICON_TYPE_ID_MAP = ImmutableMap.<String, String>builder().
			put(ErroneousAustralianReleaseFileNames.ERRONEOUS_AU_20120229_STRENGTH_REFSET_NAME, Concepts.REFSET_CONCRETE_DOMAIN_TYPE_AU).
			put(ErroneousAustralianReleaseFileNames.ERRONEOUS_AU_20120229_SUBPACK_QUANTITY_FULL_REFSET_NAME, Concepts.REFSET_CONCRETE_DOMAIN_TYPE_AU).
			put(ErroneousAustralianReleaseFileNames.ERRONEOUS_AU_20120229_UNIT_OF_USE_QUANTITY_REFSET_NAME, Concepts.REFSET_CONCRETE_DOMAIN_TYPE_AU).
			put(ErroneousAustralianReleaseFileNames.ERRONEOUS_AU_20120229_UNIT_OF_USE_SIZE_REFSET_NAME, Concepts.REFSET_CONCRETE_DOMAIN_TYPE_AU).
			put(ErroneousAustralianReleaseFileNames.ERRONEOUS_AU_20110531_CTV3_ID_REFSET_NAME, Concepts.REFSET_SIMPLE_MAP_TYPE).
			put(ErroneousAustralianReleaseFileNames.ERRONEOUS_AU_20110531_SNOMED_RT_REFSET_NAME, Concepts.REFSET_SIMPLE_MAP_TYPE).
			put(ErroneousAustralianReleaseFileNames.ERRONEOUS_AU_20110531_SNOMED_LANGUAGE_REFSET_NAME, Concepts.REFSET_LANGUAGE_TYPE).
			put(ErroneousAustralianReleaseFileNames.ERRONEOUS_AU_20120229_SNOMED_LANGUAGE_REFSET_NAME, Concepts.REFSET_LANGUAGE_TYPE).build();
	
	private static final CsvSettings CSV_SETTINGS = new CsvSettings('\0', '\t', EOL.CRLF, true);
	private static final String ACTIVE_STATUS = "1";
	private static final int DESCRIPTION_FIELD_COUNT = 9;
	private static final int CONCEPT_ID_COLUMN = 4;
	private static final int DESCRIPTION_TERM_COLUMN = 7;
	private static final int STATUS_COLUMN = 2;
	private static final int DESCRIPTION_TYPE_COLUMN = 6;
	
	private static final String CONCRETE_DOMAIN_TYPE_REFSET_ID = SnowOwlApplication.INSTANCE.getConfiguration().getModuleConfig(SnomedCoreConfiguration.class).getConcreteDomainTypeRefsetIdentifier();
	
	private ImportConfiguration configuration;
	private SubMonitor convertedMonitor;
	private Map<String, String> availableLabels = Maps.newHashMap();
	private String refSetTypeIconId;
	
	public SnomedRefSetNameCollector(IProgressMonitor monitor, String taskName) {
		this(null, monitor, taskName);
	} 
	
	public SnomedRefSetNameCollector(ImportConfiguration configuration, IProgressMonitor monitor, String taskName) {
		this.configuration = configuration;
		this.convertedMonitor = SubMonitor.convert(monitor, taskName, 2);
	}
	
	/**
	 * Parses the passed reference set, collects the available reference set IDs from it and
	 * binds them to their label.
	 * 
	 * @param refSetURL
	 *            the URL of the reference file to parse
	 */
	public void parse(URL refSetURL) {
		
		if (configuration != null) {
			parseFilesAndTerminology(refSetURL);
		} else {
			parseTerminology(refSetURL);
		}
	}
	
	private void parseFilesAndTerminology(URL refSetURL) {
		
		// Step 1: Find all reference set IDs
		final Set<String> unlabeledRefSetIds = getUnlabeledRefSetIds(refSetURL, convertedMonitor.newChild(1));

		if (refSetTypeIconId == null) {
			// Unknown reference set format, ignore
			convertedMonitor.done();
			return;
		}
		
		// Step 2: Get descriptions for reference set IDs using the description file (if present)
		if (!configuration.getDescriptionsFiles().isEmpty()) {
			readDescriptionFile(unlabeledRefSetIds, convertedMonitor.newChild(1));
		}

		// Step 3: Mine the terminology browser for more labels (if registered)
//		fillLabelsFromTeminologyBrowser(unlabeledRefSetIds);
		
		// Step 4: There may be some reference sets for which we couldn't get a label; initialize these with boilerplate text
		fillGeneralLabels(unlabeledRefSetIds);
	}

	private void readDescriptionFile(final Set<String> unlabeledRefSetIds, final SubMonitor subMonitor) {
		
		InputStreamReader descriptionReader = null;
		subMonitor.setWorkRemaining(LARGE_WORK_REMAINING);

		try {
			RecordParserCallback<String> descriptionParserCallback = new RecordParserCallback<String>() {

				@Override
				public void handleRecord(int recordCount, List<String> record) {

					String refSetId = record.get(CONCEPT_ID_COLUMN);
					String descriptionType = record.get(DESCRIPTION_TYPE_COLUMN);
					String status = record.get(STATUS_COLUMN);

					if (unlabeledRefSetIds.contains(refSetId) && 
							descriptionType.equals(SnomedConstants.Concepts.FULLY_SPECIFIED_NAME) && 
							status.equals(ACTIVE_STATUS)) {

						unlabeledRefSetIds.remove(refSetId);

						// Remove the part in parentheses for the fully specified term
						String term = record.get(DESCRIPTION_TERM_COLUMN);
						String trimmedTerm = StringUtils.substringBeforeLast(term, "(");
						availableLabels.put(refSetId, trimmedTerm);
					}
					
					subMonitor.worked(1);
					subMonitor.setWorkRemaining(LARGE_WORK_REMAINING);
				}
			};
			if (!configuration.getDescriptionsFiles().isEmpty()) {
				for(File descFile : configuration.getDescriptionsFiles()) {
					URL url = configuration.toURL(descFile);
					descriptionReader = new InputStreamReader(url.openStream());
					CsvParser parser = new CsvParser(descriptionReader, getFileName(url), CSV_SETTINGS, descriptionParserCallback, DESCRIPTION_FIELD_COUNT);
					parser.parse();
					
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			Closeables.closeQuietly(descriptionReader);
			subMonitor.done();
		}
	}

	private Set<String> getUnlabeledRefSetIds(URL refSetURL, final SubMonitor subMonitor)  {
		
		final Set<String> unlabeledRefSetIds = Sets.newHashSet();
		subMonitor.setWorkRemaining(LARGE_WORK_REMAINING);
		InputStreamReader refSetReader = null;
		
		try {
			
			int refSetColumnCount = getRefSetColumnCount(refSetURL);
			
			//guard against invalid files/folders in the SCT RF2 archive/root folder
			if (Integer.MIN_VALUE == refSetColumnCount) {
				
				return  unlabeledRefSetIds;
				
			}
			
			final String url = refSetURL.toString();
			
			RecordParserCallback<String> refSetParserCallback = new RecordParserCallback<String>() {
				
				@Override
				public void handleRecord(int recordCount, List<String> record) {
					
					if (recordCount == 1) {
						refSetTypeIconId = getRefSetTypeIconIdFromHeader(url, record);
					} else {					
						unlabeledRefSetIds.add(record.get(CONCEPT_ID_COLUMN));
					}
					
					subMonitor.worked(1);
					subMonitor.setWorkRemaining(LARGE_WORK_REMAINING);
				}
			};
			
			refSetReader = new InputStreamReader(refSetURL.openStream());
			
			CsvParser parser = new CsvParser(refSetReader, getFileName(refSetURL), CSV_SETTINGS, refSetParserCallback, refSetColumnCount);
			parser.parse();
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			Closeables.closeQuietly(refSetReader);
			subMonitor.done();
		}
		
		return unlabeledRefSetIds;
	}
	
	//returns with the number of columns for SCT reference set serialized into RF2 format.
	//if file cannot be read or empty, returns with minimum integer value, indicating invalid file
	private int getRefSetColumnCount(URL refSetURL) throws IOException  {
		
		BufferedReader reader = null;
		
		try {
			
			reader = new BufferedReader(new InputStreamReader(refSetURL.openStream()));
			final String readLine = reader.readLine();
			if (StringUtils.isEmpty(readLine)) {
				
				return Integer.MIN_VALUE;
				
			} else {
				
				return readLine.split("\t").length;
				
			}

		} finally {
			Closeables.closeQuietly(reader);
		}
	}

	private String getRefSetTypeIconIdFromHeader(String refSetURLAsString, List<String> header) {
		
		// Start with the exceptional cases first...
		for (Entry<String, String> urlToIconTypeEntry : URL_TO_ICON_TYPE_ID_MAP.entrySet()) {
			if (refSetURLAsString.contains(urlToIconTypeEntry.getKey())) {
				return urlToIconTypeEntry.getValue();
			}
		}

		// ...then carry on with the heuristics based on the release file's header
		String lastColumnName = header.get(header.size() - 1);
		
		if (lastColumnName.equalsIgnoreCase(SnomedRf2Headers.FIELD_REFERENCED_COMPONENT_ID)) {
			return Concepts.REFSET_ALL; // Simple type, but we need an exact icon here
		} else if (lastColumnName.equalsIgnoreCase(SnomedRf2Headers.FIELD_MAP_TARGET)) {
			return Concepts.REFSET_SIMPLE_MAP_TYPE;
		} else if (lastColumnName.equalsIgnoreCase(SnomedRf2Headers.FIELD_VALUE_ID)) {
			return Concepts.REFSET_ATTRIBUTE_VALUE_TYPE;
		} else if (lastColumnName.equalsIgnoreCase(SnomedRf2Headers.FIELD_TARGET_COMPONENT) || lastColumnName.equalsIgnoreCase(SnomedRf2Headers.FIELD_TARGET_COMPONENT_ID)) {
			return Concepts.REFSET_ASSOCIATION_TYPE;
		} else if (lastColumnName.equalsIgnoreCase(SnomedRf2Headers.FIELD_ACCEPTABILITY_ID)) {
			return Concepts.REFSET_LANGUAGE_TYPE;
		} else if (lastColumnName.equalsIgnoreCase(SnomedRf2Headers.FIELD_QUERY)) {
			return Concepts.REFSET_QUERY_SPECIFICATION_TYPE;
		} else if (lastColumnName.equalsIgnoreCase(SnomedRf2Headers.FIELD_CORRELATION_ID)) {
			return Concepts.REFSET_COMPLEX_MAP_TYPE;
		} else if (lastColumnName.equalsIgnoreCase(SnomedRf2Headers.FIELD_DESCRIPTION_LENGTH)) {
			return Concepts.REFSET_DESCRIPTION_TYPE;
		} else if (lastColumnName.equalsIgnoreCase(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID)) {
			return CONCRETE_DOMAIN_TYPE_REFSET_ID;
		} else if (lastColumnName.equalsIgnoreCase(SnomedRf2Headers.FIELD_VALUE)) {
			return Concepts.REFSET_CONCRETE_DOMAIN_TYPE_AU;
		} else if (lastColumnName.equalsIgnoreCase(SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME)) {
			return Concepts.REFSET_MODULE_DEPENDENCY_TYPE;
		} else if (lastColumnName.equalsIgnoreCase(SnomedRf2Headers.FIELD_MAP_CATEGORY_ID)) {
			return Concepts.EXTENDED_MAP_TYPE;
		} else if (lastColumnName.equalsIgnoreCase(SnomedRf2Headers.FIELD_MAP_TARGET_DESCRIPTION)) {
			return Concepts.REFSET_SIMPLE_MAP_TYPE;
		}
		
		return null;
	}

//	private void fillLabelsFromTeminologyBrowser(Set<String> unlabeledRefSetIds) {
//		Iterator<String> unlabeledRefSetIdIterator = unlabeledRefSetIds.iterator();
//		
//		while (unlabeledRefSetIdIterator.hasNext()) {
//			
//			String refSetId = unlabeledRefSetIdIterator.next();
//			SnomedConceptDocument refsetConcept = terminologyBrowser.getConcept(refSetId);
//			
//			if (refsetConcept != null) {
//				String refSetLabel = refsetConcept.getLabel();
//				availableLabels.put(refSetId, refSetLabel);
//				unlabeledRefSetIdIterator.remove();
//			}
//		}
//	}

	private void fillGeneralLabels(Set<String> unlabeledRefSetIds) {
		
		for (String refSetId : unlabeledRefSetIds) {
			availableLabels.put(refSetId, "Reference set " + refSetId + " (unresolved)");
		}
	}

	/*returns with the file name extracted from the given URL*/
	private Optional<String> getFileName(final URL fileUrl) {
		
		String fileName = null;
		
		try {
			
			fileName = new File(fileUrl.toURI()).getName();
			
		} catch (final Throwable t) {

			try {
				
				//assuming zip URL
				if (fileUrl.toString().startsWith("zip")) {
					final URI uri = URI.create(fileUrl.toString().substring(fileUrl.toString().indexOf('!') + 1));
					fileName = org.eclipse.emf.common.util.URI.createFileURI(uri.getPath()).lastSegment();
				}
				
			} catch (final Throwable t2) {
				//ignore it, file name is optional
			}

			
		}
		
		return Optional.<String>fromNullable(fileName);
		
	}
	
	private void parseTerminology(URL refSetURL)  {
		
		convertedMonitor.setWorkRemaining(1);
		
		// Step 1: Find all reference set IDs
		Set<String> unlabeledRefSetIds = getUnlabeledRefSetIds(refSetURL, convertedMonitor.newChild(1));

		if (refSetTypeIconId == null) {
			// Unknown reference set format, ignore
			return;
		}

		// Step 2: Mine the terminology browser for more labels (if registered)
//		fillLabelsFromTeminologyBrowser(unlabeledRefSetIds);
		
		// Step 3: There may be some reference sets for which we couldn't get a label; initialize these with boilerplate text
		fillGeneralLabels(unlabeledRefSetIds);
	}

	/**
	 * @return a map containing the parsed reference set ids and their
	 *         corresponding labels
	 */
	public Map<String, String> getAvailableLabels() {
		return availableLabels;
	}

	/**
	 * Each file contains only one kind of reference set; this method returns
	 * the reference set type id (map, language...) based on the reference set
	 * header, since at import time we may not have services to traverse trough
	 * the hierarchy from the reference set id to the parent type identifier.
	 * 
	 * @return the concept id of the common base type of the reference sets
	 *         found in the release file
	 */
	public String getRefSetTypeIconId() {
		return refSetTypeIconId;
	}
}