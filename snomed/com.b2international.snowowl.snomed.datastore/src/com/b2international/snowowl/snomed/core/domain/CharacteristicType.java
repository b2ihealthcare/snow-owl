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
package com.b2international.snowowl.snomed.core.domain;

import java.text.MessageFormat;

import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.google.common.base.Strings;

/**
 * Enumerates allowed relationship characteristic types and their corresponding concept identifiers.
 * 
 * @see <a href="http://www.snomed.org/tig?t=tsg2_metadata_enumeration_characteristic">Concept enumerations for
 * characteristicTypeId (Technical Implementation Guide)</a>
 */
public enum CharacteristicType implements ConceptEnum {

	/**
	 * The relationship is used as part of the description logic (DL) definition
	 */
	DEFINING_RELATIONSHIP(Concepts.DEFINING_RELATIONSHIP),

	/**
	 * The relationship is defining, and was created by an author (manually)
	 */
	STATED_RELATIONSHIP(Concepts.STATED_RELATIONSHIP),

	/**
	 * The relationship is defining, and was created by the reasoner (automatically)
	 */
	INFERRED_RELATIONSHIP(Concepts.INFERRED_RELATIONSHIP),

	/**
	 * When creating postcoordinated expressions based on the concept, this relationship can be included as a qualifier
	 */
	QUALIFYING_RELATIONSHIP(Concepts.QUALIFYING_RELATIONSHIP),

	/**
	 * The relationship is not part of the DL definition; it only provides supplementary information
	 */
	ADDITIONAL_RELATIONSHIP(Concepts.ADDITIONAL_RELATIONSHIP);

	private final String conceptId;

	private CharacteristicType(final String conceptId) {
		this.conceptId = conceptId;
	}

	/**
	 * Retrieves the concept identifier for this characteristic type.
	 * 
	 * @return the concept identifier corresponding to the characteristic type constant
	 */
	@Override
	public String getConceptId() {
		return conceptId;
	}

	/**
	 * Performs a reverse lookup by concept identifier and returns the matching characteristic type.
	 * 
	 * @param conceptId the concept identifier to look for
	 * 
	 * @return the resolved {@link CharacteristicType}, or {@code null} if {@code conceptId} is null or empty
	 * @throws IllegalArgumentException if no characteristic type matches the specified concept identifier
	 */
	public static CharacteristicType getByConceptId(final String conceptId) {
		if (Strings.isNullOrEmpty(conceptId)) {
			return null;
		}
		
		for (final CharacteristicType candidate : values()) {
			if (candidate.getConceptId().equals(conceptId)) {
				return candidate;
			}
		}

		throw new IllegalArgumentException(MessageFormat.format("No characteristic type value found for identifier ''{0}''.", conceptId));
	}

	public boolean isDefining() {
		return this == DEFINING_RELATIONSHIP || this == INFERRED_RELATIONSHIP || this == STATED_RELATIONSHIP;
	}
	
}
