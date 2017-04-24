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
package com.b2international.snowowl.datastore.review;

import java.util.Set;

import com.b2international.index.Doc;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 4.2
 */
@Doc(type="conceptchanges")
public final class ConceptChanges {

	private final String id;
	private final Set<String> newConcepts;
	private final Set<String> changedConcepts;
	private final Set<String> deletedConcepts;
	
	@JsonCreator
	public ConceptChanges(
			@JsonProperty("id") final String id,
			@JsonProperty("newConcepts") final Set<String> newConcepts, 
			@JsonProperty("changedConcepts") final Set<String> changedConcepts, 
			@JsonProperty("deletedConcepts") final Set<String> deletedConcepts) {
		this.id = id;
		this.newConcepts = newConcepts;
		this.changedConcepts = changedConcepts;
		this.deletedConcepts = deletedConcepts;
	}
	
	/**
	 * Returns the associated review's unique identifier.
	 */
	@JsonProperty
	public String id() {
		return id;
	}

	/**
	 * Returns a set of SNOMED CT concept identifiers which were marked as new in the comparison.
	 */
	@JsonProperty
	public Set<String> newConcepts() {
		return newConcepts;
	}

	/**
	 * Returns a set of SNOMED CT concept identifiers which were marked as changed in the comparison.
	 * <p>
	 * Changes on inbound relationships are not taken into account.
	 */
	@JsonProperty
	public Set<String> changedConcepts() {
		return changedConcepts;
	}
	
	/**
	 * Returns a set of SNOMED CT concept identifiers which were marked as deleted in the comparison.
	 */
	@JsonProperty
	public Set<String> deletedConcepts() {
		return deletedConcepts;
	}
	
}
