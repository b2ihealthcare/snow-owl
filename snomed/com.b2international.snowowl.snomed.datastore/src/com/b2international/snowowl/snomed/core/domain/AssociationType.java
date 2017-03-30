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

import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.google.common.base.Strings;

/**
 * Enumerates known historical association reference sets and their corresponding identifier concepts.
 * 
 * @see <a href="http://www.snomed.org/tig?t=tsg2_metadata_refset_historical">Historical Association Reference Sets
 * (Technical Implementation Guide)</a>
 */
public enum AssociationType {

	/**
	 * Points to an active concept replacing the referenced inactive concept derived from ICD-9, Chapter XVI.
	 */
	ALTERNATIVE(Concepts.REFSET_ALTERNATIVE_ASSOCIATION),

	/**
	 * Points to the original component counterpart of the referenced component, before the move to a new namespace
	 */
	MOVED_FROM(Concepts.REFSET_MOVED_FROM_ASSOCIATION),

	/**
	 * Points to the target namespace <i>concept</i>, when the referenced component is moved from its original namespace
	 */
	MOVED_TO(Concepts.REFSET_MOVED_TO_ASSOCIATION),

	/**
	 * Points to one of the suggested replacements of the referenced inactive ("ambiguous") concept
	 */
	POSSIBLY_EQUIVALENT_TO(Concepts.REFSET_POSSIBLY_EQUIVALENT_TO_ASSOCIATION),

	/**
	 * Points to a concept that the referenced inactive ("inappropriate") description describes more properly
	 */
	REFERS_TO(Concepts.REFSET_REFERS_TO_ASSOCIATION),

	/**
	 * Points to the single replacement concept of the referenced inactive ("erroneous") concept
	 */
	REPLACED_BY(Concepts.REFSET_REPLACED_BY_ASSOCIATION),

	/**
	 * Points to the unique equivalent of the referenced inactive (duplicate) concept
	 */
	SAME_AS(Concepts.REFSET_SAME_AS_ASSOCIATION),

	SIMILAR_TO(Concepts.REFSET_SIMILAR_TO_ASSOCIATION),

	/**
	 * Points to the closest active supertype of the referenced inactive (NOS or "otherwise specified") concept
	 */
	WAS_A(Concepts.REFSET_WAS_A_ASSOCIATION);

	private final String conceptId;

	private AssociationType(final String conceptId) {
		this.conceptId = conceptId;
	}

	/**
	 * Retrieves the concept identifier for this association reference set.
	 * 
	 * @return the concept identifier corresponding to the association constant
	 */
	public String getConceptId() {
		return conceptId;
	}

	/**
	 * Performs a reverse lookup by concept identifier and returns the matching association type.
	 * 
	 * @param conceptId the concept identifier to look for
	 * 
	 * @return the resolved {@link AssociationType}, or {@code null} if {@code conceptId} is null 
	 * or empty, or no matching enum literal is found
	 */
	public static AssociationType getByConceptId(final String conceptId) {
		if (Strings.isNullOrEmpty(conceptId)) {
			return null;
		}
		
		for (final AssociationType candidate : values()) {
			if (candidate.getConceptId().equals(conceptId)) {
				return candidate;
			}
		}

		return null;
	}
}
