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
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.FilteredQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TotalHitCountCollector;

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.exceptions.IllegalQueryParameterException;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.datastore.server.snomed.escg.IndexQueryQueryEvaluator;
import com.b2international.snowowl.dsl.escg.EscgUtils;
import com.b2international.snowowl.snomed.core.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedQueryBuilder;
import com.b2international.snowowl.snomed.datastore.server.converter.SnomedConceptConverter;
import com.b2international.snowowl.snomed.datastore.server.converter.SnomedConverters;
import com.b2international.snowowl.snomed.dsl.query.SyntaxErrorException;
import com.google.common.collect.ImmutableList;

import bak.pcj.adapter.LongCollectionToCollectionAdapter;
import bak.pcj.map.LongKeyFloatMap;
import bak.pcj.map.LongKeyFloatOpenHashMap;

/**
 * @since 4.5
 */
final class SnomedConceptSearchRequest extends SnomedSearchRequest<SnomedConcepts> {

	enum OptionKey {

		/**
		 * Description term to (smart) match
		 */
		TERM,

		/**
		 * ESCG expression to match
		 */
		ESCG,

		/**
		 * Namespace part of concept ID to match (?)
		 */
		NAMESPACE
	}
	
	SnomedConceptSearchRequest() {}

	@Override
	protected SnomedConcepts doExecute(BranchContext context) throws IOException {
		final IndexSearcher searcher = context.service(IndexSearcher.class);
		final SnomedQueryBuilder queryBuilder = SnomedMappings.newQuery().concept();
		
		if (containsKey(SnomedSearchRequest.OptionKey.ACTIVE)) {
			queryBuilder.active(getBoolean(SnomedSearchRequest.OptionKey.ACTIVE));
		}
		
		if (containsKey(OptionKey.ESCG)) {
			/* 
			 * XXX: Not using IEscgQueryEvaluatorService, as it would add the equivalent of 
			 * active() and concept() to escgQuery, which is not needed.
			 */
			final IndexQueryQueryEvaluator queryEvaluator = new IndexQueryQueryEvaluator();
			try {
				final BooleanQuery escgQuery = queryEvaluator.evaluate(EscgUtils.INSTANCE.parseRewrite(getString(OptionKey.ESCG)));
				queryBuilder.and(escgQuery);
			} catch (final SyntaxErrorException e) {
				throw new IllegalQueryParameterException(e.getMessage());
			}
		}
		
		if (containsKey(SnomedSearchRequest.OptionKey.MODULE)) {
			queryBuilder.module(getString(SnomedSearchRequest.OptionKey.MODULE));
		}
		
		final Query conceptQuery = queryBuilder.matchAll();
		final Query query;
		final Sort sort;
		
		if (containsKey(OptionKey.TERM)) {
			final LongKeyFloatMap conceptScoreMap = executeDescriptionSearch(context, getString(OptionKey.TERM));
			final Filter descriptionFilter = SnomedMappings.id().createTermsFilter(new LongCollectionToCollectionAdapter(conceptScoreMap.keySet())); 
			
//			query = new CustomScoreQuery(new FilteredQuery(conceptQuery, descriptionFilter), ...);
			query = new FilteredQuery(conceptQuery, descriptionFilter);
			sort = Sort.RELEVANCE;
		} else {
			query = new ConstantScoreQuery(conceptQuery);
			sort = Sort.INDEXORDER;
		}
		
		if (limit() == 0) {
			final TotalHitCountCollector totalCollector = new TotalHitCountCollector();
			searcher.search(new ConstantScoreQuery(query), totalCollector); 
			return new SnomedConcepts(offset(), limit(), totalCollector.getTotalHits());
		}
		
		// TODO: track score only if it should be expanded
		final TopDocs topDocs = searcher.search(query, null, offset() + limit(), sort, true, false);
		if (IndexUtils.isEmpty(topDocs)) {
			return new SnomedConcepts(offset(), limit(), topDocs.totalHits);
		}
		
		final ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		final SnomedConceptConverter converter = SnomedConverters.newConceptConverter(context);
		final ImmutableList.Builder<ISnomedConcept> conceptsBuilder = ImmutableList.builder();
		
		for (int i = offset(); i < scoreDocs.length && i < offset() + limit(); i++) {
			Document doc = searcher.doc(scoreDocs[i].doc); // TODO: should expand & filter drive fieldsToLoad? Pass custom fieldValueLoader?
			SnomedConceptIndexEntry indexEntry = SnomedConceptIndexEntry.builder(doc).score(scoreDocs[i].score).build();
			conceptsBuilder.add(converter.apply(indexEntry));
		}

		return new SnomedConcepts(conceptsBuilder.build(), offset(), limit(), topDocs.totalHits);
	}

	private LongKeyFloatMap executeDescriptionSearch(BranchContext context, String term) {
		return new LongKeyFloatOpenHashMap();
	}

	@Override
	protected Class<SnomedConcepts> getReturnType() {
		return SnomedConcepts.class;
	}
}
