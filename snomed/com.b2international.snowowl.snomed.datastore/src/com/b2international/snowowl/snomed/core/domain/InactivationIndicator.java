/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Enumeration to specify the reason of the inactivation of SNOMED CT concepts.
 * 
 * @see <a href="http://www.snomed.org/tig?t=tsg2_metadata_refset_status_cpt">Component Inactivation Reference Sets
 * (Technical Implementation Guide)</a>
 */
public enum InactivationIndicator implements ConceptEnum {

	/**
	 * The concept duplicates the definition of another concept.
	 */
	DUPLICATE(InactivationReason.DUPLICATE),

	/**
	 * The concept is outdated, and no longer used.
	 */
	OUTDATED(InactivationReason.OUTDATED),
	
	/**
	 * The concept is ambiguous.
	 */
	AMBIGUOUS(InactivationReason.AMBIGUOUS),
	
	/**
	 * The concept contains an error.
	 */
	ERRONEOUS(InactivationReason.ERRONEOUS),

	/**
	 * The concept is of limited value as it contains classification categories which do not have a stable meaning.
	 */
	LIMITED(InactivationReason.LIMITED),
	
	/**
	 * The concept was moved to another namespace.
	 */
	MOVED_ELSEWHERE(InactivationReason.MOVED_ELSEWEHERE),
	
	/**
	 * The concept is still active, but is in the process of being moved to another namespace.
	 */
	PENDING_MOVE(InactivationReason.PENDING_MOVE),
	
	/**
	 * The concept has been retired without any particular indication. 
	 */
	RETIRED(InactivationReason.RETIRED);

	private final InactivationReason inactivationReason;

	private InactivationIndicator(final InactivationReason inactivationReason) {
		this.inactivationReason = inactivationReason;
	}

	/**
	 * Retrieves the concept identifier for this inactivation indicator.
	 * 
	 * @return the concept identifier corresponding to the inactivation indicator constant
	 */
	@Override
	public String getConceptId() {
		return inactivationReason.getInactivationReasonConceptId();
	}

	/**
	 * Returns with the concept inactivation indication for the specified concept identifier.
	 * 
	 * @param conceptId the concept identifier to look up (may not be {@code null} or empty)
	 * 
	 * @return the resolved concept inactivation indicator, or {@code #RETIRED} if one cannot be found for the
	 * specified identifier
	 */
	public static InactivationIndicator getByConceptId(final String conceptId) {
		checkNotNull(conceptId, "Concept identifier may not be null.");
		checkArgument(!conceptId.isEmpty(), "Concept identifier may not be empty."); 

		for (final InactivationIndicator candidate : values()) {
			if (candidate.getConceptId().equals(conceptId)) {
				return candidate;
			}
		}

		return RETIRED;
	}

	/**
	 * Converts this enumeration to its {@link InactivationReason} counterpart (for internal use). 
	 * 
	 * @return the converted inactivation reason value
	 */
	public InactivationReason toInactivationReason() {
		return inactivationReason;
	}
}
