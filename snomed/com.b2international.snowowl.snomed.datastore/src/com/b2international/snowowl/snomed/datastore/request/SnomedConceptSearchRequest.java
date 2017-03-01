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

import static com.google.common.collect.Maps.newHashMap;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.queries.BooleanFilter;
import org.apache.lucene.queries.CustomScoreQuery;
import org.apache.lucene.queries.function.FunctionQuery;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.valuesource.DualFloatFunction;
import org.apache.lucene.queries.function.valuesource.FloatFieldSource;
import org.apache.lucene.queries.function.valuesource.LongFieldSource;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;

import com.b2international.commons.functions.StringToLongFunction;
import com.b2international.commons.options.Options;
import com.b2international.commons.pcj.LongSets;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.exceptions.IllegalQueryParameterException;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.ISnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.datastore.converter.SnomedConverters;
import com.b2international.snowowl.snomed.datastore.escg.EscgParseFailedException;
import com.b2international.snowowl.snomed.datastore.escg.EscgRewriter;
import com.b2international.snowowl.snomed.datastore.escg.IEscgQueryEvaluatorService;
import com.b2international.snowowl.snomed.datastore.escg.IndexQueryQueryEvaluator;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.datastore.index.SearchProfileQueryProvider;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry.Builder;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedQueryBuilder;
import com.b2international.snowowl.snomed.dsl.query.SyntaxErrorException;
import com.google.common.collect.ImmutableList;

import bak.pcj.LongCollection;

/**
 * Request responsible for searching SNOMED CT concepts. Requests can be executed both in
 * synchronous and asynchronous fashion.
 * <p>
 * This class should be instantiated via the corresponding builder class.
 * 
 * @since 4.5
 */
final class SnomedConceptSearchRequest extends SnomedSearchRequest<SnomedConcepts> {

	private static final ValueSource DOI_VALUE_SOURCE = new FloatFieldSource(SnomedMappings.conceptDegreeOfInterest().fieldName());

	private static final float MIN_DOI_VALUE = 1.05f;
	private static final float MAX_DOI_VALUE = 10288.383f;
	
	enum OptionKey {

		/**
		 * Description term to (smart) match
		 */
		TERM,
		
		/**
		 * Parse the term for query syntax search
		 */
		PARSED_TERM,
		
		/**
		 * Description type to match
		 */
		DESCRIPTION_TYPE,

		/**
		 * ESCG expression to match
		 */
		ESCG,

		/**
		 * Namespace part of concept ID to match (?)
		 */
		NAMESPACE,
		
		/**
		 * The definition status to match
		 */
		DEFINITION_STATUS,
		
		/**
		 * Parent concept ID that can be found in the inferred direct super type hierarchy
		 */
		PARENT,
		
		/**
		 * Ancestor concept ID that can be found in the inferred super type hierarchy (includes direct parents)
		 */
		ANCESTOR, 
		
		/**
		 * Parent concept ID that can be found in the stated direct super type hierarchy
		 */
		STATED_PARENT,
		
		/**
		 * Ancestor concept ID that can be found in the stated super type hierarchy (includes direct stated parents as well)
		 */
		STATED_ANCESTOR,
		
		/**
		 * Enable score boosting using DOI field
		 */
		USE_DOI,
		
		/**
		 * Use search profile of the user
		 */
		SEARCH_PROFILE,
		
		/**
		 * Use fuzzy query in the search
		 */
		USE_FUZZY

	}
	
	SnomedConceptSearchRequest() {}

	@Override
	protected SnomedConcepts doExecute(BranchContext context) throws IOException {
		final IndexSearcher searcher = context.service(IndexSearcher.class);

		final SnomedQueryBuilder queryBuilder = SnomedMappings.newQuery().concept();
		addActiveClause(queryBuilder);
		addModuleClause(queryBuilder);
		
		if (containsKey(OptionKey.NAMESPACE)) {
			queryBuilder.conceptNamespaceId(Long.valueOf(getString(OptionKey.NAMESPACE)));
		}
		
		if (containsKey(OptionKey.DEFINITION_STATUS)) {
			queryBuilder.primitive(Concepts.PRIMITIVE.equals(getString(OptionKey.DEFINITION_STATUS)));
		}
		
		if (containsKey(OptionKey.PARENT)) {
			queryBuilder.parent(getString(OptionKey.PARENT));
		}
		
		if (containsKey(OptionKey.STATED_PARENT)) {
			queryBuilder.statedParent(getString(OptionKey.STATED_PARENT));
		}
		
		if (containsKey(OptionKey.ANCESTOR)) {
			final String ancestorId = getString(OptionKey.ANCESTOR);
			queryBuilder.and(SnomedMappings.newQuery()
					.parent(ancestorId)
					.ancestor(ancestorId)
					.matchAny());
		}
		
		if (containsKey(OptionKey.STATED_ANCESTOR)) {
			final String ancestorId = getString(OptionKey.STATED_ANCESTOR);
			queryBuilder.and(SnomedMappings.newQuery()
					.statedParent(ancestorId)
					.statedAncestor(ancestorId)
					.matchAny());
		}

		final BooleanFilter filter = new BooleanFilter();
		Sort sort;
		Query query;
		
		if (containsKey(OptionKey.ESCG)) {
			/* 
			 * XXX: Not using IEscgQueryEvaluatorService, as it would add the equivalent of 
			 * active() and concept() to escgQuery, which is not needed.
			 */
			final String escg = getString(OptionKey.ESCG);
			try {
				final IndexQueryQueryEvaluator queryEvaluator = new IndexQueryQueryEvaluator();
				final BooleanQuery escgQuery = queryEvaluator.evaluate(context.service(EscgRewriter.class).parseRewrite(escg));
				queryBuilder.and(escgQuery);
			} catch (final SyntaxErrorException e) {
				throw new IllegalQueryParameterException(e.getMessage());
			} catch (EscgParseFailedException e) {
				final LongCollection matchingConceptIds = context.service(IEscgQueryEvaluatorService.class).evaluateConceptIds(context.branch().branchPath(), escg);
				addFilterClause(filter, SnomedMappings.id().createTermsFilter(LongSets.toSet(matchingConceptIds)), Occur.MUST);
			}
		}
		
		BooleanQuery searchProfileQuery = null;
		if (containsKey(OptionKey.SEARCH_PROFILE)) {
			final String userId = getString(OptionKey.SEARCH_PROFILE);
			searchProfileQuery = SearchProfileQueryProvider.provideQuery(context.branch().branchPath(), userId);
		}

		addComponentIdFilter(filter);
		
		if (containsKey(OptionKey.TERM)) {
			final BooleanQuery bq = buildBooleanQuery(queryBuilder.matchAll(), Occur.MUST);
			
			final String term = getString(OptionKey.TERM);
			final Map<String, Float> conceptScoreMap = executeDescriptionSearch(context, term);
			
			try {
				final ComponentCategory category = SnomedIdentifiers.getComponentCategory(term);
				if (category == ComponentCategory.CONCEPT) {
					conceptScoreMap.put(term, Float.MAX_VALUE);
					bq.add(SnomedMappings.id().toQuery(Long.valueOf(term)), Occur.SHOULD);
				}
			} catch (IllegalArgumentException e) {
				// ignored
			}
			
			if (conceptScoreMap.isEmpty()) {
				return new SnomedConcepts(offset(), limit(), 0);
			}
			
			addFilterClause(filter, SnomedMappings.id().createTermsFilter(StringToLongFunction.copyOf(conceptScoreMap.keySet())), Occur.MUST);
			
			final FunctionQuery functionQuery = new FunctionQuery(
					new DualFloatFunction(new LongFieldSource(SnomedMappings.id().fieldName()), DOI_VALUE_SOURCE) {
						@Override
						protected String name() {
							return "ConceptScoreMap";
						}

						@Override
						protected float func(int doc, FunctionValues conceptIdValues, FunctionValues interestValues) {
							final String conceptId = Long.toString(conceptIdValues.longVal(doc));
							float interest = containsKey(OptionKey.USE_DOI) ? interestValues.floatVal(doc) : 0.0f;
							
							// TODO move this normalization to index initializer.
							if (interest != 0.0f) {
								interest = (interest - MIN_DOI_VALUE) / (MAX_DOI_VALUE - MIN_DOI_VALUE);
							}
							
							if (conceptScoreMap.containsKey(conceptId)) {
								return conceptScoreMap.get(conceptId) + interest;
							} else {
								return 0.0f;
							}
						}
					});

			final Query filteredQuery = createFilteredQuery(bq, filter);
			final Query q = addSearchProfile(searchProfileQuery, filteredQuery);
			query = new CustomScoreQuery(q, functionQuery);
			sort = Sort.RELEVANCE;
		} else if (containsKey(OptionKey.USE_DOI)) {
			final Query filteredQuery = createFilteredQuery(queryBuilder.matchAll(), filter);
			final Query q = addSearchProfile(searchProfileQuery, filteredQuery);
			query = new CustomScoreQuery(createConstantScoreQuery(q), new FunctionQuery(DOI_VALUE_SOURCE));
			sort = Sort.RELEVANCE;
		} else {
			final Query filteredQuery = createFilteredQuery(queryBuilder.matchAll(), filter);
			final Query q = addSearchProfile(searchProfileQuery, filteredQuery);
			query = createConstantScoreQuery(q);
			sort = Sort.INDEXORDER;
		}
		
		final int totalHits = getTotalHits(searcher, query);
		if (limit() < 1 || totalHits < 1) {
			return new SnomedConcepts(offset(), limit(), totalHits);
		}
		
		// TODO: control score tracking
		final TopDocs topDocs = searcher.search(query, null, numDocsToRetrieve(searcher, totalHits), sort, true, false);
		if (topDocs.scoreDocs.length < 1) {
			return new SnomedConcepts(offset(), limit(), topDocs.totalHits);
		}
		
		final ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		final ImmutableList.Builder<SnomedConceptIndexEntry> conceptsBuilder = ImmutableList.builder();
		
		final Options expand = expand();
		for (int i = offset(); i < scoreDocs.length; i++) {
			Document doc = searcher.doc(scoreDocs[i].doc); // TODO: should expand & filter drive fieldsToLoad? Pass custom fieldValueLoader?
			final Builder builder = SnomedConceptIndexEntry.builder(doc).score(scoreDocs[i].score);
			
			if (expand != null) {
				if (expand.containsKey("parentIds") || expand.containsKey("ancestors")) {
					builder.parents(SnomedMappings.parent().getValueAsLongSet(doc));
					builder.statedParents(SnomedMappings.statedParent().getValueAsLongSet(doc));
				}
				
				if (expand.containsKey("ancestorIds") || expand.containsKey("ancestors")) {
					builder.ancestors(SnomedMappings.ancestor().getValueAsLongSet(doc));
					builder.statedAncestors(SnomedMappings.statedAncestor().getValueAsLongSet(doc));
				}
			}
			
			conceptsBuilder.add(builder.build());
		}
		return SnomedConverters.newConceptConverter(context, expand, locales()).convert(conceptsBuilder.build(), offset(), limit(), topDocs.totalHits);
	}

	private BooleanQuery buildBooleanQuery(final Query query, final Occur occur) {
		if (query instanceof BooleanQuery) {
			return (BooleanQuery) query;
		} else {
			final BooleanQuery booleanQuery = new BooleanQuery(true);
			booleanQuery.add(query, occur);
			return booleanQuery;
		}
	}
	
	private Query addSearchProfile(final BooleanQuery searchProfileQuery, final Query query) {
		if (searchProfileQuery == null) {
			return query;
		} else {
			searchProfileQuery.add(query, Occur.MUST);
			return searchProfileQuery;
		}
	}
	
	private Map<String, Float> executeDescriptionSearch(BranchContext context, String term) {
		final SnomedDescriptionSearchRequestBuilder requestBuilder = SnomedRequests.prepareSearchDescription()
			.all()
			.filterByActive(true)
			.filterByTerm(term)
			.filterByLanguageRefSetIds(languageRefSetIds())
			.filterByConceptId(StringToLongFunction.copyOf(componentIds()));
		
		if (containsKey(OptionKey.DESCRIPTION_TYPE)) {
			final String type = getString(OptionKey.DESCRIPTION_TYPE);
			requestBuilder.filterByType(type);
		}
		
		if (containsKey(OptionKey.USE_FUZZY)) {
			requestBuilder.withFuzzySearch();
		}
		
		if (containsKey(OptionKey.PARSED_TERM)) {
			requestBuilder.withParsedTerm();
		}
		
		final Collection<ISnomedDescription> items = requestBuilder
			.build()
			.execute(context)
			.getItems();
		
		final Map<String, Float> conceptMap = newHashMap();
		
		for (ISnomedDescription description : items) {
			if (!conceptMap.containsKey(description.getConceptId())) {
				conceptMap.put(description.getConceptId(), description.getScore());
			}
		}
		
		return conceptMap;
	}

	@Override
	protected Class<SnomedConcepts> getReturnType() {
		return SnomedConcepts.class;
	}
}
