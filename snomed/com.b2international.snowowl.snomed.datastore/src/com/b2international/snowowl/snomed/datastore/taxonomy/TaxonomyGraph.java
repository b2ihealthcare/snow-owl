/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.taxonomy;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.isEmpty;

import java.util.BitSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.collections.PrimitiveMaps;
import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.ints.IntIterator;
import com.b2international.collections.ints.IntKeyMap;
import com.b2international.collections.ints.IntSet;
import com.b2international.collections.longs.LongSet;
import com.b2international.commons.CompareUtils;
import com.b2international.commons.arrays.LongBidiMapWithInternalId;
import com.b2international.commons.status.Statuses;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.exceptions.CycleDetectedException;
import com.b2international.snowowl.snomed.datastore.taxonomy.InvalidRelationship.MissingConcept;
import com.google.common.collect.Lists;

/**
 * @since 6.14
 */
public final class TaxonomyGraph {

	private static final Logger LOGGER = LoggerFactory.getLogger(TaxonomyGraph.class);
	
	/**
	 * Matrix for storing ancestors by internal IDs.
	 */
	private int[][] ancestors;
	
	/**
	 * Matrix for storing descendants by internal IDs.
	 */
	private int[][] descendants;
	
	/**Flag representing the current state of the builder.*/
	private boolean dirty;
	
	private boolean checkCycles = true;
	
	/**
	 * Bi-directional map for storing SNOMED CT concept IDs. 
	 */
	private final LongBidiMapWithInternalId nodes;

	/**
	 * Map for storing active IS_A type SNOMED CT relationship representations.
	 */
	private final IntKeyMap<Edges> edges;
	
	public TaxonomyGraph(int numberOfExpectedNodes, int numberOfExpectedEdges) {
		this.nodes = new LongBidiMapWithInternalId(numberOfExpectedNodes);
		this.edges = PrimitiveMaps.newIntKeyOpenHashMapWithExpectedSize(numberOfExpectedEdges);
	}
	
	public void setCheckCycles(boolean checkCycles) {
		this.checkCycles = checkCycles;
	}
	
	public boolean isCheckCycles() {
		return checkCycles;
	}
	
	public boolean isDirty() {
		return dirty;
	}
	
	public void clear() {
		descendants = null;
		ancestors = null;
		
		if (null != nodes) { 
			nodes.clear();
		}
		
		if (null != edges) {
			edges.clear();
		}
	}
	
	public TaxonomyGraphStatus update() {
		final List<InvalidRelationship> invalidRelationships = Lists.newArrayList();

		// allocate data
		final int conceptCount = nodes.size();
		final int[] outgoingIsaHistogram = new int[conceptCount];
		final int[] incomingIsaHistogram = new int[conceptCount];

		ancestors = new int[conceptCount][];
		descendants = new int[conceptCount][];

		//2D integer array for storing concept internal IDs and for saving ~1M additional internal ID lookup via hash code
		//0 index source/subject concept internal ID
		//1 index destination/object concept internal ID
		int numberOfEdges = 0;
		for (final IntIterator keys = edges.keySet().iterator(); keys.hasNext(); /* nothing */) {
			final Edges statements = edges.get(keys.next());
			final long[] destinationIds = statements.destinationIds;
			numberOfEdges += destinationIds.length;
		}
		
		final int[][] _conceptInternalIds  = new int[numberOfEdges][2];
		int count = 0;
		
		// refresh all RelationshipMini concepts, since they may have been modified
		for (final IntIterator keys = edges.keySet().iterator(); keys.hasNext(); /* nothing */) {
			final Edges statements = edges.get(keys.next());

			final long sourceId = statements.sourceId;
			final long[] destinationIds = statements.destinationIds;
			final int sourceConceptInternalId = nodes.getInternalId(sourceId);

			for (long destinationId : destinationIds) {
				boolean edgeSkipped = false;
				
				if (sourceConceptInternalId < 0) {
					invalidRelationships.add(new InvalidRelationship(sourceId, destinationId, MissingConcept.SOURCE));
					edgeSkipped |= true;
				}
				
				final int destinationConceptInternalId = nodes.getInternalId(destinationId);
				if (destinationConceptInternalId < 0) {
					invalidRelationships.add(new InvalidRelationship(sourceId, destinationId, MissingConcept.DESTINATION));
					edgeSkipped |= true;
				}
				
				if (!edgeSkipped) {
					outgoingIsaHistogram[sourceConceptInternalId]++;
					incomingIsaHistogram[destinationConceptInternalId]++;
				}
				
				_conceptInternalIds[count][0] = sourceConceptInternalId;
				_conceptInternalIds[count][1] = destinationConceptInternalId;
				count++;
			}
		}

		final TaxonomyGraphStatus result;
			
			for (int i = 0; i < conceptCount; i++) {
				ancestors[i] = new int[outgoingIsaHistogram[i]];
				descendants[i] = new int[incomingIsaHistogram[i]];
			}
	
			// create index matrices for relationships
			final int[] tailsSuperTypes = new int[conceptCount];
			final int[] tailsSubTypes = new int[conceptCount];
	
			for (int i = 0; i < _conceptInternalIds.length; i++) {
				
				final int subjectId = _conceptInternalIds[i][0];
				final int objectId = _conceptInternalIds[i][1];
	
				if (objectId != -1 && subjectId != -1) {
					ancestors[subjectId][tailsSuperTypes[subjectId]++] = objectId;
					descendants[objectId][tailsSubTypes[objectId]++] = subjectId;
				}
	
			}
			
		if (isEmpty(invalidRelationships)) {
			result = new TaxonomyGraphStatus(Statuses.ok());
		} else {
			LOGGER.warn("Taxonomy builder encountered relationships referencing inactive / non-existent concepts");
			result = new TaxonomyGraphStatus(
					Statuses.error("Taxonomy builder encountered relationships referencing inactive / non-existent concepts"), invalidRelationships);
		}

		dirty = false;
		return result;
	}

	public boolean containsNode(final long nodeId) {
		return 0 < nodes.get(nodeId);
	}
	
	public void addEdge(final String edgeId, final long sourceId, final long[] destinationIds) {
		final int id = createInternalEdgeId(edgeId);
		edges.put(id, new Edges(sourceId, destinationIds));
		dirty = true;
	}

	public void addNode(final String nodeId) {
		addNode(Long.parseLong(nodeId));
	}

	public void addNode(final long conceptId) {
		nodes.put(conceptId, conceptId);
	}
	
	public void removeEdge(final String edgeId) {
		edges.remove(createInternalEdgeId(edgeId));
		dirty = true;
	}
	
	public void removeNode(final String nodeId) {
		removeNode(Long.parseLong(nodeId));
	}

	private void removeNode(long nodeIdLong) {
		nodes.remove(nodeIdLong);
		dirty = true;
	}

	public LongSet getDescendantNodeIds(final long nodeId) {
		checkState();
		checkNotNull(nodeId, "Node ID argument cannot be null.");
		return processElements(nodeId, descendants[getInternalId(nodeId)]);
	}
	
	public LongSet getAncestorNodeIds(final long conceptId) {
		checkState();
		checkNotNull(conceptId, "Concept ID argument cannot be null.");
		return processElements(conceptId, ancestors[getInternalId(conceptId)]);
	}

	public LongSet getAllIndirectAncestorNodeIds(final long conceptId) {
		return getAndProcessAncestors(conceptId);
	}

	public LongSet getAllDescendantNodeIds(final long conceptId) {
		checkState();
		checkNotNull(conceptId, "Concept ID argument cannot be null.");
		final int conceptCount = getConceptCount();
		// index is the concept id, true if it is a sub-type
		final BitSet subTypeMap = new BitSet(conceptCount);
		collectDescendants(getInternalId(conceptId), subTypeMap);
		return processElements(conceptId, subTypeMap);
	}
	
	public LongSet getAllAncestorNodeIds(final long conceptId) {
		checkState();
		checkNotNull(conceptId, "Concept ID argument cannot be null.");
		final int conceptCount = getConceptCount();
		final int id = getInternalId(conceptId);

		// index is the concept id, true if it is a sub-type
		final BitSet superTypeMap = new BitSet(conceptCount);
		collectAncestors(id, superTypeMap);
		return processElements(conceptId, superTypeMap);
	}

	public long getSourceNodeId(final int edgeInternalId) {
		return getEdge(edgeInternalId).sourceId;
	}

	public long[] getDestinationNodeIds(final int edgeInternalId) {
		return getEdge(edgeInternalId).destinationIds;
	}
	
	public IntSet getEdgeIds() {
		return edges.keySet();
	}
	
	Edges getEdge(int edgeInternalId) {
		return edges.get(edgeInternalId);
	}

//	/**
//	 * (non-API)
//	 * 
//	 * Returns with the internal ID of the node.
//	 * @param nodeId the unique SNOMED&nbsp;CT ID of the concept. 
//	 * @return the internal ID of the node.
//	 */
//	public int getInternalId(final long nodeId) {
//		checkState();
//		final int $ = nodes.getInternalId(nodeId);
//		if ($ < 0) {
//			final String msg = "Concept does not exists with ID: " + nodeId;
//			LOGGER.error(msg);
//			throw new SnowowlRuntimeException(msg);
//		}
//		return $;
//	}
	
	/* 
	 * Returns with the SNOMED&nbsp;CT ID of the concept for the 
	 * internal concept ID argument.
	 * @param internalNodeId the internal ID of the concept.
	 * @return the SNOMED&nbsp;CT ID of the concept.
	 */
	private long getNodeId(final int internalNodeId) {
		checkState();
		return nodes.get(internalNodeId);
	}
	
	private final int createInternalEdgeId(String statementId) {
		return statementId.hashCode();
	}
	
	private LongSet getAndProcessAncestors(final long conceptId) {
		checkState();
		checkNotNull(conceptId, "Concept ID argument cannot be null.");
		
		final int conceptCount = getConceptCount();
		final int id = getInternalId(conceptId);
		// index is the concept internal id, true if it is a supertype
		final BitSet ancestorInternalIds = new BitSet(conceptCount);
	
		// ancestors == supertypes of this concept's direct parents
		int[] internalId = ancestors[id];
		if (internalId != null) {
			for (final int directParent : internalId) {
				collectAncestors(directParent, ancestorInternalIds);
			}
		}
		return processElements(conceptId, ancestorInternalIds);		
	}

	private LongSet processElements(final long conceptId, final BitSet bitSet) {
		if (CompareUtils.isEmpty(bitSet)) {
			return PrimitiveSets.newLongOpenHashSet();
		}
		final int count = bitSet.cardinality();
	
		final LongSet $ = PrimitiveSets.newLongOpenHashSetWithExpectedSize(count);
		for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet.nextSetBit(i + 1)) {
			long convertedId = getNodeId(i);
			if (convertedId == conceptId && checkCycles) {
				throw new CycleDetectedException("Concept " + conceptId + " would introduce a cycle in the ISA graph (loop).");
			}
			$.add(convertedId);
		}

		return $;
	}
	
	private LongSet processElements(final long conceptId, final int... internalIds) {
		if (CompareUtils.isEmpty(internalIds)) {
			return PrimitiveSets.newLongOpenHashSet();
		}
		final LongSet $ = PrimitiveSets.newLongOpenHashSetWithExpectedSize(internalIds.length);
		for (final int i : internalIds) {
			long convertedId = getNodeId(i);
			if (conceptId == convertedId && checkCycles ) {
				throw new CycleDetectedException("Concept " + conceptId + " would introduce a cycle in the ISA graph (loop).");
			}
			$.add(convertedId);
		}
		return $;
	}
	
	private int getInternalId(final long nodeId) {
		checkState();
		final int $ = nodes.getInternalId(nodeId);
		if ($ < 0) {
			final String msg = String.format("Concept does not exists with ID: %s", nodeId);
			LOGGER.error(msg);
			throw new SnowowlRuntimeException(msg);
		}
		return $;
	}
	
	private void collectAncestors(final int type, final BitSet ancestors) {

		final int[] relationships = this.ancestors[type];
		if (relationships != null) {
			for (int i = 0; i < relationships.length; i++) {
				if (!ancestors.get(relationships[i])) {
					ancestors.set(relationships[i]); //set to true
					collectAncestors(relationships[i], ancestors);
				}
			}
		}
	}

	private void collectDescendants(final int type, final BitSet descendants) {

		final int[] relationships = this.descendants[type];
		if (relationships != null) {
			for (int i = 0; i < relationships.length; i++) {
				if (!descendants.get(relationships[i])) {
					descendants.set(relationships[i]); //set to true
					collectDescendants(relationships[i], descendants);
				}
			}
		}
	}

	private int getConceptCount() {
		return nodes.size();
	}

	private void checkState() {
		if (dirty) {
			throw new IllegalStateException("Taxonomy builder for SNOMED CT ontology is in dirty state.");
		}
	}

}