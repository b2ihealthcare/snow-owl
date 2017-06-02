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
package com.b2international.snowowl.snomed.datastore.request;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.queries.BooleanFilter;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;

import com.b2international.commons.functions.StringToLongFunction;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.datastore.converter.SnomedConverters;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedQueryBuilder;
import com.google.common.collect.ImmutableList;

/**
 * @since 4.5
 */
final class SnomedRelationshipSearchRequest extends SnomedSearchRequest<SnomedRelationships> {

	enum OptionKey {
		SOURCE,
		TYPE,
		DESTINATION,
		CHARACTERISTIC_TYPE;
	}
	
	SnomedRelationshipSearchRequest() {}

	@Override
	protected SnomedRelationships doExecute(BranchContext context) throws IOException {
		final IndexSearcher searcher = context.service(IndexSearcher.class);
		
		final SnomedQueryBuilder queryBuilder = SnomedMappings.newQuery().relationship();
		addActiveClause(queryBuilder);
		addModuleClause(queryBuilder);
		
		if (containsKey(OptionKey.TYPE)) {
			queryBuilder.relationshipType(getString(OptionKey.TYPE));
		}
		
		final BooleanFilter filter = new BooleanFilter();
		
		if (containsKey(OptionKey.CHARACTERISTIC_TYPE)) {
			final Collection<String> charTypes = getCollection(OptionKey.CHARACTERISTIC_TYPE, String.class);
			addFilterClause(filter, SnomedMappings.relationshipCharacteristicType().createTermsFilter(StringToLongFunction.copyOf(charTypes)), Occur.MUST);
		}
		
		addComponentIdFilter(filter);

		if (containsKey(OptionKey.SOURCE)) {
			addFilterClause(filter, createSourceIdFilter(), Occur.MUST);
		}
		
		if (containsKey(OptionKey.DESTINATION)) {
			addFilterClause(filter, createDestinationIdFilter(), Occur.MUST);
		}

		final Query query = createConstantScoreQuery(createFilteredQuery(queryBuilder.matchAll(), filter));
		final int totalHits = getTotalHits(searcher, query);
		
		if (limit() < 1 || totalHits < 1) {
			return new SnomedRelationships(offset(), limit(), totalHits);
		}
		
		final TopDocs topDocs = searcher.search(query, null, numDocsToRetrieve(searcher, totalHits), Sort.INDEXORDER, false, false);
		if (topDocs.scoreDocs.length < 1) {
			return new SnomedRelationships(offset(), limit(), topDocs.totalHits);
		}
		
		final ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		final ImmutableList.Builder<SnomedRelationshipIndexEntry> relationshipsBuilder = ImmutableList.builder();
		
		for (int i = offset(); i < scoreDocs.length; i++) {
			Document doc = searcher.doc(scoreDocs[i].doc); // TODO: should expand & filter drive fieldsToLoad? Pass custom fieldValueLoader?
			SnomedRelationshipIndexEntry indexEntry = SnomedRelationshipIndexEntry.builder(doc).build();
			relationshipsBuilder.add(indexEntry);
		}

		return SnomedConverters.newRelationshipConverter(context, expand(), locales()).convert(relationshipsBuilder.build(), offset(), limit(), topDocs.totalHits);
	}

	private Filter createSourceIdFilter() {
		final List<Long> sourceIds = StringToLongFunction.copyOf(getCollection(OptionKey.SOURCE, String.class));
		return SnomedMappings.relationshipSource().createTermsFilter(sourceIds);
	}
	
	private Filter createDestinationIdFilter() {
		final List<Long> sourceIds = StringToLongFunction.copyOf(getCollection(OptionKey.DESTINATION, String.class));
		return SnomedMappings.relationshipDestination().createTermsFilter(sourceIds);
	}

	@Override
	protected Class<SnomedRelationships> getReturnType() {
		return SnomedRelationships.class;
	}
}
