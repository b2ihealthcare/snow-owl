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

import com.b2international.snowowl.snomed.api.domain.CharacteristicType;
import com.b2international.snowowl.snomed.api.domain.ISnomedRelationshipUpdate;
import com.b2international.snowowl.snomed.api.domain.RelationshipModifier;

public class SnomedRelationshipUpdate extends AbstractSnomedComponentUpdate implements ISnomedRelationshipUpdate {

	private Integer group;
	private Integer unionGroup;
	private CharacteristicType characteristicType;
	private RelationshipModifier modifier;

	@Override
	public Integer getGroup() {
		return group;
	}

	public void setGroup(final Integer group) {
		this.group = group;
	}

	@Override
	public Integer getUnionGroup() {
		return unionGroup;
	}

	public void setUnionGroup(final Integer unionGroup) {
		this.unionGroup = unionGroup;
	}

	@Override
	public CharacteristicType getCharacteristicType() {
		return characteristicType;
	}

	public void setCharacteristicType(final CharacteristicType characteristicType) {
		this.characteristicType = characteristicType;
	}

	@Override
	public RelationshipModifier getModifier() {
		return modifier;
	}

	public void setModifier(final RelationshipModifier modifier) {
		this.modifier = modifier;
	}

	@Override
	public String toString() {
		return "SnomedRelationshipUpdate ["
				+ "getModuleId()=" + getModuleId() 
				+ ", isActive()=" + isActive() 
				+ ", group=" + group 
				+ ", unionGroup=" + unionGroup 
				+ ", characteristicType=" + characteristicType 
				+ ", modifier=" + modifier 
				+ "]";
	}
}
