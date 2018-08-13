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

import java.util.Objects;

import com.b2international.snowowl.snomed.core.domain.refset.DataType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 6.5
 */
public final class ConcreteDomainPredicateFragment extends PredicateFragment {

	private final String label; // "Vaccine"
	private final String name; // "canBeTaggedWithVaccine"
	private final DataType dataType;
	private final String characteristicTypeId;
	
	@JsonCreator
	public ConcreteDomainPredicateFragment(
			@JsonProperty("uuid") final String uuid, 
			@JsonProperty("active") final boolean active, 
			@JsonProperty("effectiveTime") final long effectiveTime, 
			@JsonProperty("author") final String author,
			@JsonProperty("label") final String label, 
			@JsonProperty("name") final String name, 
			@JsonProperty("dataType") final DataType dataType, 
			@JsonProperty("characteristicTypeId") final String characteristicTypeId) {

		super(uuid, active, effectiveTime, author);

		this.label = label;
		this.name = name;
		this.dataType = dataType;
		this.characteristicTypeId = characteristicTypeId;
	}

	public String getLabel() {
		return label;
	}

	public String getName() {
		return name;
	}

	public DataType getDataType() {
		return dataType;
	}

	public String getCharacteristicTypeId() {
		return characteristicTypeId;
	}
	
	@Override
	public int hashCode() {
		return 31 * super.hashCode() + Objects.hash(label, name, dataType, characteristicTypeId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass()) return false;
		
		ConcreteDomainPredicateFragment other = (ConcreteDomainPredicateFragment) obj;
		
		return Objects.equals(label, other.label)
				&& Objects.equals(name, other.name)
				&& Objects.equals(dataType, other.dataType)
				&& Objects.equals(characteristicTypeId, other.characteristicTypeId);
	}
}
