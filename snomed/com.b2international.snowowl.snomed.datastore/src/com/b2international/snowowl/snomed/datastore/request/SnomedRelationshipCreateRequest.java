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

import javax.annotation.Nonnull;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.core.domain.RelationshipModifier;
import com.b2international.snowowl.snomed.core.store.SnomedComponents;

/**
 * @since 4.0
 */
public final class SnomedRelationshipCreateRequest extends BaseSnomedComponentCreateRequest {

	@Nonnull
	private Boolean active;
	
	@NotEmpty
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
	
	public Boolean isActive() {
		return active;
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
	
	void setActive(final Boolean active) {
		this.active = active;
	}

	@Override
	public String execute(TransactionContext context) {
		ensureUniqueId("Relationship", context);
		
		try {
			final Relationship relationship = SnomedComponents.newRelationship()
					.withActive(isActive())
					.withId(getIdGenerationStrategy())
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

			return relationship.getId();
		} catch (ComponentNotFoundException e) {
			throw e.toBadRequestException();
		}
	}
	
	@Override
	protected void checkComponentExists(TransactionContext context, String componentId) throws ComponentNotFoundException {
		SnomedRequests.prepareGetRelationship().setComponentId(componentId).build().execute(context);
	}
}
