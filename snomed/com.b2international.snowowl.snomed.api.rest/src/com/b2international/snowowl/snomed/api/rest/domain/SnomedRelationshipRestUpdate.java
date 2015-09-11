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
package com.b2international.snowowl.snomed.api.rest.domain;

import com.b2international.snowowl.snomed.api.domain.CharacteristicType;
import com.b2international.snowowl.snomed.api.domain.RelationshipModifier;
import com.b2international.snowowl.snomed.api.impl.domain.SnomedRelationshipUpdate;


/**
 * @since 1.0
 */
public class SnomedRelationshipRestUpdate extends AbstractSnomedComponentRestUpdate<SnomedRelationshipUpdate> {

	private Integer group;
	private Integer unionGroup;
	private CharacteristicType characteristicType;
	private RelationshipModifier modifier;

	public Integer getGroup() {
		return group;
	}

	public void setGroup(final Integer group) {
		this.group = group;
	}

	public Integer getUnionGroup() {
		return unionGroup;
	}

	public void setUnionGroup(final Integer unionGroup) {
		this.unionGroup = unionGroup;
	}

	public CharacteristicType getCharacteristicType() {
		return characteristicType;
	}

	public void setCharacteristicType(final CharacteristicType characteristicType) {
		this.characteristicType = characteristicType;
	}

	public RelationshipModifier getModifier() {
		return modifier;
	}

	public void setModifier(final RelationshipModifier modifier) {
		this.modifier = modifier;
	}

	@Override
	protected SnomedRelationshipUpdate createComponentUpdate() {
		return new SnomedRelationshipUpdate();
	}

	@Override
	public SnomedRelationshipUpdate toComponentUpdate() {
		final SnomedRelationshipUpdate result = super.toComponentUpdate();
		result.setGroup(getGroup());
		result.setUnionGroup(getUnionGroup());
		result.setCharacteristicType(getCharacteristicType());
		result.setModifier(getModifier());
		return result;
	}
}
