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
package com.b2international.snowowl.datastore.server.snomed.history;

import com.b2international.snowowl.snomed.SnomedConstants.Concepts;

/**
 * Contains SQL queries used for populating the history page of SNOMED CT components. 
 *
 */
public abstract class SnomedHistoryQueries {

	//XXX
	public static final String SQL_GET_CONCEPT_CHANGES = "SELECT "
			+ "concept.CDO_CREATED, " // 0
			+ "concept.INBOUNDRELATIONSHIPS " // 1
			+ "FROM SNOMED_CONCEPT concept "
			+ "WHERE concept.CDO_ID = :cdoId " 
			+ "AND {visibleFromBranch(concept,true)} "
			+ "ORDER BY concept.CDO_CREATED ";
	
	/**
	 * <ol>
	 * <li>CDO ID of the concept.</li>
	 * <ol>
	 */
	public static final String CONCEPT_CHANGES_FROM_MAIN = "SELECT "
			+ "concept.CDO_CREATED, "
			+ "concept.INBOUNDRELATIONSHIPS "
			+ "FROM SNOMED_CONCEPT concept "
			+ "WHERE concept.CDO_ID = ? "
			+ "AND concept.CDO_BRANCH = 0 "
			+ "ORDER BY concept.CDO_CREATED";

	/**
	 * <ol>
	 * <li>CDO ID of the concept.</li>
	 * <li>Current CDO branch ID.</li>
	 * <li>Current CDO branch ID.</li>
	 * <li>Base timestamp for the current CDO branch.</li>
	 * <ol>
	 */
	public static final String CONCEPT_CHANGES_FROM_BRANCH = "SELECT "
			+ "concept.CDO_CREATED, "
			+ "concept.INBOUNDRELATIONSHIPS "
			+ "FROM SNOMED_CONCEPT concept "
			+ "WHERE concept.CDO_ID = ? "
			+ "AND (concept.CDO_BRANCH = ? OR concept.CDO_BRANCH = 0) "
			+ "AND (concept.CDO_BRANCH = ? OR (concept.CDO_BRANCH = 0 AND concept.CDO_CREATED <= ?)) "
			+ "ORDER BY concept.CDO_CREATED";

	/**
	 * <ol>
	 * <li>CDO ID of the concept.</li>
	 * <li>Current CDO branch ID.</li>
	 * <li>Current CDO branch ID.</li>
	 * <li>Base timestamp for the current CDO branch.</li>
	 * <ol>
	 */
	public static final String DESCRIPTION_CHANGES_FROM_BRANCH = "SELECT "
			+ "description.CDO_ID, "
			+ "description.CDO_CREATED, "
			+ "description.CDO_REVISED, "
			+ "description.CDO_BRANCH "
			+ "FROM SNOMED_CONCEPT_DESCRIPTIONS_LIST descriptionsList "
			+ "JOIN SNOMED_DESCRIPTION description "
			+ "ON descriptionsList.CDO_VALUE = description.CDO_ID "
			+ "WHERE descriptionsList.CDO_SOURCE = ? "
			+ "AND (description.CDO_BRANCH = ? OR description.CDO_BRANCH = 0) "
			+ "AND (description.CDO_BRANCH = ? OR (description.CDO_BRANCH = 0 AND description.CDO_CREATED <= ?))";
	
	/**
	 * <ol>
	 * <li>CDO ID of the concept.</li>
	 * <ol>
	 */
	public static final String DESCRIPTION_CHANGES_FROM_MAIN = "SELECT "
			+ "description.CDO_ID, "
			+ "description.CDO_CREATED, "
			+ "description.CDO_REVISED, "
			+ "description.CDO_BRANCH "
			+ "FROM SNOMED_CONCEPT_DESCRIPTIONS_LIST descriptionsList "
			+ "JOIN SNOMED_DESCRIPTION description "
			+ "ON descriptionsList.CDO_VALUE = description.CDO_ID "
			+ "WHERE descriptionsList.CDO_SOURCE = ? "
			+ "AND description.CDO_BRANCH = 0";
	
	//XXX
	public static final String SQL_GET_DESCRIPTION_CHANGES = "SELECT "
			+ "description.CDO_ID, " // 0
			+ "description.CDO_CREATED, " // 1
			+ "description.CDO_REVISED, " // 2
			+ "description.CDO_BRANCH " // 3
			+ "FROM SNOMED_CONCEPT_DESCRIPTIONS_LIST descriptionsList "
			+ "JOIN SNOMED_DESCRIPTION description "
			+ "ON descriptionsList.CDO_VALUE = description.CDO_ID "
			+ "WHERE descriptionsList.CDO_SOURCE = :cdoId " 
			+ "AND {visibleFromBranch(description,true)} ";

	//XXX
	public static final String SQL_GET_CONCEPT_PT_CHANGES = "SELECT DISTINCT "
			+ "member.CDO_ID, " //1
			+ "member.CDO_CREATED, " //2
			+ "member.CDO_REVISED, " //3
			+ "member.CDO_BRANCH " //4
			+ "FROM SNOMEDREFSET_SNOMEDLANGUAGEREFSETMEMBER member "
			+ "JOIN SNOMED_DESCRIPTION description "
			+ "ON member.REFERENCEDCOMPONENTID = description.ID "
			+ "WHERE description.CDO_CONTAINER = :cdoId "
			+ "AND member.ACCEPTABILITYID = " + Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED + " "
			+ "AND description.TYPE <> :fsnCdoId "
			+ "AND member.ACTIVE "
			+ "AND description.ACTIVE "
			+ "AND {visibleFromBranch(member,true)} "
			+ "AND {visibleFromBranch(description,true)}";
	
	/**
	 * <ol>
	 * <li>CDO ID of the concept.</li>
	 * <li>FSN description type concept CDO ID.</li>
	 * <li>Current CDO branch ID.</li>
	 * <li>Current CDO branch ID.</li>
	 * <li>Base timestamp for the current CDO branch.</li>
	 * <li>Current CDO branch ID.</li>
	 * <li>Current CDO branch ID.</li>
	 * <li>Base timestamp for the current CDO branch.</li>
	 * <ol>
	 */
	public static final String CONCEPT_PT_CHANGES_FROM_BRANCH = "SELECT DISTINCT "
			+ "member.CDO_ID, "
			+ "member.CDO_CREATED, "
			+ "member.CDO_REVISED, "
			+ "member.CDO_BRANCH FROM SNOMEDREFSET_SNOMEDLANGUAGEREFSETMEMBER member "
			+ "JOIN SNOMED_DESCRIPTION description "
			+ "ON member.REFERENCEDCOMPONENTID = description.ID "
			+ "WHERE description.CDO_CONTAINER = ? "
			+ "AND member.ACCEPTABILITYID = " + Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED + " "
			+ "AND description.TYPE <> ? "
			+ "AND member.ACTIVE "
			+ "AND description.ACTIVE "
			+ "AND (member.CDO_BRANCH = ? OR member.CDO_BRANCH = 0) "
			+ "AND (member.CDO_BRANCH = ? OR (member.CDO_BRANCH = 0 AND member.CDO_CREATED <= ?)) "
			+ "AND (description.CDO_BRANCH = ? OR description.CDO_BRANCH = 0) "
			+ "AND (description.CDO_BRANCH = ? OR (description.CDO_BRANCH = 0 AND description.CDO_CREATED <= ?))";
	
	/**
	 * <ol>
	 * <li>CDO ID of the concept.</li>
	 * <li>FSN description type concept CDO ID.</li>
	 * <ol>
	 */
	public static final String CONCEPT_PT_CHANGES_FROM_MAIN = "SELECT DISTINCT "
			+ "member.CDO_ID, "
			+ "member.CDO_CREATED, "
			+ "member.CDO_REVISED, "
			+ "member.CDO_BRANCH FROM SNOMEDREFSET_SNOMEDLANGUAGEREFSETMEMBER member "
			+ "JOIN SNOMED_DESCRIPTION description "
			+ "ON member.REFERENCEDCOMPONENTID = description.ID "
			+ "WHERE description.CDO_CONTAINER = ? "
			+ "AND member.ACCEPTABILITYID = " + Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED + " "
			+ "AND description.TYPE <> ? "
			+ "AND member.ACTIVE "
			+ "AND description.ACTIVE "
			+ "AND member.CDO_BRANCH = 0 "
			+ "AND description.CDO_BRANCH = 0";

	//XXX
	public static final String SQL_GET_RELATIONSHIP_CHANGES = "SELECT "
			+ "relationship.CDO_ID, " // 0
			+ "relationship.CDO_CREATED, " // 1
			+ "relationship.CDO_REVISED, " //2
			+ "relationship.CDO_BRANCH " // 3
			+ "FROM SNOMED_CONCEPT_OUTBOUNDRELATIONSHIPS_LIST relationshipsList "
			+ "JOIN SNOMED_RELATIONSHIP relationship "
			+ "ON relationshipsList.CDO_VALUE = relationship.CDO_ID "
			+ "WHERE relationshipsList.CDO_SOURCE = :cdoId " 
			+ "AND {visibleFromBranch(relationship,true)} ";
	
	/**
	 * <ol>
	 * <li>CDO ID of the concept.</li>
	 * <li>Current CDO branch ID.</li>
	 * <li>Current CDO branch ID.</li>
	 * <li>Base timestamp for the current CDO branch.</li>
	 * <ol>
	 */
	public static final String RELATIONSHIP_CHANGES_FROM_BRANCH = "SELECT "
			+ "relationship.CDO_ID, "
			+ "relationship.CDO_CREATED, "
			+ "relationship.CDO_REVISED, "
			+ "relationship.CDO_BRANCH FROM SNOMED_CONCEPT_OUTBOUNDRELATIONSHIPS_LIST relationshipsList "
			+ "JOIN SNOMED_RELATIONSHIP relationship "
			+ "ON relationshipsList.CDO_VALUE = relationship.CDO_ID "
			+ "WHERE relationshipsList.CDO_SOURCE = ? "
			+ "AND (relationship.CDO_BRANCH = ? OR relationship.CDO_BRANCH = 0) "
			+ "AND (relationship.CDO_BRANCH = ? OR (relationship.CDO_BRANCH = 0 AND relationship.CDO_CREATED <= ?))";
	
	/**
	 * <ol>
	 * <li>CDO ID of the concept.</li>
	 * <ol>
	 */
	public static final String RELATIONSHIP_CHANGES_FROM_MAIN = "SELECT "
			+ "relationship.CDO_ID, "
			+ "relationship.CDO_CREATED, "
			+ "relationship.CDO_REVISED, "
			+ "relationship.CDO_BRANCH FROM SNOMED_CONCEPT_OUTBOUNDRELATIONSHIPS_LIST relationshipsList "
			+ "JOIN SNOMED_RELATIONSHIP relationship "
			+ "ON relationshipsList.CDO_VALUE = relationship.CDO_ID "
			+ "WHERE relationshipsList.CDO_SOURCE = ? "
			+ "AND relationship.CDO_BRANCH = 0";
	
	/**
	 * <ol>
	 * <li>SNOMED&nbsp;CT ID of the concept.</li>
	 * <li>Current CDO branch ID.</li>
	 * <li>Current CDO branch ID.</li>
	 * <li>Base timestamp for the current CDO branch.</li>
	 * <ol>
	 */
	public static final String RELATED_REFERENCE_SET_MEMBER_CHANGES_FROM_BRANCH_TEMPLATE = "SELECT "
			+ "member.CDO_ID, "
			+ "member.CDO_CREATED, "
			+ "member.CDO_REVISED, "
			+ "member.CDO_BRANCH FROM %s member "
			+ "WHERE member.REFERENCEDCOMPONENTID = ? "
			+ "AND (member.CDO_BRANCH = ? OR member.CDO_BRANCH = 0) "
			+ "AND (member.CDO_BRANCH = ? OR (member.CDO_BRANCH = 0 AND member.CDO_CREATED <= ?))";
	
	/**
	 * <ol>
	 * <li>SNOMED&nbsp;CT ID of the concept.</li>
	 * <ol>
	 */
	public static final String RELATED_REFERENCE_SET_MEMBER_CHANGES_FROM_MAIN_TEMPLATE = "SELECT "
			+ "member.CDO_ID, "
			+ "member.CDO_CREATED, "
			+ "member.CDO_REVISED, "
			+ "member.CDO_BRANCH FROM %s member "
			+ "WHERE member.REFERENCEDCOMPONENTID = ? "
			+ "AND member.CDO_BRANCH = 0";
	
	// %s = reference set member table name
	public static final String SQL_GET_RELATED_REFERENCE_SET_MEMBER_CHANGES_TEMPLATE = "SELECT "
			+ "refsetmember.CDO_ID, " // 0
			+ "refsetmember.CDO_CREATED, " // 1
			+ "refsetmember.CDO_REVISED, " // 2
			+ "refsetmember.CDO_BRANCH " // 3
			+ "FROM %s refsetmember "
			+ "WHERE refsetmember.REFERENCEDCOMPONENTID = :conceptId "
			+ "AND {visibleFromBranch(refsetmember,true)} ";
	
	/**
	 * <ol>
	 * <li>CDO ID of the reference set.</li>
	 * <li>Current CDO branch ID.</li>
	 * <li>Current CDO branch ID.</li>
	 * <li>Base timestamp for the current CDO branch.</li>
	 * <ol>
	 */
	public static final String REFSET_CHANGES_TEMPLATE_FROM_BRANCH_TEMPLATE = "SELECT "
			+ "refset.CDO_CREATED "
			+ "FROM %s refset "
			+ "WHERE refset.CDO_ID = ? "
			+ "AND (refset.CDO_BRANCH = ? OR refset.CDO_BRANCH = 0) "
			+ "AND (refset.CDO_BRANCH = ? OR (refset.CDO_BRANCH = 0 AND refset.CDO_CREATED <= ?)) "
			+ "ORDER BY refset.CDO_CREATED";
	
	/**
	 * <ol>
	 * <li>CDO ID of the reference set.</li>
	 * <ol>
	 */
	public static final String REFSET_CHANGES_TEMPLATE_FROM_MAIN_TEMPLATE = "SELECT "
			+ "refset.CDO_CREATED "
			+ "FROM %s refset "
			+ "WHERE refset.CDO_ID = ? "
			+ "AND refset.CDO_BRANCH = 0 "
			+ "ORDER BY refset.CDO_CREATED";
	
	// %s = reference set table name
	public static final String SQL_GET_REFERENCE_SET_CHANGES_TEMPLATE = "SELECT "
			+ "refset.CDO_CREATED "
			+ "FROM %s refset "
			+ "WHERE refset.CDO_ID = :refSetCdoId " 
			+ "AND {visibleFromBranch(refset,true)} "
			+ "ORDER BY refset.CDO_CREATED ";
	
	/**
	 * <ol>
	 * <li>CDO ID of the reference set.</li>
	 * <ol>
	 */
	public static final String REFSET_MEMBER_CHANGES_TEMPLATE_FROM_MAIN_TEMPLATE = "SELECT "
			+ "member.CDO_ID, "
			+ "member.CDO_CREATED "
			+ "FROM %s member "
			+ "WHERE member.REFSET = ? "
			+ "AND member.CDO_BRANCH = 0 "
			+ "ORDER BY member.CDO_CREATED";
	
	/**
	 * <ol>
	 * <li>CDO ID of the reference set.</li>
	 * <li>Current CDO branch ID.</li>
	 * <li>Current CDO branch ID.</li>
	 * <li>Base timestamp for the current CDO branch.</li>
	 * <ol>
	 */
	public static final String REFSET_MEMBER_CHANGES_TEMPLATE_FROM_BRANCH_TEMPLATE = "SELECT "
			+ "member.CDO_ID, "
			+ "member.CDO_CREATED "
			+ "FROM %s member "
			+ "WHERE member.REFSET = ? "
			+ "AND (member.CDO_BRANCH = ? OR member.CDO_BRANCH = 0) "
			+ "AND (member.CDO_BRANCH = ? OR (member.CDO_BRANCH = 0 AND member.CDO_CREATED <= ?)) "
			+ "ORDER BY member.CDO_CREATED";
	
	// %s = reference set  member table name
	public static final String SQL_GET_REFERENCE_SET_MEMBER_CHANGES_TEMPLATE = "SELECT " +
			"member.CDO_ID, " +
			"member.CDO_CREATED " +
			"FROM %s member WHERE member.REFSET = :refSetCdoId " +
			"AND {visibleFromBranch(member,true)}";
	
	private SnomedHistoryQueries() {
		// Prevent instantiation
	}
}