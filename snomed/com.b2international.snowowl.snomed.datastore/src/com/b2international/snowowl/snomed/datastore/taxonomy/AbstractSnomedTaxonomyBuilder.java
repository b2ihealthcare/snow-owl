/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongCollections;
import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongKeyMap;
import com.b2international.collections.longs.LongSet;
import com.b2international.commons.CompareUtils;
import com.b2international.commons.Pair;
import com.b2international.commons.arrays.LongBidiMapWithInternalId;
import com.b2international.commons.collect.LongSets;
import com.b2international.commons.status.Statuses;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.exceptions.CycleDetectedException;
import com.b2international.snowowl.snomed.datastore.taxonomy.InvalidRelationship.MissingConcept;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 *
 */
public abstract class AbstractSnomedTaxonomyBuilder implements ISnomedTaxonomyBuilder {

	protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractSnomedTaxonomyBuilder.class);
	
	/**
	 * Matrix for storing ancestors by internal IDs.
	 */
	protected int[][] ancestors;
	/**
	 * Matrix for storing descendants by internal IDs.
	 */
	protected int[][] descendants;
	
	/**Flag representing the current state of the builder.*/
	private boolean dirty;
	
	private final IntToLongFunction getNodeIdFunction = new IntToLongFunction() {
		@Override public long apply(final int i) {
			return getNodeId(i);
		}
	};

	private boolean checkCycles = true;
	
	public void setCheckCycles(boolean checkCycles) {
		this.checkCycles = checkCycles;
	}
	
	public boolean isCheckCycles() {
		return checkCycles;
	}
	
	@Override
	public boolean isDirty() {
		return dirty;
	}
	
	@Override
	public void clear() {
		
		descendants = null;
		ancestors = null;
		
		if (null != getNodes()) { 
			getNodes().clear();
		}
		
		if (null != getEdges()) {
			getEdges().clear();
		}
		
	}
	
	@Override
	public SnomedTaxonomyStatus build() {
		final List<InvalidRelationship> invalidRelationships = Lists.newArrayList();

		// allocate data
		final int conceptCount = getNodes().size();
		final int[] outgoingIsaHistogram = new int[conceptCount];
		final int[] incomingIsaHistogram = new int[conceptCount];

		ancestors = new int[conceptCount][];
		descendants = new int[conceptCount][];

		//2D integer array for storing concept internal IDs and for saving ~1M additional internal ID lookup via hash code
		//0 index source/subject concept internal ID
		//1 index destination/object concept internal ID
		final LongKeyMap<long[]> edges = getEdges();
		final int[][] _conceptInternalIds  = new int[edges.size()][2];
		int count = 0;
		
		// refresh all RelationshipMini concepts, since they may have been modified
		for (final LongIterator keys = edges.keySet().iterator(); keys.hasNext(); /* nothing */) {
			
			final long relationshipId = keys.next(); //keep iterating
			final long[] statement = edges.get(relationshipId);
			

			final long destinationId = statement[0];
			final long sourceId = statement[1];
			
			boolean edgeSkipped = false;
			
			final int sourceConceptInternalId = getNodes().getInternalId(sourceId);
			if (sourceConceptInternalId < 0) {
				invalidRelationships.add(new InvalidRelationship(relationshipId, sourceId, destinationId, MissingConcept.SOURCE));
				edgeSkipped |= true;
			}
			
			final int destinationConceptInternalId = getNodes().getInternalId(destinationId);
			if (destinationConceptInternalId < 0) {
				invalidRelationships.add(new InvalidRelationship(relationshipId, sourceId, destinationId, MissingConcept.DESTINATION));
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

		final SnomedTaxonomyStatus result;
		if (isEmpty(invalidRelationships)) {
			
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
	
				ancestors[subjectId][tailsSuperTypes[subjectId]++] = objectId;
				descendants[objectId][tailsSubTypes[objectId]++] = subjectId;
	
			}
			
			result = new SnomedTaxonomyStatus(Statuses.ok());
		} else {
			LOGGER.warn("Missing concepts from relationships");
			result = new SnomedTaxonomyStatus(Statuses.error("Missing concepts from relationships."), invalidRelationships);
		}

		dirty = false;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.index.ISnomedTaxonomyBuilder#addEdge(com.b2international.snowowl.snomed.datastore.index.ISnomedTaxonomyBuilder.TaxonomyEdge)
	 */
	@Override
	public void addEdge(final TaxonomyBuilderEdge edge) {
		Preconditions.checkNotNull(edge, "Taxonomy edge argument cannot be null.");
		
		if (!edge.isValid()) { //ignore non IS_A relationships
			return;
		}

		final String id = edge.getId();
		
		if (!edge.isCurrent()) { //if inactivated remove instead
			getEdges().remove(Long.parseLong(id));
			dirty = true;
			return;
		}
		
		getEdges().put(Long.parseLong(id), new long [] {
				Long.parseLong(edge.getDestinationId()), //value ID
				Long.parseLong(edge.getSoureId())}); //object ID

		dirty = true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.index.ISnomedTaxonomyBuilder#addNode(com.b2international.snowowl.snomed.datastore.index.ISnomedTaxonomyBuilder.TaxonomyNode)
	 */
	@Override
	public void addNode(final TaxonomyBuilderNode node) {
		Preconditions.checkNotNull(node, "Taxonomy node argument cannot be null.");
		
		if (!node.isCurrent()) {
			return; //do not do anything in case of retired concept
		}
		
		final long conceptId = Long.parseLong(node.getId());
		getNodes().put(conceptId, conceptId);
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.index.ISnomedTaxonomyBuilder#containsNode(java.lang.String)
	 */
	@Override
	public boolean containsNode(final String nodeId) {
		return 0 < getNodes().get(Long.parseLong(nodeId));
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.index.ISnomedTaxonomyBuilder#containsEdge(java.lang.String)
	 */
	@Override
	public boolean containsEdge(final String edgeId) {
		return getEdges().containsKey(Long.parseLong(edgeId));
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.index.ISnomedTaxonomyBuilder#removeEdge(com.b2international.snowowl.snomed.datastore.index.ISnomedTaxonomyBuilder.TaxonomyEdge)
	 */
	@Override
	public void removeEdge(final TaxonomyBuilderEdge edge) {
		
		Preconditions.checkNotNull(edge, "Taxonomy edge argument cannot be null.");
		
		if (!edge.isValid()) { //ignore non IS_A relationships
			return;
		}

		getEdges().remove(Long.parseLong(edge.getId()));

		dirty = true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.index.ISnomedTaxonomyBuilder#removeNode(com.b2international.snowowl.snomed.datastore.index.ISnomedTaxonomyBuilder.TaxonomyNode)
	 */
	@Override
	public void removeNode(final TaxonomyBuilderNode node) {

		Preconditions.checkNotNull(node, "Taxonomy node argument cannot be null.");
		
		getNodes().remove(Long.parseLong(node.getId()));

		dirty = true;
	}

	@Override
	public Pair<LongSet, LongSet> difference(final ISnomedTaxonomyBuilder other) {
		Preconditions.checkNotNull(other, "Taxonomy builder argument cannot be null.");
		
		if (other == this) {
			return Pair.of(LongCollections.emptySet(), LongCollections.emptySet());
		}
		
		final LongSet thisStatements = getEdges().keySet();
		final LongSet otherStatements = ((AbstractSnomedTaxonomyBuilder) other).getEdges().keySet();
		
		final LongSet thisDiff = LongSets.difference(thisStatements, otherStatements);
		final LongSet otherDiff = LongSets.difference(otherStatements, thisStatements);
		
		return Pair.of(thisDiff, otherDiff);
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.index.ISnomedTaxonomyBuilder#getDescendantNodeIds(java.lang.String)
	 */
	@Override
	public LongSet getDescendantNodeIds(final String conceptId) {
		checkState();
		checkNotNull(conceptId, "Concept ID argument cannot be null.");
		return processElements(conceptId, getNodeIdFunction, descendants[getInternalId(conceptId)]);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.index.ISnomedTaxonomyBuilder#getAncestorNodeIds(java.lang.String)
	 */
	@Override
	public LongSet getAncestorNodeIds(final String conceptId) {
		checkState();
		checkNotNull(conceptId, "Concept ID argument cannot be null.");
		return processElements(conceptId, getNodeIdFunction, ancestors[getInternalId(conceptId)]);
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.index.ISnomedTaxonomyBuilder#getAllIndirectAncestorNodeIds(java.lang.String)
	 */
	@Override
	public LongSet getAllIndirectAncestorNodeIds(final String conceptId) {
		return getAndProcessAncestors(conceptId, getNodeIdFunction);
	}
	
	@Override
	public LongSet getAllDescendantNodeIds(final String conceptId) {
		checkState();
		checkNotNull(conceptId, "Concept ID argument cannot be null.");
		final int conceptCount = getConceptCount();
		// index is the concept id, true if it is a sub-type
		final BitSet subTypeMap = new BitSet(conceptCount);
		collectDescendants(getInternalId(conceptId), subTypeMap);
		return processElements(Long.parseLong(conceptId), getNodeIdFunction, subTypeMap);
	}
	
	@Override
	public LongSet getAllAncestorNodeIds(final String conceptId) {
		checkState();
		checkNotNull(conceptId, "Concept ID argument cannot be null.");
		final int conceptCount = getConceptCount();
		final int id = getInternalId(conceptId);

		// index is the concept id, true if it is a sub-type
		final BitSet superTypeMap = new BitSet(conceptCount);
		collectAncestors(id, superTypeMap);
		return processElements(Long.parseLong(conceptId), getNodeIdFunction, superTypeMap);
	}

	@Override
	public String getSourceNodeId(final String edgeId) {
		return Long.toString(getEdges0(Long.parseLong(edgeId))[1]);
	}

	@Override
	public String getDestinationNodeId(final String edgeId) {
		return Long.toString(getEdges0(Long.parseLong(edgeId))[0]);
	}

	public abstract LongBidiMapWithInternalId getNodes();
	
	public abstract LongKeyMap<long[]> getEdges();
	
	public int[][] getAncestors() {
		return ancestors;
	}
	
	public int[][] getDescendants() {
		return descendants;
	}
	
	/**
	 * (non-API)
	 * 
	 * Returns with the internal ID of the node.
	 * @param nodeId the unique SNOMED&nbsp;CT ID of the concept. 
	 * @return the internal ID of the node.
	 */
	public int getInternalId(final long nodeId) {
		checkState();
		final int $ = getNodes().getInternalId(nodeId);
		if ($ < 0) {
			final String msg = "Concept does not exists with ID: " + nodeId;
			LOGGER.error(msg);
			throw new SnowowlRuntimeException(msg);
		}
		return $;
	}
	
	/**
	 * (non-API)
	 * 
	 * Returns with the SNOMED&nbsp;CT ID of the concept for the 
	 * internal concept ID argument.
	 * @param internalNodeId the internal ID of the concept.
	 * @return the SNOMED&nbsp;CT ID of the concept.
	 */
	public long getNodeId(final int internalNodeId) {
		checkState();
		return getNodes().get(internalNodeId);
	}
	
	protected void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
	
	private LongSet getAndProcessAncestors(final String conceptId, final IntToLongFunction processingFunction) {
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
		return processElements(Long.parseLong(conceptId), processingFunction, ancestorInternalIds);		
	}

	private long[] getEdges0(final long statementId) {
		return getEdges().get(statementId);
	}

	private LongSet processElements(final long conceptId, final IntToLongFunction function, final BitSet bitSet) {
		Preconditions.checkNotNull(function, "Function argument cannot be null.");
		Preconditions.checkNotNull(function, "Bit set argument cannot be null.");
		if (CompareUtils.isEmpty(bitSet)) {
			return PrimitiveSets.newLongOpenHashSet();
		}
		final int count = bitSet.cardinality();
	
		final LongSet $ = PrimitiveSets.newLongOpenHashSetWithExpectedSize(count);
		for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet.nextSetBit(i + 1)) {
			long convertedId = function.apply(i);
			if (convertedId == conceptId && checkCycles) {
				throw new CycleDetectedException("Concept " + conceptId + " would introduce a cycle in the ISA graph (loop).");
			}
			$.add(convertedId);
		}

		return $;
	}
	
	private LongSet processElements(final String conceptId, final IntToLongFunction function, final int... internalIds) {
		Preconditions.checkNotNull(function, "Function argument cannot be null.");
		if (CompareUtils.isEmpty(internalIds)) {
			return PrimitiveSets.newLongOpenHashSet();
		}
		final LongSet $ = PrimitiveSets.newLongOpenHashSetWithExpectedSize(internalIds.length);
		final long conceptIdLong = Long.parseLong(conceptId);
		for (final int i : internalIds) {
			long convertedId = function.apply(i);
			if (conceptIdLong == convertedId && checkCycles ) {
				throw new CycleDetectedException("Concept " + conceptId + " would introduce a cycle in the ISA graph (loop).");
			}
			$.add(convertedId);
		}
		return $;
	}
	
	private int getInternalId(final String nodeId) {
		checkState();
		final int $ = getNodes().getInternalId(Long.parseLong(nodeId));
		if ($ < 0) {
			final String msg = "Concept does not exists with ID: " + nodeId;
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
		return getNodes().size();
	}


	private void checkState() {
		if (dirty) {
			throw new IllegalStateException("Taxonomy builder for SNOMED CT ontology is in dirty state.");
		}
	}

}