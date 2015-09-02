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
package com.b2international.snowowl.snomed.exporter.server.core;

import static com.b2international.commons.StringUtils.valueOfOrEmptyString;
import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Suppliers.memoize;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ReferenceManager;
import org.apache.lucene.search.TopDocs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.datastore.index.mapping.Mappings;
import com.b2international.snowowl.datastore.server.index.IndexServerService;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.ILanguageConfigurationProvider;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService.IdStorageKeyPair;
import com.b2international.snowowl.snomed.exporter.server.ComponentExportType;
import com.b2international.snowowl.snomed.exporter.server.Id2Rf1PropertyMapper;
import com.b2international.snowowl.snomed.exporter.server.SnomedReleaseFileHeaders;
import com.b2international.snowowl.snomed.exporter.server.SnomedRf1Exporter;
import com.b2international.snowowl.snomed.exporter.server.SnomedRfFileNameBuilder;
import com.b2international.snowowl.snomed.exporter.server.sandbox.SnomedExportConfiguration;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Sets;

/**
 * RF1 exporter for SNOMED&nbsp;CT descriptions.
 *
 */
public class SnomedRf1DescriptionExporter implements SnomedRf1Exporter {

	private static final Logger LOGGER = LoggerFactory.getLogger(SnomedRf1DescriptionExporter.class);
	
	private static final String LANGUAGE_CODE = "en";
	
	private static final Set<String> DESCRIPTION_FILEDS_TO_LOAD = SnomedMappings.fieldsToLoad()
			.active()
			.label()
			.descriptionConcept()
			.descriptionType()
			.field(SnomedIndexBrowserConstants.DESCRIPTION_CASE_SIGNIFICANCE_ID)
			.build();
	
	private static final Set<String> INACTIVATION_ID_FIELD_TO_LOAD = Collections.unmodifiableSet(Sets.newHashSet(
			SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_VALUE_ID
			));
	
	private final static Query INACTIVATION_QUERY = SnomedMappings.newQuery().memberRefSetId(Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR).matchAll();
	private static final Query PREFERRED_MEMBER_QUERY = SnomedMappings.newQuery().field(SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_ACCEPTABILITY_ID, 
			Long.parseLong(Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED)).matchAll();
	
	private final Id2Rf1PropertyMapper mapper;
	private final SnomedExportConfiguration configuration;
	private final boolean includeExtendedDescriptionTypes;
	private final Supplier<Iterator<String>> itrSupplier;
	private final Set<String> undefinedDescriptionTypeIds = Sets.newHashSet();
	private String preferredLanguageId;
	private Query languageRefSetQuery;
	
	public SnomedRf1DescriptionExporter(final SnomedExportConfiguration configuration, final Id2Rf1PropertyMapper mapper, final boolean includeExtendedDescriptionTypes) {
		this.configuration = checkNotNull(configuration, "configuration");
		this.mapper = checkNotNull(mapper, "mapper");
		this.includeExtendedDescriptionTypes = includeExtendedDescriptionTypes;
		preferredLanguageId = ApplicationContext.getInstance().getService(ILanguageConfigurationProvider.class).getLanguageConfiguration().getLanguageRefSetId();
		languageRefSetQuery = SnomedMappings.newQuery().memberRefSetId(preferredLanguageId).matchAll();
		itrSupplier = createSupplier();
	}
	
	private Supplier<Iterator<String>> createSupplier() {
		return memoize(new Supplier<Iterator<String>>() {
			@Override
			public Iterator<String> get() {
				return new AbstractIterator<String>() {
					
					private final Iterator<IdStorageKeyPair> idIterator = getServiceForClass(ISnomedComponentService.class)
							.getAllComponentIdStorageKeys(getBranchPath(), DESCRIPTION_NUMBER).iterator();
					
					@SuppressWarnings("rawtypes")
					private final IndexServerService indexService = (IndexServerService) ApplicationContext.getInstance().getService(SnomedIndexService.class);
					private Object[] _values;
					
					@SuppressWarnings("unchecked")
					@Override
					protected String computeNext() {
						
						while (idIterator.hasNext()) {
							
							final String descriptionId = idIterator.next().getId();
							
							_values = new Object[6];
							
							ReferenceManager<IndexSearcher> manager = null;
							IndexSearcher searcher = null;
							
							try {
								
								manager = indexService.getManager(getBranchPath());
								searcher = manager.acquire();
								
								
								final Query descriptionQuery = SnomedMappings.newQuery().type(DESCRIPTION_NUMBER).id(descriptionId).matchAll();
								final TopDocs conceptTopDocs = indexService.search(getBranchPath(), descriptionQuery, 1);
								
								Preconditions.checkState(null != conceptTopDocs && !CompareUtils.isEmpty(conceptTopDocs.scoreDocs));
								
								final Document doc = searcher.doc(conceptTopDocs.scoreDocs[0].doc, DESCRIPTION_FILEDS_TO_LOAD);
								
								_values[0] = descriptionId;
								_values[1] = 1 == SnomedMappings.active().getValue(doc) ? "1" : "0";
								_values[2] = SnomedMappings.descriptionConcept().getValueAsString(doc);
								_values[3] = Mappings.label().getValue(doc);
								_values[4] = doc.get(SnomedIndexBrowserConstants.DESCRIPTION_CASE_SIGNIFICANCE_ID);
								_values[5] = SnomedMappings.descriptionType().getValueAsString(doc);
								
								if ("0".equals(String.valueOf(_values[1]))) {
									final Query inactivationQuery = SnomedMappings.newQuery().memberReferencedComponentId(descriptionId).and(INACTIVATION_QUERY).matchAll();
									final TopDocs inactivationTopDocs = indexService.search(getBranchPath(), inactivationQuery, 1);
									
									if (null != inactivationTopDocs && !CompareUtils.isEmpty(inactivationTopDocs.scoreDocs)) {
										_values[1] = searcher.doc(inactivationTopDocs.scoreDocs[0].doc, INACTIVATION_ID_FIELD_TO_LOAD).get(SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_VALUE_ID);
									} 
									
								}
								
								final String descriptionTypeId = mapper.getDescriptionType(String.valueOf(_values[5]));
								final String typeId;
								
								boolean preferredTerm;
								
								//FSN cannot be preferred
								if (Concepts.FULLY_SPECIFIED_NAME.equals(_values[5])) {
									preferredTerm = false;
								} else {
									final Query preferredQuery = SnomedMappings.newQuery().memberReferencedComponentId(descriptionId).and(languageRefSetQuery).and(PREFERRED_MEMBER_QUERY).matchAll();
									preferredTerm = indexService.getHitCount(getBranchPath(), preferredQuery, null) > 0;
								}
								
								if (includeExtendedDescriptionTypes && null == descriptionTypeId && !preferredTerm) {
									typeId = getDescriptionType(String.valueOf(_values[5]));
								} else {
									typeId = preferredTerm ? "1" : null == descriptionTypeId ? "0" : descriptionTypeId;
								}
								
								return new StringBuilder(descriptionId) //ID
									.append(HT)
									.append(getDescriptionStatus(valueOfOrEmptyString(_values[1]))) //status
									.append(HT)
									.append(valueOfOrEmptyString(_values[2])) //concept id
									.append(HT)
									.append(valueOfOrEmptyString(_values[3])) //term
									.append(HT)
									.append(mapper.getInitialCapitalStatus(valueOfOrEmptyString(_values[4]))) //initial capital status
									.append(HT)
									.append(typeId) //type
									.append(HT)
									.append(LANGUAGE_CODE) //language code
									.append(HT)
									.toString();
								
							} catch (final IOException e) {
								
								throw new SnowowlRuntimeException(e);
								
							} finally {
								
								if (null != manager && null != searcher) {
									
									try {
										
										manager.release(searcher);
										
									} catch (final IOException e) {
										
										throw new SnowowlRuntimeException(e);
										
									}
									
								}
							
							}
							
						}
						
						return endOfData();
					}
					
					private IBranchPath getBranchPath() {
						return configuration.getCurrentBranchPath();
					}
					
				};
			}
		});
	}
	
	@Override
	public String getRelativeDirectory() {
		return RF1_CORE_RELATIVE_DIRECTORY;
	}

	@Override
	public String getFileName() {
		return SnomedRfFileNameBuilder.buildCoreRf1FileName(getType(), configuration);
	}

	@Override
	public ComponentExportType getType() {
		return ComponentExportType.DESCRIPTION;
	}

	@Override
	public String[] getColumnHeaders() {
		return SnomedReleaseFileHeaders.RF1_DESCRIPTION_HEADER;
	}

	@Override
	public boolean hasNext() {
		return itrSupplier.get().hasNext();
	}

	@Override
	public String next() {
		return itrSupplier.get().next();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<String> iterator() {
		return itrSupplier.get();
	}

	@Override
	public void close() throws IOException {
		//intentionally ignored
	}
	
	@Override
	public SnomedExportConfiguration getConfiguration() {
		return configuration;
	}
	
	private String getDescriptionType(final String descriptionType) {
		if (descriptionType.equals(Concepts.FULL_NAME)) {
			return "4";
		} else if (descriptionType.equals(Concepts.ABBREVIATION)) {
			return "5";
		} else if (descriptionType.equals(Concepts.PRODUCT_TERM)) {
			return "6";
		} else if (descriptionType.equals(Concepts.SHORT_NAME)) {
			return "7";
		} else if (descriptionType.equals(Concepts.PREFERRED_PLURAL)) {
			return "8";
		} else if (descriptionType.equals(Concepts.NOTE)) {
			return "9";
		} else if (descriptionType.equals(Concepts.SEARCH_TERM)) {
			return "10";
		} else if (descriptionType.equals(Concepts.ABBREVIATION_PLURAL)) {
			return "11";
		} else if (descriptionType.equals(Concepts.PRODUCT_TERM_PLURAL)) {
			return "12";
		} 
		
		// Report undefined type IDs only once
		if (undefinedDescriptionTypeIds.add(descriptionType)) {
			LOGGER.warn("Description type ID '" + descriptionType + "' not mapped to RF1, exporting as synonym.");
		}
		
		return "2";
	}
	
	/*returns with a number indicating the status of a description for RF1 publication.*/
	private String getDescriptionStatus(final String stringValue) {
		if ("1".equals(stringValue)) { //magic mapping between rf1 and rf2 status
			return "0";
		} else if ("0".equals(stringValue)) {
			return "1";
		} else {
			return Preconditions.checkNotNull(mapper.getDescriptionStatusProperty(stringValue));
		}
		
	}
	
}