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
public enum RelationshipRefinability {

	/**
	 * TODO document
	 */
	NOT_REFINABLE(Concepts.NOT_REFINABLE),

	/**
	 * TODO document
	 */
	OPTIONAL(Concepts.OPTIONAL_REFINABLE),

	/**
	 * TODO document
	 */
	MANDATORY(Concepts.MANDATORY_REFINABLE);

	private final String conceptId;

	private RelationshipRefinability(final String conceptId) {
		this.conceptId = conceptId;
	}

	public String getConceptId() {
		return conceptId;
	}

	public static RelationshipRefinability getByConceptId(final String conceptId) {
		for (final RelationshipRefinability candidate : values()) {
			if (candidate.getConceptId().equals(conceptId)) {
				return candidate;
			}
		}

		throw new IllegalArgumentException(MessageFormat.format("No relationship refinability value found for identifier ''{0}''.", conceptId));
	}
}