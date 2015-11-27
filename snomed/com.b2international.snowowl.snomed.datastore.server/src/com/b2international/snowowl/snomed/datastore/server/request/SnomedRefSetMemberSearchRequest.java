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
package com.b2international.snowowl.snomed.datastore.server.request;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.queries.BooleanFilter;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;

import com.b2international.commons.functions.StringToLongFunction;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedQueryBuilder;
import com.b2international.snowowl.snomed.datastore.server.converter.SnomedConverters;
import com.google.common.collect.ImmutableList;

/**
 * @since 4.5
 */
final class SnomedRefSetMemberSearchRequest extends SnomedSearchRequest<SnomedReferenceSetMembers> {

	enum OptionKey {
		/**
		 * Filter by containing reference set
		 */
		REFSET,

		/**
		 * Filter by referenced component
		 */
		REFERENCED_COMPONENT
	}
	
	SnomedRefSetMemberSearchRequest() {}
	
	@Override
	protected SnomedReferenceSetMembers doExecute(BranchContext context) throws IOException {
		final IndexSearcher searcher = context.service(IndexSearcher.class);

		final BooleanFilter filter = new BooleanFilter();
		final Collection<String> referenceSetIds = getCollection(OptionKey.REFSET, String.class);
		final Collection<String> referencedComponentIds = getCollection(OptionKey.REFERENCED_COMPONENT, String.class);
		
		if (!referenceSetIds.isEmpty()) {
			addFilterClause(filter, SnomedMappings.memberRefSetId().createTermsFilter(StringToLongFunction.copyOf(referenceSetIds)), Occur.MUST);
		}
		
		if (!referencedComponentIds.isEmpty()) {
			addFilterClause(filter, SnomedMappings.memberReferencedComponentId().createTermsFilter(StringToLongFunction.copyOf(referencedComponentIds)), Occur.MUST);
		}
		
		SnomedQueryBuilder queryBuilder = SnomedMappings.newQuery().and(SnomedMappings.memberReferencedComponentType().toExistsQuery());
		addActiveClause(queryBuilder);
		addModuleClause(queryBuilder);
		
		final Query query = createConstantScoreQuery(createFilteredQuery(queryBuilder.matchAll(), filter));
		final int totalHits = getTotalHits(searcher, query);

		if (limit() < 1 || totalHits < 1) {
			return new SnomedReferenceSetMembers(offset(), limit(), totalHits);
		}
		
		final TopDocs topDocs = searcher.search(query, null, numDocsToRetrieve(searcher, totalHits), Sort.INDEXORDER, false, false);
		if (topDocs.scoreDocs.length < 1) {
			return new SnomedReferenceSetMembers(offset(), limit(), topDocs.totalHits);
		}

		final ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		final ImmutableList.Builder<SnomedRefSetMemberIndexEntry> memberBuilder = ImmutableList.builder();
		
		for (int i = offset(); i < scoreDocs.length; i++) {
			Document doc = searcher.doc(scoreDocs[i].doc); // TODO: should expand & filter drive fieldsToLoad? Pass custom fieldValueLoader?
			SnomedRefSetMemberIndexEntry indexEntry = SnomedRefSetMemberIndexEntry.builder(doc).build();
			memberBuilder.add(indexEntry);
		}

		List<SnomedRefSetMemberIndexEntry> members = memberBuilder.build();
		return SnomedConverters.newMemberConverter(context, expand(), locales()).convert(members, offset(), limit(), topDocs.totalHits);
	}

	@Override
	protected Class<SnomedReferenceSetMembers> getReturnType() {
		return SnomedReferenceSetMembers.class;
	}
}
