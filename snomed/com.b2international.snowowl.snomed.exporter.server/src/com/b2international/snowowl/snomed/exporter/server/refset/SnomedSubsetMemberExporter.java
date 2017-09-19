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
package com.b2international.snowowl.snomed.exporter.server.refset;

import static com.b2international.snowowl.snomed.exporter.server.SnomedReleaseFileHeaders.RF1_SUBSET_MEMBERS_HEADER;
import static java.util.Collections.emptyList;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ReferenceManager;
import org.apache.lucene.search.TopDocs;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.datastore.server.index.IndexServerService;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedFieldsToLoadBuilder;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.exporter.server.ComponentExportType;
import com.b2international.snowowl.snomed.exporter.server.Id2Rf1PropertyMapper;
import com.b2international.snowowl.snomed.exporter.server.SnomedRf1Exporter;
import com.b2international.snowowl.snomed.exporter.server.sandbox.SnomedExportConfiguration;
import com.google.common.base.Function;
import com.google.common.collect.Iterators;

import bak.pcj.map.LongKeyLongMap;
import bak.pcj.map.LongKeyLongOpenHashMap;
import bak.pcj.set.LongOpenHashSet;
import bak.pcj.set.LongSet;

/**
 * Implementation of the SNOMED&nbsp;CT subset exporter. Supports RF1 output format. Used for simple type and language type reference sets.
 * <p>
 * <b>RR1</b> CSV header:
 * <ul><li>SUBSETID&#9;MEMBERID&#9;MEMBERSTATUS&#9;LINKEDID</li></ul>
 * </p>
 * @see SnomedRf1Exporter
 * @see AbstractSnomedSubsetExporter
 * @see SnomedExportSetting
 */
public class SnomedSubsetMemberExporter extends AbstractSnomedSubsetExporter {
	
	private final boolean languageType;
	private final Id2Rf1PropertyMapper mapper;
	private final LongSet distinctEffectiveTimeSet;
	
	private static final Set<String> NON_LANGUAGE_MEMBER_FIELD_TO_LOAD;
	private static final Set<String> LANGUAGE_MEMBER_FIELD_TO_LOAD;
	
	static {
		
		final SnomedFieldsToLoadBuilder commonFieldsToLoad = SnomedMappings.fieldsToLoad()
			.active()
			.effectiveTime()
			.memberReferencedComponentId();
			
		NON_LANGUAGE_MEMBER_FIELD_TO_LOAD = commonFieldsToLoad.build();
		LANGUAGE_MEMBER_FIELD_TO_LOAD = commonFieldsToLoad.memberAcceptabilityId().build();
	}
		
	private Iterator<String> itr;
	
	public SnomedSubsetMemberExporter(final SnomedExportConfiguration configuration, final String refSetId) {
		super(configuration, refSetId);
		mapper = new Id2Rf1PropertyMapper();
		languageType = isLanguageType(refSetId);
		distinctEffectiveTimeSet = new LongOpenHashSet();
		itr = Iterators.transform(createResultSet().iterator(), new Function<ReferencedComponentIdStatus, String>() {
			@Override public String apply(ReferencedComponentIdStatus input) {
				return new StringBuilder(getRefSetId())
				.append(HT)
				.append(input.referencedComponentId)
				.append(HT)
				.append(input.status)
				.append(HT)
				.toString();
			}
		});
	}

	@SuppressWarnings("unchecked")
	private Collection<ReferencedComponentIdStatus> createResultSet() {
		
		@SuppressWarnings("rawtypes")
		final IndexServerService indexService = (IndexServerService) ApplicationContext.getInstance().getService(SnomedIndexService.class);
		
		LongKeyLongMap descriptionIdTypeMap = new LongKeyLongOpenHashMap();
		
		//get referenced component's (description) ID to description type ID mapping 
		if (languageType) {
			final Query descriptions = SnomedMappings.newQuery().description().matchAll();
			final int expectedSize = indexService.getHitCount(getBranchPath(), descriptions, null);
			final DescriptionIdTypeCollector collector = new DescriptionIdTypeCollector(expectedSize);
			indexService.search(getBranchPath(), descriptions, collector);
			descriptionIdTypeMap = collector.getIdMap();
		} 
			
		final Query query = SnomedMappings.newQuery().memberRefSetId(getRefSetId()).matchAll();
		final int limit = indexService.getHitCount(getBranchPath(), query, null);
		
		if (limit > 0) {
		
			final TopDocs topDocs = indexService.search(getBranchPath(), query, limit);
			
			final ReferencedComponentIdStatus[] $ = new ReferencedComponentIdStatus[limit];
			
			ReferenceManager<IndexSearcher> manager = null;
			IndexSearcher searcher = null;
			
			try {

				manager = indexService.getManager(getBranchPath());
				searcher = manager.acquire();

				if (null != topDocs && !CompareUtils.isEmpty(topDocs.scoreDocs)) {

					for (int i = 0; i < topDocs.scoreDocs.length; i++) {

						final Document doc = searcher.doc(topDocs.scoreDocs[i].doc, languageType ? LANGUAGE_MEMBER_FIELD_TO_LOAD : NON_LANGUAGE_MEMBER_FIELD_TO_LOAD);
						final ReferencedComponentIdStatus idStatus = new ReferencedComponentIdStatus();
						idStatus.referencedComponentId = SnomedMappings.memberReferencedComponentId().getValueAsString(doc);
						
						if (!languageType) {
							
							if (1 == SnomedMappings.active().getValue(doc)) {
								idStatus.status = "1";
							} else {
								idStatus.status = "0";
							}
							
						} else {
							
							final String acceptabilityId = SnomedMappings.memberAcceptabilityId().getValueAsString(doc);
							
							if (Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED.equals(acceptabilityId)) {
								
								//if the referenced component was an FSN, it cannot be preferred
								if (Concepts.FULLY_SPECIFIED_NAME.equals(descriptionIdTypeMap.get(Long.parseLong(idStatus.referencedComponentId)))) {
									idStatus.status = "3"; //non preferred FSN code
								} else {
									idStatus.status = "1"; //preferred member is always 1
								} 
								
							} else {
								final String descriptionType = mapper.getDescriptionType(Long.toString(descriptionIdTypeMap.get(Long.parseLong(idStatus.referencedComponentId))));
								idStatus.status = null == descriptionType ? "0" : descriptionType;
							}
						}
						
						distinctEffectiveTimeSet.add(SnomedMappings.effectiveTime().getValue(doc));
						
						$[i] = idStatus;
						
					}

				}
				
				return Arrays.asList($);

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

		} else {
			
			return emptyList();
			
		}
		
	}
	
	private static final class ReferencedComponentIdStatus {
		private String status;
		private String referencedComponentId;
	}
	
	@Override
	public String[] getColumnHeaders() {
		return RF1_SUBSET_MEMBERS_HEADER;
	}
	
	@Override
	public String getFileName() {
		return new StringBuilder("der1_SubsetMembers_")
		.append(isLanguageType(getRefSetId()) ? getLanguageCode(getRefSetId()) : getFolderName())
		.append(configuration.getCountryAndNamespaceElement())
		.append(Dates.formatByHostTimeZone(new Date(), DateFormats.SHORT)).append(".txt").toString();
	}

	/**
	 * Returns the number of (distinct) effective time values as a version
	 * number found during the export.
	 */
	public int getVersion() {
		return distinctEffectiveTimeSet.size();
	}

	@Override
	public ComponentExportType getType() {
		return ComponentExportType.REF_SET;
	}

	@Override
	public boolean hasNext() {
		return itr.hasNext();
	}

	@Override
	public String next() {
		return itr.next();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<String> iterator() {
		return itr;
	}

	@Override
	public void close() throws Exception {
		
	}
}