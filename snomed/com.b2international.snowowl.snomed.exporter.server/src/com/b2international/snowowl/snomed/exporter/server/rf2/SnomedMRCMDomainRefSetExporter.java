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

import static com.google.common.base.Strings.nullToEmpty;

import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.exporter.server.SnomedExportContext;
import com.b2international.snowowl.snomed.exporter.server.SnomedRfFileNameBuilder;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;

/**
 * @since 5.10.19
 */
public class SnomedMRCMDomainRefSetExporter extends SnomedRefSetExporter {

	public SnomedMRCMDomainRefSetExporter(final SnomedExportContext exportContext, final SnomedReferenceSet refset, final RevisionSearcher revisionSearcher) {
		super(exportContext, refset, revisionSearcher);
	}

	@Override
	public String convertToString(final SnomedRefSetMemberIndexEntry doc) {
		final StringBuilder sb = new StringBuilder();
		sb.append(super.convertToString(doc));
		sb.append(HT);
		sb.append(doc.getDomainConstraint());
		sb.append(HT);
		sb.append(nullToEmpty(doc.getParentDomain()));
		sb.append(HT);
		sb.append(doc.getProximalPrimitiveConstraint());
		sb.append(HT);
		sb.append(nullToEmpty(doc.getProximalPrimitiveRefinement()));
		sb.append(HT);
		sb.append(doc.getDomainTemplateForPrecoordination());
		sb.append(HT);
		sb.append(doc.getDomainTemplateForPostcoordination());
		sb.append(HT);
		sb.append(nullToEmpty(doc.getEditorialGuideReference()));
		return sb.toString();
	}

	@Override
	public String[] getColumnHeaders() {
		return SnomedRf2Headers.MRCM_DOMAIN_HEADER;
	}
	
	@Override
	public String getFileName() {
		return new StringBuilder("der2_")
			.append(SnomedRfFileNameBuilder.getPrefix(SnomedRefSetType.MRCM_DOMAIN, false))
			.append("Refset_")
			.append("MRCMDomain")
			.append(String.valueOf(getExportContext().getContentSubType()))
			.append('_')
			.append(getExportContext().getNamespaceId())
			.append('_')
			.append(SnomedRfFileNameBuilder.getReleaseDate(getExportContext()))
			.append(".txt")
			.toString();
	}

}
