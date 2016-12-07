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
package com.b2international.snowowl.snomed.exporter.server.rf2;

import static com.google.common.base.Strings.nullToEmpty;

import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.exporter.server.SnomedExportContext;
import com.b2international.snowowl.snomed.exporter.server.SnomedRfFileNameBuilder;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;

/**
 * SNOMED CT simple map type reference set exporter (optionally with map target description).
 */
public class SnomedSimpleMapRefSetExporter extends SnomedRefSetExporter {

	private final boolean includeMapTargetDescription;
	
	public SnomedSimpleMapRefSetExporter(final SnomedExportContext exportContext, SnomedReferenceSet refset, final boolean includeMapTargetDescription, 
			final RevisionSearcher revisionSearcher) {
		super(exportContext, refset, revisionSearcher);
		this.includeMapTargetDescription = includeMapTargetDescription;
	}
	
	@Override
	protected String buildRefSetFileName(final String refSetName, final SnomedRefSet refSet) {
		return SnomedRfFileNameBuilder.buildRefSetFileName(getExportContext(), refSetName, refSet, includeMapTargetDescription);
	}

	@Override
	public String convertToString(SnomedRefSetMemberIndexEntry doc) {
		final StringBuilder sb = new StringBuilder();
		sb.append(super.convertToString(doc));
		sb.append(HT);
		sb.append(doc.getMapTarget());
		if (includeMapTargetDescription) {
			sb.append(HT);
			sb.append(nullToEmpty(doc.getMapTargetDescription()));
		}
		return sb.toString();
	}
	
	@Override
	public String[] getColumnHeaders() {
		return includeMapTargetDescription 
			? SnomedRf2Headers.SIMPLE_MAP_TYPE_HEADER_WITH_DESCRIPTION
			: SnomedRf2Headers.SIMPLE_MAP_TYPE_HEADER;
	}
}
