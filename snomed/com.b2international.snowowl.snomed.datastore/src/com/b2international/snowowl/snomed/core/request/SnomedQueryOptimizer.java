/*
 * Copyright 2020-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snomed.ecl.ecl.EclConceptReference;
import com.b2international.snomed.ecl.ecl.ExpressionConstraint;
import com.b2international.snowowl.core.domain.*;
import com.b2international.snowowl.core.ecl.EclParser;
import com.b2international.snowowl.core.id.IDs;
import com.b2international.snowowl.core.request.QueryOptimizer;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.SnomedDisplayTermType;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.*;
import com.google.common.primitives.Ints;
import com.google.common.util.concurrent.UncheckedExecutionException;

/**
 * @since 7.7
 */
public final class SnomedQueryOptimizer implements QueryOptimizer {

	private static final int PAGE_SIZE = 10_000;

	@Override
	public QueryExpressionDiffs optimize(BranchContext context, Options params) {
		final Collection<QueryExpression> inclusions = params.getCollection(QueryOptimizer.OptionKey.INCLUSIONS, QueryExpression.class);
		final List<ExtendedLocale> locales = params.getList(QueryOptimizer.OptionKey.LOCALES, ExtendedLocale.class);
		final int numberOfOptimizationsToOffer = params.getOptional(QueryOptimizer.OptionKey.LIMIT, Integer.class).orElse(Integer.MAX_VALUE);
		
		final EclParser eclParser = context.service(EclParser.class);
		final LoadingCache<String, ExpressionConstraint> eclCache = CacheBuilder.newBuilder()
				.build(CacheLoader.from(eclParser::parse));
		
		// extract inclusions that reference a single concept ID only
		final Multimap<String, QueryExpression> singleConceptInclusions = FluentIterable.from(inclusions)
				.filter(ex -> isSingleConceptExpression(eclCache, ex.getQuery()))
				.index(ex -> toSingleConceptId(eclCache, ex.getQuery()));
		
		// if there are no single concept inclusions to optimize, exit early
		if (singleConceptInclusions.isEmpty()) {
			return QueryExpressionDiffs.EMPTY;
		}
		
		// Record the ancestors (both direct and indirect) of each single concept inclusion
		final Multimap<String, QueryExpression> membersByParent = HashMultimap.create();
//		final Multimap<String, QueryExpression> membersByAncestor = HashMultimap.create();
		
		Iterables.partition(singleConceptInclusions.keySet(), PAGE_SIZE).forEach(batchIds -> {
			SnomedRequests.prepareSearchConcept()
				.filterByIds(batchIds)
				.setLimit(batchIds.size())
				.stream(context)
				.flatMap(SnomedConcepts::stream)
				.forEach(child -> {
					final Collection<QueryExpression> childExpressions = singleConceptInclusions.get(child.getId());
					final List<String> parentIds = child.getParentIdsAsString();
//					final List<String> ancestorIds = child.getAncestorIdsAsString();
					
					parentIds.forEach(parentId -> {
						if (!IComponent.ROOT_ID.equals(parentId) && !Concepts.ROOT_CONCEPT.equals(parentId)) {
							membersByParent.putAll(parentId, childExpressions);
						}
					});
					
//					ancestorIds.forEach(ancestorId -> {
//						if (!IComponent.ROOT_ID.equals(ancestorId) && !Concepts.ROOT_CONCEPT.equals(ancestorId)) {
//							membersByAncestor.putAll(ancestorId, childExpressions);
//						}
//					});
				});
		});


		// Get number of referenced descendants (taking possible duplicates into account)
		final Map<String, Long> uniqueDescendantsByParent = ImmutableMap.copyOf(Maps.transformValues(membersByParent.asMap(),
				descendants -> descendants.stream().map(QueryExpression::getQuery).distinct().count()));
		
		final ImmutableList.Builder<QueryExpressionDiff> diffs = ImmutableList.builder();
		
		int numberOfOptimizationsFound = 0;
		// Retrieve descendant counts for parents; if the two numbers match, the single concept
		// references can be replaced with a single << expression.
		Iterator<Map.Entry<String,Long>> sortedByLargestDescendantCountFirst = uniqueDescendantsByParent.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).iterator();
		
		// end the loop when we don't have more entries to process or we have found the desired amount of optimizations
		while (sortedByLargestDescendantCountFirst.hasNext() && numberOfOptimizationsFound < numberOfOptimizationsToOffer) {
			Entry<String, Long> sortedByLargestDescendantCountFirstNextEntry = sortedByLargestDescendantCountFirst.next();
			final String parentId = sortedByLargestDescendantCountFirstNextEntry.getKey();
			final int referencedDescendants = Ints.checkedCast(uniqueDescendantsByParent.get(parentId));
			
			// optimize if at least two descendants are referenced for a given parent, otherwise skip
			if (referencedDescendants <= 1) {
				continue;
			}
			
			// optimization is a "net win" if we can remove at least two non-pinned clauses from the original (pinned clauses cannot be optimized)
			final List<QueryExpression> nonPinnedMembersForParent = List.copyOf(membersByParent.get(parentId)
					.stream()
					.filter(ex -> !ex.isPinned())
					.collect(Collectors.toList()));
			
			if (nonPinnedMembersForParent.size() <= 1) {
				continue;
			}
			
			var totalDescendants = SnomedRequests.prepareSearchConcept()
				.filterByActive(true)
				.filterByAncestor(parentId)
				.setLimit(0)
				.build()
				.execute(context)
				.getTotal();
			
			if (totalDescendants == referencedDescendants) {
				var label = locales.isEmpty() ? null : SnomedDisplayTermType.FSN.getLabel(SnomedRequests.prepareGetConcept(parentId)
						.setLocales(locales)
						.setExpand("fsn()")
						.build()
						.execute(context));
				
				final QueryExpression replacement = new QueryExpression(IDs.base62UUID(), String.format("<%s", Concept.toConceptString(parentId, label)), false);
				final List<QueryExpression> addToInclusion = List.of(replacement);
				final List<QueryExpression> addToExclusion = List.of();

				final QueryExpressionDiff diff = new QueryExpressionDiff(addToInclusion, addToExclusion, nonPinnedMembersForParent);
				diffs.add(diff);
				numberOfOptimizationsFound++;
			} else {
				// TODO support optimization of certain number of children is present in the VS and replace inclusions with <parent and add exclusion for the rest of the IDs
//				var ratio = ((double) referencedDescendants / totalDescendants) * 100;
//				System.err.println(String.format("Ratio of included children for ancestor '%s' is '%s' (included: '%s', missing: '%s', total: '%s')  ", parentId, ratio, referencedDescendants, totalDescendants - referencedDescendants, totalDescendants));
			}
		}
		
		// TODO process non-direct ancestors as well
		
		return new QueryExpressionDiffs(diffs.build(), sortedByLargestDescendantCountFirst.hasNext());
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
