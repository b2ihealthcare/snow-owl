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

/**
 * SNOMED CT concrete domain reference set exporter.
 */
public class SnomedConcreteDomainRefSetExporter extends SnomedRefSetExporter {

	public SnomedConcreteDomainRefSetExporter(final SnomedExportContext exportContext, SnomedReferenceSet refset, final RevisionSearcher revisionSearcher) {
		super(exportContext, refset, revisionSearcher);
	}
	
	@Override
	public String convertToString(SnomedRefSetMemberIndexEntry doc) {
		
		final StringBuilder sb = new StringBuilder();
		sb.append(super.convertToString(doc));
		sb.append(HT);
		sb.append(doc.getUnitId());
		sb.append(HT);
		sb.append(doc.getOperatorId());
		sb.append(HT);
		sb.append(nullToEmpty(doc.getAttributeName()));
		sb.append(HT);
		sb.append(doc.getValue()); //the direct value
		sb.append(HT);
		sb.append(nullToEmpty(doc.getCharacteristicTypeId()));
		return sb.toString();
	}
	
	@Override
	public String[] getColumnHeaders() {
		return SnomedRf2Headers.CONCRETE_DATA_TYPE_HEADER_WITH_LABEL;
	}
}
