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

import com.b2international.snowowl.snomed.core.domain.RelationshipValue;

/**
 * @since 4.5
 */
public final class SnomedRelationshipUpdateRequestBuilder extends BaseSnomedComponentUpdateRequestBuilder<SnomedRelationshipUpdateRequestBuilder, SnomedRelationshipUpdateRequest> {

	private String typeId;
	private String destinationId;
	private RelationshipValue value;
	private Integer relationshipGroup;
	private Integer unionGroup;
	private String characteristicTypeId;
	private String modifierId;

	SnomedRelationshipUpdateRequestBuilder(final String componentId) {
		super(componentId);
	}

	/**
	 * @param typeId
	 * @return
	 */
	public SnomedRelationshipUpdateRequestBuilder setTypeId(final String typeId) {
		this.typeId = typeId;
		return getSelf();
	}

	/**
	 * @param destinationId
	 * @return
	 */
	public SnomedRelationshipUpdateRequestBuilder setDestinationId(final String destinationId) {
		this.destinationId = destinationId;
		return getSelf();
	}

	/**
	 * @param value
	 * @return
	 */
	public SnomedRelationshipUpdateRequestBuilder setValue(final RelationshipValue value) {
		this.value = value;
		return getSelf();
	}

	/**
	 * @param relationshipGroup
	 * @return
	 */
	public SnomedRelationshipUpdateRequestBuilder setRelationshipGroup(final Integer relationshipGroup) {
		this.relationshipGroup = relationshipGroup;
		return getSelf();
	}

	/**
	 * @param unionGroup
	 * @return
	 */
	public SnomedRelationshipUpdateRequestBuilder setUnionGroup(final Integer unionGroup) {
		this.unionGroup = unionGroup;
		return getSelf();
	}

	/**
	 * @param characteristicTypeId
	 * @return
	 */
	public SnomedRelationshipUpdateRequestBuilder setCharacteristicTypeId(final String characteristicTypeId) {
		this.characteristicTypeId = characteristicTypeId;
		return getSelf();
	}

	/**
	 * @param modifierId
	 * @return
	 */
	public SnomedRelationshipUpdateRequestBuilder setModifierId(final String modifierId) {
		this.modifierId = modifierId;
		return getSelf();
	}

	@Override
	protected SnomedRelationshipUpdateRequest create(final String componentId) {
		return new SnomedRelationshipUpdateRequest(componentId);
	}

	@Override
	protected void init(final SnomedRelationshipUpdateRequest req) {
		super.init(req);
		req.setTypeId(typeId);
		req.setDestinationId(destinationId);
		req.setValue(value);
		req.setRelationshipGroup(relationshipGroup);
		req.setUnionGroup(unionGroup);
		req.setCharacteristicTypeId(characteristicTypeId);
		req.setModifierId(modifierId);
	}
}
