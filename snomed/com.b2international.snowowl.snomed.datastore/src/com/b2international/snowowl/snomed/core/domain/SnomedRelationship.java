/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
	id = SnomedTerminologyComponentConstants.RELATIONSHIP, 
	shortId = SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER,
	name = "SNOMED CT Relationship",			
	componentCategory = ComponentCategory.RELATIONSHIP,
	docType = SnomedRelationshipIndexEntry.class
)
public final class SnomedRelationship extends SnomedCoreComponent {

	private static final long serialVersionUID = -1131388567716570593L;
	
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

 /*
	* @since 6.16 
	*/
 public static final class Fields extends SnomedCoreComponent.Fields {

	 public static final String SOURCE_ID = SnomedRf2Headers.FIELD_SOURCE_ID;
	 public static final String DESTINATION_ID = SnomedRf2Headers.FIELD_DESTINATION_ID;
	 public static final String GROUP = "group";
	 public static final String UNION_GROUP = "unionGroup";
	 public static final String CHARACTERISTIC_TYPE_ID = SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID;
	 public static final String TYPE_ID = SnomedRf2Headers.FIELD_TYPE_ID;
	 public static final String MODIFIER_ID = SnomedRf2Headers.FIELD_MODIFIER_ID;
	 
	 public static final Set<String> ALL = ImmutableSet.of(
			 // RF2 fields
			 ID,
			 ACTIVE,
			 EFFECTIVE_TIME,
			 MODULE_ID,
			 SOURCE_ID,
			 DESTINATION_ID,
			 GROUP,
			 UNION_GROUP,
			 TYPE_ID,
			 CHARACTERISTIC_TYPE_ID,
			 MODIFIER_ID,
			 // additional fields
			 RELEASED);

 }
	
	private boolean destinationNegated;
	private Integer group;
	private Integer unionGroup;
	private SnomedConcept characteristicType;
	private SnomedConcept modifier;
	private SnomedConcept source;
	private SnomedConcept destination;
	private SnomedConcept type;

	public SnomedRelationship() {
	}
	
	public SnomedRelationship(String id) {
		setId(id);
	}
	
	@Override
	public short getTerminologyComponentId() {
		return SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER;
	}

	@JsonProperty
	public String getSourceId() {
		return getSource() == null ? null : getSource().getId();
	}
	
	/**
	 * Returns the source concept of this relationship.
	 * 
	 * @return
	 */
	public SnomedConcept getSource() {
		return source;
	}

	@JsonProperty
	public String getDestinationId() {
		return getDestination() == null ? null : getDestination().getId();
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
		return getType() == null ? null : getType().getId();
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
	public Integer getGroup() {
		return group;
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
		return getCharacteristicType() == null ? null : getCharacteristicType().getId();
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
		return getModifier() == null ? null : getModifier().getId();
	}

	public void setSource(SnomedConcept source) {
		this.source = source;
	}
	
	@JsonIgnore
	public void setSourceId(String sourceId) {
		setSource(new SnomedConcept(sourceId));
	}

	public void setDestination(SnomedConcept destination) {
		this.destination = destination;
	}
	
	@JsonIgnore
	public void setDestinationId(String destinationId) {
		setDestination(new SnomedConcept(destinationId));
	}
	
	public void setType(SnomedConcept type) {
		this.type = type;
	}
	
	@JsonIgnore
	public void setTypeId(String typeId) {
		setType(new SnomedConcept(typeId));
	}
	
	public void setDestinationNegated(final boolean destinationNegated) {
		this.destinationNegated = destinationNegated;
	}

	public void setGroup(final Integer group) {
		this.group = group;
	}

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
		setCharacteristicType(new SnomedConcept(characteristicTypeId));
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
		setModifier(new SnomedConcept(modifierId));
	}

	@Override
	public Request<TransactionContext, String> toCreateRequest(String containerId) {
		return SnomedRequests.prepareNewRelationship()
				.setActive(isActive())
				.setCharacteristicTypeId(getCharacteristicTypeId())
				.setDestinationId(getDestinationId())
				.setDestinationNegated(isDestinationNegated())
				.setGroup(getGroup())
				.setId(getId())
				.setModifierId(getModifierId())
				.setModuleId(getModuleId())
				.setSourceId(containerId)
				.setTypeId(getTypeId())
				.setUnionGroup(getUnionGroup())
				.build();
	}
	
	@Override
	public Request<TransactionContext, Boolean> toUpdateRequest() {
		return SnomedRequests.prepareUpdateRelationship(getId())
				.setActive(isActive())
				.setGroup(getGroup())
				.setCharacteristicTypeId(getCharacteristicTypeId())
				.setModifierId(getModifierId())
				.setModuleId(getModuleId())
				.setUnionGroup(getUnionGroup())
				.setTypeId(getTypeId())
				.setDestinationId(getDestinationId())
				.build();
	}
	
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("SnomedRelationship [getId()=");
		builder.append(getId());
		builder.append(", isReleased()=");
		builder.append(isReleased());
		builder.append(", isActive()=");
		builder.append(isActive());
		builder.append(", getEffectiveTime()=");
		builder.append(getEffectiveTime());
		builder.append(", getModuleId()=");
		builder.append(getModuleId());
		builder.append(", getSourceId()=");
		builder.append(getSourceId());
		builder.append(", getDestinationId()=");
		builder.append(getDestinationId());
		builder.append(", isDestinationNegated()=");
		builder.append(isDestinationNegated());
		builder.append(", getTypeId()=");
		builder.append(getTypeId());
		builder.append(", getGroup()=");
		builder.append(getGroup());
		builder.append(", getUnionGroup()=");
		builder.append(getUnionGroup());
		builder.append(", getCharacteristicType()=");
		builder.append(getCharacteristicType());
		builder.append(", getModifier()=");
		builder.append(getModifier());
		builder.append("]");
		return builder.toString();
	}
}