/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.domain;

import java.text.MessageFormat;

import com.b2international.snowowl.snomed.SnomedConstants.Concepts;

/**
 * TODO document
 */
public enum CaseSignificance implements ConceptEnum {

	/**
	 * TODO document 
	 */
	ENTIRE_TERM_CASE_SENSITIVE(Concepts.ENTIRE_TERM_CASE_SENSITIVE),

	/**
	 * TODO document 
	 */
	CASE_INSENSITIVE(Concepts.ENTIRE_TERM_CASE_INSENSITIVE),

	/**
	 * TODO document
	 */
	INITIAL_CHARACTER_CASE_INSENSITIVE(Concepts.ONLY_INITIAL_CHARACTER_CASE_INSENSITIVE);

	private final String conceptId;

	private CaseSignificance(final String conceptId) {
		this.conceptId = conceptId;
	}

	@Override
	public String getConceptId() {
		return conceptId;
	}

	public static CaseSignificance getByConceptId(final String conceptId) {
		for (final CaseSignificance candidate : values()) {
			if (candidate.getConceptId().equals(conceptId)) {
				return candidate;
			}
		}

		throw new IllegalArgumentException(MessageFormat.format("No case significance value found for identifier ''{0}''.", conceptId));
	}
}
