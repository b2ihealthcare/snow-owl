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
package com.b2international.snowowl.datastore.server.review;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 4.2
 */
public abstract class ConceptChangesMixin {

	@JsonCreator
	private ConceptChangesMixin(@JsonProperty("id") final String id,
			@JsonProperty("newConcepts") final Set<String> newConcepts, 
			@JsonProperty("changedConcepts") final Set<String> changedConcepts, 
			@JsonProperty("deletedConcepts") final Set<String> deletedConcepts) {
		// Empty mixin constructor
	}

	@JsonProperty("id") 
	public abstract String id();

	@JsonProperty("newConcepts") 
	public abstract Set<String> newConcepts();

	@JsonProperty("changedConcepts") 
	public abstract Set<String> changedConcepts();

	@JsonProperty("deletedConcepts") 
	public abstract Set<String> deletedConcepts();
}
