/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.index.constraint;

import com.b2international.snowowl.snomed.mrcm.HierarchyInclusionType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 6.5
 */
public final class HierarchyDefinitionFragment extends ConceptSetDefinitionFragment {

	private final String conceptId;
	private final HierarchyInclusionType inclusionType;

	@JsonCreator
	HierarchyDefinitionFragment(
			@JsonProperty("uuid") final String uuid, 
			@JsonProperty("active") final boolean active, 
			@JsonProperty("effectiveTime") final long effectiveTime, 
			@JsonProperty("author") final String author,
			@JsonProperty("conceptId") final String conceptId, 
			@JsonProperty("inclusionType") final HierarchyInclusionType inclusionType) {

		super(uuid, active, effectiveTime, author);

		this.conceptId = conceptId;
		this.inclusionType = inclusionType;
	}

	public String getConceptId() {
		return conceptId;
	}

	public HierarchyInclusionType getInclusionType() {
		return inclusionType;
	}
	
	@Override
	public String toEcl() {
		switch (inclusionType) {
			case SELF: 
				return conceptId;
			case DESCENDANT: 
				return String.format("<%s", conceptId); 
			case SELF_OR_DESCENDANT: 
				return String.format("<<%s", conceptId); 
			default: 
				throw new IllegalArgumentException("Unknown inclusion type: " + inclusionType);
		}
	}
}
