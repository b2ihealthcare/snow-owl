/*
 * Copyright 2019-2024 B2i Healthcare, https://b2ihealthcare.com
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
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import com.b2international.index.Doc;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.RelationshipValue;
import com.b2international.snowowl.snomed.core.domain.RelationshipValueType;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.refset.OwlRelationship;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedOWLRelationship;
import com.b2international.snowowl.snomed.datastore.StatementFragment;
import com.b2international.snowowl.snomed.datastore.StatementFragmentWithDestination;
import com.b2international.snowowl.snomed.datastore.StatementFragmentWithValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.base.MoreObjects;

/**
 * @since 6.14
 */
@Doc(type = "owlRelationship", nested = true)
@JsonDeserialize(builder = SnomedOWLRelationshipDocument.Builder.class)
public final class SnomedOWLRelationshipDocument implements OwlRelationship, Serializable {

	public static SnomedOWLRelationshipDocument create(final String typeId, final String destinationId, final int relationshipGroup) {
		return new Builder()
			.typeId(typeId)
			.destinationId(destinationId)
			.relationshipGroup(relationshipGroup)
			.build();
	}
	
	public static SnomedOWLRelationshipDocument createValue(final String typeId, final RelationshipValue value, final int relationshipGroup) {
		final Builder builder = new Builder()
			.typeId(typeId)
			.relationshipGroup(relationshipGroup);
		
		return value.map(
			i -> builder.valueType(RelationshipValueType.INTEGER).numericValue(new BigDecimal(i)),
			d -> builder.valueType(RelationshipValueType.DECIMAL).numericValue(d),
			s -> builder.valueType(RelationshipValueType.STRING).stringValue(s))
		.build();
	}
	
	public static SnomedOWLRelationshipDocument createFrom(final SnomedRelationship r) {
		if (r.hasValue()) {
			return createValue(r.getTypeId(), r.getValueAsObject(), r.getRelationshipGroup());
		} else {
			return create(r.getTypeId(), r.getDestinationId(), r.getRelationshipGroup());
		}
	}
	
	public static SnomedOWLRelationshipDocument createFrom(final SnomedOWLRelationship r) {
		if (r.hasValue()) {
			return createValue(r.getTypeId(), r.getValueAsObject(), r.getRelationshipGroup());
		} else {
			return create(r.getTypeId(), r.getDestinationId(), r.getRelationshipGroup());
		}
	}
	
	public static List<SnomedOWLRelationshipDocument> createFrom(List<SnomedOWLRelationship> owLRelationships) {
		if (owLRelationships == null) {
			return null;
		}
		return owLRelationships.stream().map(SnomedOWLRelationshipDocument::createFrom).toList();
	}

	@JsonPOJOBuilder(withPrefix="")
	public static final class Builder {
		private String typeId;
		private String destinationId;
		private BigDecimal numericValue;
		private String stringValue;
		private RelationshipValueType valueType;
		private Integer relationshipGroup;
		
		@JsonCreator
		private Builder() { }
		
		public Builder typeId(final String typeId) {
			this.typeId = typeId;
			return this;
		}
		
		public Builder destinationId(final String destinationId) {
			this.destinationId = destinationId;
			return this;
		}
		
		public Builder numericValue(final BigDecimal numericValue) {
			this.numericValue = numericValue;
			return this;
		}
		
		public Builder stringValue(final String stringValue) {
			this.stringValue = stringValue;
			return this;
		}
		
		public Builder valueType(final RelationshipValueType valueType) {
			this.valueType = valueType;
			return this;
		}
		
		public Builder relationshipGroup(final Integer relationshipGroup) {
			this.relationshipGroup = relationshipGroup;
			return this;
		}
		
		public SnomedOWLRelationshipDocument build() {
			return new SnomedOWLRelationshipDocument(typeId, destinationId, numericValue, stringValue, valueType, relationshipGroup);
		}
	}
	
	private final String typeId;
	private final String destinationId;
	private final BigDecimal numericValue;
	private final String stringValue;
	private final RelationshipValueType valueType;
	private final Integer relationshipGroup;

	private SnomedOWLRelationshipDocument(
			@JsonProperty("typeId") final String typeId, 
			@JsonProperty("destinationId") final String destinationId,
			@JsonProperty("numericValue") final BigDecimal numericValue,
			@JsonProperty("stringValue") final String stringValue,
			@JsonProperty("valueType") final RelationshipValueType valueType,
			@JsonProperty("relationshipGroup") final Integer relationshipGroup) {
		this.typeId = typeId;
		this.destinationId = destinationId;
		this.numericValue = numericValue;
		this.stringValue = stringValue;
		this.valueType = valueType;
		this.relationshipGroup = relationshipGroup;
	}

	@Override
	public String getTypeId() {
		return typeId;
	}

	@Override
	public String getDestinationId() {
		return destinationId;
	}

	@JsonProperty
	BigDecimal getNumericValue() {
		return numericValue;
	}
	
	@JsonProperty
	String getStringValue() {
		return stringValue;
	}
	
	public RelationshipValueType getValueType() {
		return valueType;
	}

	@Override
	public Integer getRelationshipGroup() {
		return relationshipGroup;
	}

	@JsonIgnore
	public boolean isIsa() {
		return Concepts.IS_A.equals(typeId);
	}

	@JsonIgnore
	@Override
	public RelationshipValue getValueAsObject() {
		return RelationshipValue.fromTypeAndObjects(valueType, numericValue, stringValue);
	}

	@JsonIgnore
	@Override
	public boolean hasValue() {
		return valueType != null;
	}

	@JsonIgnore
	public StatementFragment toStatementFragment(final int groupOffset) {
		final int adjustedGroup;
		if (relationshipGroup == 0) {
			adjustedGroup = relationshipGroup;
		} else {
			adjustedGroup = relationshipGroup + groupOffset;
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
				valueType,                       // valueType
				getRawValue());                  // rawValue
		}
	}

	@JsonIgnore
	public String getRawValue() {
		if (!hasValue()) {
			return null;
		} else if (RelationshipValueType.STRING.equals(valueType)) {
			return stringValue;
		} else {
			return numericValue.toPlainString();
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(
			typeId, 
			destinationId, 
			numericValue, 
			stringValue, 
			valueType, 
			relationshipGroup);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }

		final SnomedOWLRelationshipDocument other = (SnomedOWLRelationshipDocument) obj;

		return Objects.equals(typeId, other.typeId)
			&& Objects.equals(destinationId, other.destinationId)
			&& Objects.equals(numericValue, other.numericValue)
			&& Objects.equals(stringValue, other.stringValue)
			&& Objects.equals(valueType, other.valueType)
			&& Objects.equals(relationshipGroup, other.relationshipGroup);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
			.add("typeId", typeId)
			.add("destinationId", destinationId)
			.add("value", getValueAsObject())
			.add("relationshipGroup", relationshipGroup)
			.toString();
	}
}
