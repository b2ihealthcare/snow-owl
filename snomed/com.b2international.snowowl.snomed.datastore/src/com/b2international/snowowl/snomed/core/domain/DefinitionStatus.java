/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.domain;

import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Enumerates allowed concept definition status values and their corresponding concept identifiers.
 * 
 * @see <a href="http://www.snomed.org/tig?t=tsg2_metadata_enumeration_definition">Concept enumerations for
 * definitionStatusId (Technical Implementation Guide)</a>
 */
public enum DefinitionStatus implements ConceptEnum {

	/**
	 * The concept's defining relationships do not define the concept in full; the missing parts are not known, not
	 * expressible in SNOMED CT's terms or are otherwise not present.
	 */
	PRIMITIVE(Concepts.PRIMITIVE),

	/**
	 * The concept's defining relationships make up a complete definition of the concept.
	 */
	FULLY_DEFINED(Concepts.FULLY_DEFINED);

	private final String conceptId;

	private DefinitionStatus(final String conceptId) {
		this.conceptId = conceptId;
	}

	/**
	 * Retrieves the concept identifier for this definition status.
	 * 
	 * @return the concept identifier corresponding to the definition status constant
	 */
	@Override
	public String getConceptId() {
		return conceptId;
	}

	@JsonIgnore
	public boolean isPrimitive() {
		return PRIMITIVE.equals(this);
	}
	
}
