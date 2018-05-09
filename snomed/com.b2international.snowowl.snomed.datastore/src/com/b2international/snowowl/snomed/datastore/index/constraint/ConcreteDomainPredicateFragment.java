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

import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.snomedrefset.DataType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;

/**
 * @since 6.5
 */
public final class ConcreteDomainPredicateFragment extends PredicateFragment {

	private final String label; // "Vaccine"
	private final String name; // "canBeTaggedWithVaccine"
	private final DataType type;
	private final String characteristicTypeId;
	
	@JsonCreator
	ConcreteDomainPredicateFragment(
			@JsonProperty("uuid") final String uuid, 
			@JsonProperty("active") final boolean active, 
			@JsonProperty("effectiveTime") final long effectiveTime, 
			@JsonProperty("author") final String author,
			@JsonProperty("label") final String label, 
			@JsonProperty("name") final String name, 
			@JsonProperty("type") final DataType type, 
			@JsonProperty("characteristicTypeId") final String characteristicTypeId) {

		super(uuid, active, effectiveTime, author);

		this.label = label;
		this.name = name;
		this.type = type;
		this.characteristicTypeId = characteristicTypeId;
	}

	public String getLabel() {
		return label;
	}

	public String getName() {
		return name;
	}

	public DataType getType() {
		return type;
	}

	public String getCharacteristicTypeId() {
		return characteristicTypeId;
	}

	@JsonIgnore
	public String getCharacteristicTypeExpression() {
		return Strings.isNullOrEmpty(characteristicTypeId) 
					? "<" + Concepts.CHARACTERISTIC_TYPE 
					: "<<" + characteristicTypeId;
	}
}
