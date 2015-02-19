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
package com.b2international.snowowl.snomed.datastore.index;

// XXX: result set indexes are 1-based, comments are 0-based!
public interface SnomedIndexInitializerQueries {
	
	String SQL_GET_CONCEPTS_AND_DESCRIPTIONS = "SELECT "
			+ "concept.ID, " // 0
			+ "concept.ACTIVE, " // 1
			+ "description.TERM, " // 2
			+ "descriptionType.ID, " // 3
			+ "description.ACTIVE, " // 4
			+ "description.ID, " // 5
			+ "concept.CDO_ID, " // 6
			+ "description.CDO_ID, " // 7
			+ "concept.RELEASED,  " //8
			+ "description.RELEASED " // 9
			+ "FROM SNOMED_CONCEPT AS concept "
			+ "JOIN SNOMED_DESCRIPTION AS description "
			+ "ON description.CDO_CONTAINER = concept.CDO_ID "
			+ "AND description.CDO_BRANCH = 0 "
			+ "AND description.CDO_REVISED = 0 "
			+ "AND description.CDO_VERSION > 0 "
			+ "JOIN SNOMED_CONCEPT AS descriptionType "
			+ "ON description.TYPE = descriptionType.CDO_ID "
			+ "AND descriptionType.CDO_BRANCH = 0 "
			+ "AND descriptionType.CDO_REVISED = 0 "
			+ "AND descriptionType.CDO_VERSION > 0 "
			+ "WHERE concept.CDO_BRANCH = 0 "
			+ "AND concept.CDO_REVISED = 0 "
			+ "AND concept.CDO_VERSION > 0 "
			+ "ORDER BY concept.ID, descriptionType.ID, description.EFFECTIVETIME DESC, description.ACTIVE DESC";
	
	String SQL_GET_RELATIONSHIPS = "SELECT "
			+ "relationship.ID, " // 0
			+ "source.ID, " // 1
			+ "destination.ID, " // 2 
			+ "characteristictype.ID, " // 3
			+ "relationship.CDO_ID, " // 4
			+ "type.ID, " // 5
			+ "relationship.ACTIVE, " // 6
			+ "relationship.GROUP0, " // 7
			+ "relationship.RELEASED " //8
			+ "FROM SNOMED_RELATIONSHIP as relationship "
			+ "JOIN SNOMED_CONCEPT AS source "
			+ "ON source.CDO_ID = relationship.CDO_CONTAINER "
			+ "AND source.CDO_BRANCH = 0 "
			+ "AND source.CDO_REVISED = 0 "
			+ "AND source.CDO_VERSION > 0 "
			+ "JOIN SNOMED_CONCEPT AS type "
			+ "ON type.CDO_ID = relationship.TYPE "
			+ "AND type.CDO_BRANCH = 0 "
			+ "AND type.CDO_REVISED = 0 "
			+ "AND type.CDO_VERSION > 0 "
			+ "JOIN SNOMED_CONCEPT as destination "
			+ "ON destination.CDO_ID = relationship.DESTINATION "
			+ "AND destination.CDO_BRANCH = 0 "
			+ "AND destination.CDO_REVISED = 0 "
			+ "AND destination.CDO_VERSION > 0 "
			+ "JOIN SNOMED_CONCEPT as characteristictype "
			+ "ON characteristictype.CDO_ID = relationship.CHARACTERISTICTYPE "
			+ "AND characteristictype.CDO_BRANCH = 0 "
			+ "AND characteristictype.CDO_REVISED = 0 "
			+ "AND characteristictype.CDO_VERSION > 0 "
			+ "WHERE relationship.CDO_BRANCH = 0 "
			+ "AND relationship.CDO_REVISED = 0 "
			+ "AND relationship.CDO_VERSION > 0 ";
	
	String SQL_GET_LANGUAGE_REFSET_MEMBERS = "SELECT "
			+ "l.UUID, " //1
			+ "l.REFERENCEDCOMPONENTID, " //2
			+ "l.ACTIVE, " //3
			+ "l.MODULEID, " //4
			+ "l.EFFECTIVETIME, " //5
			+ "l.ACCEPTABILITYID, " //6
			+ "l.CDO_ID, " //7
			+ "l.REFSET, " //8
			+ "l.RELEASED " //9
			+ "FROM SNOMEDREFSET_SNOMEDLANGUAGEREFSETMEMBER AS l "
			+ "WHERE "
			+ "l.CDO_BRANCH = 0 "
			+ "AND l.CDO_REVISED = 0 "
			+ "AND l.CDO_VERSION > 0";
	
	String SQL_GET_ATTRIBUTE_VALUE_REFSET_MEMBERS_CONCEPT = "SELECT "
			+ "m.UUID, " //1
			+ "m.REFERENCEDCOMPONENTID, " //2
			+ "m.ACTIVE, " //3
			+ "m.MODULEID, " //4
			+ "m.EFFECTIVETIME, " //5
			+ "m.VALUEID," //6
			+ "m.CDO_ID, " //7
			+ "m.REFSET, " //8
			+ "m.RELEASED " //9
			+ "FROM SNOMEDREFSET_SNOMEDATTRIBUTEVALUEREFSETMEMBER AS m "
			+ "WHERE "
			+ "m.CDO_BRANCH = 0 "
			+ "AND m.CDO_REVISED = 0 "
			+ "AND m.CDO_VERSION > 0";
	
	String SQL_GET_ASSOCIATION_REFSET_MEMBERS_CONCEPT = "SELECT "
			+ "m.UUID, " //1
			+ "m.REFERENCEDCOMPONENTID, " //2
			+ "m.ACTIVE, " //3
			+ "m.MODULEID, " //4
			+ "m.EFFECTIVETIME, " //5
			+ "m.TARGETCOMPONENTID," //6
			+ "m.CDO_ID, " //7
			+ "m.REFSET, " //8
			+ "m.RELEASED " //9
			+ "FROM SNOMEDREFSET_SNOMEDASSOCIATIONREFSETMEMBER AS m "
			+ "WHERE "
			+ "m.CDO_BRANCH = 0 "
			+ "AND m.CDO_REVISED = 0 "
			+ "AND m.CDO_VERSION > 0";
	
	String SQL_GET_MAP_REFSET_MEMBERS = "SELECT "
			+ "m.UUID, " //1
			+ "m.REFERENCEDCOMPONENTID, " //2
			+ "m.ACTIVE, " //3
			+ "m.MODULEID, " //4
			+ "m.EFFECTIVETIME, " //5
			+ "m.MAPTARGETCOMPONENTID, " //6
			+ "m.CDO_ID, " //7
			+ "m.REFSET, " //8
			+ "m.RELEASED " //9
			+ "FROM SNOMEDREFSET_SNOMEDSIMPLEMAPREFSETMEMBER AS m "
			+ "WHERE "
			+ "m.CDO_BRANCH = 0 "
			+ "AND m.CDO_REVISED = 0 "
			+ "AND m.CDO_VERSION > 0";

	String SQL_GET_COMPLEX_MAP_REFSET_MEMBERS = "SELECT "
			+ "m.UUID, " //1
			+ "m.REFERENCEDCOMPONENTID, " //2
			+ "m.ACTIVE, " //3
			+ "m.MODULEID, " //4
			+ "m.EFFECTIVETIME, " //5
			+ "m.MAPTARGETCOMPONENTID, " //6
			+ "m.CDO_ID, " //7
			+ "m.REFSET, " //8
			+ "m.MAPGROUP, " //9
			+ "m.MAPPRIORITY, " //10
			+ "m.MAPRULE, " //11
			+ "m.MAPADVICE, " //12
			+ "m.CORRELATIONID,  " //13
			+ "m.RELEASED " //14
			+ "FROM SNOMEDREFSET_SNOMEDCOMPLEXMAPREFSETMEMBER AS m "
			+ "WHERE "
			+ "m.CDO_BRANCH = 0 "
			+ "AND m.CDO_REVISED = 0 "
			+ "AND m.CDO_VERSION > 0";

	String SQL_GET_SIMPLE_REFSET_MEMBERS = "SELECT "
			+ "m.UUID, " //1
			+ "m.REFERENCEDCOMPONENTID, " //2
			+ "m.ACTIVE, " //3
			+ "m.MODULEID, " //4
			+ "m.EFFECTIVETIME, " //5
			+ "m.CDO_ID, " //6
			+ "m.REFSET, " //7
			+ "m.RELEASED " //8
			+ "FROM SNOMEDREFSET_SNOMEDREFSETMEMBER AS m "
			+ "WHERE "
			+ "m.CDO_BRANCH = 0 "
			+ "AND m.CDO_REVISED = 0 "
			+ "AND m.CDO_VERSION > 0";
	
	String SQL_GET_DESCRIPTION_TYPE_REFSET_MEMBERS = "SELECT " 
			+ "m.UUID, " //1
			+ "m.REFERENCEDCOMPONENTID, " //2
			+ "m.ACTIVE, " //3
			+ "m.MODULEID, " //4
			+ "m.EFFECTIVETIME, " //5
			+ "m.CDO_ID, " //6
			+ "m.REFSET, " //7
			+ "m.DESCRIPTIONFORMAT, " //8
			+ "m.DESCRIPTIONLENGTH, " //9
			+ "m.RELEASED " //10
			+ "FROM SNOMEDREFSET_SNOMEDDESCRIPTIONTYPEREFSETMEMBER AS m "
			+ "WHERE "
			+ "m.CDO_BRANCH = 0 "
			+ "AND m.CDO_REVISED = 0 "
			+ "AND m.CDO_VERSION > 0";
	
	String SQL_GET_CONCRETE_DATA_TYPE_REFSET_MEMBERS = "SELECT " 
			+ "m.UUID, " //1
			+ "m.REFERENCEDCOMPONENTID, " //2
			+ "m.ACTIVE, " //3
			+ "m.MODULEID, " //4
			+ "m.EFFECTIVETIME, " //5
			+ "m.CDO_ID, " //6
			+ "m.REFSET, " //7
			+ "m.LABEL0, " //8 can be null.
			+ "m.OPERATORCOMPONENTID, " //9
			+ "m.SERIALIZEDVALUE, " //10
			+ "m.UOMCOMPONENTID, " //11 can be null.
			+ "m.RELEASED " //12
			+ "FROM SNOMEDREFSET_SNOMEDCONCRETEDATATYPEREFSETMEMBER AS m "
			+ "WHERE "
			+ "m.CDO_BRANCH = 0 "
			+ "AND m.CDO_REVISED = 0 "
			+ "AND m.CDO_VERSION > 0";
	
	String SQL_GET_QUERY_REFSET_MEMBERS = "SELECT " 
			+ "m.UUID, " //1
			+ "m.REFERENCEDCOMPONENTID, " //2
			+ "m.ACTIVE, " //3
			+ "m.MODULEID, " //4
			+ "m.EFFECTIVETIME, " //5
			+ "m.QUERY, " //6
			+ "m.CDO_ID, " //7
			+ "m.REFSET, " //8
			+ "m.RELEASED " //9
			+ "FROM SNOMEDREFSET_SNOMEDQUERYREFSETMEMBER AS m "
			+ "WHERE "
			+ "m.CDO_BRANCH = 0 "
			+ "AND m.CDO_REVISED = 0 "
			+ "AND m.CDO_VERSION > 0";

	String SQL_GET_REFSETS = "SELECT "
			+ "r.IDENTIFIERID, " //1
			+ "r.TYPE, " //2
			+ "r.REFERENCEDCOMPONENTTYPE, " //3
			+ "r.CDO_ID " //4
			+ "FROM {0} AS r "
			+ "WHERE "
			+ "r.CDO_BRANCH = 0 "
			+ "AND r.CDO_REVISED = 0 "
			+ "AND r.CDO_VERSION > 0";
	
	/**
	 * <p>
	 * <ul>
	 * <li>input #1: concept ID</li>
	 * <li>input #2: language reference set CDO ID</li>
	 * <li>input #3: synonym concept CDO ID</li>
	 * </ul>
	 * </p> 
	 */
	String SQL_GET_PREFERRED_TERM_FOR_CONCEPT_ID = "SELECT " 
			+ "description.term "
			+ "FROM SNOMEDREFSET_SNOMEDLANGUAGEREFSETMEMBER AS languagemember "
			+ "JOIN SNOMED_DESCRIPTION description "
			+ "ON languagemember.REFERENCEDCOMPONENTID = description.ID "
			+ "AND description.CDO_BRANCH = 0 "
			+ "AND description.CDO_REVISED = 0 " 
			+ "AND description.CDO_VERSION > 0 "
			+ "JOIN SNOMED_CONCEPT concept "
			+ "ON description.CDO_CONTAINER = concept.CDO_ID "
			+ "WHERE "
			+ "concept.ID = ? "
			+ "AND languagemember.REFSET = ? "
			+ "AND languagemember.CDO_BRANCH = 0 "
			+ "AND languagemember.CDO_VERSION > 0 "
			+ "AND languagemember.CDO_REVISED = 0 "
			+ "AND languagemember.ACCEPTABILITYID = '900000000000548007' "
			+ "AND languagemember.ACTIVE = TRUE AND description.type = ?";
	
	
	/**
	 * <p>
	 * <ul>
	 * <li>input #1: relationship ID</li>
	 * <li>input #2: language reference set CDO ID</li>
	 * <li>input #3: synonym concept CDO ID</li>
	 * </ul>
	 * </p> 
	 */
	String SQL_GET_PREFERRED_TERM_FOR_RELATIONSHIP_ID = "SELECT " 
			+ "description.term "
			+ "FROM SNOMEDREFSET_SNOMEDLANGUAGEREFSETMEMBER AS languagemember "
			+ "JOIN SNOMED_DESCRIPTION description "
			+ "ON languagemember.REFERENCEDCOMPONENTID = description.ID "
			+ "AND description.CDO_BRANCH = 0 "
			+ "AND description.CDO_REVISED = 0 " 
			+ "AND description.CDO_VERSION > 0 "
			+ "JOIN SNOMED_CONCEPT concept "
			+ "ON description.CDO_CONTAINER = concept.CDO_ID "
			+ "JOIN SNOMED_RELATIONSHIP relationship "
			+ "ON concept.CDO_ID = relationship.TYPE "
			+ "WHERE "
			+ "relationship.ID = ? "
			+ "AND languagemember.REFSET = ? "
			+ "AND languagemember.CDO_BRANCH = 0 "
			+ "AND languagemember.CDO_VERSION > 0 "
			+ "AND languagemember.CDO_REVISED = 0 "
			+ "AND languagemember.ACCEPTABILITYID = '900000000000548007' "
			+ "AND languagemember.ACTIVE = TRUE AND description.type = ?";
	
	String SQL_GET_LANGUAGE_REFSET_CDO_ID = "SELECT "
			+ "r.CDO_ID " // 0
			+ "FROM SNOMEDREFSET_SNOMEDREFSET AS r "
			+ "WHERE r.IDENTIFIERID = ? "
			+ "AND r.CDO_BRANCH = 0 " 
			+ "AND r.CDO_REVISED = 0 "
			+ "AND r.CDO_VERSION > 0 " 
			+ "LIMIT 1";
	
	String SQL_GET_CONCEPT_CDO_ID = "SELECT " 
			+ "c.CDO_ID " // 0
			+ "FROM SNOMED_CONCEPT AS c " 
			+ "WHERE c.ID = ? "
			+ "AND c.CDO_BRANCH = 0 " 
			+ "AND c.CDO_REVISED = 0 "
			+ "AND c.CDO_VERSION > 0 " 
			+ "LIMIT 1";
	
	String GET_TIME_STAMP_SQL = "SELECT MAX(COMMIT_TIME) FROM CDO_COMMIT_INFOS WHERE BRANCH_ID = ?";
}