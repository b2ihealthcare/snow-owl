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

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.List;

import com.b2international.index.Hits;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.exporter.server.AbstractFilteredSnomedCoreExporter;
import com.b2international.snowowl.snomed.exporter.server.ComponentExportType;
import com.b2international.snowowl.snomed.exporter.server.SnomedExportContext;
import com.b2international.snowowl.snomed.exporter.server.SnomedRfFileNameBuilder;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * Exporter for language type reference sets.
 */
public class SnomedLanguageRefSetExporter extends AbstractFilteredSnomedCoreExporter<SnomedRefSetMemberIndexEntry> {

	private String languageCode;

	public SnomedLanguageRefSetExporter(SnomedExportContext exportContext, RevisionSearcher revisionSearcher, String languageCode) {
		super(exportContext, SnomedRefSetMemberIndexEntry.class, revisionSearcher);
		this.languageCode = checkNotNull(languageCode, "languageCode");
	}

	@Override
	protected void appendExpressionConstraint(ExpressionBuilder builder) {
		builder.filter(SnomedRefSetMemberIndexEntry.Expressions.refSetTypes(singleton(SnomedRefSetType.LANGUAGE)));
	}

	@Override
	public String convertToString(SnomedRefSetMemberIndexEntry doc) {
		final StringBuilder sb = new StringBuilder();
		sb.append(doc.getId());
		sb.append(HT);
		sb.append(formatEffectiveTime(doc.getEffectiveTime()));
		sb.append(HT);
		sb.append(formatStatus(doc.isActive()));
		sb.append(HT);
		sb.append(doc.getModuleId());
		sb.append(HT);
		sb.append(doc.getReferenceSetId());
		sb.append(HT);
		sb.append(doc.getReferencedComponentId());
		sb.append(HT);
		sb.append(doc.getAcceptabilityId());
		return sb.toString();
	}
	
	@Override
	public ComponentExportType getType() {
		return ComponentExportType.REF_SET;
	}
	
	@Override
	public String[] getColumnHeaders() {
		return SnomedRf2Headers.LANGUAGE_TYPE_HEADER;
	}
	
	@Override
	public String getRelativeDirectory() {
		return RF2_LANGUAGE_REFERENCE_SET_RELATIVE_DIR;
	}

	@Override
	public String getFileName() {
		return new StringBuilder("der2")
				.append('_')
				.append(SnomedRfFileNameBuilder.getPrefix(SnomedRefSetType.LANGUAGE, false))
				.append("Refset")
				.append('_')
				.append("Language")
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
	
	protected String getLanguageCode() {
		return languageCode;
	}

	@Override
	protected Hits<SnomedRefSetMemberIndexEntry> filter(Hits<SnomedRefSetMemberIndexEntry> allResults) throws IOException {

		Multimap<String, SnomedRefSetMemberIndexEntry> referencedComponentToMemberMap = ArrayListMultimap.create();
		allResults.getHits().forEach(m -> referencedComponentToMemberMap.put(m.getReferencedComponentId(), m));
		
		Query<String> query = Query.selectPartial(String.class, SnomedDescriptionIndexEntry.class, singleton(SnomedDescriptionIndexEntry.Fields.ID))
			.where(Expressions.builder()
				.filter(SnomedDescriptionIndexEntry.Expressions.ids(referencedComponentToMemberMap.keySet()))
				.filter(SnomedDescriptionIndexEntry.Expressions.languageCode(languageCode))
				.build())
			.limit(referencedComponentToMemberMap.keySet().size())
			.build();
				
		List<String> descriptionIdsWithLanguageCode = getRevisionSearcher().search(query).getHits();
		
		List<SnomedRefSetMemberIndexEntry> filteredMembers = descriptionIdsWithLanguageCode.stream()
				.flatMap(id -> referencedComponentToMemberMap.get(id).stream())
				.collect(toList());

		return new Hits<SnomedRefSetMemberIndexEntry>(filteredMembers, allResults.getOffset(), allResults.getLimit(), allResults.getTotal());
	}

}
