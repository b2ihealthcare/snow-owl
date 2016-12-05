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

import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.exporter.server.ComponentExportType;
import com.b2international.snowowl.snomed.exporter.server.SnomedExportContext;

/**
 * RF2 export implementation for SNOMED&nbsp;CT description.
 *
 */
public class SnomedRf2DescriptionExporter extends AbstractSnomedRf2CoreExporter<SnomedDescriptionIndexEntry> {

	
	public SnomedRf2DescriptionExporter(final SnomedExportContext configuration, final RevisionSearcher revisionSearcher, final boolean unpublished) {
		super(configuration, SnomedDescriptionIndexEntry.class, revisionSearcher, unpublished);
		
	}
	
	@Override
	public String convertToString(final SnomedDescriptionIndexEntry doc) {
		final StringBuilder sb = new StringBuilder();
		sb.append(doc.getId());
		sb.append(HT);
		sb.append(formatEffectiveTime(doc.getEffectiveTime()));
		sb.append(HT);
		sb.append(formatStatus(doc.isActive()));
		sb.append(HT);
		sb.append(doc.getModuleId());
		sb.append(HT);
		sb.append(doc.getConceptId());
		sb.append(HT);
		sb.append(doc.getLanguageCode());
		sb.append(HT);
		sb.append(doc.getTypeId());
		sb.append(HT);
		sb.append(doc.getTerm());
		sb.append(HT);
		sb.append(doc.getCaseSignificanceId());
		return sb.toString();
	}
	
	@Override
	public ComponentExportType getType() {
		return ComponentExportType.DESCRIPTION;
	}

	@Override
	public String[] getColumnHeaders() {
		return SnomedRf2Headers.DESCRIPTION_HEADER;
	}
	
	@Override
	protected void appendExpressionConstraint(ExpressionBuilder builder) {
		builder.mustNot(SnomedDescriptionIndexEntry.Expressions.type(Concepts.TEXT_DEFINITION));
	}
	
}
