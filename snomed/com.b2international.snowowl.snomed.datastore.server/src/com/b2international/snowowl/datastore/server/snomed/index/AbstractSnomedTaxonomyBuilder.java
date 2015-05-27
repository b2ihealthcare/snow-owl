/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.server.snomed.index;

import static com.b2international.snowowl.snomed.datastore.SnomedTaxonomyBuilderMode.DEFAULT;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Sets.newHashSet;

import java.util.BitSet;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bak.pcj.map.LongKeyMap;
import bak.pcj.map.LongKeyMapIterator;
import bak.pcj.set.LongOpenHashSet;
import bak.pcj.set.LongSet;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.Pair;
import com.b2international.commons.arrays.LongBidiMapWithInternalId;
import com.b2international.commons.concurrent.equinox.ForkJoinUtils;
import com.b2international.commons.pcj.LongSets;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.snomed.datastore.SnomedTaxonomyBuilderMode;
import com.b2international.snowowl.snomed.datastore.index.ISnomedTaxonomyBuilder;
import com.b2international.snowowl.snomed.datastore.index.IncompleteTaxonomyException;
import com.google.common.base.Preconditions;

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
	
	private final TransformBitSetFunction includeSelfFunction = new TransformBitSetFunction() {
		@Override public BitSet transform(final int internalId, final BitSet sourceBitSet) {
			Preconditions.checkNotNull(sourceBitSet, "Source bit set argument cannot be null.");
			sourceBitSet.set(internalId); //include self
			return sourceBitSet;
		}
	};
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.index.ISnomedTaxonomyBuilder#isDirty()
	 */
	@Override
	public boolean isDirty() {
		return dirty;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.index.ISnomedTaxonomyBuilder#clear()
	 */
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
	public AbstractSnomedTaxonomyBuilder build() {

		final Collection<Pair<String, String>> invalidNodePairs = newHashSet();
		
		final int conceptCount = getNodes().size();

		// allocate data
		final int[] outgoingIsaHistorgram = new int[conceptCount];
		final int[] incomingIsaHistogram = new int[conceptCount];

		ancestors = new int[conceptCount][];
		descendants = new int[conceptCount][];

		//2D integer array for storing concept internal IDs and for saving ~1M additional internal ID lookup via hash code
		//0 index source/subject concept internal ID
		//1 index destination/object concept internal ID
		final int[][] _conceptInternalIds  = new int[getEdges().size()][2];
		int count = 0;
		
		// refresh all RelationshipMini concepts, since they may have been modified
		for (final LongKeyMapIterator itr = getEdges().entries(); itr.hasNext(); /* nothing */) {
			
			itr.next(); //keep iterating
			
			final long[] statement = (long[]) itr.getValue();

			final long destinationId = statement[0];
			final long sourceId = statement[1];
			boolean skipEdged = false;
			
			final int sourceConceptInternalId = getNodes().getInternalId(sourceId);
			if (sourceConceptInternalId < 0) {
				final Pair<String, String> missingSource = getMode().handleMissingSource(sourceId, destinationId);
				if (null != missingSource) {
					invalidNodePairs.add(missingSource);
				}
				skipEdged |= true;
			}
			
			final int destinationConceptInternalId = getNodes().getInternalId(destinationId);
			if (destinationConceptInternalId < 0) {
				final Pair<String, String> missingDestination = getMode().handleMissingDestination(sourceId, destinationId);
				if (null != missingDestination) {
					invalidNodePairs.add(missingDestination);
				}
				skipEdged |= true;
			}

			if (!skipEdged) {
				outgoingIsaHistorgram[sourceConceptInternalId]++;
				incomingIsaHistogram[destinationConceptInternalId]++;
			}

			_conceptInternalIds[count][0] = sourceConceptInternalId;
			_conceptInternalIds[count][1] = destinationConceptInternalId;
			count++;
			
		}

		if (isEmpty(invalidNodePairs)) {
			
			for (int i = 0; i < conceptCount; i++) {
	
				ancestors[i] = new int[outgoingIsaHistorgram[i]];
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
			
		} else {
			throw new IncompleteTaxonomyException(invalidNodePairs);
		}

		dirty = false;
		
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.index.ISnomedTaxonomyBuilder#addEdge(com.b2international.snowowl.snomed.datastore.index.ISnomedTaxonomyBuilder.TaxonomyEdge)
	 */
	@Override
	public void addEdge(final TaxonomyEdge edge) {
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
	public void addNode(final TaxonomyNode node) {
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
	public void removeEdge(final TaxonomyEdge edge) {
		
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
	public void removeNode(final TaxonomyNode node) {

		Preconditions.checkNotNull(node, "Taxonomy node argument cannot be null.");
		
		getNodes().remove(Long.parseLong(node.getId()));

		dirty = true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.index.ISnomedTaxonomyBuilder#difference(com.b2international.snowowl.snomed.datastore.index.ISnomedTaxonomyBuilder)
	 */
	@Override
	public Pair<LongSet, LongSet> difference(final ISnomedTaxonomyBuilder other) {
		
		Preconditions.checkNotNull(other, "Taxonomy builder argument cannot be null.");
		
		if (other == this) {
			return new Pair<LongSet, LongSet>(new LongOpenHashSet(), new LongOpenHashSet());
		}
		
		final AtomicReference<LongSet> otherStatements = new AtomicReference<LongSet>();
		final AtomicReference<LongSet> thisStatements = new AtomicReference<LongSet>();
		
		final Runnable getOtherStatementsRunnable = new Runnable() {
			@Override public void run() { otherStatements.set(((AbstractSnomedTaxonomyBuilder) other).getEdges().keySet()); }
		};
		
		final Runnable getThisStatementsRunnable = new Runnable() {
			@Override public void run() { thisStatements.set(getEdges().keySet()); }
		};
		
		ForkJoinUtils.runInParallel(getOtherStatementsRunnable, getThisStatementsRunnable);
		
		final AtomicReference<LongSet> newStatements = new AtomicReference<LongSet>();
		final AtomicReference<LongSet> detachedStatements = new AtomicReference<LongSet>();

		final Runnable calculateNewStatementsRunnable = new Runnable() {
			@Override public void run() { newStatements.set(LongSets.difference(thisStatements.get(), otherStatements.get()));
			}
		};

		final Runnable calculateDetachedStatementsRunnable = new Runnable() {
			@Override public void run() { detachedStatements.set(LongSets.difference(otherStatements.get(), thisStatements.get()));
			}
		};

		ForkJoinUtils.runInParallel(calculateNewStatementsRunnable, calculateDetachedStatementsRunnable);
		
		return new Pair<LongSet, LongSet>(newStatements.get(), detachedStatements.get());
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
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.index.ISnomedTaxonomyBuilder#getAllDescendantNodeIds(java.lang.String)
	 */
	@Override
	public LongSet getAllDescendantNodeIds(final String conceptId) {
		checkState();
		checkNotNull(conceptId, "Concept ID argument cannot be null.");
		final int conceptCount = getConceptCount();
		// index is the concept id, true if it is a sub-type
		final BitSet subTypeMap = new BitSet(conceptCount);
		collectDescendants(getInternalId(conceptId), subTypeMap);
		return processElements(getNodeIdFunction, subTypeMap);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.index.ISnomedTaxonomyBuilder#getAllAncestorNodeIds(java.lang.String)
	 */
	@Override
	public LongSet getAllAncestorNodeIds(final String conceptId) {
		checkState();
		checkNotNull(conceptId, "Concept ID argument cannot be null.");
		final int conceptCount = getConceptCount();
		final int id = getInternalId(conceptId);

		// index is the concept id, true if it is a sub-type
		final BitSet superTypeMap = new BitSet(conceptCount);
		collectAncestors(id, superTypeMap);
		return processElements(getNodeIdFunction, superTypeMap);
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.index.ISnomedTaxonomyBuilder#getSelfAndAllAncestorNodeIds(long)
	 */
	@Override
	public LongSet getSelfAndAllAncestorNodeIds(final long conceptId) {
		checkState();
		final int conceptCount = getConceptCount();
		final int id = getInternalId(conceptId);

		// index is the concept id, true if it is a sub-type
		final BitSet superTypeMap = new BitSet(conceptCount);
		collectAncestors(id, superTypeMap);
		includeSelfFunction.transform(id, superTypeMap);
		return processElements(getNodeIdFunction, superTypeMap);
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.index.ISnomedTaxonomyBuilder#getSourceNodeId(java.lang.String)
	 */
	@Override
	public String getSourceNodeId(final String edgeId) {
		return Long.toString(getEdges0(Long.parseLong(edgeId))[1]);
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.index.ISnomedTaxonomyBuilder#getDestinationNodeId(java.lang.String)
	 */
	@Override
	public String getDestinationNodeId(final String edgeId) {
		return Long.toString(getEdges0(Long.parseLong(edgeId))[0]);
	}

	public abstract LongBidiMapWithInternalId getNodes();
	
	public abstract LongKeyMap getEdges();
	
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
	
	protected SnomedTaxonomyBuilderMode getMode() {
		return DEFAULT;
	}
	
	private LongSet getAndProcessAncestors(final String nodeId, final IntToLongFunction processingFunction) {
		checkState();
		checkNotNull(nodeId, "Concept ID argument cannot be null.");
		
		final int conceptCount = getConceptCount();
		final int id = getInternalId(nodeId);
		// index is the concept internal id, true if it is a supertype
		final BitSet ancestorInternalIds = new BitSet(conceptCount);
	
		// ancestors == supertypes of this concept's direct parents
		for (final int directParent : ancestors[id]) {
			collectAncestors(directParent, ancestorInternalIds);
		}
	
		return processElements(processingFunction, ancestorInternalIds);		
	}

	private long[] getEdges0(final long statementId) {
		return (long[]) getEdges().get(statementId);
	}

	private LongSet processElements(final IntToLongFunction function, final BitSet bitSet) {
		Preconditions.checkNotNull(function, "Function argument cannot be null.");
		Preconditions.checkNotNull(function, "Bit set argument cannot be null.");
		if (CompareUtils.isEmpty(bitSet)) {
			return new LongOpenHashSet();
		}
		final int count = bitSet.cardinality();
	
		final LongSet $ = new LongOpenHashSet(count);
		for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet.nextSetBit(i + 1)) {
			$.add(function.apply(i));
		}

		return $;
	}
	
	private LongSet processElements(final String conceptId, final IntToLongFunction function, final int... internalIds) {
		Preconditions.checkNotNull(function, "Function argument cannot be null.");
		if (CompareUtils.isEmpty(internalIds)) {
			return new LongOpenHashSet();
		}
		final LongSet $ = new LongOpenHashSet(internalIds.length); //optimized load factor
		final long conceptIdLong = Long.parseLong(conceptId);
		for (final int i : internalIds) {
			long convertedId = function.apply(i);
			if (conceptIdLong == convertedId) {
				throw new IllegalStateException("Concept ID " + conceptId + " found in parent or child result set (loop).");
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