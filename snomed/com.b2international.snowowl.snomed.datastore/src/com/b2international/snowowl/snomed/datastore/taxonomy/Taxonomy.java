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

import java.util.Set;

import com.google.common.base.Preconditions;

/**
 * @since 4.6
 */
public final class Taxonomy {
	
	private final TaxonomyGraph newTaxonomy;
	private final TaxonomyGraph oldTaxonomy;
	private final Set<String> newEdges;
	private final Set<String> changedEdges;
	private final Set<String> detachedEdges;
	private final TaxonomyGraphStatus status;

	public Taxonomy(TaxonomyGraph newTaxonomy, TaxonomyGraph oldTaxonomy, TaxonomyGraphStatus status, Set<String> newEdges, Set<String> changedEdges, Set<String> detachedEdges) {
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

	public Set<String> getNewEdges() {
		return newEdges;
	}
	
	public Set<String> getChangedEdges() {
		return changedEdges;
	}
	
	public Set<String> getDetachedEdges() {
		return detachedEdges;
	}

}
