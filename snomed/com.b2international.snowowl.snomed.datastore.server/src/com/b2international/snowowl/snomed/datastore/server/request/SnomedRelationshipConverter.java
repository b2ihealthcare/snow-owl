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

import java.util.Collection;

import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.core.domain.ISnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.RelationshipModifier;
import com.b2international.snowowl.snomed.core.domain.RelationshipRefinability;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.services.AbstractSnomedRefSetMembershipLookupService;
import com.google.common.collect.ImmutableSet;

public class SnomedRelationshipConverter extends AbstractSnomedComponentConverter<SnomedRelationshipIndexEntry, ISnomedRelationship> {

	public SnomedRelationshipConverter(final AbstractSnomedRefSetMembershipLookupService refSetMembershipLookupService) {
		super(refSetMembershipLookupService);
	}

	@Override
	public ISnomedRelationship apply(final SnomedRelationshipIndexEntry input) {
		final SnomedRelationship result = new SnomedRelationship();
		result.setActive(input.isActive());
		result.setCharacteristicType(toCharacteristicType(input.getCharacteristicTypeId()));
		result.setDestinationId(input.getValueId());
		result.setDestinationNegated(input.isDestinationNegated());
		result.setEffectiveTime(toEffectiveTime(input.getEffectiveTimeAsLong()));
		result.setGroup(input.getGroup());
		result.setId(input.getId());
		result.setModifier(toRelationshipModifier(input.isUniversal()));
		result.setModuleId(input.getModuleId());
		result.setRefinability(getRelationshipRefinability(input.getId()));
		result.setReleased(input.isReleased());
		result.setSourceId(input.getObjectId());
		result.setTypeId(input.getAttributeId());
		result.setUnionGroup(input.getUnionGroup());

		return result;
	}
	
	public ISnomedRelationship apply(Relationship input) {
		final SnomedRelationship result = new SnomedRelationship();
		result.setActive(input.isActive());
		result.setCharacteristicType(toCharacteristicType(input.getCharacteristicType().getId()));
		result.setDestinationId(input.getDestination().getId());
		result.setDestinationNegated(input.isDestinationNegated());
		result.setEffectiveTime(input.getEffectiveTime());
		result.setGroup(input.getGroup());
		result.setId(input.getId());
		result.setModifier(toRelationshipModifier(input.getModifier().getId()));
		result.setModuleId(input.getModule().getId());
		result.setRefinability(getRelationshipRefinability(input.getId()));
		result.setReleased(input.isReleased());
		result.setSourceId(input.getSource().getId());
		result.setTypeId(input.getType().getId());
		result.setUnionGroup(input.getUnionGroup());

		return result;
	}

	private CharacteristicType toCharacteristicType(final String characteristicTypeId) {
		return CharacteristicType.getByConceptId(characteristicTypeId);
	}

	private RelationshipModifier toRelationshipModifier(final String modifiedId) {
		return RelationshipModifier.getByConceptId(modifiedId);
	}
	
	private RelationshipModifier toRelationshipModifier(final boolean universal) {
		return universal ? RelationshipModifier.UNIVERSAL : RelationshipModifier.EXISTENTIAL;
	}

	private RelationshipRefinability getRelationshipRefinability(final String relationshipId) {
		final Collection<SnomedRefSetMemberIndexEntry> relationshipMembers = getRefSetMembershipLookupService().getRelationshipMembers(
				ImmutableSet.of(Concepts.REFSET_RELATIONSHIP_REFINABILITY),
				ImmutableSet.of(relationshipId));

		for (final SnomedRefSetMemberIndexEntry relationshipMember : relationshipMembers) {
			if (relationshipMember.isActive()) {
				return relationshipMember.getRefinability();
			}
		}

		// TODO: is this the proper fallback value?
		return RelationshipRefinability.NOT_REFINABLE;
	}

}