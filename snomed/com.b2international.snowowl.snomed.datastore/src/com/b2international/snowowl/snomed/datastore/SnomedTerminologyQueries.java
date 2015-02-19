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
package com.b2international.snowowl.snomed.datastore;

/**
 * Contains SQL queries used in Snow Owl's SNOMED CT data store plug-in. 
 *
 */
public abstract class SnomedTerminologyQueries {

	public static final String SQL_GET_CONCEPT_BY_ID = "SELECT * "
			+ "FROM SNOMED_CONCEPT concept "
			+ "WHERE concept.ID = :conceptId "
			+ "AND {visibleFromBranch(concept)} ";
	
	public static final String SQL_GET_DESCRIPTION_BY_ID = "SELECT * "
			+ "FROM SNOMED_DESCRIPTION description "
			+ "WHERE description.ID = :descriptionId "
			+ "AND {visibleFromBranch(description)} ";

	public static final String SQL_GET_RELATIONSHIP_BY_ID = "SELECT * "
			+ "FROM SNOMED_RELATIONSHIP relationship "
			+ "WHERE relationship.ID = :relationshipId "
			+ "AND {visibleFromBranch(relationship)} ";

	// %s = reference set member table name
	public static final String SQL_GET_REFSET_MEMBER_BY_UUID = "SELECT * "
			+ "FROM %s member "
			+ "WHERE member.UUID = :uuid "  
			+ "AND {visibleFromBranch(member)} ";
	
	// %s = reference set table name
	public static final String SQL_GET_REFSET_BY_IDENTIFIER_CONCEPT_ID = "SELECT * "
			+ "FROM %s AS refset "
			+ "WHERE refset.IDENTIFIERID = :identifierConceptId "
			+ "AND {visibleFromBranch(refset)} ";
	
	private SnomedTerminologyQueries() {
		// Prevent instantiation
	}
}