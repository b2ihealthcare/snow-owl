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

import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.google.common.base.Strings;

/**
 * Enumerates allowed relationship modifiers and maps them to concept identifiers.
 * 
 * @see <a href="http://www.snomed.org/tig?t=tsg2_metadata_enumeration_modifier">Concept enumerations for modifierId
 * (Technical Implementation Guide)</a>
 */
public enum RelationshipModifier implements ConceptEnum {

	/** 
	 * Existential restriction (&exist;)
	 */
	EXISTENTIAL(Concepts.EXISTENTIAL_RESTRICTION_MODIFIER),

	/** 
	 * Universal restriction (&forall;)
	 */
	UNIVERSAL(Concepts.UNIVERSAL_RESTRICTION_MODIFIER);

	private final String conceptId;

	private RelationshipModifier(final String conceptId) {
		this.conceptId = conceptId;
	}

	/**
	 * Retrieves the concept identifier for this relationship modifier value.
	 * 
	 * @return the concept identifier corresponding to the modifier constant
	 */
	@Override
	public String getConceptId() {
		return conceptId;
	}
	
	/**
	 * Performs a reverse lookup by concept identifier and returns the matching modifier value.
	 * 
	 * @param conceptId the concept identifier to look for
	 * @return the resolved {@link RelationshipModifier}, or {@code null} if {@code conceptId} is null or empty
	 * @throws IllegalArgumentException if no known relationship modifier matches the specified concept identifier
	 */
	public static RelationshipModifier getByConceptId(final String conceptId) {
		if (Strings.isNullOrEmpty(conceptId)) {
			return null;
		}
		
		for (final RelationshipModifier candidate : values()) {
			if (candidate.getConceptId().equals(conceptId)) {
				return candidate;
			}
		}

		throw new IllegalArgumentException(MessageFormat.format("No relationship modifier value found for identifier ''{0}''.", conceptId));
	}
}
