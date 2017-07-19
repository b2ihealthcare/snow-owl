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
import java.util.Collection;
import java.util.Collections;

import com.b2international.collections.longs.LongSet;
import com.b2international.commons.collect.LongSets;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.exporter.server.SnomedExportContext;
import com.google.common.collect.ImmutableSet;

/**
 * Exporter for language type reference sets, fetching only those language members which belong to the specified langugage code. 
 */
public class LanguageCodeAwareSnomedLanguageRefSetExporter extends SnomedLanguageRefSetExporter {

	public LanguageCodeAwareSnomedLanguageRefSetExporter(SnomedExportContext exportContext, RevisionSearcher revisionSearcher, String languageCode, LongSet descriptionIds) {
		super(exportContext, revisionSearcher, languageCode, descriptionIds);
	}

	protected ExpressionBuilder appendReferencedComponentIdClause(ExpressionBuilder builder, LongSet descriptionIds) {
		// exclude language members that are already exported previously (members belonging to descriptionIds) 
		builder.mustNot(SnomedRefSetMemberIndexEntry.Expressions.referencedComponentIds(LongSets.toStringSet(descriptionIds)));
		
		// only allow language refset members that are belonging to the current languageCode
		builder.filter(SnomedRefSetMemberIndexEntry.Expressions.referencedComponentIds(getDescriptionsWithLanguageCode(getLanguageCode())));

		return builder; 
	}

	private Collection<String> getDescriptionsWithLanguageCode(String languageCode) {
		try {
			return getRevisionSearcher().search(
					Query.selectPartial(String.class, SnomedDescriptionIndexEntry.class, ImmutableSet.of(SnomedDescriptionIndexEntry.Fields.ID))
					.where(SnomedDescriptionIndexEntry.Expressions.languageCode(languageCode))
					.limit(Integer.MAX_VALUE)
					.build()
				).getHits();
		} catch (IOException e) {
			return Collections.emptyList();
		}
	}

}
