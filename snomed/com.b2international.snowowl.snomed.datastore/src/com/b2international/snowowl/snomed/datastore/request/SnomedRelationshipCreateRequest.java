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

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;
import com.b2international.snowowl.snomed.core.domain.ConstantIdStrategy;
import com.b2international.snowowl.snomed.core.domain.RelationshipValue;
import com.b2international.snowowl.snomed.core.store.SnomedComponents;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

/**
 * @since 4.0
 */
public final class SnomedRelationshipCreateRequest extends BaseSnomedComponentCreateRequest {

	private static final long serialVersionUID = 1L;

	// Not @NotEmpty, it can be populated after the request is validated
	private String sourceId;

	@NotEmpty
	private String typeId;

	// Not @NotEmpty, it will be null if a value is set
	private String destinationId;

	private boolean destinationNegated;

	// Not @NotNull, it will be null if a destination ID is set
	private RelationshipValue value; 

	@NotNull @Min(0)
	private Integer relationshipGroup;

	@NotNull @Min(0)
	private Integer unionGroup;

	@NotNull
	private String characteristicTypeId;

	@NotNull
	private String modifierId;

	SnomedRelationshipCreateRequest() {}

	/**
	 * @return
	 */
	public String getSourceId() {
		return sourceId;
	}

	/**
	 * @return
	 */
	public String getTypeId() {
		return typeId;
	}

	/**
	 * @return
	 */
	public String getDestinationId() {
		return destinationId;
	}

	/**
	 * @return
	 */
	public boolean isDestinationNegated() {
		return destinationNegated;
	}

	/**
	 * @return
	 */
	public RelationshipValue getValue() {
		return value;
	}

	/**
	 * @return
	 */
	public Integer getRelationshipGroup() {
		return relationshipGroup;
	}

	/**
	 * @return
	 */
	public Integer getUnionGroup() {
		return unionGroup;
	}

	/**
	 * @return
	 */
	public String getCharacteristicTypeId() {
		return characteristicTypeId;
	}

	/**
	 * @return
	 */
	public String getModifierId() {
		return modifierId;
	}

	void setSourceId(final String sourceId) {
		this.sourceId = sourceId;
	}

	void setTypeId(final String typeId) {
		this.typeId = typeId;
	}

	void setDestinationId(final String destinationId) {
		this.destinationId = destinationId;
	}

	void setDestinationNegated(final boolean destinationNegated) {
		this.destinationNegated = destinationNegated;
	}

	void setValue(final RelationshipValue value) {
		this.value = value;
	}

	void setRelationshipGroup(final Integer relationshipGroup) {
		this.relationshipGroup = relationshipGroup;
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
	public Set<String> getRequiredComponentIds(final TransactionContext context) {
		final ImmutableSet.Builder<String> result = ImmutableSet.builder();
		result.add(getModifierId());
		result.add(getCharacteristicTypeId());
		result.add(getTypeId());

		if (getDestinationId() != null) { result.add(getDestinationId()); }
		if (getModuleId() != null) { result.add(getModuleId()); }
		if (getSourceId() != null) { result.add(getSourceId()); }

		return result.build();
	}

	@Override
	public String execute(final TransactionContext context) {
		if (Strings.isNullOrEmpty(getSourceId())) {
			throw new BadRequestException("'sourceId' may not be empty");
		}

		if (Strings.isNullOrEmpty(getDestinationId()) && getValue() == null) {
			throw new BadRequestException("'destinationId' or 'value' should be specified");
		}

		if (!Strings.isNullOrEmpty(getDestinationId()) && getValue() != null) {
			throw new BadRequestException("'destinationId' and 'value' can not be set for the same relationship");
		}

		try {

			final String relationshipId = ((ConstantIdStrategy) getIdGenerationStrategy()).getId();
			final SnomedRelationshipIndexEntry relationship = SnomedComponents.newRelationship()
				.withId(relationshipId)
				.withActive(isActive())
				.withModuleId(getModuleId())
				.withSourceId(getSourceId())
				.withTypeId(getTypeId())
				.withDestinationId(getDestinationId())
				.withDestinationNegated(isDestinationNegated())
				.withValue(getValue())
				.withRelationshipGroup(getRelationshipGroup())
				.withUnionGroup(getUnionGroup())
				.withCharacteristicTypeId(getCharacteristicTypeId())
				.withModifierId(getModifierId())
				.build(context);

			convertMembers(context, relationshipId);
			context.add(relationship);
			return relationship.getId();

		} catch (final ComponentNotFoundException e) {
			throw e.toBadRequestException();
		}
	}
}
