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

import org.apache.lucene.document.Document;
import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.FilteredQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TotalHitCountCollector;

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedQueryBuilder;
import com.b2international.snowowl.snomed.datastore.server.converter.SnomedConverters;
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
		
		if (containsKey(SnomedSearchRequest.OptionKey.ACTIVE)) {
			queryBuilder.active(getBoolean(SnomedSearchRequest.OptionKey.ACTIVE));
		}
		
		if (containsKey(SnomedSearchRequest.OptionKey.MODULE)) {
			queryBuilder.module(getString(SnomedSearchRequest.OptionKey.MODULE));
		}
		
		if (containsKey(OptionKey.SOURCE)) {
			queryBuilder.relationshipSource(getString(OptionKey.SOURCE));
		}
		
		if (containsKey(OptionKey.TYPE)) {
			queryBuilder.relationshipType(getString(OptionKey.TYPE));
		}
		
		if (containsKey(OptionKey.DESTINATION)) {
			queryBuilder.relationshipDestination(getString(OptionKey.DESTINATION));
		}
		
		if (containsKey(OptionKey.CHARACTERISTIC_TYPE)) {
			queryBuilder.relationshipCharacteristicType(getString(OptionKey.CHARACTERISTIC_TYPE));
		}
		
		final Query query;
		
		if (!componentIds().isEmpty()) {
			query = new ConstantScoreQuery(new FilteredQuery(queryBuilder.matchAll(), createComponentIdFilter()));
		} else {
			query = new ConstantScoreQuery(queryBuilder.matchAll());
		}
		
		if (limit() == 0) {
			final TotalHitCountCollector totalCollector = new TotalHitCountCollector();
			searcher.search(query, totalCollector); 
			return new SnomedRelationships(offset(), limit(), totalCollector.getTotalHits());
		}
		
		final TopDocs topDocs = searcher.search(query, null, offset() + limit(), Sort.INDEXORDER, false, false);
		if (IndexUtils.isEmpty(topDocs)) {
			return new SnomedRelationships(offset(), limit(), topDocs.totalHits);
		}
		
		final ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		final ImmutableList.Builder<SnomedRelationshipIndexEntry> relationshipsBuilder = ImmutableList.builder();
		
		for (int i = offset(); i < scoreDocs.length && i < offset() + limit(); i++) {
			Document doc = searcher.doc(scoreDocs[i].doc); // TODO: should expand & filter drive fieldsToLoad? Pass custom fieldValueLoader?
			SnomedRelationshipIndexEntry indexEntry = SnomedRelationshipIndexEntry.builder(doc).score(scoreDocs[i].score).build();
			relationshipsBuilder.add(indexEntry);
		}

		return SnomedConverters.newRelationshipConverter(context, expand(), locales()).convert(relationshipsBuilder.build(), offset(), limit(), topDocs.totalHits);
	}

	@Override
	protected Class<SnomedRelationships> getReturnType() {
		return SnomedRelationships.class;
	}
}
