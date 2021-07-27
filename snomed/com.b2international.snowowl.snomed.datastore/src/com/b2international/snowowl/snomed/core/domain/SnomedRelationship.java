/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.domain;

import java.util.Set;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.request.IndexResourceRequestBuilder;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.core.terminology.TerminologyComponent;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;

/**
 * Represents a SNOMED&nbsp;CT relationship.
 * <br>
 * Relationships returned by search requests are populated based on the expand parameters passed into the {@link IndexResourceRequestBuilder#setExpand(String)}
 * methods. The expand parameters can be nested allowing a fine control for the details returned in the resultset.  
 * 
 * The supported expand parameters are:
 * <p>
 * <ul>
 * <li>{@code source()} - returns the source concept of the relationship</li>
 * <li>{@code destination()} - returns the destination concept of the relationship</li>
 * <li>{@code type()} - returns the concept representing the type of the relationship</li>
 * <li>{@code members()} - returns the reference set members referencing this component</li>
 * </ul>
 * 
 * Expand parameters can be nested to further expand or filter the details returned. 
 * For example the expand string:
 * <p>{@code source(expand(pt()))}, would return the source concept's preferred term as well.
 * 
 * @see SnomedConcept
 * @see SnomedDescription
 * @see SnomedReferenceSet
 * @see SnomedReferenceSetMember
 */
@TerminologyComponent(
	name = "SNOMED CT Relationship",			
	componentCategory = ComponentCategory.RELATIONSHIP,
	docType = SnomedRelationshipIndexEntry.class
)
public final class SnomedRelationship extends SnomedCoreComponent {

	private static final long serialVersionUID = -1131388567716570593L;
	
	public static final String TYPE = "relationship";

	/**
	 * Enumerates expandable property keys.
	 * 
	 * @since 7.0
	 */
	public static final class Expand {
		public static final String SOURCE = "source";
		public static final String TYPE = "type";
		public static final String DESTINATION = "destination";
		public static final String CHARACTERISTIC_TYPE = "characteristicType";
		public static final String MODIFIER = "modifier";
	}

	/**
	 * @since 6.16 
	 */
	public static final class Fields extends SnomedCoreComponent.Fields {

		public static final String SOURCE_ID = SnomedRf2Headers.FIELD_SOURCE_ID;
		public static final String DESTINATION_ID = SnomedRf2Headers.FIELD_DESTINATION_ID;
		public static final String RELATIONSHIP_GROUP = SnomedRf2Headers.FIELD_RELATIONSHIP_GROUP;
		public static final String TYPE_ID = SnomedRf2Headers.FIELD_TYPE_ID;
		public static final String MODIFIER_ID = SnomedRf2Headers.FIELD_MODIFIER_ID;
		public static final String CHARACTERISTIC_TYPE_ID = SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID;
		
		public static final String VALUE_TYPE = SnomedRelationshipIndexEntry.Fields.VALUE_TYPE;
		public static final String NUMERIC_VALUE = SnomedRelationshipIndexEntry.Fields.NUMERIC_VALUE;
		public static final String STRING_VALUE = SnomedRelationshipIndexEntry.Fields.STRING_VALUE;
		public static final String UNION_GROUP = SnomedRelationshipIndexEntry.Fields.UNION_GROUP;

		public static final Set<String> ALL = ImmutableSet.of(
			// RF2 fields
			ID,
			ACTIVE,
			EFFECTIVE_TIME,
			MODULE_ID,
			SOURCE_ID,
			DESTINATION_ID,
			NUMERIC_VALUE,
			STRING_VALUE,
			RELATIONSHIP_GROUP,
			UNION_GROUP,
			TYPE_ID,
			CHARACTERISTIC_TYPE_ID,
			MODIFIER_ID,
			// additional fields
			VALUE_TYPE,
			RELEASED);
	}
	
	private boolean destinationNegated;
	private Integer relationshipGroup;
	private Integer unionGroup;
	private SnomedConcept characteristicType;
	private SnomedConcept modifier;
	private SnomedConcept source;
	private SnomedConcept type;
	private SnomedConcept destination;
	private RelationshipValue value;

	public SnomedRelationship() {
	}
	
	public SnomedRelationship(String id) {
		setId(id);
	}
	
	@Override
	public String getComponentType() {
		return SnomedRelationship.TYPE;
	}

	/**
	 * @return
	 */
	@JsonProperty
	public String getSourceId() {
		return ifNotNull(getSource(), SnomedConcept::getId);
	}
	
	/**
	 * Returns the source concept of this relationship.
	 * 
	 * @return
	 */
	public SnomedConcept getSource() {
		return source;
	}

	/**
	 * @return
	 */
	@JsonProperty
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
	@JsonIgnore
	public RelationshipValue getValueAsObject() {
		return value;
	}
	
	@JsonIgnore
	public boolean hasValue() {
		return (value != null);
	}

	/**
	 * @return
	 */
	// XXX: Literal form is used when transferring data over JSON
	public String getValue() {
		return ifNotNull(getValueAsObject(), RelationshipValue::toLiteral);
	}

	/**
	 * Checks whether the destination concept's meaning should be negated ({@code ObjectComplementOf} semantics in OWL2).
	 * 
	 * @return {@code true} if the destination concept is negated, {@code false} if it should be interpreted normally
	 */
	public boolean isDestinationNegated() {
		return destinationNegated;
	}

	/**
	 * Returns the type identifier of this relationship.
	 * 
	 * @return the relationship type identifier
	 */
	@JsonProperty
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
	public Integer getRelationshipGroup() {
		return relationshipGroup;
	}

	/**
	 * If multiple relationship destinations are to be taken as a disjunction, the relationships are assigned a common, positive union group number.
	 * 
	 * @return the relationship union group, or 0 if this relationship is not part of a disjunction
	 */
	public Integer getUnionGroup() {
		return unionGroup;
	}

	/**
	 * Returns the characteristic type of the relationship.
	 * 
	 * @return the relationship's characteristic type
	 */
	public SnomedConcept getCharacteristicType() {
		return characteristicType;
	}
	
	/**
	 * @return the characteristicType ID of the relationship
	 */
	public String getCharacteristicTypeId() {
		return ifNotNull(getCharacteristicType(), SnomedConcept::getId);
	}
	
	/**
	 * Returns the relationship's modifier value.
	 * 
	 * @return the modifier of this relationship
	 */
	@JsonProperty
	public SnomedConcept getModifier() {
		return modifier;
	}
	
	/**
	 * @return the modifierId of the relationship.
	 */
	public String getModifierId() {
		return ifNotNull(getModifier(), SnomedConcept::getId);
	}

	/**
	 * @param source
	 */
	public void setSource(SnomedConcept source) {
		this.source = source;
	}

	/**
	 * @param sourceId
	 */
	@JsonIgnore
	public void setSourceId(String sourceId) {
		setSource(ifNotNull(sourceId, SnomedConcept::new));
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
	@JsonIgnore
	public void setValueAsObject(final RelationshipValue value) {
		this.value = value;
	}

	/**
	 * @param literal
	 */
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
	 * @param destinationNegated
	 */
	public void setDestinationNegated(final boolean destinationNegated) {
		this.destinationNegated = destinationNegated;
	}

	/**
	 * @param relationshipGroup
	 */
	public void setRelationshipGroup(final Integer relationshipGroup) {
		this.relationshipGroup = relationshipGroup;
	}

	/**
	 * @param unionGroup
	 */
	public void setUnionGroup(final Integer unionGroup) {
		this.unionGroup = unionGroup;
	}

	/**
	 * @param characteristicType
	 */
	public void setCharacteristicType(final SnomedConcept characteristicType) {
		this.characteristicType = characteristicType;
	}
	
	/**
	 * @param characteristicTypeId
	 */
	public void setCharacteristicTypeId(final String characteristicTypeId) {
		setCharacteristicType(ifNotNull(characteristicTypeId, SnomedConcept::new));
	}

	/**
	 * @param modifier
	 */
	public void setModifier(final SnomedConcept modifier) {
		this.modifier = modifier;
	}
	
	/**
	 * @param modifierId
	 */
	public void setModifierId(final String modifierId) {
		setModifier(ifNotNull(modifierId, SnomedConcept::new));
	}
	
	@Override
	public Request<TransactionContext, String> toCreateRequest(String containerId) {
		return SnomedRequests.prepareNewRelationship()
			.setActive(isActive())
			.setCharacteristicTypeId(getCharacteristicTypeId())
			.setDestinationId(getDestinationId())
			.setDestinationNegated(isDestinationNegated())
			.setRelationshipGroup(getRelationshipGroup())
			.setId(getId())
			.setModifierId(getModifierId())
			.setModuleId(getModuleId())
			.setSourceId(containerId)
			.setTypeId(getTypeId())
			.setUnionGroup(getUnionGroup())
			.setValue(getValueAsObject())
			.build();
	}
	
	@Override
	public Request<TransactionContext, Boolean> toUpdateRequest() {
		return SnomedRequests.prepareUpdateRelationship(getId())
			.setActive(isActive())
			.setCharacteristicTypeId(getCharacteristicTypeId())
			.setDestinationId(getDestinationId())
			// TODO: add setDestinationNegated(...) here?
			.setRelationshipGroup(getRelationshipGroup())
			// no setId(...) - SCTID should not be updated
			.setModifierId(getModifierId())
			.setModuleId(getModuleId())
			// no setSourceId(...) - source concept SCTID should not be updated
			.setTypeId(getTypeId())
			.setUnionGroup(getUnionGroup())
			.setValue(getValueAsObject())
			.build();
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
			.add("id", getId())
			.add("effectiveTime", getEffectiveTime())
			.add("released", isReleased())
			.add("active", isActive())
			.add("moduleId", getModuleId())
			.add("sourceId", getSourceId())
			.add("destinationId", getDestinationId())
			.add("destinationNegated", isDestinationNegated())
			.add("value", getValue())
			.add("typeId", getTypeId())
			.add("relationshipGroup", getRelationshipGroup())
			.add("unionGroup", getUnionGroup())
			.add("characteristicTypeId", getCharacteristicTypeId())
			.add("modifierId", getModifierId())
			.toString();
	}
}
