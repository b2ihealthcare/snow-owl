/*
 * Copyright 2019-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.index.entry;

import java.io.Serializable;
import java.util.Objects;

import com.b2international.index.Doc;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.RelationshipValue;
import com.b2international.snowowl.snomed.core.domain.RelationshipValueType;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.datastore.StatementFragment;
import com.b2international.snowowl.snomed.datastore.StatementFragmentWithDestination;
import com.b2international.snowowl.snomed.datastore.StatementFragmentWithValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

/**
 * @since 6.14
 */
@Doc(type = "owlRelationship", nested = true)
public final class SnomedOWLRelationshipDocument implements Serializable {

	public static SnomedOWLRelationshipDocument create(final String typeId, final String destinationId, final int group) {
		return new SnomedOWLRelationshipDocument(typeId, destinationId, null, null, null, null, group);
	}
	
	public static SnomedOWLRelationshipDocument createValue(final String typeId, final RelationshipValue value, final int group) {
		return value.map(
			i -> new SnomedOWLRelationshipDocument(typeId, null, i, null, null, RelationshipValueType.INTEGER, group),
			d -> new SnomedOWLRelationshipDocument(typeId, null, null, d, null, RelationshipValueType.DECIMAL, group),
			s -> new SnomedOWLRelationshipDocument(typeId, null, null, null, s, RelationshipValueType.STRING, group));
	}
	
	public static SnomedOWLRelationshipDocument createFrom(final SnomedRelationship r) {
		if (r.hasValue()) {
			return createValue(r.getTypeId(), r.getValueAsObject(), r.getGroup());
		} else {
			return create(r.getTypeId(), r.getDestinationId(), r.getGroup());
		}
	}

	private final String typeId;
	private final String destinationId;
	private final Integer integerValue;
	private final Double decimalValue;
	private final String stringValue;
	private final RelationshipValueType valueType;
	private final int group;

	@JsonCreator
	private SnomedOWLRelationshipDocument(
			@JsonProperty("typeId") final String typeId, 
			@JsonProperty("destinationId") final String destinationId,
			@JsonProperty("integerValue") final Integer integerValue,
			@JsonProperty("decimalValue") final Double decimalValue,
			@JsonProperty("stringValue") final String stringValue,
			@JsonProperty("valueType") final RelationshipValueType valueType,
			@JsonProperty("group") final int group) {
		this.typeId = typeId;
		this.destinationId = destinationId;
		this.integerValue = integerValue;
		this.decimalValue = decimalValue;
		this.stringValue = stringValue;
		this.valueType = valueType;
		this.group = group;
	}

	public String getTypeId() {
		return typeId;
	}

	public String getDestinationId() {
		return destinationId;
	}

	Integer getIntegerValue() {
		return integerValue;
	}
	
	Double getDecimalValue() {
		return decimalValue;
	}
	
	String getStringValue() {
		return stringValue;
	}
	
	public RelationshipValueType getValueType() {
		return valueType;
	}

	public int getGroup() {
		return group;
	}

	@JsonIgnore
	public boolean isIsa() {
		return Concepts.IS_A.equals(typeId);
	}

	@JsonIgnore
	public RelationshipValue getValueAsObject() {
		return RelationshipValue.fromTypeAndObjects(valueType, integerValue, decimalValue, stringValue);
	}

	@JsonIgnore
	public boolean hasValue() {
		return valueType != null;
	}

	@JsonIgnore
	public StatementFragment toStatementFragment(final int groupOffset) {
		final int adjustedGroup;
		if (group == 0) {
			adjustedGroup = group;
		} else {
			adjustedGroup = group + groupOffset;
		}

		if (destinationId != null) {
			return new StatementFragmentWithDestination(
				Long.parseLong(typeId),         // typeId        
				adjustedGroup,                  // adjustedGroup
				0,                              // unionGroup   
				false,                          // universal    
				-1L,                            // statementId  
				-1L,                            // moduleId     
				false,                          // released     
				Long.parseLong(destinationId),  // destinationId
				false);                         // destinationNegated	
		} else {
			return new StatementFragmentWithValue(
				Long.parseLong(typeId),          // typeId        
				adjustedGroup,                   // adjustedGroup
				0,                               // unionGroup   
				false,                           // universal    
				-1L,                             // statementId  
				-1L,                             // moduleId     
				false,                           // released     
				getValueAsObject().toLiteral()); // value	
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(
			typeId, 
			destinationId, 
			integerValue, 
			decimalValue, 
			stringValue, 
			valueType, 
			group);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }

		final SnomedOWLRelationshipDocument other = (SnomedOWLRelationshipDocument) obj;

		return Objects.equals(typeId, other.typeId)
			&& Objects.equals(destinationId, other.destinationId)
			&& Objects.equals(integerValue, other.integerValue)
			&& Objects.equals(decimalValue, other.decimalValue)
			&& Objects.equals(stringValue, other.stringValue)
			&& Objects.equals(valueType, other.valueType)
			&& Objects.equals(group, other.group);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
			.add("typeId", typeId)
			.add("destinationId", destinationId)
			.add("value", getValueAsObject())
			.add("group", group)
			.toString();
	}
}
