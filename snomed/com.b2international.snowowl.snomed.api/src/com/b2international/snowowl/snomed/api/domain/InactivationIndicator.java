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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.b2international.snowowl.snomed.datastore.SnomedInactivationPlan.InactivationReason;

/**
 * TODO document
 */
public enum InactivationIndicator {

	/**
	 * 
	 */
	RETIRED(InactivationReason.RETIRED),

	/**
	 * 
	 */
	AMBIGUOUS(InactivationReason.AMBIGUOUS),

	/**
	 * 
	 */
	DUPLICATE(InactivationReason.DUPLICATE),

	/**
	 * 
	 */
	ERRONEOUS(InactivationReason.ERRONEOUS),

	/**
	 * 
	 */
	MOVED_ELSEWHERE(InactivationReason.MOVED_TO);

	private final InactivationReason inactivationReason;

	private InactivationIndicator(final InactivationReason inactivationReason) {
		this.inactivationReason = inactivationReason;
	}

	public String getConceptId() {
		return inactivationReason.getInactivationReasonConceptId();
	}

	public static InactivationIndicator getByConceptId(final String conceptId) {
		checkNotNull(conceptId, "Concept identifier may not be null.");
		// XXX: Avoid matching on RETIRED by concept identifier 
		checkArgument(!conceptId.isEmpty(), "Concept identifier may not be empty."); 

		for (final InactivationIndicator candidate : values()) {
			if (candidate.getConceptId().equals(conceptId)) {
				return candidate;
			}
		}

		return RETIRED;
	}

	public InactivationReason toInactivationReason() {
		return inactivationReason;
	}
}