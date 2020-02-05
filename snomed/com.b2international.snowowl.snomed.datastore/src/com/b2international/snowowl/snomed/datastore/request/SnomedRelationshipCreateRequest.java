/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.core.domain.ConstantIdStrategy;
import com.b2international.snowowl.snomed.core.domain.RelationshipModifier;
import com.b2international.snowowl.snomed.core.store.SnomedComponents;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * @since 4.0
 */
public final class SnomedRelationshipCreateRequest extends BaseSnomedComponentCreateRequest {

	private String sourceId;

	@NotEmpty
	private String destinationId;

	@NotEmpty
	private String typeId;

	private boolean destinationNegated;

	@Min(0)
	@Max(Byte.MAX_VALUE)
	private int group;

	@Min(0)
	@Max(Byte.MAX_VALUE)
	private int unionGroup;

	@NotNull
	private CharacteristicType characteristicType;

	@NotNull
	private RelationshipModifier modifier;

	SnomedRelationshipCreateRequest() {}
	
	public String getSourceId() {
		return sourceId;
	}

	public String getDestinationId() {
		return destinationId;
	}

	public boolean isDestinationNegated() {
		return destinationNegated;
	}

	public String getTypeId() {
		return typeId;
	}

	public int getGroup() {
		return group;
	}

	public int getUnionGroup() {
		return unionGroup;
	}

	public CharacteristicType getCharacteristicType() {
		return characteristicType;
	}

	public RelationshipModifier getModifier() {
		return modifier;
	}
	
	void setSourceId(final String sourceId) {
		this.sourceId = sourceId;
	}

	void setDestinationId(final String destinationId) {
		this.destinationId = destinationId;
	}

	void setDestinationNegated(final boolean destinationNegated) {
		this.destinationNegated = destinationNegated;
	}

	void setTypeId(final String typeId) {
		this.typeId = typeId;
	}

	void setGroup(final int group) {
		this.group = group;
	}

	void setUnionGroup(final int unionGroup) {
		this.unionGroup = unionGroup;
	}

	void setCharacteristicType(final CharacteristicType characteristicType) {
		this.characteristicType = characteristicType;
	}

	void setModifier(final RelationshipModifier modifier) {
		this.modifier = modifier;
	}
	
	@Override
	public Set<String> getRequiredComponentIds(TransactionContext context) {
		Builder<String> result = ImmutableSet.<String>builder()
				.add(modifier.getConceptId())
				.add(characteristicType.getConceptId())
				.add(getTypeId())
				.add(getDestinationId());
		if (getModuleId() != null) {
			result.add(getModuleId());
		}
		if (getSourceId() != null) {
			result.add(getSourceId());
		}
		return result.build();
	}
	
	@Override
	public String execute(TransactionContext context) {
		if (Strings.isNullOrEmpty(getSourceId())) {
			throw new BadRequestException("'sourceId' may not be empty (was '%s')", getSourceId());
		}
		
		try {
			
			final String relationshipId = ((ConstantIdStrategy) getIdGenerationStrategy()).getId();
			final SnomedRelationshipIndexEntry relationship = SnomedComponents.newRelationship()
					.withActive(isActive())
					.withId(relationshipId)
					.withModule(getModuleId())
					.withSource(getSourceId())
					.withDestination(getDestinationId())
					.withType(getTypeId())
					.withGroup(getGroup())
					.withUnionGroup(getUnionGroup())
					.withCharacteristicType(getCharacteristicType())
					.withModifier(getModifier())
					.withDestinationNegated(isDestinationNegated())
					.build(context);
			
			convertMembers(context, relationshipId);
			context.add(relationship);
			
			return relationship.getId();
			
		} catch (ComponentNotFoundException e) {
			throw e.toBadRequestException();
		}
	}
}
