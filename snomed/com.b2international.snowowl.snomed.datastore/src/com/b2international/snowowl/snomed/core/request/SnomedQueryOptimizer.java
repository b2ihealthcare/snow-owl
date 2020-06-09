/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.request;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.QueryExpression;
import com.b2international.snowowl.core.domain.QueryExpressionDiff;
import com.b2international.snowowl.core.domain.QueryExpressionDiffs;
import com.b2international.snowowl.core.id.IDs;
import com.b2international.snowowl.core.request.QueryOptimizer;
import com.b2international.snowowl.core.request.SearchResourceRequestIterator;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.ecl.EclParser;
import com.b2international.snowowl.snomed.datastore.request.SnomedConceptSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.ecl.ecl.EclConceptReference;
import com.b2international.snowowl.snomed.ecl.ecl.ExpressionConstraint;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.primitives.Ints;
import com.google.common.util.concurrent.UncheckedExecutionException;

/**
 * @since 7.7
 */
public final class SnomedQueryOptimizer implements QueryOptimizer {

	@Override
	public QueryExpressionDiffs optimize(BranchContext context, Options params) {
		final Collection<QueryExpression> inclusions = params.getCollection(QueryOptimizer.OptionKey.INCLUSIONS, QueryExpression.class);
		final List<ExtendedLocale> locales = params.getList(QueryOptimizer.OptionKey.LOCALES, ExtendedLocale.class);
		
		final EclParser eclParser = context.service(EclParser.class);
		final LoadingCache<String, ExpressionConstraint> eclCache = CacheBuilder.newBuilder()
				.build(CacheLoader.from(eclParser::parse));
		
		final Multimap<String, QueryExpression> singleConceptInclusions = FluentIterable.from(inclusions)
				.filter(ex -> isSingleConceptExpression(eclCache, ex.getQuery()))
				.index(ex -> toSingleConceptId(eclCache, ex.getQuery()));
		
		final SnomedConceptSearchRequestBuilder req = SnomedRequests.prepareSearchConcept()
				.filterByIds(singleConceptInclusions.keySet())
				.setLocales(locales)
				.setLimit(10_000)
				.setExpand("pt()");

		final SearchResourceRequestIterator<SnomedConceptSearchRequestBuilder, SnomedConcepts> itr = new SearchResourceRequestIterator<>(
				req, builder -> builder.build().execute(context));

		// Record the ancestors (both direct and indirect) of each single concept inclusion
		final Multimap<String, QueryExpression> membersByAncestor = HashMultimap.create();

		itr.forEachRemaining(batch -> {
			batch.forEach(child -> {
				final Collection<QueryExpression> childExpressions = singleConceptInclusions.get(child.getId());
				final List<String> parentIds = child.getParentIdsAsString();
				final List<String> ancestorIds = child.getAncestorIdsAsString();
				
				parentIds.forEach(parentId -> {
					membersByAncestor.putAll(parentId, childExpressions);
				});
				
				ancestorIds.forEach(ancestorId -> {
					membersByAncestor.putAll(ancestorId, childExpressions);
				});
			});
		});
		
		// Get number of referenced descendants (taking possible duplicates into account)
		final Map<String, Long> uniqueDescendantsByParent = ImmutableMap.copyOf(Maps.transformValues(
				membersByAncestor.asMap(), 
				descendants -> descendants.stream()
					.map(QueryExpression::getQuery)
					.distinct()
					.count()));
		
		// Retrieve descendant counts for parents; if the two numbers match, the single concept
		// references can be replaced with a single << expression.
		final SnomedConceptSearchRequestBuilder descendantReq = SnomedRequests.prepareSearchConcept()
				.filterByIds(uniqueDescendantsByParent.keySet())
				.setLocales(locales)
				.setLimit(1_000)
				.setExpand("pt(),descendants(direct:false,limit:0)");

		final SearchResourceRequestIterator<SnomedConceptSearchRequestBuilder, SnomedConcepts> parentItr = new SearchResourceRequestIterator<>(
				descendantReq, builder -> builder.build().execute(context));

		final ImmutableList.Builder<QueryExpressionDiff> diffs = ImmutableList.builder();

		parentItr.forEachRemaining(batch -> {
			batch.forEach(parent -> {
				final String parentId = parent.getId();
				final int referencedDescendants = Ints.checkedCast(uniqueDescendantsByParent.get(parentId));
				final int totalDescendants = parent.getDescendants().getTotal();
				
				if (totalDescendants == referencedDescendants) {
					final List<QueryExpression> remove = List.copyOf(membersByAncestor.get(parentId)
							.stream()
							.filter(ex -> !ex.isPinned())
							.collect(Collectors.toList()));
					
					// The optimization is a "net win" if we can remove at least two clauses from the original
					if (remove.size() > 1) {
						final QueryExpression replacement = new QueryExpression(IDs.base64UUID(), String.format("<%s%s", parent.getId(), getTerm(parent)), false);
						final List<QueryExpression> addToInclusion = List.of(replacement);
						final List<QueryExpression> addToExclusion = List.of();

						final QueryExpressionDiff diff = new QueryExpressionDiff(addToInclusion, addToExclusion, remove);
						diffs.add(diff);
					}
				}
			});
		});
		
		return new QueryExpressionDiffs(diffs.build());
	}

	private String getTerm(SnomedConcept parent) {
		return parent.getPt() != null ? String.format("|%s|", parent.getPt().getTerm()) : "";
	}

	private boolean isSingleConceptExpression(LoadingCache<String, ExpressionConstraint> eclCache, String query) {
		try {
			final ExpressionConstraint expressionConstraint = eclCache.getUnchecked(query);
			return (expressionConstraint instanceof EclConceptReference);
		} catch (UncheckedExecutionException | BadRequestException e) {
			return false;
		}
	}

	private String toSingleConceptId(LoadingCache<String, ExpressionConstraint> eclCache, String query) {
		try {
			final ExpressionConstraint expressionConstraint = eclCache.getUnchecked(query);
			return ((EclConceptReference) expressionConstraint).getId();
		} catch (UncheckedExecutionException | BadRequestException e) {
			return "";
		}
	}
}
