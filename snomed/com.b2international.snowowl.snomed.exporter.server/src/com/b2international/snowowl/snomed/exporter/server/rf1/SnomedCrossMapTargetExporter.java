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
package com.b2international.snowowl.snomed.exporter.server.rf1;

import static com.b2international.snowowl.snomed.exporter.server.rf1.SnomedRf1ReleaseFileHeaders.RF1_CROSS_MAP_TARGETS_HEADER;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import com.b2international.commons.StringUtils;
import com.b2international.index.Hits;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.snomed.datastore.SnomedMapSetSetting;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.exporter.server.ComponentExportType;
import com.b2international.snowowl.snomed.exporter.server.SnomedExportContext;
import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;

/**
 * SNOMED&nbsp;CT cross map target exporter for complex map and simple map type
 * reference sets.
 * <p>
 * <b>RF1</b> CSV header:
 * <ul>
 * <li>TARGETID&#9;TARGETSCHEMEID&#9;TARGETCODES&#9;TARGETRULE&#9;TARGETADVICE
 * </li>
 * </ul>
 * </p>
 * 
 * @see SnomedRf1Exporter
 * @see AbstractSnomedCrossMapExporter
 */
public class SnomedCrossMapTargetExporter extends AbstractSnomedCrossMapExporter {

	private static final String FILE_NAME_PREFIX = "CrossMapTargets";

	private boolean complex;
	private Iterator<String> itr;

	public SnomedCrossMapTargetExporter(final SnomedExportContext configuration, final String refSetId, 
			final SnomedMapSetSetting mapSetSetting, final RevisionSearcher revisionSearcher) {
		super(configuration, refSetId, mapSetSetting, revisionSearcher);
		complex = getMapSetSetting().isComplex();
		itr = Iterators.transform(createResultSet().iterator(), new Function<MapTargetEntry, String>() {
			public String apply(final MapTargetEntry input) {
				return new StringBuilder(input.uuid).append(HT).append(getMapSetSetting().getMapSchemeId()).append(HT)
						.append(StringUtils.valueOfOrEmptyString(input.mapTarget)).append(complex ? StringUtils.valueOfOrEmptyString(input.rule) : "")
						.append(HT).append(complex ? StringUtils.valueOfOrEmptyString(input.advice) : "").append(HT).toString();
			}
		});
	}

	@Override
	protected String getFileNameprefix() {
		return FILE_NAME_PREFIX;
	}

	private Collection<MapTargetEntry> createResultSet() {

		try {
			// we need every target, limit needs to be set as the default is 50 hits
			Query<SnomedRefSetMemberIndexEntry> query = Query.select(SnomedRefSetMemberIndexEntry.class).where(Expressions.matchAll()).limit(Integer.MAX_VALUE).build();
			Hits<SnomedRefSetMemberIndexEntry> hits;
			hits = revisionSearcher.search(query);

			Set<MapTargetEntry> mapTargetEntries = Sets.newHashSet();

			for (SnomedRefSetMemberIndexEntry snomedRefSetMemberIndexEntry : hits) {

				final MapTargetEntry mapTargetEntry = new MapTargetEntry();
				mapTargetEntry.uuid = snomedRefSetMemberIndexEntry.getId();
				mapTargetEntry.mapTarget = snomedRefSetMemberIndexEntry.getMapTarget();

				if (complex) {
					mapTargetEntry.rule = snomedRefSetMemberIndexEntry.getMapRule();
					mapTargetEntry.advice = snomedRefSetMemberIndexEntry.getMapAdvice();
				}
				mapTargetEntries.add(mapTargetEntry);
			}
			return mapTargetEntries;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String[] getColumnHeaders() {
		return RF1_CROSS_MAP_TARGETS_HEADER;
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