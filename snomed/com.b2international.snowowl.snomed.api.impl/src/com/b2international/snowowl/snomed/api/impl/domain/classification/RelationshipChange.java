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
package com.b2international.snowowl.snomed.api.impl.domain.classification;

import com.b2international.snowowl.snomed.api.domain.RelationshipModifier;
import com.b2international.snowowl.snomed.api.domain.classification.ChangeNature;
import com.b2international.snowowl.snomed.api.domain.classification.IRelationshipChange;

/**
 */
public class RelationshipChange implements IRelationshipChange {

	private ChangeNature changeNature;
	private String sourceId;
	private String typeId;
	private String destinationId;
	private boolean destinationNegated;
	private String characteristicTypeId;
	private int group;
	private int unionGroup;
	private RelationshipModifier modifier;

	@Override
	public ChangeNature getChangeNature() {
		return changeNature;
	}

	@Override
	public String getSourceId() {
		return sourceId;
	}

	@Override
	public String getTypeId() {
		return typeId;
	}

	@Override
	public String getDestinationId() {
		return destinationId;
	}

	@Override
	public boolean isDestinationNegated() {
		return destinationNegated;
	}

	public String getCharacteristicTypeId() {
		return characteristicTypeId;
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
	public RelationshipModifier getModifier() {
		return modifier;
	}

	public void setChangeNature(final ChangeNature changeNature) {
		this.changeNature = changeNature;
	}

	public void setSourceId(final String sourceId) {
		this.sourceId = sourceId;
	}

	public void setTypeId(final String typeId) {
		this.typeId = typeId;
	}

	public void setDestinationId(final String destinationId) {
		this.destinationId = destinationId;
	}

	public void setDestinationNegated(final boolean destinationNegated) {
		this.destinationNegated = destinationNegated;
	}

	public void setCharacteristicTypeId(String characteristicTypeId) {
		this.characteristicTypeId = characteristicTypeId;
	}

	public void setGroup(final int group) {
		this.group = group;
	}

	public void setUnionGroup(final int unionGroup) {
		this.unionGroup = unionGroup;
	}

	public void setModifier(final RelationshipModifier modifier) {
		this.modifier = modifier;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("RelationshipChange [changeNature=");
		builder.append(changeNature);
		builder.append(", sourceId=");
		builder.append(sourceId);
		builder.append(", typeId=");
		builder.append(typeId);
		builder.append(", destinationId=");
		builder.append(destinationId);
		builder.append(", destinationNegated=");
		builder.append(destinationNegated);
		builder.append(", characteristicTypeId=");
		builder.append(characteristicTypeId);
		builder.append(", group=");
		builder.append(group);
		builder.append(", unionGroup=");
		builder.append(unionGroup);
		builder.append(", modifier=");
		builder.append(modifier);
		builder.append("]");
		return builder.toString();
	}
}