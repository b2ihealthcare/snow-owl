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
package com.b2international.snowowl.snomed.api.impl;

import com.b2international.commons.ClassUtils;
import com.b2international.snowowl.api.domain.IComponentRef;
import com.b2international.snowowl.api.impl.domain.InternalComponentRef;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedFactory;
import com.b2international.snowowl.snomed.api.ISnomedRelationshipService;
import com.b2international.snowowl.snomed.api.domain.CharacteristicType;
import com.b2international.snowowl.snomed.api.domain.ISnomedRelationship;
import com.b2international.snowowl.snomed.api.domain.ISnomedRelationshipInput;
import com.b2international.snowowl.snomed.api.domain.ISnomedRelationshipUpdate;
import com.b2international.snowowl.snomed.api.domain.RelationshipModifier;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.SnomedRelationshipLookupService;

/**
 */
public class SnomedRelationshipServiceImpl 
	extends AbstractSnomedComponentServiceImpl<ISnomedRelationshipInput, ISnomedRelationship, ISnomedRelationshipUpdate, Relationship> 
	implements ISnomedRelationshipService {

	private final SnomedRelationshipLookupService snomedRelationshipLookupService = new SnomedRelationshipLookupService();

	public SnomedRelationshipServiceImpl() {
		super(SnomedDatastoreActivator.REPOSITORY_UUID, ComponentCategory.RELATIONSHIP);
	}

	private SnomedRelationshipConverter getRelationshipConverter(final IBranchPath branchPath) {
		return new SnomedRelationshipConverter(getMembershipLookupService(branchPath));
	}

	@Override
	protected boolean componentExists(final IComponentRef ref) {
		final InternalComponentRef internalRef = ClassUtils.checkAndCast(ref, InternalComponentRef.class);
		return snomedRelationshipLookupService.exists(internalRef.getBranch().branchPath(), internalRef.getComponentId());
	}

	@Override
	protected Relationship convertAndRegister(final ISnomedRelationshipInput input, final SnomedEditingContext editingContext) {
		
		try {
			final Relationship relationship = SnomedFactory.eINSTANCE.createRelationship();
			
			relationship.setId(input.getIdGenerationStrategy().getId());
			relationship.setActive(true);
			relationship.unsetEffectiveTime();
			relationship.setReleased(false);
			relationship.setModule(getModuleConcept(input, editingContext));
			relationship.setCharacteristicType(getConcept(input.getCharacteristicType().getConceptId(), editingContext));
			relationship.setDestination(getConcept(input.getDestinationId(), editingContext));
			relationship.setDestinationNegated(input.isDestinationNegated());
			relationship.setModifier(getConcept(input.getModifier().getConceptId(), editingContext));
			relationship.setSource(getConcept(input.getSourceId(), editingContext));
			relationship.setType(getConcept(input.getTypeId(), editingContext));
			
			validateGroup(input.getGroup(), relationship);
			relationship.setGroup(input.getGroup());
			
			validateUnionGroup(input.getUnionGroup(), relationship);
			relationship.setUnionGroup(input.getUnionGroup());
			
			// TODO: add a refinability refset member here?
			return relationship;
		} catch (ComponentNotFoundException e) {
			throw e.toBadRequestException();
		}
	}

	@Override
	protected ISnomedRelationship doRead(final IComponentRef ref) {
		final InternalComponentRef internalRef = ClassUtils.checkAndCast(ref, InternalComponentRef.class);
		final IBranchPath branch = internalRef.getBranch().branchPath();
		final SnomedRelationshipIndexEntry relationshipIndexEntry = snomedRelationshipLookupService.getComponent(branch, internalRef.getComponentId());
		return getRelationshipConverter(branch).apply(relationshipIndexEntry);
	}

	private Relationship getRelationship(final String relationshipId, final SnomedEditingContext editingContext) {
		return snomedRelationshipLookupService.getComponent(relationshipId, editingContext.getTransaction());
	}

	@Override
	protected void doUpdate(final IComponentRef ref, final ISnomedRelationshipUpdate update, final SnomedEditingContext editingContext) {
		final Relationship relationship = getRelationship(ref.getComponentId(), editingContext);

		boolean changed = false;
		changed |= updateStatus(update.isActive(), relationship, editingContext);
		changed |= updateModule(update.getModuleId(), relationship, editingContext);
		changed |= updateGroup(update.getGroup(), relationship, editingContext);
		changed |= updateUnionGroup(update.getUnionGroup(), relationship, editingContext);
		changed |= updateCharacteristicType(update.getCharacteristicType(), relationship, editingContext);
		changed |= updateModifier(update.getModifier(), relationship, editingContext);

		if (changed) {
			relationship.unsetEffectiveTime();
		}
	}

	private boolean updateGroup(final Integer newGroup, final Relationship relationship, final SnomedEditingContext editingContext) {
		if (null == newGroup) {
			return false;
		}

		validateGroup(newGroup, relationship);

		if (relationship.getGroup() != newGroup) {
			relationship.setGroup(newGroup);
			return true;
		} else {
			return false;
		}
	}

	private void validateGroup(final Integer group, final Relationship relationship) {
		if (group < 0 || group > Byte.MAX_VALUE) {
			throw new BadRequestException("Group value for relationship %s must be between 0 and 127.", relationship.getId());
		}
	}

	private boolean updateUnionGroup(final Integer newUnionGroup, final Relationship relationship, final SnomedEditingContext editingContext) {
		if (null == newUnionGroup) {
			return false;
		}

		validateUnionGroup(newUnionGroup, relationship);

		if (relationship.getUnionGroup() != newUnionGroup) {
			relationship.setUnionGroup(newUnionGroup);
			return true;
		} else {
			return false;
		}
	}

	private void validateUnionGroup(final Integer unionGroup, final Relationship relationship) {
		if (unionGroup < 0 || unionGroup > Byte.MAX_VALUE) {
			throw new BadRequestException("Union group value for relationship %s must be between 0 and 127.", relationship.getId());
		}
	}

	protected boolean updateCharacteristicType(final CharacteristicType newCharacteristicType, final Relationship relationship, final SnomedEditingContext editingContext) {
		if (null == newCharacteristicType) {
			return false;
		}

		final CharacteristicType currentCharacteristicType = CharacteristicType.getByConceptId(relationship.getCharacteristicType().getId());
		if (!currentCharacteristicType.equals(newCharacteristicType)) {
			relationship.setCharacteristicType(getConcept(newCharacteristicType.getConceptId(), editingContext));
			return true;
		} else {
			return false;
		}
	}

	protected boolean updateModifier(final RelationshipModifier newModifier, final Relationship relationship, final SnomedEditingContext editingContext) {
		if (null == newModifier) {
			return false;
		}

		final RelationshipModifier currentModifier = RelationshipModifier.getByConceptId(relationship.getModifier().getId());
		if (!currentModifier.equals(newModifier)) {
			relationship.setModifier(getConcept(newModifier.getConceptId(), editingContext));
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void doDelete(final IComponentRef ref, final SnomedEditingContext editingContext) {
		final Relationship relationship = getRelationship(ref.getComponentId(), editingContext);
		editingContext.delete(relationship);
	}
}
