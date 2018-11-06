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
package com.b2international.snowowl.snomed.reasoner.classification.entry;

import java.io.Serializable;

import com.b2international.snowowl.snomed.snomedrefset.DataType;
import java.util.Objects;

/**
 * Represents a summary of a concrete domain reference set member in an {@link IConcreteDomainChangeEntry}.
 */
public class ConcreteDomainElement implements Serializable {

	private static final long serialVersionUID = 2L;

	private final ChangeConcept type;
	private final String value;
	private final DataType dataType;
	private final int group;

	/**
	 * Creates a new concrete domain element with the specified arguments.
	 * 
	 * @param type the CD member's type, represented as a {@link ChangeConcept}
	 * @param value the value of the concrete domain member, suitable for display on the UI
	 * @param dataType the CD member data type
	 * @param group the relationship group of the CD member
	 */
	public ConcreteDomainElement(final ChangeConcept type, final String value, final DataType dataType, final int group) {
		this.type = type;
		this.value = value;
		this.dataType = dataType;
		this.group = group;
	}

	public ChangeConcept getType() {
		return type;
	}

	public String getValue() {
		return value;
	}

	public DataType getDataType() {
		return dataType;
	}

	public int getGroup() {
		return group;
	}

	@Override 
	public int hashCode() {
		return Objects.hash(type, value, dataType, group);
	}

	@Override 
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }

		final ConcreteDomainElement other = (ConcreteDomainElement) obj;

		if (!Objects.equals(type, other.type)) { return false; }
		if (!Objects.equals(value, other.value)) { return false; }
		if (dataType != other.dataType) { return false; }
		if (group != other.group) { return false; }
		
		return true;
	}
}
