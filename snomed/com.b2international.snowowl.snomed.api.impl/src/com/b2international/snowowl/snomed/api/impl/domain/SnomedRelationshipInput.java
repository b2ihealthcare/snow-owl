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
package com.b2international.snowowl.snomed.api.impl.domain;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.snomed.api.domain.CharacteristicType;
import com.b2international.snowowl.snomed.api.domain.ISnomedRelationshipInput;
import com.b2international.snowowl.snomed.api.domain.RelationshipModifier;

/**
 * @since 4.0
 */
public class SnomedRelationshipInput extends AbstractSnomedComponentInput implements ISnomedRelationshipInput {

	@NotEmpty
	private String sourceId;
	
	@NotEmpty
	private String destinationId;
	
	@NotEmpty
	private String typeId;
	
	private boolean destinationNegated;
	
	@Min(0)
	private int group;
	
	@Min(0)
	private int unionGroup;
	
	@NotNull
	private CharacteristicType characteristicType;
	
	@NotNull
	private RelationshipModifier modifier;

	@Override
	public String getSourceId() {
		return sourceId;
	}

	@Override
	public String getDestinationId() {
		return destinationId;
	}

	@Override
	public boolean isDestinationNegated() {
		return destinationNegated;
	}

	@Override
	public String getTypeId() {
		return typeId;
	}

	@Override
	public int getGroup() {
		return group;
	}

	@Override
	public int getUnionGroup() {
		return unionGroup;
	}

	@Override
	public CharacteristicType getCharacteristicType() {
		return characteristicType;
	}

	@Override
	public RelationshipModifier getModifier() {
		return modifier;
	}

	public void setSourceId(final String sourceId) {
		this.sourceId = sourceId;
	}

	public void setDestinationId(final String destinationId) {
		this.destinationId = destinationId;
	}

	public void setDestinationNegated(final boolean destinationNegated) {
		this.destinationNegated = destinationNegated;
	}

	public void setTypeId(final String typeId) {
		this.typeId = typeId;
	}

	public void setGroup(final int group) {
		this.group = group;
	}

	public void setUnionGroup(final int unionGroup) {
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
		builder.append("SnomedRelationshipInput [getIdGenerationStrategy()=");
		builder.append(getIdGenerationStrategy());
		builder.append(", getModuleId()=");
		builder.append(getModuleId());
		builder.append(", getCodeSystemShortName()=");
		builder.append(getCodeSystemShortName());
		builder.append(", getBranchPath()=");
		builder.append(getBranchPath());
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