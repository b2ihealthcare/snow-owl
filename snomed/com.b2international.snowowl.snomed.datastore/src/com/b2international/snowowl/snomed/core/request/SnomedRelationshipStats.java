/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.QueryExpression;
import com.b2international.snowowl.core.id.IDs;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

/**
 * @since 8.12.0
 */
public record SnomedRelationshipStats(
	Table<String, String, Integer> positiveSources, 
	Table<String, String, Integer> totalSources
) {

	/**
	 * @since 8.12.0
	 */
	@FunctionalInterface
	public interface RelationshipSearchBySource {

		RelationshipSearchBySource DEFAULT = (context, sourceIds, pageSize) -> SnomedRequests.prepareSearchRelationship()
			.filterByActive(true)
			.filterByCharacteristicType(Concepts.INFERRED_RELATIONSHIP)
			.filterBySources(sourceIds)
			.setLimit(pageSize)
			.setFields(
				SnomedRelationshipIndexEntry.Fields.ID, 
				SnomedRelationshipIndexEntry.Fields.SOURCE_ID, 
				SnomedRelationshipIndexEntry.Fields.TYPE_ID, 
				SnomedRelationshipIndexEntry.Fields.DESTINATION_ID,
				SnomedRelationshipIndexEntry.Fields.VALUE_TYPE,
				SnomedRelationshipIndexEntry.Fields.NUMERIC_VALUE,
				SnomedRelationshipIndexEntry.Fields.STRING_VALUE)
			.stream(context)
			.flatMap(SnomedRelationships::stream);

		Stream<SnomedRelationship> findRelationshipsBySource(BranchContext context, Set<String> sourceIds, int pageSize);
	}

	/**
	 * @since 8.12.0
	 */
	@FunctionalInterface
	public interface RelationshipSearchByTypeAndDestination {

		RelationshipSearchByTypeAndDestination DEFAULT = (context, typeIds, destinationIds, pageSize) -> SnomedRequests.prepareSearchRelationship()
			.filterByActive(true)
			.filterByCharacteristicType(Concepts.INFERRED_RELATIONSHIP)
			.filterByTypes(typeIds)
			.filterByDestinations(destinationIds)
			.setLimit(pageSize)
			.setFields(
				SnomedRelationshipIndexEntry.Fields.ID, 
				SnomedRelationshipIndexEntry.Fields.TYPE_ID, 
				SnomedRelationshipIndexEntry.Fields.DESTINATION_ID)
			.stream(context)
			.flatMap(SnomedRelationships::stream);

		Stream<SnomedRelationship> findRelationshipsByTypeAndDestination(BranchContext context, Set<String> typeIds, Set<String> destinationIds, int pageSize);
	}

	public static SnomedRelationshipStats create(
		final BranchContext context, 
		final int pageSize,
		final Set<String> conceptIds, 
		final RelationshipSearchBySource searchBySource, 
		final RelationshipSearchByTypeAndDestination searchByTypeAndDestination
	) {
		// How many member concepts are there for a particular type-destination pair?
		final Table<String, String, Integer> positiveSources = HashBasedTable.create();
		// How many source concepts are there in total for a particular type-destination pair?
		final Table<String, String, Integer> totalSources = HashBasedTable.create();
		
		if (conceptIds.isEmpty()) {
			// No input concepts
			return new SnomedRelationshipStats(positiveSources, totalSources);	
		}

		searchBySource.findRelationshipsBySource(context, conceptIds, pageSize)
			// Exclude IS_A relationships and relationship values
			.filter(r -> !r.hasValue() && !Concepts.IS_A.equals(r.getTypeId()))
			.forEachOrdered(r -> incrementTableCount(positiveSources, r));

		if (positiveSources.isEmpty()) {
			// No relevant relationships collected
			return new SnomedRelationshipStats(positiveSources, totalSources);
		}
		
		final Set<String> typeIds = positiveSources.rowKeySet();
		final Set<String> destinationIds = positiveSources.columnKeySet();

		searchByTypeAndDestination.findRelationshipsByTypeAndDestination(context, typeIds, destinationIds, pageSize)
			// XXX: This request may return any combination of the given type-destination pairs, which need to be filtered here
			.filter(r -> positiveSources.contains(r.getTypeId(), r.getDestinationId()))
			.forEachOrdered(r -> incrementTableCount(totalSources, r));

		return new SnomedRelationshipStats(positiveSources, totalSources);
	}

	private static void incrementTableCount(final Table<String, String, Integer> countByTypeAndDestination, final SnomedRelationship relationship) {
		final Map<String, Integer> countByDestination = countByTypeAndDestination.row(relationship.getTypeId());
		countByDestination.merge(relationship.getDestinationId(), 1, (oldCount, newCount) -> oldCount + newCount);
	}

	/**
	 * Excludes type-destination pair candidates where the precision (relevant
	 * concepts covered by a relationship refinement expression divided by the total
	 * number of concepts) is less than the specified threshold.
	 * 
	 * @param precisionThreshold the precision threshold (should be in the
	 * <code>0..1</code> range)
	 */
	public void filterByPrecision(final float precisionThreshold) {
		positiveSources.cellSet().removeIf(cell -> {
			final int truePositives = cell.getValue();
			final int total = totalSources.get(cell.getRowKey(), cell.getColumnKey());
			
			if (precisionThreshold >= 1.0f) {
				return truePositives < total;	
			} else {
				final float precision = ((float) truePositives) / total;
				return precision < precisionThreshold;
			}
		});
	}

	/**
	 * Excludes type-destination pair candidates where the number of <b>relevant</b> concepts
	 * covered by a relationship refinement expression is <b>less than</b> the specified value.
	 * 
	 * @param minTruePositives the lower bound for covered concepts (inclusive)
	 */
	public void filterByMinTruePositives(final int minTruePositives) {
		positiveSources.values().removeIf(truePositives -> {
			return truePositives < minTruePositives;
		});
	}

	/**
	 * Excludes type-destination pair candidates where the number of <b>irrelevant</b> concepts
	 * covered by a relationship refinement expression is <b>greater than</b> the specified value.
	 * 
	 * @param maxFalsePositives the upper bound for irrelevant concepts (inclusive)
	 */
	public void filterByMaxFalsePositives(final int maxFalsePositives) {
		positiveSources.cellSet().removeIf(cell -> {
			final int truePositives = cell.getValue();
			final int total = totalSources.get(cell.getRowKey(), cell.getColumnKey());
			final int falsePositives = total - truePositives;
			return falsePositives > maxFalsePositives;
		});
	}

	/**
	 * Converts any remaining type-destination pair candidates (after calling filter
	 * methods) to relationship refinement query expressions.
	 * 
	 * @return a list of converted query expressions in the form of
	 * "<code>* : ${typeId} = ${destinationId}</code>"
	 */
	public List<QueryExpression> optimizeRefinements() {
		if (positiveSources.isEmpty()) {
			return List.of();
		}

		return positiveSources.cellSet()
			.stream()
			.map(cell -> new QueryExpression(IDs.base62UUID(), String.format("* : %s = %s", cell.getRowKey(), cell.getColumnKey()), false))
			.collect(Collectors.toList());
	}
}
