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
 * Enumerates allowed acceptability values and maps them to concept identifiers.
 * 
 * @see <a href="http://www.snomed.org/tig?t=tsg2_metadata_refset_language">Language Reference Sets (Technical
 * Implementation Guide)</a>
 */
public enum Acceptability {

	/**
	 * Description is acceptable in language or dialect
	 */
	ACCEPTABLE(Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_ACCEPTABLE),

	/**
	 * Description is preferred in language or dialect
	 */
	PREFERRED(Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED);

	private final String conceptId;

	private Acceptability(final String conceptId) {
		this.conceptId = conceptId;
	}

	/**
	 * Retrieves the concept identifier for this acceptability value.
	 * 
	 * @return the concept identifier corresponding to the acceptability constant
	 */
	public String getConceptId() {
		return conceptId;
	}

	/**
	 * Performs a reverse lookup by concept identifier and returns the matching acceptability value.
	 * 
	 * @param conceptId the concept identifier to look for
	 * 
	 * @return the resolved {@link Acceptability}, or {@code null} if {@code conceptId} is null or empty
	 * @throws IllegalArgumentException if no acceptability value matches the specified concept identifier
	 */
	public static Acceptability getByConceptId(final String conceptId) {
		if (Strings.isNullOrEmpty(conceptId)) {
			return null;
		}

		for (final Acceptability candidate : values()) {
			if (candidate.getConceptId().equals(conceptId)) {
				return candidate;
			}
		}

		throw new IllegalArgumentException(MessageFormat.format("No acceptability value found for identifier ''{0}''.", conceptId));
	}
}
