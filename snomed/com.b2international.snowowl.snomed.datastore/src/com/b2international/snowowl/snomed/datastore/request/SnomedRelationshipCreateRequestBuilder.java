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

import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.RelationshipValue;

/**
 * <i>Builder</i> class to build requests responsible for creating SNOMED CT relationships.
 * This class should be instantiated from the corresponding static method on the central {@link SnomedRequests} class.
 * 
 * @since 4.5
 */
public final class SnomedRelationshipCreateRequestBuilder extends SnomedComponentCreateRequestBuilder<SnomedRelationshipCreateRequestBuilder> {
	
	private String sourceId;
	private String typeId;
	private String destinationId;
	private boolean destinationNegated;
	private RelationshipValue value;
	private Integer relationshipGroup = 0;
	private Integer unionGroup = 0;
	private String characteristicTypeId = Concepts.STATED_RELATIONSHIP;
	private String modifierId = Concepts.EXISTENTIAL_RESTRICTION_MODIFIER;

	SnomedRelationshipCreateRequestBuilder() { 
		super();
	}

	/**
	 * @param sourceId
	 * @return
	 */
	public SnomedRelationshipCreateRequestBuilder setSourceId(final String sourceId) {
		this.sourceId = sourceId;
		return getSelf();
	}

	/**
	 * @param typeId
	 * @return
	 */
	public SnomedRelationshipCreateRequestBuilder setTypeId(final String typeId) {
		this.typeId = typeId;
		return getSelf();
	}

	/**
	 * @param destinationId
	 * @return
	 */
	public SnomedRelationshipCreateRequestBuilder setDestinationId(final String destinationId) {
		this.destinationId = destinationId;
		return getSelf();
	}

	/**
	 * @param destinationNegated
	 * @return
	 */
	public SnomedRelationshipCreateRequestBuilder setDestinationNegated(final boolean destinationNegated) {
		this.destinationNegated = destinationNegated;
		return getSelf();
	}

	/**
	 * @param value
	 * @return
	 */
	public SnomedRelationshipCreateRequestBuilder setValue(final RelationshipValue value) {
		this.value = value;
		return getSelf();
	}

	/**
	 * @param relationshipGroup
	 * @return
	 */
	public SnomedRelationshipCreateRequestBuilder setRelationshipGroup(final Integer relationshipGroup) {
		this.relationshipGroup = relationshipGroup;
		return getSelf();
	}

	/**
	 * @param unionGroup
	 * @return
	 */
	public SnomedRelationshipCreateRequestBuilder setUnionGroup(final Integer unionGroup) {
		this.unionGroup = unionGroup;
		return getSelf();
	}

	/**
	 * @param characteristicTypeId
	 * @return
	 */
	public SnomedRelationshipCreateRequestBuilder setCharacteristicTypeId(final String characteristicTypeId) {
		this.characteristicTypeId = characteristicTypeId;
		return getSelf();
	}

	/**
	 * @param modifierId
	 * @return
	 */
	public SnomedRelationshipCreateRequestBuilder setModifierId(final String modifierId) {
		this.modifierId = modifierId;
		return getSelf();
	}

	@Override
	protected void init(final BaseSnomedComponentCreateRequest request) {
		final SnomedRelationshipCreateRequest req = (SnomedRelationshipCreateRequest) request;
		req.setSourceId(sourceId);
		req.setTypeId(typeId);
		req.setDestinationId(destinationId);
		req.setDestinationNegated(destinationNegated);
		req.setValue(value);
		req.setRelationshipGroup(relationshipGroup);
		req.setUnionGroup(unionGroup);
		req.setCharacteristicTypeId(characteristicTypeId);
		req.setModifierId(modifierId);
	}

	@Override
	protected SnomedRelationshipCreateRequest createRequest() {
		return new SnomedRelationshipCreateRequest();
	}
}
