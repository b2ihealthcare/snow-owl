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
package com.b2international.snowowl.snomed.datastore.server.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.core.domain.RelationshipModifier;

/**
 * @since 4.5
 */
public class SnomedRelationshipUpdateRequest extends BaseSnomedComponentUpdateRequest {

	@Min(0)
	@Max(Byte.MAX_VALUE)
	private Integer group;
	
	@Min(0)
	@Max(Byte.MAX_VALUE)
	private Integer unionGroup;
	
	private CharacteristicType characteristicType;
	private RelationshipModifier modifier;

	SnomedRelationshipUpdateRequest(String componentId) {
		super(componentId);
	}
	
	void setCharacteristicType(CharacteristicType characteristicType) {
		this.characteristicType = characteristicType;
	}
	
	void setGroup(Integer group) {
		this.group = group;
	}
	
	void setModifier(RelationshipModifier modifier) {
		this.modifier = modifier;
	}
	
	void setUnionGroup(Integer unionGroup) {
		this.unionGroup = unionGroup;
	}
	
	@Override
	public Void execute(TransactionContext context) {
		final Relationship relationship = context.lookup(getComponentId(), Relationship.class);

		boolean changed = false;
		changed |= updateStatus(context, relationship, isActive());
		changed |= updateModule(context, relationship, getModuleId());
		changed |= updateGroup(group, relationship, context);
		changed |= updateUnionGroup(unionGroup, relationship, context);
		changed |= updateCharacteristicType(characteristicType, relationship, context);
		changed |= updateModifier(modifier, relationship, context);

		if (changed) {
			relationship.unsetEffectiveTime();
		}
		return null;
	}
	
	private boolean updateGroup(final Integer newGroup, final Relationship relationship, final TransactionContext context) {
		if (null == newGroup) {
			return false;
		}

		if (relationship.getGroup() != newGroup) {
			relationship.setGroup(newGroup);
			return true;
		} else {
			return false;
		}
	}

	private boolean updateUnionGroup(final Integer newUnionGroup, final Relationship relationship, final TransactionContext context) {
		if (null == newUnionGroup) {
			return false;
		}

		if (relationship.getUnionGroup() != newUnionGroup) {
			relationship.setUnionGroup(newUnionGroup);
			return true;
		} else {
			return false;
		}
	}

	protected boolean updateCharacteristicType(final CharacteristicType newCharacteristicType, final Relationship relationship, final TransactionContext context) {
		if (null == newCharacteristicType) {
			return false;
		}

		final CharacteristicType currentCharacteristicType = CharacteristicType.getByConceptId(relationship.getCharacteristicType().getId());
		if (!currentCharacteristicType.equals(newCharacteristicType)) {
			relationship.setCharacteristicType(context.lookup(newCharacteristicType.getConceptId(), Concept.class));
			return true;
		} else {
			return false;
		}
	}

	protected boolean updateModifier(final RelationshipModifier newModifier, final Relationship relationship, final TransactionContext context) {
		if (null == newModifier) {
			return false;
		}

		final RelationshipModifier currentModifier = RelationshipModifier.getByConceptId(relationship.getModifier().getId());
		if (!currentModifier.equals(newModifier)) {
			relationship.setModifier(context.lookup(newModifier.getConceptId(), Concept.class));
			return true;
		} else {
			return false;
		}
	}

}
