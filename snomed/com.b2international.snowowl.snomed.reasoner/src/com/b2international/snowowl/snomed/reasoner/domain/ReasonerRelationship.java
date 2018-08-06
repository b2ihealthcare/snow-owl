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
package com.b2international.snowowl.snomed.reasoner.domain;

import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.core.domain.RelationshipModifier;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 7.0
 */
public final class ReasonerRelationship {

	private String id;
	private boolean released;
	private String moduleId;
	private boolean destinationNegated;
	private Integer group;
	private Integer unionGroup;
	private CharacteristicType characteristicType;
	private RelationshipModifier modifier;
	private SnomedConcept source;
	private SnomedConcept destination;
	private SnomedConcept type;
	private SnomedReferenceSetMembers members;

	public ReasonerRelationship() {
	}
	
	public ReasonerRelationship(String id) {
		setId(id);
	}
	
	public String getId() {
		return id;
	}
	
	private void setId(String id) {
		this.id = id;
	}
	
	public boolean isReleased() {
		return released;
	}
	
	public void setReleased(boolean released) {
		this.released = released;
	}
	
	public String getModuleId() {
		return moduleId;
	}
	
	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}
	
	public void setMembers(SnomedReferenceSetMembers members) {
		this.members = members;
	}

	/**
	 * Returns the expanded reference set members if any, otherwise it returns a <code>null</code> {@link SnomedReferenceSetMembers}.
	 * @return
	 */
	public SnomedReferenceSetMembers getMembers() {
		return members;
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
	public CharacteristicType getCharacteristicType() {
		return characteristicType;
	}

	/**
	 * Returns the relationship's modifier value.
	 * 
	 * @return the modifier of this relationship
	 */
	public RelationshipModifier getModifier() {
		return modifier;
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

	public void setCharacteristicType(final CharacteristicType characteristicType) {
		this.characteristicType = characteristicType;
	}

	public void setModifier(final RelationshipModifier modifier) {
		this.modifier = modifier;
	}
	
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("ReasonerRelationship [getId()=");
		builder.append(getId());
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
