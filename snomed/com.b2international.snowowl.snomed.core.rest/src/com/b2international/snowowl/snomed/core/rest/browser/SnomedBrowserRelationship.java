/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.b2international.snowowl.snomed.core.rest.browser;

import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.core.domain.RelationshipModifier;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public class SnomedBrowserRelationship extends SnomedBrowserComponent implements ISnomedBrowserRelationship {

	private String relationshipId;
	
	public SnomedBrowserRelationship () {
	}
	
	public SnomedBrowserRelationship (String relationshipId) {
		this.relationshipId = relationshipId;
	}

	@JsonDeserialize(as=SnomedBrowserRelationshipType.class)
	private ISnomedBrowserRelationshipType type;

	@JsonDeserialize(as=SnomedBrowserRelationshipTarget.class)
	private ISnomedBrowserRelationshipTarget target;

	private String sourceId;
	private int groupId;
	private CharacteristicType characteristicType;
	private RelationshipModifier modifier;

	@Override
	public String getId() {
		return relationshipId;
	}

	@Override
	public String getRelationshipId() {
		return relationshipId;
	}

	@Override
	public ISnomedBrowserRelationshipType getType() {
		return type;
	}

	@Override
	public ISnomedBrowserRelationshipTarget getTarget() {
		return target;
	}

	@Override
	public String getSourceId() {
		return sourceId;
	}

	@Override
	public int getGroupId() {
		return groupId;
	}

	@Override
	public CharacteristicType getCharacteristicType() {
		return characteristicType;
	}

	@Override
	public RelationshipModifier getModifier() {
		return modifier;
	}

	public void setRelationshipId(String relationshipId) {
		this.relationshipId = relationshipId;
	}

	public void setType(final ISnomedBrowserRelationshipType type) {
		this.type = type;
	}

	public void setTarget(final ISnomedBrowserRelationshipTarget target) {
		this.target = target;
	}

	public void setSourceId(final String sourceId) {
		this.sourceId = sourceId;
	}

	public void setGroupId(final int groupId) {
		this.groupId = groupId;
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
		builder.append("SnomedBrowserRelationship [ id=");
		builder.append(relationshipId);
		builder.append(", type=");
		builder.append(type);
		builder.append(", relationshipId=");
		builder.append(relationshipId);
		builder.append(", target=");
		builder.append(target);
		builder.append(", sourceId=");
		builder.append(sourceId);
		builder.append(", groupId=");
		builder.append(groupId);
		builder.append(", characteristicType=");
		builder.append(characteristicType);
		builder.append(", modifier=");
		builder.append(modifier);
		builder.append(", getEffectiveTime()=");
		builder.append(getEffectiveTime());
		builder.append(", getModuleId()=");
		builder.append(getModuleId());
		builder.append(", isActive()=");
		builder.append(isActive());
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((relationshipId == null) ? 0 : relationshipId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SnomedBrowserRelationship other = (SnomedBrowserRelationship) obj;
		if (relationshipId == null) {
			if (other.relationshipId != null)
				return false;
		} else if (!relationshipId.equals(other.relationshipId))
			return false;
		return true;
	}

}
