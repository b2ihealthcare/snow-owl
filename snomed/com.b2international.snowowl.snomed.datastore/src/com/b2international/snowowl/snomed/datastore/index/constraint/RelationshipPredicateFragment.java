/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.index.constraint;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 6.5
 */
public final class RelationshipPredicateFragment extends PredicateFragment {

	private final ConceptSetDefinitionFragment attribute;
	private final ConceptSetDefinitionFragment range;
	private final String characteristicTypeId;

	@JsonCreator
	RelationshipPredicateFragment(
			@JsonProperty("uuid") final String uuid, 
			@JsonProperty("active") final boolean active, 
			@JsonProperty("effectiveTime") final long effectiveTime, 
			@JsonProperty("author") final String author,
			@JsonProperty("attribute") final ConceptSetDefinitionFragment attribute, 
			@JsonProperty("range") final ConceptSetDefinitionFragment range,
			@JsonProperty("characteristicTypeId") final String characteristicTypeId) {

		super(uuid, active, effectiveTime, author);

		this.attribute = attribute;
		this.range = range;
		this.characteristicTypeId = characteristicTypeId;
	}

	public ConceptSetDefinitionFragment getAttribute() {
		return attribute;
	}

	public ConceptSetDefinitionFragment getRange() {
		return range;
	}

	public String getCharacteristicTypeId() {
		return characteristicTypeId;
	}
}
