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
package com.b2international.snowowl.snomed.datastore.factory;

import com.b2international.commons.TypeSafeAdapterFactory;
import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;

/**
 * Adapter factory implementation for SNOMED CT relationships.
 */
public class SnomedRelationshipAdapterFactory extends TypeSafeAdapterFactory {

	public SnomedRelationshipAdapterFactory() {
		super(IComponent.class, SnomedRelationshipIndexEntry.class);
	}

	@Override
	protected <T> T getAdapterSafe(final Object adaptableObject, final Class<T> adapterType) {

		if (adaptableObject instanceof SnomedRelationshipIndexEntry) {
			return adapterType.cast(adaptableObject);
		} 

		if (adaptableObject instanceof Relationship) {

			final Relationship relationship = (Relationship) adaptableObject;
			final SnomedRelationshipIndexEntry adaptedEntry = SnomedRelationshipIndexEntry.builder()
					.id(relationship.getId())
					.storageKey(CDOUtils.getStorageKey(relationship))
					.moduleId(relationship.getModule().getId())
					.released(relationship.isReleased())
					.active(relationship.isActive())
					.effectiveTimeLong(relationship.isSetEffectiveTime() ? relationship.getEffectiveTime().getTime() : EffectiveTimes.UNSET_EFFECTIVE_TIME)
					.sourceId(relationship.getSource().getId())
					.typeId(relationship.getType().getId())
					.destinationId(relationship.getDestination().getId())
					.characteristicTypeId(relationship.getCharacteristicType().getId())
					.modifierId(relationship.getModifier().getId())
					.group((byte) relationship.getGroup())
					.unionGroup((byte) relationship.getUnionGroup())
					.destinationNegated(relationship.isDestinationNegated())
					.build();

			return adapterType.cast(adaptedEntry);
		}

		return null;
	}
}
