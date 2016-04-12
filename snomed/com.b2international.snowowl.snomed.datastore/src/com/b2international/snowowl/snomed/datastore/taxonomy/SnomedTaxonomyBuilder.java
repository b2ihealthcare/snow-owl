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
package com.b2international.snowowl.snomed.datastore.taxonomy;

import com.b2international.commons.arrays.Arrays2;
import com.b2international.commons.arrays.LongBidiMapWithInternalId;
import com.b2international.commons.collections.primitive.LongCollection;
import com.b2international.commons.collections.primitive.LongIterator;
import com.b2international.commons.collections.primitive.map.LongKeyMap;
import com.b2international.commons.pcj.PrimitiveCollections;
import com.b2international.snowowl.snomed.datastore.IsAStatementWithId;
import com.google.common.base.Preconditions;

/**
 * Builds the taxonomy for the SNOMED&nbsp;CT ontology.
 */
public class SnomedTaxonomyBuilder extends AbstractSnomedTaxonomyBuilder {

	/**
	 * Returns with a new taxonomy builder instance after replicating the specified one.
	 * @param builder the builder to replicate.
	 * @return the new builder instance.
	 */
	public static SnomedTaxonomyBuilder newInstance(final SnomedTaxonomyBuilder builder) {
		Preconditions.checkNotNull(builder, "Builder argument cannot be null.");
		
		final SnomedTaxonomyBuilder $ = new SnomedTaxonomyBuilder();
		$.nodes = new LongBidiMapWithInternalId( builder.nodes);
		$.edges = builder.edges.dup();
		$.setDirty(builder.isDirty());
		$.descendants = Arrays2.copy(builder.descendants);
		$.ancestors = Arrays2.copy(builder.ancestors);
		
		return $;
	}

	/**
	 * Bi-directional map for storing SNOMED CT concept IDs. 
	 */
	private LongBidiMapWithInternalId nodes;

	/**
	 * Map for storing active IS_A type SNOMED CT relationship representations. Keys are the unique relationship identifiers.
	 * <br>For values see: {@link IsAStatementWithId}.
	 */
	private LongKeyMap<long[]> edges;

	private SnomedTaxonomyBuilder() {}
	
	public SnomedTaxonomyBuilder(final LongCollection conceptIds, final IsAStatementWithId[] isAStatements) {
		nodes = new LongBidiMapWithInternalId(conceptIds.size());
		for (final LongIterator itr = conceptIds.iterator(); itr.hasNext(); /**/) {
			final long id = itr.next();
			nodes.put(id, id);
		}
		
		edges = isAStatements.length < 1 
				? PrimitiveCollections.<long[]>newLongKeyOpenHashMap() 
				: PrimitiveCollections.<long[]>newLongKeyOpenHashMap(isAStatements.length);
		for (final IsAStatementWithId statement : isAStatements) {
			edges.put(statement.getRelationshipId(), new long[] { statement.getDestinationId(), statement.getSourceId() });
		}
		
		setDirty(true);
	}
	
	@Override
	public LongBidiMapWithInternalId getNodes() {
		return nodes;
	}

	@Override
	public LongKeyMap<long[]> getEdges() {
		return edges;
	}

}