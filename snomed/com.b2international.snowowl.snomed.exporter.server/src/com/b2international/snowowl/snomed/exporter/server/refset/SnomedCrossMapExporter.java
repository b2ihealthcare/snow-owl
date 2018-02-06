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

import static com.b2international.snowowl.snomed.exporter.server.SnomedReleaseFileHeaders.RF1_CROSS_MAP_HEADER;
import static java.util.Collections.emptyList;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ReferenceManager;
import org.apache.lucene.search.TopDocs;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.datastore.server.index.IndexServerService;
import com.b2international.snowowl.snomed.datastore.SnomedMapSetSetting;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.exporter.server.ComponentExportType;
import com.b2international.snowowl.snomed.exporter.server.SnomedRf1Exporter;
import com.b2international.snowowl.snomed.exporter.server.sandbox.SnomedExportConfiguration;
import com.google.common.base.Function;
import com.google.common.collect.Iterators;

/**
 * SNOMED&nbsp;CT cross map exporter for complex map and simple map type reference sets.
 * <p>
 * <b>RF1</b> CSV header:
 * <ul><li>MAPSETID&#9;MAPCONCEPTID&#9;MAPOPTION&#9;MAPPRIORITY&#9;MAPTARGETID&#9;MAPRULE&#9;MAPADVICE</li></ul>
 * </p>
 * @see SnomedRf1Exporter
 * @see AbstractSnomedCrossMapExporter
 */
public class SnomedCrossMapExporter extends AbstractSnomedCrossMapExporter {

	private static final Set<String> MEMBER_FIELD_TO_LOAD = SnomedMappings.fieldsToLoad()
			.memberReferencedComponentId()
			.memberMapTargetComponentId()
			.build();
	
	private static final Set<String> COMPLEX_MEMBER_FIELD_TO_LOAD = SnomedMappings.fieldsToLoad()
			.memberReferencedComponentId()
			.memberMapTargetComponentId()
			.memberMapPriority()
			.memberMapGroup()
			.memberMapRule()
			.memberMapAdvice()
			.build();
	
	private boolean complex;

	private Iterator<String> itr;
	
	public SnomedCrossMapExporter(final SnomedExportConfiguration configuration, final String refSetId, final SnomedMapSetSetting mapSetSetting) {
		super(configuration, refSetId, mapSetSetting);
		complex = getMapSetSetting().isComplex();
		itr = Iterators.transform(createResultSet().iterator(), new Function<MapTargetEntry, String>() {
			@Override
			public String apply(final MapTargetEntry input) {
				return new StringBuilder(refSetId).append(HT)
					.append(input.mapSource).append(HT)
					.append(complex ? StringUtils.valueOfOrEmptyString(input.group) : "0").append(HT)
					.append(complex ? StringUtils.valueOfOrEmptyString(input.priority) : "0").append(HT)
					.append(StringUtils.valueOfOrEmptyString(input.mapTarget))
					.append(complex ? StringUtils.valueOfOrEmptyString(input.rule) : "").append(HT)
					.append(complex ? StringUtils.valueOfOrEmptyString(input.advice) : "").append(HT)
					.toString();
			}
		});
	}

	private static final String FILE_NAME_PREFIX = "CrossMaps";

	@Override
	protected String getFileNameprefix() {
		return FILE_NAME_PREFIX;
	}

	@SuppressWarnings("unchecked")
	private Collection<MapTargetEntry> createResultSet() {
		
		@SuppressWarnings("rawtypes")
		final IndexServerService indexService = (IndexServerService) ApplicationContext.getInstance().getService(SnomedIndexService.class);
		
		final Query query = SnomedMappings.newQuery().memberRefSetId(getRefSetId()).matchAll();
		final int limit = indexService.getHitCount(getBranchPath(), query, null);
		
		if (limit > 0) {
		
			final TopDocs topDocs = indexService.search(getBranchPath(), query, limit);
			
			final MapTargetEntry[] $ = new MapTargetEntry[limit];
			
			ReferenceManager<IndexSearcher> manager = null;
			IndexSearcher searcher = null;
			
			try {

				manager = indexService.getManager(getBranchPath());
				searcher = manager.acquire();

				if (null != topDocs && !CompareUtils.isEmpty(topDocs.scoreDocs)) {

					for (int i = 0; i < topDocs.scoreDocs.length; i++) {

						final Document doc = searcher.doc(topDocs.scoreDocs[i].doc, complex ? COMPLEX_MEMBER_FIELD_TO_LOAD : MEMBER_FIELD_TO_LOAD);
						final MapTargetEntry entry = new MapTargetEntry();
						entry.mapSource = SnomedMappings.memberReferencedComponentId().getValueAsString(doc);
						entry.mapTarget = SnomedMappings.memberMapTargetComponentId().getValue(doc);
						
						if (complex) {
							
							entry.rule = SnomedMappings.memberMapRule().getOptionalValue(doc);
							entry.advice = SnomedMappings.memberMapAdvice().getOptionalValue(doc);
							entry.group = SnomedMappings.memberMapGroup().getValueAsString(doc);
							entry.priority = SnomedMappings.memberMapPriority().getValueAsString(doc); 
							
						}
							
						
						$[i] = entry;
						
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
			//empty reference set
			return emptyList();
		}
		
	}
	
	@Override
	public String[] getColumnHeaders() {
		return RF1_CROSS_MAP_HEADER;
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

}