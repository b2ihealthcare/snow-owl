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
 * Enumeration to specify the reason of the inactivation of SNOMED CT descriptions.
 * 
 * @see <a href="http://www.snomed.org/tig?t=tsg2_metadata_refset_status_cpt">Component Inactivation Reference Sets
 * (Technical Implementation Guide)</a>
 */
public enum DescriptionInactivationIndicator implements ConceptEnum {

	/**
	 * The description duplicates another description.
	 */
	DUPLICATE(Concepts.DUPLICATE),
	
	/**
	 * The description is outdated, and should not be used.
	 */
	OUTDATED(Concepts.OUTDATED),
	
	/**
	 * The description contains mistakes in its text, or was otherwise added in error.
	 */
	ERRONEOUS(Concepts.ERRONEOUS),

	/**
	 * The description refers to a limited concept. 
	 */
	LIMITED(Concepts.LIMITED),
	
	/**
	 * The description was moved to another namespace. 
	 */
	MOVED_ELSEWHERE(Concepts.MOVED_ELSEWHERE),
	
	/**
	 * The description is still active, but will be moved to another namespace.
	 */
	PENDING_MOVE(Concepts.PENDING_MOVE),
	
	/**
	 * The description does not describe the concept properly. 
	 */
	INAPPROPRIATE(Concepts.INAPPROPRIATE),

	/**
	 * The description is still active, but it refers to an inactive concept.
	 */
	CONCEPT_NON_CURRENT(Concepts.CONCEPT_NON_CURRENT),
	
	/**
	 * The description has been retired without any particular indication. 
	 */
	RETIRED("");

	private String conceptId;

	private DescriptionInactivationIndicator(final String conceptId) {
		this.conceptId = conceptId;
	}

	/**
	 * Returns the identifier of the concept that represents the inactivation reason.
	 * 
	 * @return the concept identifier of the inactivation reason
	 */
	@Override
	public String getConceptId() {
		return conceptId;
	}

	/**
	 * Returns with the description inactivation indication for the specified concept identifier.
	 * 
	 * @param conceptId the concept identifier to look up
	 * 
	 * @return the resolved description inactivation indicator, or {@code null} if {@code conceptId} is null or empty,
	 * or a suitable enum literal cannot be found for the identifier
	 */
	public static DescriptionInactivationIndicator getInactivationIndicatorByValueId(final String conceptId) {
		if (Strings.isNullOrEmpty(conceptId)) {
			return null;
		}
		
		for (final DescriptionInactivationIndicator candidate : values()) {
			if (candidate.getConceptId().equals(conceptId)) {
				return candidate;
			}
		}

		return null;
	}
}
