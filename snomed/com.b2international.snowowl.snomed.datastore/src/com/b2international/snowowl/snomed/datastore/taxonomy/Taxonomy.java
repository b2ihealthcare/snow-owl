/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.collections.ints.IntSet;
import com.google.common.base.Preconditions;

/**
 * @since 4.6
 */
public final class Taxonomy {
	
	private final TaxonomyGraph newTaxonomy;
	private final TaxonomyGraph oldTaxonomy;
	private final IntSet newEdges;
	private final IntSet changedEdges;
	private final IntSet detachedEdges;
	private final TaxonomyGraphStatus status;

	public Taxonomy(TaxonomyGraph newTaxonomy, TaxonomyGraph oldTaxonomy, TaxonomyGraphStatus status, IntSet newEdges, IntSet changedEdges, IntSet detachedEdges) {
		this.newTaxonomy = newTaxonomy;
		Preconditions.checkState(!newTaxonomy.isDirty(), "Builder for representing the new state of the taxonomy has dirty state.");
		this.oldTaxonomy = oldTaxonomy;
		this.status = status;
		this.newEdges = newEdges;
		this.changedEdges = changedEdges;
		this.detachedEdges = detachedEdges;
	}
	
	public TaxonomyGraph getNewTaxonomy() {
		return newTaxonomy;
	}
	
	public TaxonomyGraph getOldTaxonomy() {
		return oldTaxonomy;
	}
	
	public TaxonomyGraphStatus getStatus() {
		return status;
	}

	public IntSet getNewEdges() {
		return newEdges;
	}
	
	public IntSet getChangedEdges() {
		return changedEdges;
	}
	
	public IntSet getDetachedEdges() {
		return detachedEdges;
	}

}
