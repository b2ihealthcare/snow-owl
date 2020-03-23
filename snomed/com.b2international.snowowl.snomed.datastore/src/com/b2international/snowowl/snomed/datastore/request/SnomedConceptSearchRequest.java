/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.ancestors;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.defining;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.parents;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.primitive;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.statedAncestors;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.statedParents;
import static com.google.common.collect.Maps.newHashMap;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.b2international.index.Hits;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.datastore.index.RevisionDocument;
import com.b2international.snowowl.snomed.cis.SnomedIdentifiers;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.ecl.EclExpression;
import com.b2international.snowowl.snomed.core.ql.SnomedQueryExpression;
import com.b2international.snowowl.snomed.core.tree.Trees;
import com.b2international.snowowl.snomed.datastore.converter.SnomedConverters;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;
import com.google.common.collect.ImmutableMap;

/**
 * @since 4.5
 */
public class SnomedConceptSearchRequest extends SnomedComponentSearchRequest<SnomedConcepts, SnomedConceptDocument> {

	private static final float MIN_DOI_VALUE = 1.05f;
	private static final float MAX_DOI_VALUE = 10288.383f;
	
	public enum OptionKey {

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
		 * Description semantic tag(s) to match
		 */
		DESCRIPTION_SEMANTIC_TAG,

		/**
		 * ECL expression to match on the inferred form
		 */
		ECL,
		
		/**
		 * ECL expression to match on the state form
		 */
		STATED_ECL,
		
		/**
		 * Snomed CT Query expression to match
		 */
		QUERY,
		
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
		 * Use fuzzy query in the search
		 */
		USE_FUZZY

	}
	
	protected SnomedConceptSearchRequest() {}

	@Override
	protected Expression prepareQuery(BranchContext context) {
		ExpressionBuilder queryBuilder = Expressions.builder();
		
		addActiveClause(queryBuilder);
		addReleasedClause(queryBuilder);
		addIdFilter(queryBuilder, RevisionDocument.Expressions::ids);
		addEclFilter(context, queryBuilder, SnomedSearchRequest.OptionKey.MODULE, SnomedDocument.Expressions::modules);
		addNamespaceFilter(queryBuilder);
		addEffectiveTimeClause(queryBuilder);
		addActiveMemberOfClause(context, queryBuilder);
		addMemberOfClause(context, queryBuilder);
		
		if (containsKey(OptionKey.DEFINITION_STATUS)) {
			if (Concepts.PRIMITIVE.equals(getString(OptionKey.DEFINITION_STATUS))) {
				queryBuilder.filter(primitive());
			} else if (Concepts.FULLY_DEFINED.equals(getString(OptionKey.DEFINITION_STATUS))) {
				queryBuilder.filter(defining());
			}
		}
		
		if (containsKey(OptionKey.PARENT)) {
			queryBuilder.filter(parents(getCollection(OptionKey.PARENT, String.class)));
		}
		
		if (containsKey(OptionKey.STATED_PARENT)) {
			queryBuilder.filter(statedParents(getCollection(OptionKey.STATED_PARENT, String.class)));
		}
		
		if (containsKey(OptionKey.ANCESTOR)) {
			final Collection<String> ancestorIds = getCollection(OptionKey.ANCESTOR, String.class);
			queryBuilder.filter(Expressions.builder()
					.should(parents(ancestorIds))
					.should(ancestors(ancestorIds))
					.build());
		}
		
		if (containsKey(OptionKey.STATED_ANCESTOR)) {
			final Collection<String> ancestorIds = getCollection(OptionKey.STATED_ANCESTOR, String.class);
			queryBuilder.filter(Expressions.builder()
					.should(statedParents(ancestorIds))
					.should(statedAncestors(ancestorIds))
					.build());
		}

		if (containsKey(OptionKey.ECL)) {
			final String ecl = getString(OptionKey.ECL);
			queryBuilder.filter(EclExpression.of(ecl, Trees.INFERRED_FORM).resolveToExpression(context).getSync(3, TimeUnit.MINUTES));
		}
		
		if (containsKey(OptionKey.STATED_ECL)) {
			final String ecl = getString(OptionKey.STATED_ECL);
			queryBuilder.filter(EclExpression.of(ecl, Trees.STATED_FORM).resolveToExpression(context).getSync(3, TimeUnit.MINUTES));
		}
		
		if (containsKey(OptionKey.QUERY)) {
			final String ql = getString(OptionKey.QUERY);
			queryBuilder.filter(SnomedQueryExpression.of(ql).resolveToExpression(context).getSync(3, TimeUnit.MINUTES));
		}
		
		Expression searchProfileQuery = null;
		
		final Expression queryExpression;
		
		if (containsKey(OptionKey.TERM)) {
			final ExpressionBuilder bq = Expressions.builder();
			// nest current query
			bq.filter(queryBuilder.build());
			queryBuilder = bq;
			
			final String term = getString(OptionKey.TERM);
			final Map<String, Float> conceptScoreMap = executeDescriptionSearch(context, term);
			
			try {
				final ComponentCategory category = SnomedIdentifiers.getComponentCategory(term);
				if (category == ComponentCategory.CONCEPT) {
					conceptScoreMap.put(term, Float.MAX_VALUE);
				}
			} catch (IllegalArgumentException e) {
				// ignored
			}
			
			if (conceptScoreMap.isEmpty()) {
				throw new NoResultException();
			}
			
			queryBuilder.filter(RevisionDocument.Expressions.ids(conceptScoreMap.keySet()));
			
			final Expression q = addSearchProfile(searchProfileQuery, queryBuilder.build());
			queryExpression = Expressions.scriptScore(q, "doiFactor", ImmutableMap.of("termScores", conceptScoreMap, "useDoi", containsKey(OptionKey.USE_DOI), "minDoi", MIN_DOI_VALUE, "maxDoi", MAX_DOI_VALUE));
		} else if (containsKey(OptionKey.USE_DOI)) {
			final Expression q = addSearchProfile(searchProfileQuery, queryBuilder.build());
			queryExpression = Expressions.scriptScore(q, "doi");
		} else {
			queryExpression = addSearchProfile(searchProfileQuery, queryBuilder.build());
		}
		
		return queryExpression;
	}
	
	@Override
	protected boolean trackScores() {
		return containsKey(OptionKey.TERM) || containsKey(OptionKey.USE_DOI);
	}

	@Override
	protected Class<SnomedConceptDocument> getDocumentType() {
		return SnomedConceptDocument.class;
	}
	
	@Override
	protected SnomedConcepts toCollectionResource(BranchContext context, Hits<SnomedConceptDocument> hits) {
		if (limit() < 1 || hits.getTotal() < 1) {
			return new SnomedConcepts(limit(), hits.getTotal());
		} else {
			return SnomedConverters.newConceptConverter(context, expand(), locales()).convert(hits.getHits(), hits.getSearchAfter(), limit(), hits.getTotal());
		}
	}
	
	@Override
	protected SnomedConcepts createEmptyResult(int limit) {
		return new SnomedConcepts(limit, 0);
	}

	private Expression addSearchProfile(final Expression searchProfileQuery, final Expression query) {
		if (searchProfileQuery == null) {
			return query;
		} else {
			return Expressions.builder()
					.filter(searchProfileQuery)
					.filter(query)
					.build();
		}
	}
	
	private Map<String, Float> executeDescriptionSearch(BranchContext context, String term) {
		final SnomedDescriptionSearchRequestBuilder requestBuilder = SnomedRequests.prepareSearchDescription()
			.all()
			.filterByActive(true)
			.filterByTerm(term)
			.setFields(SnomedDescriptionIndexEntry.Fields.ID, SnomedDescriptionIndexEntry.Fields.CONCEPT_ID)
			.sortBy(SCORE);
		
		if (containsKey(SnomedDescriptionSearchRequest.OptionKey.LANGUAGE_REFSET)) {
			requestBuilder.filterByLanguageRefSets(getCollection(SnomedDescriptionSearchRequest.OptionKey.LANGUAGE_REFSET, String.class));
		}
			
		applyIdFilter(requestBuilder, (rb, ids) -> rb.filterByConceptId(ids));
		
		if (containsKey(OptionKey.DESCRIPTION_TYPE)) {
			final String type = getString(OptionKey.DESCRIPTION_TYPE);
			requestBuilder.filterByType(type);
		}
		
		if (containsKey(OptionKey.DESCRIPTION_SEMANTIC_TAG)) {
			final Collection<String> semanticTags = getCollection(OptionKey.DESCRIPTION_SEMANTIC_TAG, String.class);
			requestBuilder.filterBySemanticTags(semanticTags);
		}
		
		if (containsKey(OptionKey.USE_FUZZY)) {
			requestBuilder.withFuzzySearch();
		}
		
		if (containsKey(OptionKey.PARSED_TERM)) {
			requestBuilder.withParsedTerm();
		}
		
		final Collection<SnomedDescription> items = requestBuilder
			.build()
			.execute(context)
			.getItems();
		
		final Map<String, Float> conceptMap = newHashMap();
		
		for (SnomedDescription description : items) {
			if (!conceptMap.containsKey(description.getConceptId())) {
				conceptMap.put(description.getConceptId(), description.getScore());
			}
		}
		
		return conceptMap;
	}

}
