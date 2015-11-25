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

import static com.google.common.collect.Maps.newHashMap;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.queries.BooleanFilter;
import org.apache.lucene.queries.CustomScoreQuery;
import org.apache.lucene.queries.function.FunctionQuery;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.valuesource.LongFieldSource;
import org.apache.lucene.queries.function.valuesource.SimpleFloatFunction;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.FilteredQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TotalHitCountCollector;

import com.b2international.commons.functions.StringToLongFunction;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.exceptions.IllegalQueryParameterException;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.datastore.server.snomed.escg.IndexQueryQueryEvaluator;
import com.b2international.snowowl.dsl.escg.EscgUtils;
import com.b2international.snowowl.snomed.core.domain.ISnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedQueryBuilder;
import com.b2international.snowowl.snomed.datastore.server.converter.SnomedConverters;
import com.b2international.snowowl.snomed.dsl.query.SyntaxErrorException;
import com.google.common.collect.ImmutableList;

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
		NAMESPACE,
		
		/**
		 * Parent concept ID
		 */
		PARENT,
		
		/**
		 * Ancestor concept ID (includes direct parents)
		 */
		ANCESTOR
	}
	
	SnomedConceptSearchRequest() {}

	@Override
	protected SnomedConcepts doExecute(BranchContext context) throws IOException {
		final IndexSearcher searcher = context.service(IndexSearcher.class);
		final SnomedQueryBuilder queryBuilder = SnomedMappings.newQuery().concept();
		
		if (containsKey(SnomedSearchRequest.OptionKey.ACTIVE)) {
			queryBuilder.active(getBoolean(SnomedSearchRequest.OptionKey.ACTIVE));
		}
		
		if (containsKey(OptionKey.PARENT)) {
			queryBuilder.parent(getString(OptionKey.PARENT));
		}
		
		if (containsKey(OptionKey.ANCESTOR)) {
			final String ancestorId = getString(OptionKey.ANCESTOR);
			queryBuilder.and(SnomedMappings.newQuery()
					.parent(ancestorId)
					.parent(ancestorId)
					.matchAny());
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
		final BooleanFilter filter = new BooleanFilter();
		
		if (!componentIds().isEmpty()) {
			filter.add(createComponentIdFilter(), Occur.MUST);
		}
		
		if (containsKey(OptionKey.TERM)) {
			final Map<String, Integer> conceptScoreMap = executeDescriptionSearch(context, getString(OptionKey.TERM));
			filter.add(SnomedMappings.id().createTermsFilter(StringToLongFunction.copyOf(conceptScoreMap.keySet())), Occur.MUST); 
			final FunctionQuery functionQuery = new FunctionQuery(new SimpleFloatFunction(new LongFieldSource(SnomedMappings.id().fieldName())) {
				
				@Override
				protected String name() {
					return "ConceptScoreMap";
				}
				
				@Override
				protected float func(int doc, FunctionValues vals) {
					final String conceptId = Long.toString(vals.longVal(doc));
					if (conceptScoreMap.containsKey(conceptId)) {
						return conceptScoreMap.get(conceptId);
					} else {
						return 0.0f;
					}
				}
			});
			
			query = new CustomScoreQuery(createQuery(conceptQuery, filter), functionQuery);
			sort = Sort.RELEVANCE;
		} else {
			query = new ConstantScoreQuery(createQuery(conceptQuery, filter));
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
		final ImmutableList.Builder<SnomedConceptIndexEntry> conceptsBuilder = ImmutableList.builder();
		
		for (int i = offset(); i < scoreDocs.length && i < offset() + limit(); i++) {
			Document doc = searcher.doc(scoreDocs[i].doc); // TODO: should expand & filter drive fieldsToLoad? Pass custom fieldValueLoader?
			SnomedConceptIndexEntry indexEntry = SnomedConceptIndexEntry.builder(doc).score(scoreDocs[i].score).build();
			conceptsBuilder.add(indexEntry);
		}

		return SnomedConverters.newConceptConverter(context, expand(), locales()).convert(conceptsBuilder.build(), offset(), limit(), topDocs.totalHits);
	}

	private Query createQuery(final Query query, final BooleanFilter filter) {
		if (filter.clauses().size() > 0) {
			return new FilteredQuery(query, filter);
		}
		return query;
	}

	private Map<String, Integer> executeDescriptionSearch(BranchContext context, String term) {
		
		final Collection<ISnomedDescription> items = SnomedRequests.prepareDescriptionSearch()
			.all()
			.filterByActive(true)
			.filterByTerm(term)
			.filterByLanguageRefSetIds(languageRefSetIds())
			.build()
			.execute(context)
			.getItems();

		final Map<String, Integer> conceptMap = newHashMap();
		int i = items.size();
		
		for (ISnomedDescription description : items) {
			if (!conceptMap.containsKey(description.getConceptId())) {
				conceptMap.put(description.getConceptId(), i);
			}
			
			i--;
		}

		return conceptMap;
	}

	@Override
	protected Class<SnomedConcepts> getReturnType() {
		return SnomedConcepts.class;
	}
}
