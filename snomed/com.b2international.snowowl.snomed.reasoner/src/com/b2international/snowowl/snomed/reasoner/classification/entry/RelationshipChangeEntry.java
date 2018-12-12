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
package com.b2international.snowowl.snomed.reasoner.classification.entry;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a reasoner change entry capturing the details of inferred or redundant relationships.
 */
public final class RelationshipChangeEntry extends ChangeEntry {

	private static final long serialVersionUID = 2L;

	private String destinationId;
	private int unionGroup;
	private String modifierId;
	private boolean destinationNegated;

	/**
	 * Creates a new relationship change entry with the specified arguments.
	 * 
	 * @param id of the entry
	 * @param nature the change nature
	 * @param sourceId the source component SCTID
	 * @param typeId the type component SCTID
	 * @param destinationId the destination component SCTID
	 * @param group the relationship's group
	 * @param unionGroup the relationship's union group
	 * @param modifierId the modifier SCTID
	 * @param destinationNegated {@code true} if the destination component is to be negated, {@code false} otherwise
	 */
	@JsonCreator
	public RelationshipChangeEntry(
			@JsonProperty("id") final String id, 
			@JsonProperty("nature") final Nature nature, 
			@JsonProperty("sourceId") final String sourceId, 
			@JsonProperty("typeId")final String typeId,
			@JsonProperty("group") final int group, 
			@JsonProperty("destinationId") final String destinationId, 
			@JsonProperty("unionGroup") final int unionGroup, 
			@JsonProperty("modifierId") final String modifierId,
			@JsonProperty("destinationNegated") final boolean destinationNegated) {

		super(id, nature, sourceId, typeId, group);

		this.destinationId = destinationId;
		this.unionGroup = unionGroup;
		this.modifierId = modifierId;
		this.destinationNegated = destinationNegated;
	}

	public String getDestinationId() {
		return destinationId;
	}

	/**
	 * @return the relationship union group
	 */
	public int getUnionGroup() {
		return unionGroup;
	}

	/**
	 * @return the modifier SCTID
	 */
	public String getModifierId() {
		return modifierId;
	}

	/**
	 * @return {@code true} if the destination component is to be negated, {@code false} otherwise
	 */
	public boolean isDestinationNegated() {
		return destinationNegated;
	}

	@Override 
	public int hashCode() {
		return 31 * super.hashCode() + Objects.hash(destinationId, unionGroup, modifierId, destinationNegated);
	}

	@Override 
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (!super.equals(obj)) { return false; }
		if (getClass() != obj.getClass()) { return false; }

		final RelationshipChangeEntry other = (RelationshipChangeEntry) obj;

		if (!Objects.equals(destinationId, other.destinationId)) { return false; }
		if (unionGroup != other.unionGroup) { return false; }
		if (destinationNegated != other.destinationNegated) { return false; }
		if (!Objects.equals(modifierId, other.modifierId)) { return false; }

		return true;
	}
}
