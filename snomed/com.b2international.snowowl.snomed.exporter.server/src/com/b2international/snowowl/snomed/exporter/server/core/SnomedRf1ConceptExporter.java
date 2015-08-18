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
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.CONCEPT_NUMBER;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_ACTIVE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_FULLY_SPECIFIED_NAME;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_PRIMITIVE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_REFERENCED_COMPONENT_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_REFERENCE_SET_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_VALUE_ID;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Suppliers.memoize;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.unmodifiableSet;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ReferenceManager;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.datastore.index.query.IndexQueries;
import com.b2international.snowowl.datastore.server.index.IndexServerService;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
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

/**
 * RF1 exporter for SNOMED&nbsp;CT concepts.
 *
 */
public class SnomedRf1ConceptExporter implements SnomedRf1Exporter {

	private static final Set<String> CONCEPT_FILEDS_TO_LOAD = unmodifiableSet(newHashSet(
			COMPONENT_ACTIVE,
			CONCEPT_FULLY_SPECIFIED_NAME,
			CONCEPT_PRIMITIVE
			));
	
	private static final Set<String> MAP_TARGET_ID_FIELD_TO_LOAD = unmodifiableSet(newHashSet(
			REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_ID
			));
	
	private static final Set<String> INACTIVATION_ID_FIELD_TO_LOAD = unmodifiableSet(newHashSet(
			REFERENCE_SET_MEMBER_VALUE_ID
			));

	private final static TermQuery INACTIVATION_QUERY = 
			new TermQuery(new Term(REFERENCE_SET_MEMBER_REFERENCE_SET_ID, IndexUtils.longToPrefixCoded(Concepts.REFSET_CONCEPT_INACTIVITY_INDICATOR)));
	private final static TermQuery CTV3_QUERY = 
			new TermQuery(new Term(REFERENCE_SET_MEMBER_REFERENCE_SET_ID, IndexUtils.longToPrefixCoded(Concepts.CTV3_SIMPLE_MAP_TYPE_REFERENCE_SET_ID)));
	private final static TermQuery SNOMED_RT_QUERY = 
			new TermQuery(new Term(REFERENCE_SET_MEMBER_REFERENCE_SET_ID, IndexUtils.longToPrefixCoded(Concepts.SNOMED_RT_SIMPLE_MAP_TYPE_REFERENCE_SET_ID)));
	
	private final Id2Rf1PropertyMapper mapper;
	private final SnomedExportConfiguration configuration;
	private final Supplier<Iterator<String>> itrSupplier;

	public SnomedRf1ConceptExporter(final SnomedExportConfiguration configuration, final Id2Rf1PropertyMapper mapper) {
		this.configuration = checkNotNull(configuration, "configuration");
		this.mapper = checkNotNull(mapper, "mapper");
		itrSupplier = createSupplier();
	}
	
	private Supplier<Iterator<String>> createSupplier() {
		return memoize(new Supplier<Iterator<String>>() {
			@Override
			public Iterator<String> get() {
				return new AbstractIterator<String>() {
					
					private final Iterator<IdStorageKeyPair> idIterator = getServiceForClass(ISnomedComponentService.class)
							.getAllComponentIdStorageKeys(getBranchPath(), CONCEPT_NUMBER).iterator();
					
					@SuppressWarnings("rawtypes")
					private final IndexServerService indexService = (IndexServerService) ApplicationContext.getInstance().getService(SnomedIndexService.class);
					private Object[] _values;
					
					@SuppressWarnings("unchecked")
					@Override
					protected String computeNext() {
						
						while (idIterator.hasNext()) {
							
							final String conceptId = idIterator.next().getId();
							_values = new Object[6];
							
							ReferenceManager<IndexSearcher> manager = null;
							IndexSearcher searcher = null;
							
							try {
								
								manager = indexService.getManager(getBranchPath());
								searcher = manager.acquire();
								
								final Query conceptQuery = IndexQueries.queryComponentByLongId(CONCEPT_NUMBER, conceptId);
								final TopDocs conceptTopDocs = indexService.search(getBranchPath(), conceptQuery, 1);
								
								Preconditions.checkState(null != conceptTopDocs && !CompareUtils.isEmpty(conceptTopDocs.scoreDocs));
								
								final Document doc = searcher.doc(conceptTopDocs.scoreDocs[0].doc, CONCEPT_FILEDS_TO_LOAD);
								
								_values[0] = conceptId;
								_values[1] = 1 == IndexUtils.getIntValue(doc.getField(COMPONENT_ACTIVE)) ? "1" : "0";
								_values[2] = doc.get(CONCEPT_FULLY_SPECIFIED_NAME);
								_values[3] = 1 == IndexUtils.getIntValue(doc.getField(CONCEPT_PRIMITIVE)) ? "1" : "0";
								
								if ("0".equals(String.valueOf(_values[1]))) {
									
									final BooleanQuery inactivationQuery = new BooleanQuery(true);
									inactivationQuery.add(INACTIVATION_QUERY, Occur.MUST);
									inactivationQuery.add(new TermQuery(new Term(REFERENCE_SET_MEMBER_REFERENCED_COMPONENT_ID, conceptId)), Occur.MUST);
									final TopDocs inactivationTopDocs = indexService.search(getBranchPath(), inactivationQuery, 1);
									
									if (null != inactivationTopDocs && !CompareUtils.isEmpty(inactivationTopDocs.scoreDocs)) {
										_values[1] = searcher.doc(inactivationTopDocs.scoreDocs[0].doc, INACTIVATION_ID_FIELD_TO_LOAD).get(REFERENCE_SET_MEMBER_VALUE_ID);
									} 
									
								}
								
								final BooleanQuery ctv3Query = new BooleanQuery(true);
								ctv3Query.add(CTV3_QUERY, Occur.MUST);
								ctv3Query.add(new TermQuery(new Term(REFERENCE_SET_MEMBER_REFERENCED_COMPONENT_ID, conceptId)), Occur.MUST);
								final TopDocs ctv3TopDocs = indexService.search(getBranchPath(), ctv3Query, 1);
								
								if (null != ctv3TopDocs && !CompareUtils.isEmpty(ctv3TopDocs.scoreDocs)) {
									_values[4] = searcher.doc(ctv3TopDocs.scoreDocs[0].doc, MAP_TARGET_ID_FIELD_TO_LOAD).get(REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_ID);
								}
								
								final BooleanQuery snomedRtQuery = new BooleanQuery(true);
								snomedRtQuery.add(SNOMED_RT_QUERY, Occur.MUST);
								snomedRtQuery.add(new TermQuery(new Term(REFERENCE_SET_MEMBER_REFERENCED_COMPONENT_ID, conceptId)), Occur.MUST);
								final TopDocs snomedRtTopDocs = indexService.search(getBranchPath(), snomedRtQuery, 1);
								
								if (null != snomedRtTopDocs && !CompareUtils.isEmpty(snomedRtTopDocs.scoreDocs)) {
									_values[5] = searcher.doc(snomedRtTopDocs.scoreDocs[0].doc, MAP_TARGET_ID_FIELD_TO_LOAD).get(REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_ID);
								}
								
								return new StringBuilder(valueOfOrEmptyString(_values[0])) //ID
									.append(HT)
									.append(getConceptStatus(valueOfOrEmptyString(_values[1]))) //status
									.append(HT)
									.append(valueOfOrEmptyString(_values[2])) //FSN
									.append(HT)
									.append(valueOfOrEmptyString(_values[4])) //CTV3
									.append(HT)
									.append(valueOfOrEmptyString(_values[5])) //SNOMEDRT
									.append(HT)
									.append(valueOfOrEmptyString(_values[3])) //definition status
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
		return ComponentExportType.CONCEPT;
	}

	@Override
	public String[] getColumnHeaders() {
		return SnomedReleaseFileHeaders.RF1_CONCEPT_HEADER;
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
	public void close() throws Exception {
		//intentionally ignored
	}
	
	@Override
	public SnomedExportConfiguration getConfiguration() {
		return configuration;
	}
	
	/*returns with a number indicating the status of a concept for RF1 publication.*/
	private String getConceptStatus(final String stringValue) {
		//magic mapping between RF1 and RF2 statuses
		if ("1".equals(stringValue)) {
			return "0";
		} else if ("0".equals(stringValue)) {
			return "1";
		} else {
			return Preconditions.checkNotNull(mapper.getConceptStatusProperty(stringValue));
		}
	}

}