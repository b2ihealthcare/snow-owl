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
package com.b2international.snowowl.snomed.exporter.server.rf2;

import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.exporter.server.SnomedExportContext;
import com.b2international.snowowl.snomed.exporter.server.SnomedRfFileNameBuilder;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;

/**
 * @since 6.1.0
 */
public class SnomedMRCMAttributeDomainRefSetExporter extends SnomedRefSetExporter {

	public SnomedMRCMAttributeDomainRefSetExporter(final SnomedExportContext exportContext, final SnomedReferenceSet refset, final RevisionSearcher revisionSearcher) {
		super(exportContext, refset, revisionSearcher);
	}

	@Override
	public String convertToString(final SnomedRefSetMemberIndexEntry doc) {
		final StringBuilder sb = new StringBuilder();
		sb.append(super.convertToString(doc));
		sb.append(HT);
		sb.append(doc.getDomainId());
		sb.append(HT);
		sb.append(formatStatus(doc.isGrouped()));
		sb.append(HT);
		sb.append(doc.getAttributeCardinality());
		sb.append(HT);
		sb.append(doc.getAttributeInGroupCardinality());
		sb.append(HT);
		sb.append(doc.getRuleStrengthId());
		sb.append(HT);
		sb.append(doc.getContentTypeId());
		return sb.toString();
	}

	@Override
	public String[] getColumnHeaders() {
		return SnomedRf2Headers.MRCM_ATTRIBUTE_DOMAIN_HEADER;
	}
	
	@Override
	public String getFileName() {
		return new StringBuilder("der2_")
			.append(SnomedRfFileNameBuilder.getPrefix(SnomedRefSetType.MRCM_ATTRIBUTE_DOMAIN, false))
			.append("Refset_")
			.append("MRCMAttributeDomain")
			.append(String.valueOf(getExportContext().getContentSubType()))
			.append('_')
			.append(getExportContext().getNamespaceId())
			.append('_')
			.append(SnomedRfFileNameBuilder.getReleaseDate(getExportContext()))
			.append(".txt")
			.toString();
	}

}
