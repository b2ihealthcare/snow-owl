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
import com.google.common.base.Objects;

/**
 * Represents a summary of a concrete domain reference set member in an {@link IConcreteDomainChangeEntry}.
 */
public class ConcreteDomainElement implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String attributeDisplayName;
	private final String value;
	private final ChangeConcept unit;
	private final DataType dataType;

	/**
	 * Creates a new concrete domain element with the specified arguments.
	 * 
	 * @param attributeDisplayName the display name of the concrete domain member's attribute
	 * @param value the value of the concrete domain member, suitable for display on the UI
	 * @param unit the UOM component of the concrete domain member (can be {@code null} if no unit is associated with this member)
	 */
	public ConcreteDomainElement(final String attributeDisplayName, final String value, final ChangeConcept unit, DataType dataType) {
		this.attributeDisplayName = attributeDisplayName;
		this.value = value;
		this.unit = unit;
		this.dataType = dataType;
	}

	/**
	 * @return the display name of the concrete domain member's attribute
	 */
	public String getAttributeDisplayName() {
		return attributeDisplayName;
	}

	/**
	 * @return the value of the concrete domain member, suitable for display on the UI
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @return the UOM component of the concrete domain member (can be {@code null} if no unit is associated with this member)
	 */
	public ChangeConcept getUnit() {
		return unit;
	}
	
	/**
	 * @return the DataType of the concrete domain member
	 */
	public DataType getDataType() {
		return dataType;
	}

	@Override 
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attributeDisplayName == null) ? 0 : attributeDisplayName.hashCode());
		result = prime * result + ((unit == null) ? 0 : unit.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override 
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }

		final ConcreteDomainElement other = (ConcreteDomainElement) obj;

		if (!Objects.equal(attributeDisplayName, other.attributeDisplayName)) { return false; }
		if (!Objects.equal(unit, other.unit)) { return false; }
		if (!Objects.equal(value, other.value)) { return false; }
		return true;
	}
}
