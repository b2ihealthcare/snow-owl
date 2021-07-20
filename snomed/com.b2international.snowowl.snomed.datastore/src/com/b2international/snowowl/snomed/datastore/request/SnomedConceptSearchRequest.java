/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.parents;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.statedAncestors;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.statedParents;
import static com.google.common.collect.Maps.newHashMap;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.b2international.commons.options.Options;
import com.b2international.index.Hits;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.query.SortBy.Builder;
import com.b2international.index.query.SortBy.Order;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.repository.RevisionDocument;
import com.b2international.snowowl.core.request.TermFilter;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.cis.SnomedIdentifiers;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.ecl.EclExpression;
import com.b2international.snowowl.snomed.core.tree.Trees;
import com.b2international.snowowl.snomed.datastore.SnomedDescriptionUtils;
import com.b2international.snowowl.snomed.datastore.converter.SnomedConceptConverter;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * @since 4.5
 */
public class SnomedConceptSearchRequest extends SnomedComponentSearchRequest<SnomedConcepts, SnomedConceptDocument> {

	private static final long serialVersionUID = 1L;
	
	private static final float MIN_DOI_VALUE = 1.05f;
	private static final float MAX_DOI_VALUE = 10288.383f;
	
	public enum OptionKey {

		/**
		 * Description term to (smart) match
		 */
		TERM,
		
		/**
		 * Description type to match
		 */
		DESCRIPTION_TYPE,
		
		/**
		 * Description semantic tag(s) to match
		 */
		SEMANTIC_TAG,

		/**
		 * ECL expression to match on the inferred form
		 */
		ECL,
		
		/**
		 * ECL expression to match on the state form
		 */
		STATED_ECL,
		
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
	}
	
	protected SnomedConceptSearchRequest() {}

	@Override
	protected Enum<?> getSpecialOptionKey() {
		return OptionKey.TERM;
	}
	
	@Override
	protected void collectAdditionalFieldsToFetch(ImmutableSet.Builder<String> additionalFieldsToLoad) {
		// load preferred descriptions field if not requested, but either pt or fsn is expanded
		if (!fields().contains(SnomedConceptDocument.Fields.PREFERRED_DESCRIPTIONS) 
				&& (expand().containsKey(SnomedConcept.Expand.FULLY_SPECIFIED_NAME) || expand().containsKey(SnomedConcept.Expand.PREFERRED_TERM))) {
			additionalFieldsToLoad.add(SnomedConceptDocument.Fields.PREFERRED_DESCRIPTIONS);
		}
	}
	
	@Override
	protected String extractSpecialOptionValue(Options options, Enum<?> key) {
		return options.get(OptionKey.TERM, TermFilter.class).getTerm();
	}
	
	@Override
	protected Expression prepareQuery(BranchContext context) {
		ExpressionBuilder queryBuilder = Expressions.builder();
		
		addActiveClause(queryBuilder);
		addReleasedClause(queryBuilder);
		addIdFilter(queryBuilder, RevisionDocument.Expressions::ids);
		addEclFilter(context, queryBuilder, SnomedSearchRequest.OptionKey.MODULE, SnomedDocument.Expressions::modules);
		addEclFilter(context, queryBuilder, OptionKey.DEFINITION_STATUS, SnomedConceptDocument.Expressions::definitionStatusIds);
		addNamespaceFilter(queryBuilder);
		addEffectiveTimeClause(queryBuilder);
		addActiveMemberOfClause(context, queryBuilder);
		addMemberOfClause(context, queryBuilder);
		
		addFilter(queryBuilder, OptionKey.PARENT, String.class, SnomedConceptDocument.Expressions::parents);
		addFilter(queryBuilder, OptionKey.STATED_PARENT, String.class, SnomedConceptDocument.Expressions::statedParents);
		
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
			Expression eclExpression = EclExpression.of(ecl, Trees.INFERRED_FORM).resolveToExpression(context).getSync(3, TimeUnit.MINUTES);
			if (eclExpression.isMatchNone()) {
				throw new NoResultException();
			} else if (!eclExpression.isMatchAll()) {
				queryBuilder.filter(eclExpression);
			}
		}
		
		if (containsKey(OptionKey.STATED_ECL)) {
			final String ecl = getString(OptionKey.STATED_ECL);
			Expression statedEclExpression = EclExpression.of(ecl, Trees.STATED_FORM).resolveToExpression(context).getSync(3, TimeUnit.MINUTES);
			if (statedEclExpression.isMatchNone()) {
				throw new NoResultException();
			} else if (!statedEclExpression.isMatchAll()) {
				queryBuilder.filter(statedEclExpression);
			}
		}
		
		Expression searchProfileQuery = null;
		
		final Expression queryExpression;
		
		if (containsKey(OptionKey.SEMANTIC_TAG)) {
			queryBuilder.filter(SnomedConceptDocument.Expressions.semanticTags(getCollection(OptionKey.SEMANTIC_TAG, String.class)));
		}
		
		if (containsKey(OptionKey.TERM)) {
			final ExpressionBuilder bq = Expressions.builder();
			// nest current query
			bq.filter(queryBuilder.build());
			queryBuilder = bq;
			
			final TermFilter termFilter = containsKey(OptionKey.TERM) ? get(OptionKey.TERM, TermFilter.class) : null;
			final Map<String, Float> conceptScoreMap = executeDescriptionSearch(context, termFilter);
			
			if (termFilter != null) {
				try {
					final ComponentCategory category = SnomedIdentifiers.getComponentCategory(termFilter.getTerm());
					if (category == ComponentCategory.CONCEPT) {
						conceptScoreMap.put(termFilter.getTerm(), Float.MAX_VALUE);
					}
				} catch (IllegalArgumentException e) {
					// ignored
				}
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
	protected void toQuerySortBy(BranchContext context, Builder sortBuilder, Sort sort) {
		if (sort instanceof SortField) {
			SortField sortField = (SortField) sort;
			if (SnomedConceptSearchRequestBuilder.TERM_SORT.equals(sortField.getField())) {
				final Set<String> synonymIds = context.service(Synonyms.class).get();
				final Map<String, Object> args = ImmutableMap.of("locales", SnomedDescriptionUtils.getLanguageRefSetIds(locales()), "synonymIds", synonymIds);
				sortBuilder.sortByScript("termSort", args, sort.isAscending() ? Order.ASC : Order.DESC);
				return;
			}
		}
		super.toQuerySortBy(context, sortBuilder, sort);
	}
	
	@Override
	protected boolean trackScores() {
		return containsKey(OptionKey.TERM) || containsKey(OptionKey.USE_DOI);
	}

	@Override
	protected Class<SnomedConceptDocument> getSelect() {
		return SnomedConceptDocument.class;
	}
	
	@Override
	protected SnomedConcepts toCollectionResource(BranchContext context, Hits<SnomedConceptDocument> hits) {
		if (limit() < 1 || hits.getTotal() < 1) {
			return new SnomedConcepts(limit(), hits.getTotal());
		} else {
			return new SnomedConceptConverter(context, expand(), locales()).convert(hits);
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
	
	private Map<String, Float> executeDescriptionSearch(BranchContext context, TermFilter termFilter) {
		final SnomedDescriptionSearchRequestBuilder requestBuilder = SnomedRequests.prepareSearchDescription()
			.all()
			.filterByActive(true)
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
		
		if (termFilter != null) {
			requestBuilder.filterByTerm(termFilter);
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
