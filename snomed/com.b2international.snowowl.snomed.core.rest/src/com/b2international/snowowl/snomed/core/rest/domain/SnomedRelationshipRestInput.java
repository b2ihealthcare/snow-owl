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
package com.b2international.snowowl.snomed.core.rest.domain;

import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.request.SnomedRelationshipCreateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;

/**
 * @since 1.0
 */
public class SnomedRelationshipRestInput extends AbstractSnomedComponentRestInput<SnomedRelationshipCreateRequestBuilder> {

	private String characteristicTypeId = Concepts.STATED_RELATIONSHIP;
	private String destinationId;
	private boolean destinationNegated = false;
	private int group = 0;
	private String modifierId = Concepts.EXISTENTIAL_RESTRICTION_MODIFIER;
	private String sourceId;
	private String typeId;
	private int unionGroup = 0;

	public String getCharacteristicTypeId() {
		return characteristicTypeId;
	}

	public String getDestinationId() {
		return destinationId;
	}

	public boolean isDestinationNegated() {
		return destinationNegated;
	}

	public int getGroup() {
		return group;
	}

	public String getModifierId() {
		return modifierId;
	}

	public String getSourceId() {
		return sourceId;
	}

	public String getTypeId() {
		return typeId;
	}

	public int getUnionGroup() {
		return unionGroup;
	}

	public void setCharacteristicTypeId(final String characteristicTypeId) {
		this.characteristicTypeId = characteristicTypeId;
	}

	public void setDestinationId(final String destinationId) {
		this.destinationId = destinationId;
	}

	public void setDestinationNegated(final boolean destinationNegated) {
		this.destinationNegated = destinationNegated;
	}

	public void setGroup(final int group) {
		this.group = group;
	}

	public void setModifierId(final String modifierId) {
		this.modifierId = modifierId;
	}

	public void setSourceId(final String sourceId) {
		this.sourceId = sourceId;
	}

	public void setTypeId(final String typeId) {
		this.typeId = typeId;
	}

	public void setUnionGroup(final int unionGroup) {
		this.unionGroup = unionGroup;
	}

	@Override
	protected SnomedRelationshipCreateRequestBuilder createRequestBuilder() {
		return SnomedRequests.prepareNewRelationship();
	}

	@Override
	public SnomedRelationshipCreateRequestBuilder toRequestBuilder() {
		return super.toRequestBuilder()
				.setCharacteristicTypeId(getCharacteristicTypeId())
				.setDestinationId(getDestinationId())
				.setDestinationNegated(isDestinationNegated())
				.setGroup(getGroup())
				.setModifierId(getModifierId())
				.setSourceId(getSourceId())
				.setTypeId(getTypeId())
				.setUnionGroup(getUnionGroup());
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("SnomedRelationshipRestInput [getId()=");
		builder.append(getId());
		builder.append(", getModuleId()=");
		builder.append(getModuleId());
		builder.append(", getCharacteristicTypeId()=");
		builder.append(getCharacteristicTypeId());
		builder.append(", getDestinationId()=");
		builder.append(getDestinationId());
		builder.append(", isDestinationNegated()=");
		builder.append(isDestinationNegated());
		builder.append(", getGroup()=");
		builder.append(getGroup());
		builder.append(", getModifierId()=");
		builder.append(getModifierId());
		builder.append(", getSourceId()=");
		builder.append(getSourceId());
		builder.append(", getTypeId()=");
		builder.append(getTypeId());
		builder.append(", getUnionGroup()=");
		builder.append(getUnionGroup());
		builder.append(", createComponentInput()=");
		builder.append(createRequestBuilder());
		builder.append("]");
		return builder.toString();
	}
}