/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.io.Serializable;

import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedCoreComponent;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 6.11
 */
public final class ReasonerConcreteDomainMember implements Serializable {

	private String originMemberId;
	
	/*
	 * Note that the rest of the values below can be completely different (or even
	 * absent) when compared to the "origin" relationship, especially if the change
	 * is a new inference!
	 */
	private boolean released;
	private SnomedCoreComponent referencedComponent;
	private String referenceSetId;
	private Integer group;
	private String typeId;
	private String serializedValue;
	private String characteristicTypeId;
	
	// Default constructor is used in JSON de-serialization
	public ReasonerConcreteDomainMember() { }
	
	public ReasonerConcreteDomainMember(final String originMemberId) {
		setOriginMemberId(originMemberId);
	}

	public String getOriginMemberId() {
		return originMemberId;
	}

	private void setOriginMemberId(final String originMemberId) {
		this.originMemberId = originMemberId;
	}

	public boolean isReleased() {
		return released;
	}

	public void setReleased(final boolean released) {
		this.released = released;
	}

	public SnomedCoreComponent getReferencedComponent() {
		return referencedComponent;
	}

	public void setReferencedComponent(final SnomedCoreComponent referencedComponent) {
		this.referencedComponent = referencedComponent;
	}
	
	@JsonProperty
	public String getReferencedComponentId() {
		return getReferencedComponent() == null ? null : getReferencedComponent().getId();
	}
	
	@JsonIgnore
	public void setReferencedComponentId(final String referencedComponentId) {
		final ComponentCategory referencedComponentCategory = SnomedIdentifiers.getComponentCategory(referencedComponentId);

		switch (referencedComponentCategory) {
			case CONCEPT:
				setReferencedComponent(new SnomedConcept(referencedComponentId));
				break;
			case DESCRIPTION:
				setReferencedComponent(new SnomedDescription(referencedComponentId));
				break;
			case RELATIONSHIP:
				setReferencedComponent(new SnomedRelationship(referencedComponentId));
				break;
			default:
				throw new IllegalStateException(String.format("Unexpected referenced component category '%s' for SCTID '%s'.", 
						referencedComponentCategory, 
						referencedComponentId));
		}
	}

	public String getReferenceSetId() {
		return referenceSetId;
	}

	public void setReferenceSetId(final String referenceSetId) {
		this.referenceSetId = referenceSetId;
	}

	public Integer getGroup() {
		return group;
	}

	public void setGroup(final Integer group) {
		this.group = group;
	}
	
	public String getTypeId() {
		return typeId;
	}
	
	public void setTypeId(final String typeId) {
		this.typeId = typeId;
	}

	public String getSerializedValue() {
		return serializedValue;
	}

	public void setSerializedValue(final String serializedValue) {
		this.serializedValue = serializedValue;
	}

	public String getCharacteristicTypeId() {
		return characteristicTypeId;
	}

	public void setCharacteristicTypeId(final String characteristicTypeId) {
		this.characteristicTypeId = characteristicTypeId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ReasonerConcreteDomainMember [originMemberId=");
		builder.append(originMemberId);
		builder.append(", released=");
		builder.append(released);
		builder.append(", referencedComponent=");
		builder.append(referencedComponent);
		builder.append(", referenceSetId=");
		builder.append(referenceSetId);
		builder.append(", group=");
		builder.append(group);
		builder.append(", typeId=");
		builder.append(typeId);
		builder.append(", serializedValue=");
		builder.append(serializedValue);
		builder.append(", characteristicTypeId=");
		builder.append(characteristicTypeId);
		builder.append("]");
		return builder.toString();
	}
}
