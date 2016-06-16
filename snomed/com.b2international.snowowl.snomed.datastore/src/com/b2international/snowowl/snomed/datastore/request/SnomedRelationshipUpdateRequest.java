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
package com.b2international.snowowl.snomed.datastore.request;

import java.util.Date;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.core.domain.RelationshipModifier;
import com.b2international.snowowl.snomed.datastore.SnomedStatementBrowser;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;

/**
 * @since 4.5
 */
public final class SnomedRelationshipUpdateRequest extends BaseSnomedComponentUpdateRequest {

	private static final Logger LOGGER = LoggerFactory.getLogger(SnomedRelationshipUpdateRequest.class);

	@Min(0)
	@Max(Integer.MAX_VALUE)
	private Integer group;
	
	@Min(0)
	@Max(Integer.MAX_VALUE)
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
		changed |= updateStatus(context, relationship);
		changed |= updateModule(context, relationship);
		changed |= updateGroup(group, relationship, context);
		changed |= updateUnionGroup(unionGroup, relationship, context);
		changed |= updateCharacteristicType(characteristicType, relationship, context);
		changed |= updateModifier(modifier, relationship, context);

		if (changed) {
			if (relationship.isSetEffectiveTime()) {
				relationship.unsetEffectiveTime();
			} else {
				if (relationship.isReleased()) {
					long start = new Date().getTime();
					final IBranchPath branchPath = getLatestReleaseBranch(context);
					final SnomedStatementBrowser statementBrowser = context.service(SnomedStatementBrowser.class);
					final SnomedRelationshipIndexEntry releasedRelationship = statementBrowser.getStatement(branchPath, getComponentId());
	
					if (!isDifferentToPreviousRelease(relationship, releasedRelationship)) {
						relationship.setEffectiveTime(EffectiveTimes.parse(releasedRelationship.getEffectiveTime()));
					}
					LOGGER.info("Previous version comparison took {}", new Date().getTime() - start);
				}
			}
		}
		
		return null;
	}
	
	private boolean isDifferentToPreviousRelease(Relationship relationship, SnomedRelationshipIndexEntry releasedRelationship) {
		if (releasedRelationship.isActive() != relationship.isActive()) return true;
		if (!releasedRelationship.getModuleId().equals(relationship.getModule().getId())) return true;
		if (!releasedRelationship.getValueId().equals(relationship.getDestination().getId())) return true;
		if (releasedRelationship.getGroup() != relationship.getGroup()) return true;
		if (releasedRelationship.getUnionGroup() != relationship.getUnionGroup()) return true;
		if (!releasedRelationship.getAttributeId().equals(relationship.getType().getId())) return true;
		if (!releasedRelationship.getCharacteristicType().getConceptId().equals(relationship.getCharacteristicType().getId())) return true;
		if (!releasedRelationship.getModifierId().equals(relationship.getModifier().getId())) return true;
		
		return false;
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
