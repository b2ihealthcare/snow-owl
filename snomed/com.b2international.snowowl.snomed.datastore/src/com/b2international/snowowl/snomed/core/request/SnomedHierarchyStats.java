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
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

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

		// How many descendants does each ancestor have in total (relevant and irrelevant ones)?
		final Multiset<String> totalDescendantsAndSelf = HashMultiset.create();

		// +1 is added to the descendants count for the "and self" part
		conceptDescendantCountById.findConceptDescendantCountById(context, positiveDescendantsAndSelf.elementSet(), false)
			.forEachOrdered(c -> totalDescendantsAndSelf.setCount(c.getId(), c.getDescendants().getTotal() + 1));

		final Multiset<String> totalChildren = HashMultiset.create();

		// No +1 for the child concept counter
		conceptDescendantCountById.findConceptDescendantCountById(context, positiveChildren.elementSet(), true)
			.forEachOrdered(c -> totalChildren.setCount(c.getId(), c.getDescendants().getTotal()));

		return new SnomedHierarchyStats(
			graph, 
			positiveDescendantsAndSelf, 
			positiveChildren, 
			totalDescendantsAndSelf, 
			totalChildren);
	}

	public Set<String> getAllAncestors() {
		return totalDescendantsAndSelf.elementSet();
	}

	public void filterAncestorsForInclusion(
		final int minimumClusterSize, 
		final Set<String> conceptIds, 
		final OptimizerStrategy optimizerStrategy, 
		final float falsePositiveThreshold
	) {
		final Set<Entry<String>> positiveEntrySet = positiveDescendantsAndSelf.entrySet();

		filterByClusterSize(minimumClusterSize, positiveEntrySet);
		filterSingleChildren(positiveEntrySet);

		if (!OptimizerStrategy.LOSSY.equals(optimizerStrategy) || falsePositiveThreshold < 0.75f) {
			filterByChildren(positiveEntrySet);
			filterByChildrenAndScore(positiveEntrySet);
		}

		filterRedundantNoFalsePositives(conceptIds, positiveEntrySet);
	}

	public void filterAncestorsForExclusion(
		final Set<String> inclusionAncestors, 
		final int minimumClusterSize, 
		final Set<String> conceptIds
	) {
		// No ancestor of an included concept can be used for exclusions -- it would remove at least one concept we want to keep in the set. 
		removeCandidates(inclusionAncestors);

		final Set<Entry<String>> positiveEntrySet = positiveDescendantsAndSelf.entrySet();

		filterByClusterSize(minimumClusterSize, positiveEntrySet);
		filterSingleChildren(positiveEntrySet);
		filterByChildren(positiveEntrySet);
		filterRedundantNoFalsePositives(conceptIds, positiveEntrySet);
	}

	// a) ancestor does not cover the minimum required members of the set
	private void filterByClusterSize(final int minimumClusterSize, final Set<Entry<String>> positiveEntrySet) {
		positiveEntrySet.removeIf(e -> {
			final int truepos = e.getCount();
			return truepos < minimumClusterSize;	
		});
	}

	// b) a non-member with a single member as its child -- there is no reason to replace "=child" with "<parent" in this case
	private void filterSingleChildren(final Set<Entry<String>> positiveEntrySet) {
		positiveEntrySet.removeIf(e -> {
			final int truepos = e.getCount();
			final int children = totalChildren.count(e.getElement());
			// "truepos" would be 2 if the concept itself is also a member
			return children == 1 && truepos == 1;
		});
	}

	// c) more false positive children than true positive children
	private void filterByChildren(final Set<Entry<String>> positiveEntrySet) {
		positiveEntrySet.removeIf(e -> {
			final int total = totalChildren.count(e.getElement());
			final int truepos = positiveChildren.count(e.getElement());
			final int falsepos = total - truepos;
			return falsepos > truepos;
		});
	}

	// d) 10 or more children but less than 60% precision across children
	private void filterByChildrenAndScore(final Set<Entry<String>> positiveEntrySet) {
		positiveEntrySet.removeIf(e -> {
			final int total = totalChildren.count(e.getElement());
			final int truepos = positiveChildren.count(e.getElement());
			final float score = ((float) truepos) / total;
			return total > 9 && score < 0.6f;
		});
	}

	// e) ancestor has no false positives, but it has an _ancestor_ which has no false positives either (or at most 1 and is a non-member)
	private void filterRedundantNoFalsePositives(final Set<String> conceptIds, final Set<Entry<String>> positiveEntrySet) {
		@SuppressWarnings("unchecked")
		final Entry<String>[] positiveArray = positiveEntrySet.toArray(Entry[]::new);
		final Set<String> redundantIds = newHashSet();

		for (int i = 0; i < positiveArray.length; i++) {
			for (int j = 0; j < positiveArray.length; j++) {
				if (i == j) { continue; }

				final Entry<String> pos1 = positiveArray[i];
				final String conceptId1 = pos1.getElement();
				final int truepos1 = pos1.getCount(); 
				final int total1 = totalDescendantsAndSelf.count(conceptId1);
				final int trueneg1 = total1 - truepos1;

				final Entry<String> pos2 = positiveArray[j];
				final String conceptId2 = pos2.getElement();
				final int truepos2 = pos2.getCount(); 
				final int total2 = totalDescendantsAndSelf.count(conceptId2);
				final int trueneg2 = total2 - truepos2;

				if (trueneg1 == 0 && (trueneg2 == 0 || (trueneg2 == 1 && !conceptIds.contains(conceptId2)))) {
					if (graph.subsumes(conceptId1, conceptId2)) {
						redundantIds.add(conceptId1);
					}
				}
			}
		}

		removeCandidates(redundantIds);
		redundantIds.clear();
	}

	public List<QueryExpression> optimizeNoFalsePositives(
		final BranchContext context,
		final Set<String> conceptIds, 
		final OptimizerStrategy optimizerStrategy, 
		final float falsePositiveThreshold
	) {
		final Set<String> noFalsePositives = positiveDescendantsAndSelf.entrySet()
			.stream()
			.filter(e -> {
				final String conceptId = e.getElement();
				final int truepos = e.getCount();
				final int total = totalDescendantsAndSelf.count(conceptId);
				final int falsepos = total - truepos; 

				// Accept some false positives if lossy optimizer is enabled
				if (OptimizerStrategy.LOSSY.equals(optimizerStrategy)) {
					final float ratio = ((float) falsepos) / total;
					if (ratio < falsePositiveThreshold) {
						return true;
					}
				}

				final boolean member = conceptIds.contains(conceptId);
				return falsepos == (member ? 0 : 1);
			})
			.map(e -> e.getElement())
			.collect(Collectors.toSet());

		// Remove selected ancestors from future consideration
		removeCandidates(noFalsePositives);

		return noFalsePositives.stream()
			.map(ancestorId -> {
				final boolean member = conceptIds.contains(ancestorId);
				final String operator = member ? "<<" : "<";
				return new QueryExpression(IDs.base62UUID(), String.format("%s %s", operator, ancestorId), false);
			})
			.toList();
	}

	public List<QueryExpression> optimizeNoFalsePositives(
		final BranchContext context, 
		final Set<String> conceptIds
	) {
		return optimizeNoFalsePositives(context, conceptIds, OptimizerStrategy.DEFAULT, 0.0f);
	}

	private void removeCandidates(final Set<String> conceptIds) {
		// Remove counters from the first Multimap...
		final Set<String> positiveElementSet = positiveDescendantsAndSelf.elementSet();
		positiveElementSet.removeAll(conceptIds);

		// ...then retain the same set of keys across all Multimaps
		positiveChildren.elementSet().retainAll(positiveElementSet);
		totalChildren.elementSet().retainAll(positiveElementSet);
		totalDescendantsAndSelf.elementSet().retainAll(positiveElementSet);
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
			final long idAsLong = Long.parseLong(id);

			final int truepos = e.getCount();
			final int total = totalDescendantsAndSelf.count(id);
			float score = ((float) truepos) / total;

			final int truechildren = positiveChildren.count(id); 
			final int allchildren = totalChildren.count(id); 
			final float childScore = ((float) truechildren) / allchildren;

			switch (optimizerStrategy) {
			case DEFAULT:
				// Nothing to do
				break;

			case SCORE_BOOST_1:
				// Boost scores with the fraction of the precision of direct children, for selected candidates
				if (score < 0.4f && allchildren > 6 && childScore > 0.8f) {
					score += childScore / 2.0f;
				}
				break;

			case SCORE_BOOST_2:
				// Boost scores with the precision of direct children for all candidates
				score += childScore;
				break;

			case LOSSY:
				// Nothing to do here either
				break;

			default:
				throw new IllegalArgumentException("Unexpected optimizer strategy: " + optimizerStrategy);
			}

			// Slightly lower the rating of non-member ancestors
			if (!conceptSet.contains(id)) {
				score *= 0.95f;
			}

			// How many clauses would we get for the original concept set if we take the coverage of this ancestor?
			if (score > 0.0f && score < 0.7f) {
				final float numClauses = ((float) conceptSet.size()) / truepos;
				score *= Math.pow(1.0 / Math.abs(numClauses - zoom + 1), clauseCountWeighting);
			}

			ancestorScore.put(idAsLong, score);
		});

		return ancestorScore;
	}

	public boolean subsumes(final String conceptA, final String conceptB) {
		return graph.subsumes(conceptA, conceptB);
	}
}
