/*
 * Copyright 2022-2024 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.taxonomy;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.IntConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.collections.PrimitiveLists;
import com.b2international.collections.ints.IntDeque;
import com.b2international.commons.CompareUtils;
import com.b2international.commons.collections.OrderedSetImpl;
import com.b2international.commons.collections.SparseFixedBitSet;
import com.b2international.commons.exceptions.NotFoundException;
import com.google.common.collect.ImmutableSet;

/**
 * @since 8.0.1
 */
public final class SimpleTaxonomyGraph {

	private static final Logger LOGGER = LoggerFactory.getLogger("taxonomy-issues");
	
	private static final int MAX_ISSUES = 100;
	
	public interface Issue {
		@Override
		public String toString();
	}
	
	public record MissingSource(String sourceId) implements Issue {
		@Override
		public String toString() {
			return String.format("Concept '%s' is referenced as a source ID, but it is not registered as a node.", sourceId);
		}
	}
	
	public record MissingDestination(String destinationId) implements Issue { 
		@Override
		public String toString() {
			return String.format("Concept '%s' is referenced as a destination ID, but it is not registered as a node.", destinationId);
		}
	}
	
	public record NodePartOfCycle(String cycleNodeId) implements Issue {
		@Override
		public String toString() {
			return String.format("Node '%s' is part of a cycle.", cycleNodeId);
		}		
	}

	private final OrderedSetImpl<String> nodes;
	private final Map<String, SimpleEdge> edges;
	
	private SparseFixedBitSet[] parents;
	private SparseFixedBitSet[] indirectAncestors;
	private SparseFixedBitSet[] descendants;
	private boolean built = false;

	public SimpleTaxonomyGraph(final int numberOfExpectedNodes, final int numberOfExpectedEdges) {
		this.nodes = new OrderedSetImpl<>(numberOfExpectedNodes);
		this.edges = newHashMapWithExpectedSize(numberOfExpectedEdges);
	}
	
	public SimpleTaxonomyGraph(final SimpleTaxonomyGraph original) {
		this.nodes = new OrderedSetImpl<>(original.nodes);
		this.edges = newHashMap(original.edges);
	}

	public boolean isBuilt() {
		return built;
	}

	public void addNode(final String nodeId) {
		nodes.add(nodeId);
		built = false;
	}

	public void removeNode(final String nodeId) {
		nodes.remove(nodeId);
		built = false;
	}

	public boolean containsNode(final String nodeId) {
		return nodes.contains(nodeId);
	}

	public void addEdge(final String sourceId, final String destinationId) {
		// XXX: Uses "<source ID>" to identify edge pointing to the single destination
		addEdge(sourceId, sourceId, destinationId);
	}
	
	public void addEdge(final String sourceId, final Set<String> destinationIds) {
		// XXX: Uses "<source ID>" to identify edge pointing to multiple destinations
		addEdge(sourceId, sourceId, destinationIds);
	}

	public void addEdge(final String edgeId, final String sourceId, final String destinationId) {
		addEdge(edgeId, sourceId, Set.of(destinationId));
	}

	public void addEdge(final String edgeId, final String sourceId, final Set<String> destinationIds) {
		addEdge(edgeId, new SimpleEdge(sourceId, destinationIds));
	}

	public void addEdge(final String edgeId, final SimpleEdge edge) {
		edges.put(edgeId, edge);
		built = false;
	}

	public void removeEdge(final String edgeId) {
		edges.remove(edgeId);
		built = false;
	}

	public boolean containsEdge(final String edgeId) {
		return edges.containsKey(edgeId);
	}
	
	public Set<String> getEdgeIds() {
		return edges.keySet();
	}

	private SimpleEdge getEdge(final String edgeId) {
		return edges.get(edgeId);
	}

	public String getSourceId(final String edgeId) {
		return getEdge(edgeId).getSourceId();
	}

	public Set<String> getDestinationIds(final String edgeId) {
		return getEdge(edgeId).getDestinationIds();
	}

	public void clear() {
		nodes.clear();
		edges.clear();

		parents = null;
		indirectAncestors = null;
		descendants = null;
		built = false;
	}

	public List<Issue> build() {
		final List<Issue> issues = newArrayList();
		final int conceptCount = nodes.size();

		parents = new SparseFixedBitSet[conceptCount];
		indirectAncestors = new SparseFixedBitSet[conceptCount];
		descendants = new SparseFixedBitSet[conceptCount];

		// Provide continuous values for indexOf
		nodes.compact();

		for (final SimpleEdge edges : edges.values()) {
			// Check if source and destination concepts are known, don't exit early (we would like to know about both)
			final String sourceId = edges.getSourceId();
			final int sourceIdx = nodes.indexOf(sourceId);
			if (sourceIdx < 0) {
				addIssue(issues, new MissingSource(sourceId));
			}

			for (final String destinationId : edges.getDestinationIds()) {
				final int destinationIdx = nodes.indexOf(destinationId);
				if (destinationIdx < 0) {
					issues.add(new MissingDestination(destinationId));
				} else if (sourceIdx >= 0) {
					getBitSet(parents, sourceIdx).set(destinationIdx);
					getBitSet(descendants, destinationIdx).set(sourceIdx);
				}
			}
		}

		// Look for cycles based on direct parent information
		for (int idx = 0; idx < conceptCount; idx++) {
			final SparseFixedBitSet visited = new SparseFixedBitSet(nodes.size());
			final IntDeque toProcess = PrimitiveLists.newIntArrayDeque();
			visited.set(idx);
			toProcess.add(idx);

			while (!toProcess.isEmpty()) {
				final int currentIdx = toProcess.removeInt(0);
				final SparseFixedBitSet parentsOfCurrent = parents[currentIdx];

				if (parentsOfCurrent == null || parentsOfCurrent.isEmpty()) {
					continue;
				}
				
				if (parentsOfCurrent.get(idx)) {
					String cycleNodeId = getNodeId(idx);
					issues.add(new NodePartOfCycle(cycleNodeId));
				}
				
				forEachBit(parentsOfCurrent, parentIdx -> {
					if (!visited.get(parentIdx)) {
						visited.set(parentIdx);
						toProcess.add(parentIdx);
					}
				});				
			}
		}
		
		for (int idx = 0; idx < conceptCount; idx++) {
			final SparseFixedBitSet parentsOfConcept = parents[idx];
			if (parentsOfConcept != null && !parentsOfConcept.isEmpty()) {
				// "idx" has at least one parent, so it is not a root concept 
				continue;
			}

			// At this point only direct descendants are recorded
			final SparseFixedBitSet childrenOfConcept = descendants[idx];
			if (childrenOfConcept == null || childrenOfConcept.isEmpty()) {
				// "idx" has neither parents nor children, ie. an isolated root
				continue;
			}

			/*
			 * "idx" is a root concept that can serve as the starting point for processing.
			 * Prime the queue with its children (the _children_ of which will need
			 * computing ancestors first).
			 */
			final SparseFixedBitSet visited = new SparseFixedBitSet(nodes.size());
			final IntDeque toProcess = PrimitiveLists.newIntArrayDeque();
			forEachBit(childrenOfConcept, toProcess::add);

			while (!toProcess.isEmpty()) {
				final int currentIdx = toProcess.removeInt(0);

				final SparseFixedBitSet childrenOfCurrent = descendants[currentIdx];
				if (childrenOfCurrent == null || childrenOfCurrent.isEmpty()) {
					/*
					 * "currentIdx" is a leaf, it has no children for which an ancestor update would
					 * be required.
					 */
					continue;
				}

				/*
				 *       o indirect ancestors of "currentIdx" \
				 *       ▲                                     | Add these IDs as indirect
				 *       │                                     | ancestors to children of
				 *       │                                     | "currentIdx"
				 * o     o parents of "currentIdx"            /
				 * ▲     ▲
				 * └──┬──┘
				 *    │
				 *    o    "currentIdx"
				 * ┌─►▲◄─┐
				 * │  │  │
				 * │  │  │
				 * o  o  o children of "currentIdx"
				 */
				final SparseFixedBitSet parentsOfCurrent = parents[currentIdx];
				final SparseFixedBitSet indirectAncestorsOfCurrent = indirectAncestors[currentIdx];

				forEachBit(childrenOfCurrent, childIdx -> {
					final SparseFixedBitSet indirectAncestorsOfChild = getBitSet(indirectAncestors, childIdx);

					// Use BitSet's "or" operation for unions
					if (parentsOfCurrent != null) { indirectAncestorsOfChild.or(parentsOfCurrent); }
					if (indirectAncestorsOfCurrent != null) { indirectAncestorsOfChild.or(indirectAncestorsOfCurrent); }

					// Queue "childIdx" to take the place of "currentIdx" at a later time (breadth-first queuing)
					if (!visited.get(childIdx)) {
						visited.set(childIdx);
						toProcess.add(childIdx);
					}
				});
			}
		}

		for (int idx = 0; idx < conceptCount; idx++) {
			// At this point only direct descendants are recorded
			final SparseFixedBitSet childrenOfConcept = descendants[idx];
			if (childrenOfConcept != null && !childrenOfConcept.isEmpty()) {
				// "idx" is not a leaf
				continue;
			}

			final SparseFixedBitSet parentsOfConcept = parents[idx];
			if (parentsOfConcept == null || parentsOfConcept.isEmpty()) {
				// "idx" has neither children nor parents, ie. an isolated root 
				continue;
			}

			/*
			 * "idx" is a leaf concept that can serve as the starting point for processing.
			 * Prime the queue with its parents (the _parents_ of which will need
			 * computing descendants first).
			 */
			final SparseFixedBitSet visited = new SparseFixedBitSet(nodes.size());
			final IntDeque toProcess = PrimitiveLists.newIntArrayDeque();
			forEachBit(parentsOfConcept, toProcess::add);

			while (!toProcess.isEmpty()) {
				final int currentIdx = toProcess.removeInt(0);

				final SparseFixedBitSet parentsOfCurrent = parents[currentIdx];
				if (parentsOfCurrent == null || parentsOfCurrent.isEmpty()) {
					/*
					 * "currentIdx" is a root, it has no parents for which a descendant update would
					 * be required.
					 */
					continue;
				}

				final SparseFixedBitSet descendantsOfCurrent = descendants[currentIdx];
				forEachBit(parentsOfCurrent, parentIdx -> {
					// Pass the descendants known by the current concept upwards
					final SparseFixedBitSet descendantsOfParent = descendants[parentIdx];
					descendantsOfParent.or(descendantsOfCurrent);

					// Queue "parentIdx" to take the place of "currentIdx" at a later time (breadth-first queuing)
					if (!visited.get(parentIdx)) {
						visited.set(parentIdx);
						toProcess.add(parentIdx);
					}
				});
			}
		}

		built = true;
		issues.forEach(i -> LOGGER.error(i.toString()));
		return issues;
	}

	private void addIssue(final List<Issue> list, final Issue issue) {
		list.add(issue);
		
		// Maintain a sliding window of the most recently recorded issues
		if (list.size() > MAX_ISSUES) {
			list.remove(0);
		}
	}

	private SparseFixedBitSet getBitSet(final SparseFixedBitSet[] bitSets, final int sourceIdx) {
		// Lazily initialize BitSets
		if (bitSets[sourceIdx] == null) {
			bitSets[sourceIdx] = new SparseFixedBitSet(nodes.size());
		}

		return bitSets[sourceIdx];
	}

	private static void forEachBit(final SparseFixedBitSet bitSet, final IntConsumer action) {
		if (bitSet != null) {
			for (int i = bitSet.nextSetBit(0); i != -1; i = bitSet.nextSetBit(i + 1)) {
				action.accept(i);
			}
		}
	}

	public Set<String> getDescendantIds(final String nodeId) {
		checkNotNull(nodeId, "Node ID cannot be null.");
		checkBuilt();
		final int idx = getNodeIdx(nodeId);
		return bitSetToIds(descendants[idx]);
	}

	public boolean subsumes(final String descendantId, final String ancestorId) {
		checkNotNull(descendantId, "Descendant node ID cannot be null.");
		checkNotNull(ancestorId, "Ancestor node ID cannot be null.");
		checkBuilt();
		final int descendantIdx = getNodeIdx(descendantId);
		final int ancestorIdx = getNodeIdx(ancestorId);
		final SparseFixedBitSet descendantsOfAncestor = descendants[ancestorIdx];
		
		if (CompareUtils.isEmpty(descendantsOfAncestor)) {
			return false;
		} else {
			return descendantsOfAncestor.get(descendantIdx);
		}
	}

	public Set<String> getParentIds(final String nodeId) {
		checkNotNull(nodeId, "Node ID cannot be null.");
		checkBuilt();
		final int idx = getNodeIdx(nodeId);
		return bitSetToIds(parents[idx]);
	}

	public Set<String> getIndirectAncestorIds(final String nodeId) {
		checkNotNull(nodeId, "Node ID cannot be null.");
		checkBuilt();
		final int idx = getNodeIdx(nodeId);
		return bitSetToIds(indirectAncestors[idx]);
	}

	private int getNodeIdx(final String nodeId) {
		final int idx = nodes.indexOf(nodeId);
		if (idx < 0) {
			throw new NotFoundException("Graph node", nodeId);
		} else {
			return idx;
		}
	}

	private String getNodeId(final int idx) {
		return nodes.get(idx);
	}

	private Set<String> bitSetToIds(final SparseFixedBitSet bitSet) {
		if (CompareUtils.isEmpty(bitSet)) {
			return Set.of();
		}

		final ImmutableSet.Builder<String> builder = ImmutableSet.builder();
		forEachBit(bitSet, idx -> builder.add(getNodeId(idx)));
		return builder.build();
	}

	private void checkBuilt() {
		if (!built) {
			throw new IllegalStateException("Taxonomy graph queries should only be called when the hierarchy is already built.");
		}
	}
}
