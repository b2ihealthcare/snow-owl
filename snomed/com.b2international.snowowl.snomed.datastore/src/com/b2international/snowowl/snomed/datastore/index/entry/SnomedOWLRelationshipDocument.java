/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.index.entry;

import java.util.Objects;

import com.b2international.index.Doc;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.StatementFragment;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

/**
 * @since 6.14
 */
@Doc(type = "owlRelationship", nested = true)
public final class SnomedOWLRelationshipDocument {

	private final String typeId;
	private final String destinationId;
	private final int group;
	
	@JsonCreator
	public SnomedOWLRelationshipDocument(
			@JsonProperty("typeId") String typeId, 
			@JsonProperty("destinationId") String destinationId, 
			@JsonProperty("group") int group) {
		this.typeId = typeId;
		this.destinationId = destinationId;
		this.group = group;
	}
	
	public String getTypeId() {
		return typeId;
	}
	
	public String getDestinationId() {
		return destinationId;
	}
	
	public int getGroup() {
		return group;
	}
	
	@JsonIgnore
	public boolean isIsa() {
		return Concepts.IS_A.equals(typeId);
	}

	@JsonIgnore
	public StatementFragment toStatementFragment() {
		return new StatementFragment(Long.parseLong(typeId), Long.parseLong(destinationId), false, group, 0, false, -1L, false, false);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(typeId, destinationId, group);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		SnomedOWLRelationshipDocument other = (SnomedOWLRelationshipDocument) obj;
		return Objects.equals(typeId, other.typeId)
				&& Objects.equals(destinationId, other.destinationId)
				&& Objects.equals(group, other.group);
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("typeId", typeId)
				.add("destinationId", destinationId)
				.add("group", group)
				.toString();
	}
	
}
