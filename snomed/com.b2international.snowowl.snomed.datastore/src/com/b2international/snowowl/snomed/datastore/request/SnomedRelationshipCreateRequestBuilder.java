/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.core.domain.RelationshipModifier;

/**
 * <i>Builder</i> class to build requests responsible for creating SNOMED CT relationships.
 * This class should be instantiated from the corresponding static method on the central {@link SnomedRequests} class.
 * 
 * @since 4.5
 */
public final class SnomedRelationshipCreateRequestBuilder extends SnomedComponentCreateRequestBuilder<SnomedRelationshipCreateRequestBuilder> {

	private CharacteristicType characteristicType = CharacteristicType.STATED_RELATIONSHIP;
	private String destinationId;
	private String sourceId;
	private boolean destinationNegated;
	private int group = 0;
	private RelationshipModifier modifier = RelationshipModifier.EXISTENTIAL;
	private Integer unionGroup = 0;
	private String typeId;

	SnomedRelationshipCreateRequestBuilder() { 
		super();
	}
	
	public SnomedRelationshipCreateRequestBuilder setDestinationId(String destinationId) {
		this.destinationId = destinationId;
		return getSelf();
	}
	
	public SnomedRelationshipCreateRequestBuilder setSourceId(String sourceId) {
		this.sourceId = sourceId;
		return getSelf();
	}
	
	public SnomedRelationshipCreateRequestBuilder setCharacteristicType(CharacteristicType characteristicType) {
		this.characteristicType = characteristicType;
		return getSelf();
	}
	
	public SnomedRelationshipCreateRequestBuilder setDestinationNegated(boolean destinationNegated) {
		this.destinationNegated = destinationNegated;
		return getSelf();
	}
	
	public SnomedRelationshipCreateRequestBuilder setGroup(Integer group) {
		this.group = group == null ? 0 : group;
		return getSelf();
	}
	
	public SnomedRelationshipCreateRequestBuilder setModifier(RelationshipModifier modifier) {
		this.modifier = modifier;
		return getSelf();
	}
	
	public SnomedRelationshipCreateRequestBuilder setTypeId(String typeId) {
		this.typeId = typeId;
		return getSelf();
	}
	
	public SnomedRelationshipCreateRequestBuilder setUnionGroup(Integer unionGroup) {
		this.unionGroup = unionGroup == null ? 0 : unionGroup;
		return getSelf();
	}
	
	@Override
	protected void init(BaseSnomedComponentCreateRequest request) {
		final SnomedRelationshipCreateRequest req = (SnomedRelationshipCreateRequest) request;
		req.setCharacteristicType(characteristicType);
		req.setDestinationId(destinationId);
		req.setSourceId(sourceId);
		req.setDestinationNegated(destinationNegated);
		req.setGroup(group);
		req.setModifier(modifier);
		req.setUnionGroup(unionGroup);
		req.setTypeId(typeId);
	}

	@Override
	protected BaseSnomedComponentCreateRequest createRequest() {
		return new SnomedRelationshipCreateRequest();
	}

}
