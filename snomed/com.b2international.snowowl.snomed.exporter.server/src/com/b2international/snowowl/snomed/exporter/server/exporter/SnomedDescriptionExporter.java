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
package com.b2international.snowowl.snomed.exporter.server.exporter;

import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;
import com.b2international.snowowl.snomed.exporter.server.ComponentExportType;
import com.b2international.snowowl.snomed.exporter.server.SnomedExportContext;

/**
 * RF2 export implementation for SNOMED&nbsp;CT description.
 *
 */
public class SnomedDescriptionExporter extends SnomedCoreExporter<SnomedDescriptionIndexEntry> {

	public SnomedDescriptionExporter(final SnomedExportContext configuration, final RevisionSearcher revisionSearcher, final boolean unpublished) {
		super(configuration, SnomedDescriptionIndexEntry.class, revisionSearcher, unpublished);
	}

	@Override
	public String transform(final SnomedDescriptionIndexEntry doc) {
		final StringBuilder sb = new StringBuilder();
		sb.append(doc.getId());
		sb.append(HT);
		sb.append(doc.getEffectiveTimeAsString());
		sb.append(HT);
		sb.append(doc.isActive() ? "1" : "0");
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
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.exporter.server.sandbox.SnomedCoreExporter#getQueryExpression()
	 */
	@Override
	protected Expression getQueryExpression() {
		if (isUnpublished()) {
			Expression unpublishedExpression = Expressions.builder()
					.must(super.getQueryExpression())
					.must(SnomedDocument.Expressions.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME)).build();
			return unpublishedExpression;
		} else {
			return super.getQueryExpression();
		}
	}
	
	@Override
	public ComponentExportType getType() {
		return ComponentExportType.DESCRIPTION;
	}

	@Override
	public String[] getColumnHeaders() {
		return SnomedRf2Headers.DESCRIPTION_HEADER;
	}
	
}
