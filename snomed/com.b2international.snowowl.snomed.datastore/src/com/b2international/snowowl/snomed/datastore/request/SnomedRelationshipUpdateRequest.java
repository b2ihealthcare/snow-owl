/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Set;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.RelationshipValue;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

/**
 * @since 4.5
 */
public final class SnomedRelationshipUpdateRequest extends SnomedComponentUpdateRequest {

	// Not @NotEmpty, update of type ID is optional
	private String typeId;

	// Not @NotEmpty, update of destination ID is optional
	private String destinationId;

	// Not @NotNull, update of relationship value is optional
	private RelationshipValue value;

	@Min(0)	@Max(Integer.MAX_VALUE)
	private Integer group;

	@Min(0)	@Max(Integer.MAX_VALUE)
	private Integer unionGroup;

	// Not @NotEmpty, update of characteristic type ID is optional
	private String characteristicTypeId;

	// Not @NotEmpty, update of modifier ID is optional
	private String modifierId;

	SnomedRelationshipUpdateRequest(final String componentId) {
		super(componentId);
	}

	void setTypeId(final String typeId) {
		this.typeId = typeId;
	}

	void setDestinationId(final String destinationId) {
		this.destinationId = destinationId;
	}

	void setValue(final RelationshipValue value) {
		this.value = value;
	}

	void setGroup(final Integer group) {
		this.group = group;
	}

	void setUnionGroup(final Integer unionGroup) {
		this.unionGroup = unionGroup;
	}

	void setCharacteristicTypeId(final String characteristicTypeId) {
		this.characteristicTypeId = characteristicTypeId;
	}

	void setModifierId(final String modifierId) {
		this.modifierId = modifierId;
	}

	@Override
	public Boolean execute(final TransactionContext context) {
		if (!Strings.isNullOrEmpty(destinationId) && value != null) {
			throw new BadRequestException("'destinationId' and 'value' can not be updated at same time");
		}

		final SnomedRelationshipIndexEntry relationship = context.lookup(componentId(), SnomedRelationshipIndexEntry.class);
		final SnomedRelationshipIndexEntry.Builder updatedRelationship = SnomedRelationshipIndexEntry.builder(relationship);

		boolean changed = false;
		changed |= updateStatus(context, relationship, updatedRelationship);
		changed |= updateModuleId(context, relationship, updatedRelationship);
		changed |= updateTypeId(context, relationship, updatedRelationship);
		changed |= updateDestinationId(context, relationship, updatedRelationship);
		changed |= updateValue(context, relationship, updatedRelationship);
		changed |= updateGroup(context, relationship, updatedRelationship);
		changed |= updateUnionGroup(context, relationship, updatedRelationship);
		changed |= updateCharacteristicTypeId(context, relationship, updatedRelationship);
		changed |= updateModifierId(context, relationship, updatedRelationship);
		changed |= updateEffectiveTime(relationship, updatedRelationship);

		if (changed) {
			if (!isEffectiveTimeUpdate() && relationship.getEffectiveTime() != EffectiveTimes.UNSET_EFFECTIVE_TIME) {
				updatedRelationship.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME);
			}
			context.update(relationship, updatedRelationship.build());
		}

		return changed;
	}

	@Override
	protected String getInactivationIndicatorRefSetId() {
		throw new UnsupportedOperationException("Relationship inactivation does not support inactivationProperties yet");
	}

	private String ensureConceptExists(final String conceptId, final TransactionContext context) {
		return context.lookup(conceptId, SnomedConceptDocument.class).getId();
	}

	private boolean updateTypeId(final TransactionContext context, final SnomedRelationshipIndexEntry relationship, final SnomedRelationshipIndexEntry.Builder updatedRelationship) {
		return updateProperty(typeId, () -> relationship.getTypeId(), newTypeId -> {
			checkUpdateOnReleased(relationship, SnomedRf2Headers.FIELD_TYPE_ID, newTypeId);
			updatedRelationship.typeId(ensureConceptExists(newTypeId, context));
		});
	}

	private boolean updateDestinationId(final TransactionContext context, final SnomedRelationshipIndexEntry relationship, final SnomedRelationshipIndexEntry.Builder updatedRelationship) {
		return updateProperty(destinationId, () -> relationship.getDestinationId(), newDestinationId -> {
			checkUpdateOnReleased(relationship, SnomedRf2Headers.FIELD_DESTINATION_ID, newDestinationId);
			updatedRelationship.destinationId(ensureConceptExists(newDestinationId, context));
		});
	}

	private boolean updateValue(final TransactionContext context, final SnomedRelationshipIndexEntry relationship, final SnomedRelationshipIndexEntry.Builder updatedRelationship) {
		return updateProperty(value, () -> relationship.getValue(), newValue -> {
			checkUpdateOnReleased(relationship, SnomedRf2Headers.FIELD_VALUE, newValue);
			updatedRelationship.value(newValue);
		});
	}

	private boolean updateGroup(final TransactionContext context, final SnomedRelationshipIndexEntry relationship, final SnomedRelationshipIndexEntry.Builder updatedRelationship) {
		return updateProperty(group, () -> relationship.getGroup(), newGroup -> {
			updatedRelationship.group(newGroup);
		});
	}

	private boolean updateUnionGroup(final TransactionContext context, final SnomedRelationshipIndexEntry relationship, final SnomedRelationshipIndexEntry.Builder updatedRelationship) {
		return updateProperty(unionGroup, () -> relationship.getUnionGroup(), newUnionGroup -> {
			updatedRelationship.unionGroup(newUnionGroup);
		});
	}

	private boolean updateCharacteristicTypeId(final TransactionContext context, final SnomedRelationshipIndexEntry relationship, final SnomedRelationshipIndexEntry.Builder updatedRelationship) {
		return updateProperty(characteristicTypeId, () -> relationship.getCharacteristicTypeId(), newCharacteristicTypeId -> {
			updatedRelationship.characteristicTypeId(ensureConceptExists(newCharacteristicTypeId, context));
		});
	}

	private boolean updateModifierId(final TransactionContext context, final SnomedRelationshipIndexEntry relationship, final SnomedRelationshipIndexEntry.Builder updatedRelationship) {
		return updateProperty(modifierId, () -> relationship.getModifierId(), newModifierId -> {
			updatedRelationship.modifierId(ensureConceptExists(newModifierId, context));
		});
	}

	@Override
	public Set<String> getRequiredComponentIds(final TransactionContext context) {
		final ImmutableSet.Builder<String> ids = ImmutableSet.builder();
		ids.add(componentId());

		if (typeId != null) { ids.add(typeId); }
		if (destinationId != null) { ids.add(destinationId); }
		if (characteristicTypeId != null) { ids.add(characteristicTypeId); }
		if (modifierId != null) { ids.add(modifierId); }

		return ids.build();
	}
}
