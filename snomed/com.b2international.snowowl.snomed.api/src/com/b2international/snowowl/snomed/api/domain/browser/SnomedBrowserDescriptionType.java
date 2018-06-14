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
package com.b2international.snowowl.snomed.api.domain.browser;

import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.ConceptEnum;

/**
 * Enumerates supported description types in the IHTSDO SNOMED CT Browser.
 */
public enum SnomedBrowserDescriptionType implements ConceptEnum {

	/** Fully Specified Name */
	FSN(Concepts.FULLY_SPECIFIED_NAME),
	/** Synonym */
	SYNONYM(Concepts.SYNONYM),
	/** Text definition */
	TEXT_DEFINITION(Concepts.TEXT_DEFINITION);

	private final String conceptId;

	private SnomedBrowserDescriptionType(final String conceptId) {
		this.conceptId = conceptId;
	}

	@Override
	public String getConceptId() {
		return conceptId;
	}

	public static SnomedBrowserDescriptionType getByConceptId(final String conceptId) {
		for (final SnomedBrowserDescriptionType candidate : values()) {
			if (candidate.getConceptId().equals(conceptId)) {
				return candidate;
			}
		}

		return null;
	}
}
