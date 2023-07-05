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

import static com.google.common.collect.Sets.newHashSet;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.b2international.collections.PrimitiveMaps;
import com.b2international.collections.longs.LongKeyFloatMap;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.domain.QueryExpression;
import com.b2international.snowowl.core.id.IDs;
import com.b2international.snowowl.core.taxonomy.SimpleTaxonomyGraph;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.core.request.SnomedQueryOptimizer.OptimizerStrategy;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multiset;

/**
 * @since 8.11.0
 */
public record SnomedHierarchyStats(
	SimpleTaxonomyGraph graph,
	Multiset<String> positiveDescendantsAndSelf,
	Multiset<String> positiveChildren,
	Multiset<String> totalDescendantsAndSelf,
	Multiset<String> totalChildren
) { 

	@FunctionalInterface
	public interface ConceptSearchById {

		ConceptSearchById DEFAULT = (context, conceptIds) -> SnomedRequests.prepareSearchConcept()
			.filterByActive(true)
			.filterByIds(conceptIds)
			.setLimit(SnomedQueryOptimizer.PAGE_SIZE)
			.setFields(
				SnomedConceptDocument.Fields.ID, 
				SnomedConceptDocument.Fields.PARENTS, 
				SnomedConceptDocument.Fields.ANCESTORS)
			.stream(context)
			.flatMap(SnomedConcepts::stream);

		Stream<SnomedConcept> findConceptsById(BranchContext context, Set<String> conceptIds);
	}

	@FunctionalInterface
	public interface ConceptDescendantCountById {

		ConceptDescendantCountById DEFAULT = (context, conceptIds, direct) -> SnomedRequests.prepareSearchConcept()
			.filterByActive(true)
			.filterByIds(conceptIds)
			.setLimit(SnomedQueryOptimizer.PAGE_SIZE)
			.setFields(SnomedConceptDocument.Fields.ID)
			.setExpand(String.format("descendants(direct:%b, limit:0)", direct))
			.stream(context)
			.flatMap(SnomedConcepts::stream);

		Stream<SnomedConcept> findConceptDescendantCountById(BranchContext context, Set<String> conceptIds, boolean direct);
	}

	@FunctionalInterface
	public interface EdgeSearchBySourceId {

		EdgeSearchBySourceId DEFAULT = (context, sourceIds) -> SnomedRequests.prepareSearchRelationship()
			.filterByActive(true)
			.filterByCharacteristicType(Concepts.INFERRED_RELATIONSHIP)
			.filterBySources(sourceIds)
			.filterByType(Concepts.IS_A)
			.setLimit(SnomedQueryOptimizer.PAGE_SIZE)
			.setFields(
				SnomedRelationshipIndexEntry.Fields.ID, 
				SnomedRelationshipIndexEntry.Fields.SOURCE_ID, 
				SnomedRelationshipIndexEntry.Fields.DESTINATION_ID)
			.stream(context)
			.flatMap(SnomedRelationships::stream);

		Stream<SnomedRelationship> findEdgesBySourceId(BranchContext context, Set<String> sourceIds);
	}

	public static SnomedHierarchyStats create(
		final BranchContext context, 
		final Set<String> conceptIds,
		final ConceptSearchById conceptSearchById,
		final EdgeSearchBySourceId edgeSearchBySourceId,
		final ConceptDescendantCountById conceptDescendantCountById
	) {
		// How many member concepts do we find if we use this ancestor in a descendant-of or descendant-or-self-of expression?
		final Multiset<String> positiveDescendantsAndSelf = HashMultiset.create();
		// The same counter, but restricted to direct children only (at the moment this only guides optimization)
		final Multiset<String> positiveChildren = HashMultiset.create();
		// How many descendants does each ancestor have in total (relevant and irrelevant ones)?
		final Multiset<String> totalDescendantsAndSelf = HashMultiset.create();
		// How many direct children does each ancestor have in total (relevant and irrelevant ones)?
		final Multiset<String> totalChildren = HashMultiset.create();

		if (conceptIds.isEmpty()) {
			// No input concepts
			final SimpleTaxonomyGraph emptyGraph = new SimpleTaxonomyGraph(1, 1);
			emptyGraph.build();
			
			return new SnomedHierarchyStats(
				emptyGraph, 
				positiveDescendantsAndSelf, 
				positiveChildren, 
				totalDescendantsAndSelf, 
				totalChildren);	
		}

		conceptSearchById.findConceptsById(context, conceptIds)
			.forEachOrdered(c -> {
				positiveDescendantsAndSelf.add(c.getId());
	
				for (final String parentId : c.getParentIdsAsString()) {
					positiveDescendantsAndSelf.add(parentId);
					positiveChildren.add(parentId);
				}
	
				for (final String ancestorId : c.getAncestorIdsAsString()) {
					positiveDescendantsAndSelf.add(ancestorId);
				}
			});

		// Remove mentions of the artificial root "-1"
		positiveDescendantsAndSelf.elementSet().remove(IComponent.ROOT_ID);
		positiveChildren.elementSet().remove(IComponent.ROOT_ID);

		final Set<String> conceptsAndAncestors = positiveDescendantsAndSelf.elementSet();
		final SimpleTaxonomyGraph graph = new SimpleTaxonomyGraph(conceptsAndAncestors.size(), conceptsAndAncestors.size());
		conceptsAndAncestors.forEach(graph::addNode);

		edgeSearchBySourceId.findEdgesBySourceId(context, conceptsAndAncestors)
			.forEachOrdered(r -> graph.addEdge(r.getSourceId(), r.getDestinationId()));

		graph.build();

		// +1 is added to the total descendants count for the "and self" part
		conceptDescendantCountById.findConceptDescendantCountById(context, conceptsAndAncestors, false)
			.forEachOrdered(c -> totalDescendantsAndSelf.setCount(c.getId(), c.getDescendants().getTotal() + 1));

		// No +1 for the total children counter however
		conceptDescendantCountById.findConceptDescendantCountById(context, conceptsAndAncestors, true)
			.forEachOrdered(c -> totalChildren.setCount(c.getId(), c.getDescendants().getTotal()));

		return new SnomedHierarchyStats(
			graph, 
			positiveDescendantsAndSelf, 
			positiveChildren, 
			totalDescendantsAndSelf, 
			totalChildren);
	}

	public Set<String> conceptsAndAncestors() {
		return ImmutableSet.copyOf(positiveDescendantsAndSelf.elementSet());
	}

	public void filterByClusterSize(final int minimumClusterSize) {
		positiveDescendantsAndSelf.entrySet().removeIf(e -> {
			final int truePositives = e.getCount();
			return truePositives < minimumClusterSize;	
		});
	}

	public void filterSingleChildMember() {
		positiveDescendantsAndSelf.entrySet().removeIf(e -> {
			final int truePositives = e.getCount();
			final int total = totalDescendantsAndSelf.count(e.getElement());
			// "truePositives" would be 2 if the concept itself is also a member
			return total == 2 && truePositives == 1;
		});
	}

	public void filterByChildren() {
		positiveDescendantsAndSelf.entrySet().removeIf(e -> {
			final int truePositives = positiveChildren.count(e.getElement());
			final int total = totalChildren.count(e.getElement());
			final int falsePositives = total - truePositives;
			return falsePositives > truePositives;
		});
	}
	
	public void filterByChildrenAndPrecision(final int childLimit, final float precisionThreshold) {
		positiveDescendantsAndSelf.entrySet().removeIf(e -> {
			final int trueposPositives = positiveChildren.count(e.getElement());
			final int total = totalChildren.count(e.getElement());
			final float precision = ((float) trueposPositives) / total;
			return total > childLimit && precision < precisionThreshold;
		});
	}

	public void filterRedundantNoFalsePositives(final Set<String> memberIds) {
		final Set<String> allAncestors = conceptsAndAncestors();
		final Set<String> redundantIds = newHashSet();

		for (String conceptId1 : allAncestors) {
			for (String conceptId2 : allAncestors) {
				if (conceptId1.equals(conceptId2)) { continue; }

				final int truePositives1 = positiveDescendantsAndSelf.count(conceptId1); 
				final int total1 = totalDescendantsAndSelf.count(conceptId1);
				final int falsePositives1 = total1 - truePositives1;

				final int truePositives2 = positiveDescendantsAndSelf.count(conceptId2); 
				final int total2 = totalDescendantsAndSelf.count(conceptId2);
				final int falsePositives2 = total2 - truePositives2;

				if (falsePositives1 == 0 
					&& falsePositives2 == (memberIds.contains(conceptId2) ? 0 : 1)
					&& graph.subsumes(conceptId1, conceptId2)) {

					redundantIds.add(conceptId1);
				}
			}
		}

		removeCandidates(redundantIds);
		redundantIds.clear();
	}

	public void removeCandidates(final Set<String> conceptIds) {
		// Remove counters from the first Multimap...
		final Set<String> positiveElementSet = positiveDescendantsAndSelf.elementSet();
		positiveElementSet.removeAll(conceptIds);

		// ...then retain the same set of keys across all Multimaps
		totalDescendantsAndSelf.elementSet().retainAll(positiveElementSet);
		positiveChildren.elementSet().retainAll(positiveElementSet);
		totalChildren.elementSet().retainAll(positiveElementSet);
	}

	public List<QueryExpression> optimizeNoFalsePositives(final BranchContext context, final Set<String> memberIds) {
		return optimizeNoFalsePositives(context, memberIds, 0.0f);
	}

	public List<QueryExpression> optimizeNoFalsePositives(final BranchContext context, final Set<String> memberIds, final float falsePositiveThreshold) {
		final Set<String> noFalsePositives = positiveDescendantsAndSelf.entrySet()
			.stream()
			.filter(e -> {
				final String conceptId = e.getElement();
				final int truePositives = e.getCount();
				final int total = totalDescendantsAndSelf.count(conceptId);
				final int falsePositives = total - truePositives; 

				// Accept some false positives if allowed
				if (falsePositiveThreshold > 0.0f) {
					final float falsePositiveRate = ((float) falsePositives) / total;
					if (falsePositiveRate < falsePositiveThreshold) {
						return true;
					}
				}

				// Otherwise check if the concept is the only false positive (if not a member), or there are no false positives at all
				final boolean member = memberIds.contains(conceptId);
				return falsePositives == (member ? 0 : 1);
			})
			.map(e -> e.getElement())
			.collect(Collectors.toSet());

		// Remove selected ancestors from future consideration
		removeCandidates(noFalsePositives);

		return noFalsePositives.stream()
			.map(ancestorId -> {
				final boolean member = memberIds.contains(ancestorId);
				final String operator = member ? "<<" : "<";
				return new QueryExpression(IDs.base62UUID(), String.format("%s %s", operator, ancestorId), false);
			})
			.collect(Collectors.toList());
	}

	public LongKeyFloatMap initAncestorScores(
		final BranchContext context,
		final Set<String> conceptSet,
		final OptimizerStrategy optimizerStrategy,
		final int zoom,
		final double clauseCountWeighting
	) {
		final int conceptCount = positiveDescendantsAndSelf.elementSet().size();
		final LongKeyFloatMap ancestorScore = PrimitiveMaps.newLongKeyFloatOpenHashMapWithExpectedSize(conceptCount);

		positiveDescendantsAndSelf.entrySet().forEach(e -> {
			final String id = e.getElement();

			final int truePositives = e.getCount();
			final int total = totalDescendantsAndSelf.count(id);
			float precision = ((float) truePositives) / total;

			final int truePositiveChildren = positiveChildren.count(id); 
			final int allChildren = totalChildren.count(id); 
			final float childPrecision = ((float) truePositiveChildren) / allChildren;

			// Apply boost based on the currently selected strategy
			precision = optimizerStrategy.adjustScore(precision, allChildren, childPrecision);

			// Slightly lower the rating of non-member ancestors
			if (!conceptSet.contains(id)) {
				precision *= 0.95f;
			}

			if (precision > 0.0f && precision < 0.7f) {
				/*
				 * How many clauses would we get for the original concept set if we assume that
				 * all clauses cover the number of concepts taken care of by this ancestor?
				 */
				final float numClauses = ((float) conceptSet.size()) / truePositives;
				
				// Reflect this information in the ancestor score with exponential weighting
				precision *= ((float) Math.pow(1.0 / Math.abs(numClauses - zoom + 1), clauseCountWeighting));
			}

			final long idAsLong = Long.parseLong(id);
			ancestorScore.put(idAsLong, precision);
		});

		return ancestorScore;
	}

	public boolean subsumes(final String conceptA, final String conceptB) {
		return graph.subsumes(conceptA, conceptB);
	}
}
