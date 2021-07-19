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
package com.b2international.snowowl.snomed.core.rest.domain;

import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.RelationshipValue;
import com.b2international.snowowl.snomed.datastore.request.SnomedRelationshipCreateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.base.MoreObjects;

/**
 * @since 1.0
 */
public class SnomedRelationshipRestInput extends AbstractSnomedComponentRestInput<SnomedRelationshipCreateRequestBuilder> {

	private String sourceId;
	private String typeId;
	private String destinationId;
	private boolean destinationNegated = false;
	private String value;
	private int relationshipGroup = 0;
	private int unionGroup = 0;
	private String characteristicTypeId = Concepts.STATED_RELATIONSHIP;
	private String modifierId = Concepts.EXISTENTIAL_RESTRICTION_MODIFIER;

	public String getSourceId() {
		return sourceId;
	}

	public String getTypeId() {
		return typeId;
	}

	public String getDestinationId() {
		return destinationId;
	}

	public boolean isDestinationNegated() {
		return destinationNegated;
	}

	public String getValue() {
		return value;
	}

	public int getRelationshipGroup() {
		return relationshipGroup;
	}

	public int getUnionGroup() {
		return unionGroup;
	}

	public String getCharacteristicTypeId() {
		return characteristicTypeId;
	}

	public String getModifierId() {
		return modifierId;
	}

	public void setSourceId(final String sourceId) {
		this.sourceId = sourceId;
	}

	public void setTypeId(final String typeId) {
		this.typeId = typeId;
	}

	public void setDestinationId(final String destinationId) {
		this.destinationId = destinationId;
	}

	public void setDestinationNegated(final boolean destinationNegated) {
		this.destinationNegated = destinationNegated;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	public void setRelationshipGroup(final int relationshipGroup) {
		this.relationshipGroup = relationshipGroup;
	}

	public void setUnionGroup(final int unionGroup) {
		this.unionGroup = unionGroup;
	}

	public void setCharacteristicTypeId(final String characteristicTypeId) {
		this.characteristicTypeId = characteristicTypeId;
	}

	public void setModifierId(final String modifierId) {
		this.modifierId = modifierId;
	}

	@Override
	protected SnomedRelationshipCreateRequestBuilder createRequestBuilder() {
		return SnomedRequests.prepareNewRelationship();
	}

	@Override
	public SnomedRelationshipCreateRequestBuilder toRequestBuilder() {
		return super.toRequestBuilder()
			.setSourceId(sourceId)
			.setTypeId(typeId)
			.setDestinationId(destinationId)
			.setDestinationNegated(destinationNegated)
			.setValue(RelationshipValue.fromLiteral(value))
			.setRelationshipGroup(relationshipGroup)
			.setUnionGroup(unionGroup)
			.setCharacteristicTypeId(characteristicTypeId)
			.setModifierId(modifierId);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
			.add("id", getId())
			.add("moduleId", getModuleId())
			.add("sourceId", sourceId)
			.add("typeId", typeId)
			.add("destinationId", destinationId)
			.add("destinationNegated", destinationNegated)
			.add("value", value)
			.add("relationshipGroup", relationshipGroup)
			.add("unionGroup", unionGroup)
			.add("characteristicTypeId", characteristicTypeId)
			.add("modifierId", modifierId)
			.toString();
	}
}