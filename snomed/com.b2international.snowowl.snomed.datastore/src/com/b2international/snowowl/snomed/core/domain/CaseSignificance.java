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
 * Enumerates allowed description case significance values and their SNOMED CT concept counterparts.
 * 
 * @see <a href="http://www.snomed.org/tig?t=tsg2_metadata_enumeration_case">Concept enumerations for caseSignificanceId
 * (Technical Implementation Guide)</a>
 */
public enum CaseSignificance implements ConceptEnum {

	/**
	 * The description term case must be preserved in its entirety
	 */
	ENTIRE_TERM_CASE_SENSITIVE(Concepts.ENTIRE_TERM_CASE_SENSITIVE),

	/**
	 * The description term is case insensitive
	 */
	CASE_INSENSITIVE(Concepts.ENTIRE_TERM_CASE_INSENSITIVE),

	/**
	 * The initial character of the term is case insensitive
	 */
	INITIAL_CHARACTER_CASE_INSENSITIVE(Concepts.ONLY_INITIAL_CHARACTER_CASE_INSENSITIVE);

	private final String conceptId;

	private CaseSignificance(final String conceptId) {
		this.conceptId = conceptId;
	}

	/**
	 * Retrieves the concept identifier for this case significance value.
	 * 
	 * @return the concept identifier corresponding to the case significance constant
	 */
	@Override
	public String getConceptId() {
		return conceptId;
	}

	/**
	 * Performs a reverse lookup by concept identifier and returns the matching case significance.
	 * 
	 * @param conceptId the concept identifier to look for
	 * 
	 * @return the resolved {@link CaseSignificance}, or {@code null} if {@code conceptId} is null or empty
	 * @throws IllegalArgumentException if no case significance matches the specified concept identifier
	 */
	public static CaseSignificance getByConceptId(final String conceptId) {
		if (Strings.isNullOrEmpty(conceptId)) {
			return null;
		}
		
		for (final CaseSignificance candidate : values()) {
			if (candidate.getConceptId().equals(conceptId)) {
				return candidate;
			}
		}

		throw new IllegalArgumentException(MessageFormat.format("No case significance value found for identifier ''{0}''.", conceptId));
	}
}
