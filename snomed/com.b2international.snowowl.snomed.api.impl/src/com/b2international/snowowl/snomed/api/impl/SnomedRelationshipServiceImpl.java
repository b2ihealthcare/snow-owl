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
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.domain.IComponentRef;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.datastore.server.domain.InternalComponentRef;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.api.ISnomedRelationshipService;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.core.domain.ISnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.ISnomedRelationshipUpdate;
import com.b2international.snowowl.snomed.core.domain.RelationshipModifier;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedRelationshipLookupService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.server.request.SnomedRelationshipConverter;

/**
 */
public class SnomedRelationshipServiceImpl 
	extends AbstractSnomedComponentServiceImpl<ISnomedRelationship, ISnomedRelationshipUpdate, Relationship> 
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

		if (relationship.getGroup() != newGroup) {
			relationship.setGroup(newGroup);
			return true;
		} else {
			return false;
		}
	}

	private boolean updateUnionGroup(final Integer newUnionGroup, final Relationship relationship, final SnomedEditingContext editingContext) {
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

	protected boolean updateCharacteristicType(final CharacteristicType newCharacteristicType, final Relationship relationship, final SnomedEditingContext editingContext) {
		if (null == newCharacteristicType) {
			return false;
		}

		final CharacteristicType currentCharacteristicType = CharacteristicType.getByConceptId(relationship.getCharacteristicType().getId());
		if (!currentCharacteristicType.equals(newCharacteristicType)) {
			relationship.setCharacteristicType(editingContext.getConcept(newCharacteristicType.getConceptId()));
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
			relationship.setModifier(editingContext.getConcept(newModifier.getConceptId()));
			return true;
		} else {
			return false;
		}
	}

}
