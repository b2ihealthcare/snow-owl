/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

/**
 * @since 4.5
 */
public final class SnomedRelationshipUpdateRequestBuilder extends BaseSnomedComponentUpdateRequestBuilder<SnomedRelationshipUpdateRequestBuilder, SnomedRelationshipUpdateRequest> {

	private String characteristicTypeId;
	private Integer group;
	private Integer unionGroup;
	private String modifierId;
	private String destinationId;
	private String typeId;

	SnomedRelationshipUpdateRequestBuilder(String componentId) {
		super(componentId);
	}
	
	public SnomedRelationshipUpdateRequestBuilder setCharacteristicTypeId(String characteristicTypeId) {
		this.characteristicTypeId = characteristicTypeId;
		return getSelf();
	}
	
	public SnomedRelationshipUpdateRequestBuilder setGroup(Integer group) {
		this.group = group;
		return getSelf();
	}
	
	public SnomedRelationshipUpdateRequestBuilder setModifierId(String modifierId) {
		this.modifierId = modifierId;
		return getSelf();
	}
	
	public SnomedRelationshipUpdateRequestBuilder setUnionGroup(Integer unionGroup) {
		this.unionGroup = unionGroup;
		return getSelf();
	}
	
	public SnomedRelationshipUpdateRequestBuilder setDestinationId(String destinationId) {
		this.destinationId = destinationId;
		return getSelf();
	}
	
	public SnomedRelationshipUpdateRequestBuilder setTypeId(String typeId) {
		this.typeId = typeId;
		return getSelf();
	}
	
	@Override
	protected SnomedRelationshipUpdateRequest create(String componentId) {
		return new SnomedRelationshipUpdateRequest(componentId);
	}
	
	@Override
	protected void init(SnomedRelationshipUpdateRequest req) {
		super.init(req);
		req.setCharacteristicTypeId(characteristicTypeId);
		req.setGroup(group);
		req.setUnionGroup(unionGroup);
		req.setModifierId(modifierId);
		req.setDestinationId(destinationId);
		req.setTypeId(typeId);
	}

}
