/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.exporter.server.rf1;

import static com.b2international.snowowl.snomed.exporter.server.rf1.SnomedRf1ReleaseFileHeaders.RF1_CROSS_MAP_HEADER;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;

import com.b2international.commons.StringUtils;
import com.b2international.index.Hits;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.snomed.datastore.SnomedMapSetSetting;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.exporter.server.ComponentExportType;
import com.b2international.snowowl.snomed.exporter.server.SnomedExportContext;
import com.google.common.collect.Sets;

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

	private static final String FILE_NAME_PREFIX = "CrossMaps";
	
	/**
	 * 
	 * @param configuration
	 * @param refSetId
	 * @param mapSetSetting
	 */
	public SnomedCrossMapExporter(
			final SnomedExportContext configuration, 
			final String refSetId, 
			final SnomedMapSetSetting mapSetSetting, 
			final RevisionSearcher revisionSearcher) {
		super(configuration, refSetId, mapSetSetting, revisionSearcher);
	}

	@Override
	protected String getFileNameprefix() {
		return FILE_NAME_PREFIX;
	}
	
	private String toLine(MapTargetEntry input) {
		return new StringBuilder(getRefSetId()).append(HT)
				.append(input.mapSource).append(HT)
				.append(isComplex() ? StringUtils.valueOfOrEmptyString(input.group) : "0").append(HT)
				.append(isComplex() ? StringUtils.valueOfOrEmptyString(input.priority) : "0").append(HT)
				.append(StringUtils.valueOfOrEmptyString(input.mapTarget))
				.append(isComplex() ? StringUtils.valueOfOrEmptyString(input.rule) : "").append(HT)
				.append(isComplex() ? StringUtils.valueOfOrEmptyString(input.advice) : "").append(HT)
				.toString();
	}
	
	private Collection<MapTargetEntry> fetchResultSet() throws IOException {
		//we need every target, limit needs to be set as the default is 50 hits
		Query<SnomedRefSetMemberIndexEntry> query = Query.select(SnomedRefSetMemberIndexEntry.class).where(Expressions.matchAll()).limit(Integer.MAX_VALUE).build();
		Hits<SnomedRefSetMemberIndexEntry> hits = revisionSearcher.search(query);

		Set<MapTargetEntry> mapTargetEntries = Sets.newHashSet();
		
		for (SnomedRefSetMemberIndexEntry snomedRefSetMemberIndexEntry : hits) {
			
			final MapTargetEntry mapTargetEntry = new MapTargetEntry();
			mapTargetEntry.mapSource = snomedRefSetMemberIndexEntry.getReferencedComponentId();
			mapTargetEntry.mapTarget = snomedRefSetMemberIndexEntry.getMapTarget();
			
			if (isComplex()) {
				mapTargetEntry.rule = snomedRefSetMemberIndexEntry.getMapRule();
				mapTargetEntry.advice = snomedRefSetMemberIndexEntry.getMapAdvice();
				
				//nulls are converted to empty strings when writing it out
				mapTargetEntry.group = String.valueOf(snomedRefSetMemberIndexEntry.getMapGroup());
				mapTargetEntry.priority = String.valueOf(snomedRefSetMemberIndexEntry.getMapPriority()); 
			}
			mapTargetEntries.add(mapTargetEntry);
		}
		return mapTargetEntries;
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
	public void writeLines(Consumer<String> lineProcessor) throws IOException {
		fetchResultSet().stream().map(this::toLine).forEach(lineProcessor);
	}

}