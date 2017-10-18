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

import java.util.BitSet;

import com.b2international.collections.longs.LongSet;
import com.b2international.commons.Pair;

/**
 *
 */
public interface ISnomedTaxonomyBuilder {

	boolean isDirty();

	void clear();

	SnomedTaxonomyStatus build();

	void addEdge(final TaxonomyBuilderEdge edge);

	void addNode(final TaxonomyBuilderNode node);

	boolean containsNode(final String nodeId);

	boolean containsEdge(final String edgeId);

	void removeEdge(final TaxonomyBuilderEdge edge);

	void removeNode(final TaxonomyBuilderNode node);

	Pair<LongSet, LongSet> difference(final ISnomedTaxonomyBuilder other);

	LongSet getDescendantNodeIds(final String nodeId);

	LongSet getAncestorNodeIds(final String nodeId);

	LongSet getAllIndirectAncestorNodeIds(final String nodeId);

	LongSet getAllDescendantNodeIds(final String nodeId);

	LongSet getAllAncestorNodeIds(final String nodeId);

	String getSourceNodeId(final String edgeId);

	String getDestinationNodeId(final String edgeId);
	
	/**
	 * Workaround to avoid autoboxing.
	 */
	static interface IntToLongFunction {
		/**
		 * Applies the function to the specified primitive integer value.
		 * @param the primitive integer value.
		 * @return the long value after applying the current function.
		 * */
		long apply(final int i);
	}
	
	/**
	 * Function for performing {@link BitSet} transformation.
	 */
	static interface TransformBitSetFunction {
		/**
		 * Performs any arbitrary transformation on the specified {@link BitSet}.
		 * @param internalId the internal ID.
		 * @param sourceBitSet the {@link BitSet} to transform.
		 * @return the transformed {@link BitSet}.
		 */
		BitSet transform(final int internalId, final BitSet sourceBitSet);
	}

	static interface TaxonomyItem {
		
		String getId();
		
		boolean isCurrent();
		
	}
	
	public static interface TaxonomyBuilderNode extends TaxonomyItem {
		
	}
	
	public static interface TaxonomyBuilderEdge extends TaxonomyItem {
		String getSoureId();
		String getDestinationId();
		boolean isValid();
	}
	
}