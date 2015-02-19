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

import com.b2international.snowowl.snomed.SnomedConstants.Concepts;

/**
 * TODO document
 */
public enum AssociationType {

	/**
	 * 
	 */
	ALTERNATIVE(Concepts.REFSET_ALTERNATIVE_ASSOCIATION),

	/**
	 * 
	 */
	MOVED_FROM(Concepts.REFSET_MOVED_FROM_ASSOCIATION),

	/**
	 * 
	 */
	MOVED_TO(Concepts.REFSET_MOVED_TO_ASSOCIATION),

	/**
	 * 
	 */
	POSSIBLY_EQUIVALENT_TO(Concepts.REFSET_POSSIBLY_EQUIVALENT_TO_ASSOCIATION),

	/**
	 * 
	 */
	REFERS_TO(Concepts.REFSET_REFERS_TO_ASSOCIATION),

	/**
	 * 
	 */
	REPLACED_BY(Concepts.REFSET_REPLACED_BY_ASSOCIATION),

	/**
	 * 
	 */
	SAME_AS(Concepts.REFSET_SAME_AS_ASSOCIATION),

	/**
	 * 
	 */
	SIMILAR_TO(Concepts.REFSET_SIMILAR_TO_ASSOCIATION),

	/**
	 * 
	 */
	WAS_A(Concepts.REFSET_WAS_A_ASSOCIATION);

	private final String conceptId;

	private AssociationType(final String conceptId) {
		this.conceptId = conceptId;
	}

	public String getConceptId() {
		return conceptId;
	}

	public static AssociationType getByConceptId(final String conceptId) {
		for (final AssociationType candidate : values()) {
			if (candidate.getConceptId().equals(conceptId)) {
				return candidate;
			}
		}

		return null;
	}
}