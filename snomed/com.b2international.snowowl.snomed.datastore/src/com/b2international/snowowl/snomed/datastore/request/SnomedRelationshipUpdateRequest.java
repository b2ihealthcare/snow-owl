/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request;

import java.util.Objects;
import java.util.Set;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.core.domain.RelationshipModifier;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * @since 4.5
 */
public final class SnomedRelationshipUpdateRequest extends SnomedComponentUpdateRequest {

	@Min(0)
	@Max(Integer.MAX_VALUE)
	private Integer group;
	
	@Min(0)
	@Max(Integer.MAX_VALUE)
	private Integer unionGroup;
	
	private CharacteristicType characteristicType;
	private RelationshipModifier modifier;
	private String destinationId;
	private String typeId;

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
	
	void setDestinationId(String destinationId) {
		this.destinationId = destinationId;
	}
	
	void setTypeId(String typeId) {
		this.typeId = typeId;
	}
	
	@Override
	public Boolean execute(TransactionContext context) {
		final SnomedRelationshipIndexEntry relationship = context.lookup(getComponentId(), SnomedRelationshipIndexEntry.class);
		final SnomedRelationshipIndexEntry.Builder updatedRelationship = SnomedRelationshipIndexEntry.builder(relationship);

		boolean changed = false;
		changed |= updateStatus(context, relationship, updatedRelationship);
		changed |= updateModule(context, relationship, updatedRelationship);
		changed |= updateGroup(group, relationship, updatedRelationship, context);
		changed |= updateUnionGroup(unionGroup, relationship, updatedRelationship, context);
		changed |= updateCharacteristicType(characteristicType, relationship, updatedRelationship, context);
		changed |= updateModifier(modifier, relationship, updatedRelationship, context);
		changed |= updateDestinationId(context, relationship, updatedRelationship);
		changed |= updateTypeId(context, relationship, updatedRelationship);

		if (changed) {
			if (relationship.getEffectiveTime() != EffectiveTimes.UNSET_EFFECTIVE_TIME) {
				updatedRelationship.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME);
			}
			context.update(relationship, updatedRelationship.build());
		}
		
		return changed;
	}
	
	private boolean updateTypeId(TransactionContext context, SnomedRelationshipIndexEntry original, SnomedRelationshipIndexEntry.Builder relationship) {
		if (null == typeId) {
			return false;
		}
		
		if (!original.getTypeId().equals(typeId)) {
			checkUpdateOnReleased(original, SnomedRf2Headers.FIELD_TYPE_ID, typeId);
			relationship.typeId(context.lookup(typeId, SnomedConceptDocument.class).getId());
			return true;
		}
		
		return false;
	}
	
	private boolean updateDestinationId(TransactionContext context, SnomedRelationshipIndexEntry original, SnomedRelationshipIndexEntry.Builder relationship) {
		if (null == destinationId) {
			return false;
		}
		
		if (!original.getDestinationId().equals(destinationId)) {
			checkUpdateOnReleased(original, SnomedRf2Headers.FIELD_DESTINATION_ID, destinationId);
			relationship.destinationId(context.lookup(destinationId, SnomedConceptDocument.class).getId());
			return true;
		}
		
		return false;
	}

	private boolean updateGroup(final Integer newGroup, final SnomedRelationshipIndexEntry original, SnomedRelationshipIndexEntry.Builder relationship, final TransactionContext context) {
		if (null == newGroup) {
			return false;
		}

		if (!Objects.equals(original.getGroup(), newGroup)) {
			relationship.group(newGroup);
			return true;
		} else {
			return false;
		}
	}

	private boolean updateUnionGroup(final Integer newUnionGroup, final SnomedRelationshipIndexEntry original, SnomedRelationshipIndexEntry.Builder relationship, final TransactionContext context) {
		if (null == newUnionGroup) {
			return false;
		}

		if (!Objects.equals(original.getUnionGroup(), newUnionGroup)) {
			relationship.unionGroup(newUnionGroup);
			return true;
		} else {
			return false;
		}
	}

	private boolean updateCharacteristicType(final CharacteristicType newCharacteristicType, final SnomedRelationshipIndexEntry original, SnomedRelationshipIndexEntry.Builder relationship, final TransactionContext context) {
		if (null == newCharacteristicType) {
			return false;
		}

		final CharacteristicType currentCharacteristicType = original.getCharacteristicType();
		if (!currentCharacteristicType.equals(newCharacteristicType)) {
			relationship.characteristicTypeId(context.lookup(newCharacteristicType.getConceptId(), SnomedConceptDocument.class).getId());
			return true;
		} else {
			return false;
		}
	}

	private boolean updateModifier(final RelationshipModifier newModifier, final SnomedRelationshipIndexEntry original, SnomedRelationshipIndexEntry.Builder relationship, final TransactionContext context) {
		if (null == newModifier) {
			return false;
		}

		final RelationshipModifier currentModifier = RelationshipModifier.getByConceptId(original.getModifierId());
		if (!currentModifier.equals(newModifier)) {
			relationship.modifierId(context.lookup(newModifier.getConceptId(), SnomedConceptDocument.class).getId());
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public Set<String> getRequiredComponentIds(TransactionContext context) {
		final Builder<String> ids = ImmutableSet.<String>builder();
		ids.add(getComponentId());
		if (characteristicType != null) {
			ids.add(characteristicType.getConceptId());
		}
		if (destinationId != null) {
			ids.add(destinationId);
		}
		if (typeId != null) {
			ids.add(typeId);
		}
		if (modifier != null) {
			ids.add(modifier.getConceptId());
		}
		return ids.build();
	}
	
}
