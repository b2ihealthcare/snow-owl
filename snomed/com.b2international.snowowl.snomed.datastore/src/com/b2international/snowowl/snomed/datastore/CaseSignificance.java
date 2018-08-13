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
package com.b2international.snowowl.snomed.datastore;

import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.google.common.base.Preconditions;

/**
 * Enumeration representing a value that can be applied to the SNOMED&nbsp;CT description.
 * <p>Available values:
 * <ul>
 * <li>{@link #ENTIRE_TERM_CASE_SENSITIVE <em>Case sensitive</em>}</li>
 * <li>{@link #ENTIRE_TERM_CASE_INSENSITIVE <em>Case insensitive</em>}</li>
 * <li>{@link #ONLY_INITIAL_CHARACTER_CASE_INSENSITIVE <em>Initial character case insensitive</em>}</li>
 * </ul>
 * </p>
 */
public enum CaseSignificance {

	/**
	 * Case sensitive. Concept ID: {@code 900000000000017005}.
	 * <p>
	 * The text of the Description.term must be presented in the case in which it is specified.
	 * @see CaseSignificance 
	 */
	ENTIRE_TERM_CASE_SENSITIVE("Case sensitive", Concepts.ENTIRE_TERM_CASE_SENSITIVE),
	
	/**
	 * Case insensitive. Concept ID: {@code 900000000000448009}.
	 * <p>
	 * The entire Description.term is case insensitive and can be can be changed from upper to lower case
     * (or vice-versa) if appropriate to the context in which it is used.
	 * @see CaseSignificance
	 */
	ENTIRE_TERM_CASE_INSENSITIVE("Case insensitive", Concepts.ENTIRE_TERM_CASE_INSENSITIVE),
	
	/**
	 * Initial character case insensitive. Concept ID: {@code 900000000000020002}.
	 * <p>
	 * The initial character of the Description.term is case insensitive and can be changed 
	 * from upper to lower case (or vice-versa) if appropriate to the context in which it is used.
	 * @see CaseSignificance
	 */
	ONLY_INITIAL_CHARACTER_CASE_INSENSITIVE("Initial character case insensitive", Concepts.ONLY_INITIAL_CHARACTER_CASE_INSENSITIVE);
	
	private final String name;
	private final String id;
	
	private CaseSignificance(final String name, final String id) {
		this.name = name;
		this.id = id;
	}

	/**
	 * Returns with the associated metadata concept ID.
	 * @return the unique identifier of the case significance metadata concept.
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Returns with the human readable name of the case significance enumeration.
	 * @return the name of the case significance.
	 */
	public String getName() {
		return name;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return name;
	}
	
	/**
	 * Returns with the proper case significance instance identified by the specified SNOMED&nbsp;CT ID. 
	 * @param conceptId the case significance concept ID.
	 * @return the case significance enumeration.
	 */
	public static CaseSignificance getById(final String conceptId) {
		Preconditions.checkNotNull(conceptId, "Case significance metadata concept ID argument cannot be null.");
		for (CaseSignificance caseSignificance : values()) {
			if (conceptId.equals(caseSignificance.getId())) {
				return caseSignificance;
			}
		}
		throw new IllegalArgumentException("Case significance metadata does not exists for ID: " + conceptId);
	}

	/**
	 * Returns with the proper case significance instance identified by the specified SNOMED&nbsp;CT ID. 
	 * @param conceptId the case significance concept ID.
	 * @return the case significance enumeration.
	 */
	public static CaseSignificance getById(final long conceptId) {
		return getById(String.valueOf(conceptId));
	}
	
	/**
	 * Returns with the proper case significance instance based on the specified description index entry's case significance concept.
	 * @param description SNOMED&nbsp;CT description index entry.
	 * @return the case significance enumeration.
	 */
	public static CaseSignificance getForDescriptionIndexEntry(final SnomedDescriptionIndexEntry description) {
		return getById(description.getCaseSignificanceId());
	}
}