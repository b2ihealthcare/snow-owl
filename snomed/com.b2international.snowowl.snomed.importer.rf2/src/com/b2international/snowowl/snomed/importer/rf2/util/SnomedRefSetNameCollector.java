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
package com.b2international.snowowl.snomed.importer.rf2.util;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

import com.b2international.commons.csv.CsvLexer.EOL;
import com.b2international.commons.csv.CsvParser;
import com.b2international.commons.csv.CsvSettings;
import com.b2international.commons.csv.RecordParserCallback;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.SnowOwlApplication;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedConstants;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.importer.net4j.ImportConfiguration;
import com.google.common.base.Optional;
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
	
	private static final String UNLABELED_REFSET_PREFIX = "Reference set ";
	private static final String UNLABELED_REFSET_SUFFIX = " (unresolved)";
	private static final String UNLABELED_REFSET_TEMPLATE = UNLABELED_REFSET_PREFIX + "%s" + UNLABELED_REFSET_SUFFIX;
	public static final String UNLABELED_REFSET_REGEX = UNLABELED_REFSET_PREFIX + "([\\d]*)" + Pattern.quote(UNLABELED_REFSET_SUFFIX);

	private static final CsvSettings CSV_SETTINGS = new CsvSettings('\0', '\t', EOL.CRLF, true);

	private static final String ACTIVE_STATUS = "1";
	
	private static final int STATUS_COLUMN = 2;
	private static final int REFSET_ID_COLUMN = 4;
	private static final int DESCRIPTION_CONCEPT_ID_COLUMN = 4;
	private static final int DESCRIPTION_TYPE_COLUMN = 6;
	private static final int DESCRIPTION_TERM_COLUMN = 7;
	
	private static final int DESCRIPTION_FIELD_COUNT = SnomedRf2Headers.DESCRIPTION_HEADER.length;
	
	private static final String CONCRETE_DOMAIN_TYPE_REFSET_ID = SnowOwlApplication.INSTANCE.getConfiguration()
			.getModuleConfig(SnomedCoreConfiguration.class).getConcreteDomainTypeRefsetIdentifier();

	private ImportConfiguration configuration;
	private IProgressMonitor monitor;
	private Map<String, String> refsetIdToLabelMap = newHashMap();
	private Map<String, String> refSetIdToIconIdMap = newHashMap();
	private Collection<URL> refsetUrls;
	
	public SnomedRefSetNameCollector(Collection<URL> refsetUrls, ImportConfiguration configuration, IProgressMonitor monitor) {
		this.refsetUrls = refsetUrls;
		this.configuration = configuration;
		this.monitor = monitor;
	}
	
	public void parse() {
		
		SubMonitor subMonitor = SubMonitor.convert(monitor, "Collecting reference set labels...", 2);
		
		// Step 1: Find all reference set IDs
		final Set<String> unlabeledRefSetIds = getUnlabeledRefSetIds(refsetUrls, subMonitor.newChild(1));

		if (unlabeledRefSetIds.isEmpty()) {
			monitor.done();
			return;
		}
		
		// Step 2: Use the general label for refsets that already exist (the client will fill out those labels later)
		useGeneralLabelForExistingRefsets(unlabeledRefSetIds);
		
		if (unlabeledRefSetIds.isEmpty()) {
			monitor.done();
			return;
		}
		
		// Step 3: Get descriptions for reference set IDs using the description file (if present)
		if (!configuration.getDescriptionFiles().isEmpty()) {
			readDescriptionFiles(unlabeledRefSetIds, subMonitor.newChild(1));
		}

		// Step 4: There may be some reference sets for which we couldn't get a label; initialize these with boilerplate text
		fillGeneralLabels(unlabeledRefSetIds);
	}

	private void useGeneralLabelForExistingRefsets(Set<String> unlabeledRefSetIds) {
		
		Set<String> existingConceptIds = SnomedRequests.prepareSearchConcept()
			.setLimit(unlabeledRefSetIds.size())
			.filterByActive(true)
			.filterByIds(unlabeledRefSetIds)
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, configuration.getBranchPath())
			.execute(ApplicationContext.getServiceForClass(IEventBus.class))
			.then(concepts -> concepts.stream().map(SnomedConcept::getId).collect(Collectors.toSet()))
			.getSync();
		
		fillGeneralLabels(existingConceptIds);
		unlabeledRefSetIds.removeAll(existingConceptIds);
	}

	private void readDescriptionFiles(final Set<String> unlabeledRefSetIds, final IProgressMonitor monitor) {
		
		RecordParserCallback<String> descriptionParserCallback = new RecordParserCallback<String>() {

			@Override
			public void handleRecord(int recordCount, List<String> record) {

				if (!unlabeledRefSetIds.isEmpty()) {
					
					String refSetId = record.get(DESCRIPTION_CONCEPT_ID_COLUMN);
					String descriptionType = record.get(DESCRIPTION_TYPE_COLUMN);
					String status = record.get(STATUS_COLUMN);
					
					if (unlabeledRefSetIds.contains(refSetId) && 
							descriptionType.equals(SnomedConstants.Concepts.FULLY_SPECIFIED_NAME) && 
							status.equals(ACTIVE_STATUS)) {
						
						// Remove the part in parentheses for the fully specified term
						String term = record.get(DESCRIPTION_TERM_COLUMN);
						String trimmedTerm = StringUtils.substringBeforeLast(term, "(");
						refsetIdToLabelMap.put(refSetId, trimmedTerm);
						unlabeledRefSetIds.remove(refSetId);
						
					}
					
				}
				
			}
		};

		
		if (!configuration.getDescriptionFiles().isEmpty()) {
			
			SubMonitor subMonitor = SubMonitor.convert(monitor, configuration.getDescriptionFiles().size());
			subMonitor.setTaskName("Parsing description files for reference set labels...");

			for (File descFile : configuration.getDescriptionFiles()) {
				
				if (!unlabeledRefSetIds.isEmpty()) {
					
					InputStreamReader descriptionReader = null;
					
					try {
						
						final URL url = configuration.toURL(descFile);
						descriptionReader = new InputStreamReader(url.openStream());
						final CsvParser parser = new CsvParser(descriptionReader, getFileName(url), CSV_SETTINGS,
								descriptionParserCallback, DESCRIPTION_FIELD_COUNT);
						parser.parse();
						
					} catch (IOException e) {
						throw new RuntimeException(e);
					} finally {
						Closeables.closeQuietly(descriptionReader);
					}
					
				}
				
				subMonitor.worked(1);
			}
			
		}
		
		monitor.done();
	}

	private Set<String> getUnlabeledRefSetIds(Collection<URL> urls, final IProgressMonitor monitor)  {
		
		final Set<String> unlabeledRefSetIds = newHashSet();
		final Map<URL, String> urlToIconIdMap = newHashMap();
		
		SubMonitor subMonitor = SubMonitor.convert(monitor, urls.size());
		subMonitor.setTaskName("Collecting available reference sets...");
		
		for (URL url : urls) {
			
			InputStreamReader refSetReader = null;
			
			try {
				
				int refSetColumnCount = getRefSetColumnCount(url);
				
				//guard against invalid files/folders in the SCT RF2 archive/root folder
				if (Integer.MIN_VALUE == refSetColumnCount) {
					subMonitor.worked(1);
					continue;
				}
				
				RecordParserCallback<String> refSetParserCallback = new RecordParserCallback<String>() {
					@Override
					public void handleRecord(int recordCount, List<String> record) {
						
						if (recordCount == 1) {
							urlToIconIdMap.put(url, getRefSetTypeIconIdFromHeader(record));
						} else {					
							String refsetId = record.get(REFSET_ID_COLUMN);
							if (!unlabeledRefSetIds.contains(refsetId)) {
								unlabeledRefSetIds.add(refsetId);
								refSetIdToIconIdMap.put(refsetId, urlToIconIdMap.get(url));
							}
						}
						
					}
				};
				
				refSetReader = new InputStreamReader(url.openStream());
				
				CsvParser parser = new CsvParser(refSetReader, getFileName(url), CSV_SETTINGS, refSetParserCallback, refSetColumnCount);
				parser.parse();
				
			} catch (IOException e) {
				throw new RuntimeException(e);
			} finally {
				Closeables.closeQuietly(refSetReader);
				subMonitor.worked(1);
			}
		}
		
		monitor.done();
		
		return unlabeledRefSetIds;
	}
	
	private int getRefSetColumnCount(URL refSetURL) throws IOException  {
		try (InputStreamReader inputStreamReader = new InputStreamReader(refSetURL.openStream())) {
			try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
				final String readLine = reader.readLine();
				return StringUtils.isEmpty(readLine) ? Integer.MIN_VALUE : readLine.split("\t").length;
			}
		}
	}

	private String getRefSetTypeIconIdFromHeader(List<String> header) {
		
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
		} else if (lastColumnName.equalsIgnoreCase(SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME)) {
			return Concepts.REFSET_MODULE_DEPENDENCY_TYPE;
		} else if (lastColumnName.equalsIgnoreCase(SnomedRf2Headers.FIELD_MAP_CATEGORY_ID)) {
			return Concepts.EXTENDED_MAP_TYPE;
		} else if (lastColumnName.equalsIgnoreCase(SnomedRf2Headers.FIELD_MAP_TARGET_DESCRIPTION)) {
			return Concepts.REFSET_SIMPLE_MAP_TYPE;
		}
		
		return null;
	}

	private void fillGeneralLabels(Set<String> unlabeledRefSetIds) {
		for (String refSetId : unlabeledRefSetIds) {
			refsetIdToLabelMap.put(refSetId, String.format(UNLABELED_REFSET_TEMPLATE, refSetId));
		}
	}

	/* returns with the file name extracted from the given URL */
	private Optional<String> getFileName(final URL fileUrl) {
		
		String fileName = null;
		
		try {
			
			fileName = new File(fileUrl.toURI()).getName();
			
		} catch (final Throwable t) {

			try {
				
				// assuming zip URL
				if (fileUrl.toString().startsWith("zip")) {
					final URI uri = URI.create(fileUrl.toString().substring(fileUrl.toString().indexOf('!') + 1));
					fileName = org.eclipse.emf.common.util.URI.createFileURI(uri.getPath()).lastSegment();
				}
				
			} catch (final Throwable t2) {
				// ignore it, file name is optional
			}

			
		}
		
		return Optional.<String>fromNullable(fileName);
		
	}

	public Map<String, String> getRefsetIdToLabelMap() {
		return refsetIdToLabelMap;
	}

	public String getRefSetTypeIconId(String refsetId) {
		return refSetIdToIconIdMap.get(refsetId);
	}
}