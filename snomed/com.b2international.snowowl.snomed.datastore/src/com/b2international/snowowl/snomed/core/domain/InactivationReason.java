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

import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.SnomedInactivationPlan;

/**
 * Enumeration representing the reason of the SNOMED&nbsp;CT concept inactivation.
 * <p>
 * The following reasons are available:
 * <ul>
 * <li>{@link #DUPLICATE <em>Duplicate</em>}</li>
 * <li>{@link #OUTDATED <em>Outdated</em>}</li>
 * <li>{@link #AMBIGUOUS <em>Ambiguous</em>}</li>
 * <li>{@link #ERRONEOUS <em>Erroneous</em>}</li>
 * <li>{@link #LIMITED <em>Limited</em>}</li>
 * <li>{@link #MOVED_ELSEWEHERE <em>Moved elsewhere</em>}</li>
 * <li>{@link #PENDING_MOVE <em>Pending move</em>}</li>
 * <li>{@link #RETIRED <em>Retired</em>}</li>
 * </ul>
 * </p>
 * <p>
 * Note that "Pending move" is present for completeness, however the process requires the concept to stay active, and so can not be handled by
 * {@code SnomedInactivationPlan} correctly at this time.
 * </p>
 * 
 * @see SnomedInactivationPlan
 */
public enum InactivationReason {

	/**
	 * Duplicate. <br>
	 * SNOMED&nbsp;CT reference set: SAME AS (ID: 900000000000527005) <br>
	 * SNOMED&nbsp;CT concept of the inactivation reason: DUPLICATE (ID: 900000000000482003)
	 * 
	 * @see InactivationReason
	 */
	DUPLICATE("Duplicate", Concepts.REFSET_SAME_AS_ASSOCIATION, Concepts.DUPLICATE), // SAME AS

	/**
	 * Outdated. <br>
	 * SNOMED&nbsp;CT reference set: REPLACED BY (ID: 900000000000526001) <br>
	 * SNOMED&nbsp;CT concept of the inactivation reason: OUTDATED (ID: 900000000000483008)
	 * 
	 * @see InactivationReason
	 */
	OUTDATED("Outdated", Concepts.REFSET_REPLACED_BY_ASSOCIATION, Concepts.OUTDATED), // REPLACED BY

	/**
	 * Ambiguous. <br>
	 * SNOMED&nbsp;CT reference set: POSSIBLY EQUIVALENT TO (ID: 900000000000523009) <br>
	 * SNOMED&nbsp;CT concept of the inactivation reason: AMBIGUOUS (ID: 900000000000484002)
	 * 
	 * @see InactivationReason
	 */
	AMBIGUOUS("Ambiguous", Concepts.REFSET_POSSIBLY_EQUIVALENT_TO_ASSOCIATION, Concepts.AMBIGUOUS), // POSSIBLY EQUIVALENT TO

	/**
	 * Erroneous. <br>
	 * SNOMED&nbsp;CT reference set: REPLACED BY (ID: 900000000000526001) <br>
	 * SNOMED&nbsp;CT concept of the inactivation reason: ERRONEOUS (ID: 900000000000485001)
	 * 
	 * @see InactivationReason
	 */
	ERRONEOUS("Erroneous", Concepts.REFSET_REPLACED_BY_ASSOCIATION, Concepts.ERRONEOUS), // REPLACED BY

	/**
	 * Limited. <br>
	 * SNOMED&nbsp;CT reference set: WAS A (ID: 900000000000528000) <br>
	 * SNOMED&nbsp;CT concept of the inactivation reason: LIMITED (ID: 900000000000486000)
	 * 
	 * @see InactivationReason
	 */
	LIMITED("Limited", Concepts.REFSET_WAS_A_ASSOCIATION, Concepts.LIMITED), // WAS A (may not be applicable universally)

	/**
	 * Moved elsewhere. <br>
	 * SNOMED&nbsp;CT reference set: MOVED TO (ID: 900000000000524003) <br>
	 * SNOMED&nbsp;CT concept of the inactivation reason: MOVED_ELSEWHERE (ID: 900000000000487009)
	 * 
	 * @see InactivationReason
	 */
	MOVED_ELSEWEHERE("Moved elsewhere", Concepts.REFSET_MOVED_TO_ASSOCIATION, Concepts.MOVED_ELSEWHERE), // MOVED TO

	/**
	 * Pending move. <br>
	 * SNOMED&nbsp;CT reference set: MOVED TO (ID: 900000000000524003) <br>
	 * SNOMED&nbsp;CT concept of the inactivation reason: PENDING_MOVE (ID: 900000000000492006)
	 * 
	 * @see InactivationReason
	 */
	PENDING_MOVE("Pending move", Concepts.REFSET_MOVED_TO_ASSOCIATION, Concepts.PENDING_MOVE), // MOVED TO

	/**
	 * Retired ("inactive with no reason given for inactivation"). Neither a historical association reference set member nor a component inactivation
	 * reference set member is required.
	 * 
	 * @see InactivationReason
	 */
	RETIRED("Retired", "", "");

	private final String name;
	private final String associatedRefSetId;
	private final String inactivationReasonConceptId;

	private InactivationReason(final String name, final String associatedRefSetId, final String inactivationReasonConceptId) {
		this.name = name;
		this.associatedRefSetId = associatedRefSetId;
		this.inactivationReasonConceptId = inactivationReasonConceptId;
	}

	/**
	 * Returns with the human readable name of the inactivation reason.
	 * 
	 * @return the human readable reason.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns with the associated SNOMED&nbsp;CT reference set concept identifier.
	 * <p>
	 * <b>Note:</b> can be empty string if the inactivation reason is {@link #RETIRED retired}.
	 * 
	 * @return the associated reference set concept identifier.
	 */
	public String getAssociatedRefSetId() {
		return associatedRefSetId;
	}

	/**
	 * Returns with the ID of the inactivation reason SNOMED&nbsp;CT concept.
	 * <p>
	 * <b>Note:</b> can be empty string if the inactivation reason is {@link #RETIRED retired}.
	 * 
	 * @return the ID of the inactivation reason.
	 */
	public String getInactivationReasonConceptId() {
		return inactivationReasonConceptId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return getName();
	}

}