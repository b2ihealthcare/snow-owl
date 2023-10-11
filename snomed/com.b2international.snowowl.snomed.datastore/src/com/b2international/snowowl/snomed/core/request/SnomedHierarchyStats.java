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
 * @since 8.12.0
 */
public record SnomedHierarchyStats(
	SimpleTaxonomyGraph graph,
	Multiset<String> positiveDescendantsAndSelf,
	Multiset<String> positiveChildren,
	Multiset<String> totalDescendantsAndSelf,
	Multiset<String> totalChildren
) { 

	private static final String CLINICAL_FINDING = "404684003";

	/**
	 * @since 8.12.0
	 */
	@FunctionalInterface
	public interface ConceptSearchById {

		ConceptSearchById DEFAULT = (context, conceptIds, pageSize) -> SnomedRequests.prepareSearchConcept()
			.filterByActive(true)
			.filterByIds(conceptIds)
			.setLimit(pageSize)
			.setFields(
				SnomedConceptDocument.Fields.ID, 
				SnomedConceptDocument.Fields.PARENTS, 
				SnomedConceptDocument.Fields.ANCESTORS)
			.stream(context)
			.flatMap(SnomedConcepts::stream);

		Stream<SnomedConcept> findConceptsById(BranchContext context, Set<String> conceptIds, int pageSize);
	}

	/**
	 * @since 8.12.0
	 */
	@FunctionalInterface
	public interface ConceptDescendantCountById {

		ConceptDescendantCountById DEFAULT = (context, conceptIds, direct, pageSize) -> SnomedRequests.prepareSearchConcept()
			.filterByActive(true)
			.filterByIds(conceptIds)
			.setLimit(pageSize)
			.setFields(SnomedConceptDocument.Fields.ID)
			.setExpand(String.format("descendants(direct:%b, limit:0)", direct))
			.stream(context)
			.flatMap(SnomedConcepts::stream);

		Stream<SnomedConcept> findConceptDescendantCountById(BranchContext context, Set<String> conceptIds, boolean direct, int pageSize);
	}

	/**
	 * @since 8.12.0
	 */
	@FunctionalInterface
	public interface EdgeSearchBySourceId {

		EdgeSearchBySourceId DEFAULT = (context, sourceIds, pageSize) -> SnomedRequests.prepareSearchRelationship()
			.filterByActive(true)
			.filterByCharacteristicType(Concepts.INFERRED_RELATIONSHIP)
			.filterBySources(sourceIds)
			.filterByType(Concepts.IS_A)
			.setLimit(pageSize)
			.setFields(
				SnomedRelationshipIndexEntry.Fields.ID, 
				SnomedRelationshipIndexEntry.Fields.SOURCE_ID, 
				SnomedRelationshipIndexEntry.Fields.DESTINATION_ID)
			.stream(context)
			.flatMap(SnomedRelationships::stream);

		Stream<SnomedRelationship> findEdgesBySourceId(BranchContext context, Set<String> sourceIds, int pageSize);
	}

	public static SnomedHierarchyStats create(
		final BranchContext context, 
		final int pageSize,
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

		conceptSearchById.findConceptsById(context, conceptIds, pageSize)
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

		edgeSearchBySourceId.findEdgesBySourceId(context, conceptsAndAncestors, pageSize)
			.forEachOrdered(r -> graph.addEdge(r.getId(), r.getSourceId(), r.getDestinationId()));

		graph.build();

		/*
		 * Remove "SNOMED CT Concept" and "Clinical finding" as it is unlikely that they
		 * will turn out to be good replacements (and computing descendant counts for
		 * them is costly)
		 */
		positiveDescendantsAndSelf.elementSet().remove(Concepts.ROOT_CONCEPT);
		positiveChildren.elementSet().remove(Concepts.ROOT_CONCEPT);
		
		positiveDescendantsAndSelf.elementSet().remove(CLINICAL_FINDING);
		positiveChildren.elementSet().remove(CLINICAL_FINDING);
		
		// +1 is added to the total descendants count for the "and self" part
		conceptDescendantCountById.findConceptDescendantCountById(context, conceptsAndAncestors, false, pageSize)
			.forEachOrdered(c -> totalDescendantsAndSelf.setCount(c.getId(), c.getDescendants().getTotal() + 1));

		// No +1 for the total children counter however
		conceptDescendantCountById.findConceptDescendantCountById(context, conceptsAndAncestors, true, pageSize)
			.forEachOrdered(c -> totalChildren.setCount(c.getId(), c.getDescendants().getTotal()));

		return new SnomedHierarchyStats(
			graph, 
			positiveDescendantsAndSelf, 
			positiveChildren, 
			totalDescendantsAndSelf, 
			totalChildren);
	}

	/**
	 * @return an {@link ImmutableSet} containing all known ancestor candidates
	 * (including the members of the evaluated set)
	 */
	public Set<String> conceptsAndAncestors() {
		return ImmutableSet.copyOf(positiveDescendantsAndSelf.elementSet());
	}

	/**
	 * Excludes ancestor candidates where the number of <b>relevant</b> descendants
	 * is <b>less than</b> the specified value.
	 * 
	 * @param minimumClusterSize the minimum number of concepts to cover (inclusive)
	 */
	public void filterByClusterSize(final int minimumClusterSize) {
		positiveDescendantsAndSelf.entrySet().removeIf(e -> {
			final int truePositives = e.getCount();
			return truePositives < minimumClusterSize;	
		});
	}

	/**
	 * Excludes ancestor candidates that have a single child and either the parent
	 * or the child is a non-member.
	 * <p>
	 * <ul>
	 * <li>
	 * If the parent is irrelevant, then it does not matter whether we use
	 * "<code>&lt;&nbsp;${parentId}</code>" or "<code>=&nbsp;${childId}</code>" for
	 * the inclusion expression, and the former is slightly more complex;
	 * </li>
	 * <li>
	 * If the child is irrelevant, an inclusion of
	 * "<code>&lt;&lt;&nbsp;${parentId}</code>" would also need an exclusion of
	 * "<code>=&nbsp;${childId}</code>". Just as above,
	 * "<code>=&nbsp;${parentId}</code>" is the more reasonable choice in these
	 * cases.
	 * </li>
	 * </ul>
	 */
	public void filterSingleChildMember() {
		positiveDescendantsAndSelf.entrySet().removeIf(e -> {
			final int truePositives = e.getCount();
			final int total = totalDescendantsAndSelf.count(e.getElement());
			// "truePositives" would be 2 if the concept itself is also a member
			return total == 2 && truePositives == 1;
		});
	}

	/**
	 * Excludes ancestor candidates that have more non-member children than
	 * relevant ones.
	 */
	public void filterByChildren() {
		positiveDescendantsAndSelf.entrySet().removeIf(e -> {
			final int truePositives = positiveChildren.count(e.getElement());
			final int total = totalChildren.count(e.getElement());
			final int falsePositives = total - truePositives;
			return falsePositives > truePositives;
		});
	}
	
	/**
	 * Excludes ancestor candidates that have more children than the specified limit
	 * and the fraction of relevant children is lower than the given threshold.
	 * 
	 * @param childLimit the number of children above which ancestors are
	 * considered for exclusion (exclusive)
	 * @param precisionThreshold the precision threshold below which ancestors are
	 * excluded from the candidate set (inclusive)
	 */
	public void filterByChildrenAndPrecision(final int childLimit, final float precisionThreshold) {
		positiveDescendantsAndSelf.entrySet().removeIf(e -> {
			final int trueposPositives = positiveChildren.count(e.getElement());
			final int total = totalChildren.count(e.getElement());
			final float precision = ((float) trueposPositives) / total;
			return total > childLimit && precision < precisionThreshold;
		});
	}

	/**
	 * Checks all ancestor candidates in a pairwise fashion and eliminates the ones
	 * which:
	 * 
	 * <ul>
	 * <li>Do not have any false positives whatsoever, ie. all of their descendants
	 * are relevant (a single false positive is permitted if the candidate itself is
	 * not a member of the concept set)
	 * <li>One of the ancestors is a descendant of the other
	 * </ul>
	 * 
	 * <p>
	 * Information about filtered candidates is removed from descendant and child
	 * counting {@link Multiset}s, but not the taxonomy graph.
	 * </p>
	 * 
	 * @param memberIds the {@link Set} of concept IDs that are members of the
	 * evaluated concept set
	 */
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

	/**
	 * Removes descendant and child counters for the specified ancestors. The
	 * taxonomy graph is not modified.
	 * 
	 * @param conceptIds the {@link Set} of ancestors to remove from the candidate pool
	 */
	public void removeCandidates(final Set<String> conceptIds) {
		// Remove counters from the first Multimap...
		final Set<String> positiveElementSet = positiveDescendantsAndSelf.elementSet();
		positiveElementSet.removeAll(conceptIds);

		// ...then retain the same set of keys across all other Multimaps
		totalDescendantsAndSelf.elementSet().retainAll(positiveElementSet);
		positiveChildren.elementSet().retainAll(positiveElementSet);
		totalChildren.elementSet().retainAll(positiveElementSet);
	}

	/**
	 * Converts ancestor candidates who have 100% precision to
	 * <code>"&lt;/&lt;&lt; ${ancestorId}"</code> expressions. The operator to use
	 * is determined by whether the ancestor itself is a member of the concept set
	 * or not.
	 * <p>
	 * This method is used for collecting expressions for exclusion, as they can not
	 * have any false positives at all.
	 * </p>
	 * 
	 * @param memberIds the {@link Set} of concept IDs that are members of the
	 * evaluated concept set
	 * @return a {@link List} of resulting {@link QueryExpression}s
	 */
	public List<QueryExpression> optimizeNoFalsePositives(final Set<String> memberIds) {
		return optimizeNoFalsePositives(memberIds, 0.0f);
	}

	/**
	 * Converts ancestor candidates who have a false positive rate below the
	 * specified threshold to <code>"&lt;/&lt;&lt; ${ancestorId}"</code>
	 * expressions. The operator to use is determined by whether the ancestor itself
	 * is a member of the concept set or not.
	 * <p>
	 * This method is used for collecting expressions for inclusion.
	 * <code>falsePositivesThreshold</code> is only set to a non-zero value when
	 * using the lossy optimization strategy; see
	 * {@link SnomedQueryOptimizer.OptimizerStrategy#LOSSY}.
	 * </p>
	 * 
	 * @param memberIds the {@link Set} of concept IDs that are members
	 * of the evaluated concept set
	 * @param falsePositiveThreshold the fraction of allowed false positives (should
	 * be in the <code>0..1</code> range)
	 * @return a {@link List} of resulting {@link QueryExpression}s
	 */
	public List<QueryExpression> optimizeNoFalsePositives(final Set<String> memberIds, final float falsePositiveThreshold) {
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

		return noFalsePositives.stream()
			.map(ancestorId -> {
				final boolean member = memberIds.contains(ancestorId);
				final String operator = member ? "<<" : "<";
				return new QueryExpression(IDs.base62UUID(), String.format("%s %s", operator, ancestorId), false);
			})
			.collect(Collectors.toList());
	}

	/**
	 * Computes a fitness metric for each candidate ancestor.
	 * 
	 * @param conceptSet the original set of concept IDs that are members
	 * @param optimizerStrategy the strategy to be used for adjusting ancestor scoring
	 * @param targetClauses the currently selected target clause count
	 * @param clauseCountWeighting the exponent for the ideal clause count weighting function
	 * @return fitness scores keyed by candidate ancestor ID
	 */
	public LongKeyFloatMap computeAncestorScores(
		final Set<String> conceptSet,
		final OptimizerStrategy optimizerStrategy,
		final int targetClauses,
		final double clauseCountWeighting
	) {
		final int conceptCount = positiveDescendantsAndSelf.elementSet().size();
		final LongKeyFloatMap ancestorScore = PrimitiveMaps.newLongKeyFloatOpenHashMapWithExpectedSize(conceptCount);

		positiveDescendantsAndSelf.entrySet().forEach(e -> {
			final String id = e.getElement();

			final int truePositives = e.getCount();
			final int total = totalDescendantsAndSelf.count(id);
			
			// The initial score is determined by "descendant precision" -- what fraction of the covered descendants is relevant?
			float score = ((float) truePositives) / total;

			// Apply boost based on the currently selected optimizer strategy
			final int truePositiveChildren = positiveChildren.count(id); 
			final int allChildren = totalChildren.count(id); 
			final float childPrecision = ((float) truePositiveChildren) / allChildren;
			score = optimizerStrategy.adjustScore(score, allChildren, childPrecision);

			// Slightly lower the rating of non-member ancestors
			if (!conceptSet.contains(id)) {
				score *= 0.95f;
			}

			if (score > 0.0f && score < 0.7f) {
				/*
				 * How many clauses would we get if we assume that all clauses cover the same
				 * number of concepts as this ancestor?
				 */
				final float numClauses = ((float) conceptSet.size()) / truePositives;
				final float differenceFromTarget = Math.abs(numClauses - targetClauses + 1);
				
				// Reflect this information in the ancestor score with exponential weighting
				score *= ((float) Math.pow(1.0 / differenceFromTarget, clauseCountWeighting));
			}

			final long idAsLong = Long.parseLong(id);
			ancestorScore.put(idAsLong, score);
		});

		return ancestorScore;
	}

	/**
	 * Checks whether the first concept is a descendant of the second concept using
	 * information in the collected taxonomy graph. As a result, this method only
	 * knows about concepts that were considered as ancestors at the creation of
	 * this instance.
	 * 
	 * @param conceptA the ID of the first concept to check
	 * @param conceptB the ID of the second conept to check
	 * @return <code>true</code> if conceptA is subsumed by conceptB,
	 * <code>false</code> otherwise
	 */
	public boolean subsumes(final String conceptA, final String conceptB) {
		return graph.subsumes(conceptA, conceptB);
	}
}
