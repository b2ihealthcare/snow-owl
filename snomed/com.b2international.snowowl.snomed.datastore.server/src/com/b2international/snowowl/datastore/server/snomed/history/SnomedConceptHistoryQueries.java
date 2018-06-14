/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.server.snomed.history;

import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;

/**
 * Contains SQL queries used for populating the history page of SNOMED CT concepts. 
 */
public enum SnomedConceptHistoryQueries {

	/**
	 * Query parameters:
	 * <ol>
	 * <li>CDO ID of the concept</li>
	 * <li>Current CDO branch ID</li>
	 * <li>Ending timestamp for the current CDO branch segment ("infinity" or base of child branch)</li>
	 * <ol>
	 */
	CONCEPT_CHANGES("SELECT "
			+ "concept.CDO_CREATED "
			// -------------------------------
			+ "FROM SNOMED_CONCEPT concept "
			// -------------------------------
			+ "WHERE concept.CDO_ID = ? "
			+ "AND concept.CDO_BRANCH = ? "
			+ "AND concept.CDO_CREATED <= ? "
			+ "ORDER BY concept.CDO_CREATED DESC "),

	/**
	 * Query parameters:
	 * <ol>
	 * <li>CDO ID of the concept</li>
	 * <li>Current CDO branch ID</li>
	 * <li>Ending timestamp for the current CDO branch segment ("infinity" or base of child branch)</li>
	 * <ol>
	 */
	DESCRIPTION_CHANGES("SELECT "
			+ "description.CDO_ID, "
			+ "description.CDO_CREATED, "
			+ "description.CDO_REVISED "
			// -------------------------------
			+ "FROM SNOMED_CONCEPT_DESCRIPTIONS_LIST descriptionsList "
			+ "JOIN SNOMED_DESCRIPTION description "
			+ "ON descriptionsList.CDO_VALUE = description.CDO_ID "
			// -------------------------------
			+ "WHERE descriptionsList.CDO_SOURCE = ? "
			+ "AND description.CDO_BRANCH = ? "
			+ "AND description.CDO_CREATED <= ? "),

	/**
	 * Query parameters:
	 * <ol>
	 * <li>CDO ID of the concept</li>
	 * <li>Current CDO branch ID</li>
	 * <li>Ending timestamp for the current CDO branch segment ("infinity" or base of child branch)</li>
	 * <ol>
	 */
	RELATIONSHIP_CHANGES("SELECT "
			+ "relationship.CDO_ID, "
			+ "relationship.CDO_CREATED, "
			+ "relationship.CDO_REVISED "
			// -------------------------------
			+ "FROM SNOMED_CONCEPT_OUTBOUNDRELATIONSHIPS_LIST relationshipsList "
			+ "JOIN SNOMED_RELATIONSHIP relationship "
			+ "ON relationshipsList.CDO_VALUE = relationship.CDO_ID "
			// -------------------------------
			+ "WHERE relationshipsList.CDO_SOURCE = ? "
			+ "AND relationship.CDO_BRANCH = ? "
			+ "AND relationship.CDO_CREATED <= ? "),

	/**
	 * Query parameters:
	 * <ol>
	 * <li>CDO ID of the concept</li>
	 * <li>CDO ID of FSN description type concept</li>
	 * <li>Current CDO branch ID</li>
	 * <li>Ending timestamp for the current CDO branch segment ("infinity" or base of child branch)</li>
	 * <ol>
	 */
	CONCEPT_PT_CHANGES("SELECT "
			+ "member.CDO_ID, "
			+ "member.CDO_CREATED, "
			+ "member.CDO_REVISED "
			// -------------------------------
			+ "FROM SNOMEDREFSET_SNOMEDLANGUAGEREFSETMEMBER member "
			+ "JOIN SNOMED_DESCRIPTION description "
			+ "ON member.REFERENCEDCOMPONENTID = description.ID "
			// -------------------------------
			+ "WHERE member.ACCEPTABILITYID = " + Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED + " "
			+ "AND member.ACTIVE "
			+ "AND description.CDO_CONTAINER = ? "
			+ "AND member.CDO_BRANCH = ? "
			+ "AND member.CDO_CREATED <= ? "
			+ "AND description.TYPE <> ? "),

	/**
	 * Query parameters:
	 * <ol>
	 * <li>SCTID of the concept</li>
	 * <li>Current CDO branch ID</li>
	 * <li>Ending timestamp for the current CDO branch segment ("infinity" or base of child branch)</li>
	 * <ol>
	 */
	RELATED_REFERENCE_SET_MEMBER_CHANGES("SELECT "
			+ "member.CDO_ID, "
			+ "member.CDO_CREATED, "
			+ "member.CDO_REVISED "
			// -------------------------------
			+ "FROM %s member "
			// -------------------------------
			+ "WHERE member.REFERENCEDCOMPONENTID = ? "
			+ "AND member.CDO_BRANCH = ? "
			+ "AND member.CDO_CREATED <= ? ");
	
	private final String query;

	private SnomedConceptHistoryQueries(final String query) {
		this.query = query;
	}
	
	public String getQuery() {
		return query;
	}
	
	// FIXME: "verbatim" constants and templated ones should probably be in separate enums
	public String getQuery(final String tableName) {
		return String.format(query, tableName);
	}
}
