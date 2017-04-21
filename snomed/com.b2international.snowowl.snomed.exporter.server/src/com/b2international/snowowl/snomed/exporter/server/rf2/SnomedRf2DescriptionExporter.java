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

import java.io.IOException;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongSet;
import com.b2international.commons.collect.LongSets;
import com.b2international.commons.collect.LongSets.LongFunction;
import com.b2international.index.Hits;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.exporter.server.ComponentExportType;
import com.b2international.snowowl.snomed.exporter.server.SnomedExportContext;
import com.b2international.snowowl.snomed.exporter.server.SnomedExportExecutor;
import com.b2international.snowowl.snomed.exporter.server.SnomedRfFileNameBuilder;

/**
 * RF2 export implementation for SNOMED&nbsp;CT description.
 *
 */
public class SnomedRf2DescriptionExporter extends AbstractSnomedRf2CoreExporter<SnomedDescriptionIndexEntry> {

	private String languageCode;
	private LongSet descriptionIds;

	public SnomedRf2DescriptionExporter(final SnomedExportContext exportContext, final RevisionSearcher revisionSearcher, final String languageCode) {
		super(exportContext, SnomedDescriptionIndexEntry.class, revisionSearcher);
		this.languageCode = languageCode;
		descriptionIds = PrimitiveSets.newLongOpenHashSet();
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
	public String getFileName() {
		return new StringBuilder("sct2")
			.append('_')
			.append(String.valueOf(getType()))
			.append('_')
			.append(String.valueOf(getExportContext().getContentSubType()))
			.append('-')
			.append(getLanguageCode())
			.append('_')
			.append(getExportContext().getNamespaceId())
			.append('_')
			.append(SnomedRfFileNameBuilder.getReleaseDate(getExportContext()))
			.append(".txt")
			.toString();
	}
	
	@Override
	protected void appendExpressionConstraint(ExpressionBuilder builder) {
		builder
			.mustNot(SnomedDescriptionIndexEntry.Expressions.type(Concepts.TEXT_DEFINITION))
			.filter(SnomedDescriptionIndexEntry.Expressions.languageCode(getLanguageCode()));
	}
	
	protected String getLanguageCode() {
		return languageCode;
	}
	
	@Override
	protected void collectHits(Hits<SnomedDescriptionIndexEntry> hits) {
		descriptionIds.addAll(LongSets.newLongSet(LongSets.transform(hits, new LongFunction<SnomedDescriptionIndexEntry>() {
			@Override
			public long apply(SnomedDescriptionIndexEntry input) {
				return Long.valueOf(input.getId());
			}
		})));
	}
	
	@Override
	public void execute() throws IOException {
		super.execute();
		new SnomedExportExecutor(new SnomedLanguageRefSetExporter(getExportContext(), getRevisionSearcher(), getLanguageCode(), descriptionIds)).execute();
	}
}
