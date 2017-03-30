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
 * Enumerates allowed relationship refinability values and maps them to concept identifiers.
 * 
 * @see <a href="http://www.snomed.org/tig?t=tsg2_metadata_refset_status_relref">Relationship Refinability Reference Set
 * (Technical Implementation Guide)</a>
 */
public enum RelationshipRefinability {

	/**
	 * Relationship can not be refined in postcoordinated concept expressions.
	 */
	NOT_REFINABLE(Concepts.NOT_REFINABLE),

	/**
	 * Relationship can optionally be refined in postcoordinated concept expressions. 
	 */
	OPTIONAL(Concepts.OPTIONAL_REFINABLE),

	/**
	 * Relationship must be refined in postcoordinated concept expressions.
	 */
	MANDATORY(Concepts.MANDATORY_REFINABLE);

	private final String conceptId;

	private RelationshipRefinability(final String conceptId) {
		this.conceptId = conceptId;
	}

	/**
	 * Retrieves the concept identifier for this refinability value.
	 * 
	 * @return the concept identifier corresponding to the refinability constant
	 */
	public String getConceptId() {
		return conceptId;
	}

	/**
	 * Performs a reverse lookup by concept identifier and returns the matching refinability value.
	 * 
	 * @param conceptId the concept identifier to look for
	 * 
	 * @return the resolved {@link RelationshipRefinability}, or {@code null} if {@code conceptId} is null or empty
	 * @throws IllegalArgumentException if no refinability value matches the specified concept identifier
	 */
	public static RelationshipRefinability getByConceptId(final String conceptId) {
		if (Strings.isNullOrEmpty(conceptId)) {
			return null;
		}
		
		for (final RelationshipRefinability candidate : values()) {
			if (candidate.getConceptId().equals(conceptId)) {
				return candidate;
			}
		}

		throw new IllegalArgumentException(MessageFormat.format("No relationship refinability value found for identifier ''{0}''.", conceptId));
	}
}
