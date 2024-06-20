/*
 * Copyright 2024 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.snomed.core.domain.refset;

import static com.b2international.snowowl.core.domain.BaseComponent.ifNotNull;

import java.io.Serializable;
import java.util.Objects;

import com.b2international.snowowl.snomed.core.domain.RelationshipValue;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 9.3
 */
public final class SnomedOWLRelationship implements OwlRelationship, Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Enumerates expandable property keys.
	 * 
	 * @since 9.3
	 */
	public static final class Expand {
		public static final String TYPE = "type";
		public static final String DESTINATION = "destination";
	}
	
	private SnomedConcept type;
	
	private SnomedConcept destination;
	
	private RelationshipValue value;
	
	private Integer relationshipGroup;
	
	/**
	 * @return
	 */
	@JsonProperty
	@Override
	public String getDestinationId() {
		return ifNotNull(getDestination(), SnomedConcept::getId);
	}

	/**
	 * Returns the destination concept of this relationship.
	 * 
	 * @return
	 */
	public SnomedConcept getDestination() {
		return destination;
	}
	
	/**
	 * Returns the value associated with this relationship.
	 * 
	 * @return
	 */
	@JsonProperty("value")
	@Override
	public RelationshipValue getValueAsObject() {
		return value;
	}
	
	@JsonIgnore
	@Override
	public boolean hasValue() {
		return (value != null);
	}

	/**
	 * @return
	 */
	@JsonIgnore
	public String getValue() {
		return ifNotNull(getValueAsObject(), RelationshipValue::toLiteral);
	}
	
	/**
	 * Returns the type identifier of this relationship.
	 * 
	 * @return the relationship type identifier
	 */
	@JsonProperty
	@Override
	public String getTypeId() {
		return ifNotNull(getType(), SnomedConcept::getId);
	}

	/**
	 * Returns the type concept of this relationship.
	 * 
	 * @return
	 */
	public SnomedConcept getType() {
		return type;
	}
	
	/**
	 * Returns the relationship group number.
	 * 
	 * @return the relationship group, or 0 if this relationship can not be grouped, or is in an unnumbered, singleton group
	 */
	@Override
	public Integer getRelationshipGroup() {
		return relationshipGroup;
	}
	
	/**
	 * @param destination
	 */
	public void setDestination(SnomedConcept destination) {
		this.destination = destination;
	}

	/**
	 * @param destinationId
	 */
	@JsonIgnore
	public void setDestinationId(String destinationId) {
		setDestination(ifNotNull(destinationId, SnomedConcept::new));
	}
	
	/**
	 * @param value
	 */
	@JsonProperty("value")
	public void setValueAsObject(final RelationshipValue value) {
		this.value = value;
	}

	/**
	 * @param literal
	 */
	@JsonIgnore
	public void setValue(final String literal) {
		setValueAsObject(RelationshipValue.fromLiteral(literal));
	}

	/**
	 * @param type
	 */
	public void setType(SnomedConcept type) {
		this.type = type;
	}

	/**
	 * @param typeId
	 */
	@JsonIgnore
	public void setTypeId(String typeId) {
		setType(ifNotNull(typeId, SnomedConcept::new));
	}

	/**
	 * @param relationshipGroup
	 */
	public void setRelationshipGroup(final Integer relationshipGroup) {
		this.relationshipGroup = relationshipGroup;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getTypeId(), getDestinationId(), getValueAsObject(), getRelationshipGroup());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }

		final SnomedOWLRelationship other = (SnomedOWLRelationship) obj;

		return Objects.equals(getTypeId(), other.getTypeId())
			&& Objects.equals(getDestinationId(), other.getDestinationId())
			&& Objects.equals(getValueAsObject(), other.getValueAsObject())
			&& Objects.equals(getRelationshipGroup(), other.getRelationshipGroup());
	}
	
	public static SnomedOWLRelationship createFrom(final SnomedRelationship r) {
		if (r.hasValue()) {
			return create(r.getTypeId(), r.getValueAsObject(), r.getRelationshipGroup());
		} else {
			return create(r.getTypeId(), r.getDestinationId(), r.getRelationshipGroup());
		}
	}
	
	public static SnomedOWLRelationship create(final String typeId, final String destinationId, final int relationshipGroup) {
		SnomedOWLRelationship owlRelationship = new SnomedOWLRelationship();
		
		owlRelationship.setTypeId(typeId);
		owlRelationship.setDestinationId(destinationId);
		owlRelationship.setRelationshipGroup(relationshipGroup);
		
		return owlRelationship;
	}
	
	public static SnomedOWLRelationship create(final String typeId, final RelationshipValue value, final int relationshipGroup) {
		SnomedOWLRelationship owlRelationship = new SnomedOWLRelationship();
		
		owlRelationship.setTypeId(typeId);
		owlRelationship.setValueAsObject(value);
		owlRelationship.setRelationshipGroup(relationshipGroup);
		
		return owlRelationship;
	}
	
}
