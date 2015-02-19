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
package com.b2international.snowowl.datastore.server.snomed.index;


// XXX: result set indexes are 1-based, comments are 0-based! 
public interface SnomedIndexInitializerQueries {

	String SQL_GET_CONCEPT_IDS = "SELECT " 
			+ "CDO_ID, " // 0
			+ "ID " // 1
			+ "FROM SNOMED_CONCEPT " 
			+ "WHERE CDO_BRANCH = 0 "
			+ "AND CDO_REVISED = 0 " 
			+ "AND CDO_VERSION > 0";
	
	String SQL_GET_CONCEPTS = "SELECT "
			+ "concept.CDO_ID, " //1
			+ "concept.ID, " //2
			+ "concept.ACTIVE, " //3
			+ "concept.RELEASED, " //4
			+ "concept.DEFINITIONSTATUS, " //5
			+ "concept.EXHAUSTIVE, " //6
			+ "concept.MODULE0, " //7
			+ "concept.EFFECTIVETIME " //8
			+ "FROM SNOMED_CONCEPT AS concept "
			+ "WHERE concept.CDO_BRANCH = 0 "
			+ "AND concept.CDO_REVISED = 0 "
			+ "AND concept.CDO_VERSION > 0 ";
	
	String SQL_GET_DESCRIPTIONS = "SELECT "
			+ "description.CDO_ID, " //1
			+ "description.ID, " //2
			+ "description.TERM, " //3
			+ "description.ACTIVE, " //4
			+ "description.RELEASED, " //5
			+ "description.MODULE0, " //6
			+ "description.TYPE, " //7
			+ "description.CASESIGNIFICANCE," //8
			+ "description.CDO_CONTAINER, " //9
			+ "description.EFFECTIVETIME " //10
			+ "FROM SNOMED_DESCRIPTION AS description "
			+ "WHERE description.CDO_BRANCH = 0 "
			+ "AND description.CDO_REVISED = 0 "
			+ "AND description.CDO_VERSION > 0 ";
	
	String SQL_GET_DESCRIPTION_FRAGMENTS = "SELECT "
			+ "description.ID, " //1
			+ "description.TERM, " //2
			+ "description.TYPE, " //3
			+ "description.CDO_CONTAINER " //4
			+ "FROM SNOMED_DESCRIPTION AS description "
			+ "WHERE description.CDO_BRANCH = 0 "
			+ "AND description.ACTIVE = TRUE "
			+ "AND description.CDO_REVISED = 0 "
			+ "AND description.CDO_VERSION > 0 ";
			
	
	String SQL_GET_RELATIONSHIPS = "SELECT "
			+ "relationship.CDO_ID, " // 1
			+ "relationship.ID, " // 2
			+ "relationship.CDO_CONTAINER, " // 3
			+ "relationship.TYPE, " // 4
			+ "relationship.DESTINATION, " // 5 
			+ "relationship.CHARACTERISTICTYPE, " // 6
			+ "relationship.ACTIVE, " // 7
			+ "relationship.GROUP0, " // 8
			+ "relationship.RELEASED, " //9
			+ "relationship.MODIFIER, " //10
			+ "relationship.UNIONGROUP, " //11
			+ "relationship.DESTINATIONNEGATED, " //12
			+ "relationship.MODULE0, " //13
			+ "relationship.EFFECTIVETIME " //14
			+ "FROM SNOMED_RELATIONSHIP as relationship "
			+ "WHERE relationship.CDO_BRANCH = 0 "
			+ "AND relationship.CDO_REVISED = 0 "
			+ "AND relationship.CDO_VERSION > 0 ";
	
	// structural
	String SQL_GET_LANGUAGE_REFSET_MEMBERS = "SELECT "
			+ "m.UUID, " //1
			+ "m.REFERENCEDCOMPONENTID, " //2
			+ "m.ACTIVE, " //3
			+ "m.MODULEID, " //4
			+ "m.EFFECTIVETIME, " //5
			+ "m.ACCEPTABILITYID, " //6
			+ "m.CDO_ID, " //7
			+ "m.REFSET, " //8
			+ "m.RELEASED " //9
			+ "FROM SNOMEDREFSET_SNOMEDLANGUAGEREFSETMEMBER AS m "
			+ "WHERE "
			+ "m.CDO_BRANCH = 0 "
			+ "AND m.CDO_REVISED = 0 "
			+ "AND m.CDO_VERSION > 0";
	
	// mixed
	String SQL_GET_ATTRIBUTE_VALUE_REFSET_MEMBERS_CONCEPT = "SELECT "
			+ "m.UUID, " //1
			+ "m.REFERENCEDCOMPONENTID, " //2
			+ "m.ACTIVE, " //3
			+ "m.MODULEID, " //4
			+ "m.EFFECTIVETIME, " //5
			+ "m.VALUEID," //6
			+ "m.CDO_ID, " //7
			+ "m.REFSET, " //8
			+ "m.RELEASED, " //9
			+ "r.IDENTIFIERID, " //10
			+ "r.REFERENCEDCOMPONENTTYPE, " //11
			+ "r.TYPE " //12
			+ "FROM SNOMEDREFSET_SNOMEDATTRIBUTEVALUEREFSETMEMBER AS m "
			+ "JOIN %s r ON m.REFSET = r.CDO_ID "
			+ "AND r.CDO_BRANCH = 0 "
			+ "AND r.CDO_REVISED = 0 "
			+ "AND r.CDO_VERSION > 0 "		
			+ "WHERE "
			+ "m.CDO_BRANCH = 0 "
			+ "AND m.CDO_REVISED = 0 "
			+ "AND m.CDO_VERSION > 0";
	
	// structural
	String SQL_GET_ASSOCIATION_REFSET_MEMBERS_CONCEPT = "SELECT "
			+ "m.UUID, " //1
			+ "m.REFERENCEDCOMPONENTID, " //2
			+ "m.ACTIVE, " //3
			+ "m.MODULEID, " //4
			+ "m.EFFECTIVETIME, " //5
			+ "m.TARGETCOMPONENTID," //6
			+ "m.CDO_ID, " //7
			+ "m.REFSET, " //8
			+ "m.RELEASED, " //9
			+ "r.IDENTIFIERID, " //10
			+ "r.REFERENCEDCOMPONENTTYPE, " //11
			+ "r.TYPE " //12
			+ "FROM SNOMEDREFSET_SNOMEDASSOCIATIONREFSETMEMBER AS m "
			+ "JOIN snomedrefset_snomedstructuralrefset r ON m.REFSET = r.CDO_ID "
			+ "AND r.CDO_BRANCH = 0 "
			+ "AND r.CDO_REVISED = 0 "
			+ "AND r.CDO_VERSION > 0 "		
			+ "WHERE "
			+ "m.CDO_BRANCH = 0 "
			+ "AND m.CDO_REVISED = 0 "
			+ "AND m.CDO_VERSION > 0";
	
	// regular
	String SQL_GET_MAP_REFSET_MEMBERS = "SELECT "
			+ "m.UUID, " //1
			+ "m.REFERENCEDCOMPONENTID, " //2
			+ "m.ACTIVE, " //3
			+ "m.MODULEID, " //4
			+ "m.EFFECTIVETIME, " //5
			+ "m.MAPTARGETCOMPONENTID, " //6
			+ "m.CDO_ID, " //7
			+ "m.REFSET, " //8
			+ "m.RELEASED, " //9
			+ "r.IDENTIFIERID, " //10
			+ "r.REFERENCEDCOMPONENTTYPE, " //11
			+ "r.TYPE, " //12
			+ "r.MAPTARGETCOMPONENTTYPE " //13
			+ "FROM SNOMEDREFSET_SNOMEDSIMPLEMAPREFSETMEMBER AS m "
			+ "JOIN SNOMEDREFSET_SNOMEDMAPPINGREFSET r ON m.REFSET = r.CDO_ID "
			+ "AND r.CDO_BRANCH = 0 "
			+ "AND r.CDO_REVISED = 0 "
			+ "AND r.CDO_VERSION > 0 "		
			+ "WHERE "
			+ "m.CDO_BRANCH = 0 "
			+ "AND m.CDO_REVISED = 0 "
			+ "AND m.CDO_VERSION > 0";

	// regular
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
			+ "m.RELEASED, " //14
			+ "r.IDENTIFIERID, " //15
			+ "r.REFERENCEDCOMPONENTTYPE, " //16
			+ "r.TYPE, " //17
			+ "r.MAPTARGETCOMPONENTTYPE " //18
			+ "FROM SNOMEDREFSET_SNOMEDCOMPLEXMAPREFSETMEMBER AS m "
			+ "JOIN SNOMEDREFSET_SNOMEDMAPPINGREFSET r ON m.REFSET = r.CDO_ID "
			+ "AND r.CDO_BRANCH = 0 "
			+ "AND r.CDO_REVISED = 0 "
			+ "AND r.CDO_VERSION > 0 "		
			+ "WHERE "
			+ "m.CDO_BRANCH = 0 "
			+ "AND m.CDO_REVISED = 0 "
			+ "AND m.CDO_VERSION > 0";

	// regular
	String SQL_GET_SIMPLE_REFSET_MEMBERS = "SELECT "
			+ "m.UUID, " //1
			+ "m.REFERENCEDCOMPONENTID, " //2
			+ "m.ACTIVE, " //3
			+ "m.MODULEID, " //4
			+ "m.EFFECTIVETIME, " //5
			+ "m.CDO_ID, " //6
			+ "m.REFSET, " //7
			+ "m.RELEASED, " //8
			+ "r.IDENTIFIERID, " //9
			+ "r.REFERENCEDCOMPONENTTYPE, " //10		
			+ "r.TYPE " //11
			+ "FROM SNOMEDREFSET_SNOMEDREFSETMEMBER AS m "
			+ "JOIN snomedrefset_snomedregularrefset r ON m.REFSET = r.CDO_ID "
			+ "AND r.CDO_BRANCH = 0 "
			+ "AND r.CDO_REVISED = 0 "
			+ "AND r.CDO_VERSION > 0 "			
			+ "WHERE "
			+ "m.CDO_BRANCH = 0 "
			+ "AND m.CDO_REVISED = 0 "
			+ "AND m.CDO_VERSION > 0";
	
	// regular
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
			+ "m.RELEASED, " //10
			+ "r.IDENTIFIERID, " //11
			+ "r.REFERENCEDCOMPONENTTYPE, " //12
			+ "r.TYPE " //13
			+ "FROM SNOMEDREFSET_SNOMEDDESCRIPTIONTYPEREFSETMEMBER AS m "
			+ "JOIN snomedrefset_snomedregularrefset r ON m.REFSET = r.CDO_ID "
			+ "AND r.CDO_BRANCH = 0 "
			+ "AND r.CDO_REVISED = 0 "
			+ "AND r.CDO_VERSION > 0 "			
			+ "WHERE "
			+ "m.CDO_BRANCH = 0 "
			+ "AND m.CDO_REVISED = 0 "
			+ "AND m.CDO_VERSION > 0";
	
	// structural
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
			+ "m.RELEASED, " //12
			+ "m.CHARACTERISTICTYPEID, "	//13
			+ "r.IDENTIFIERID, " //14
			+ "r.REFERENCEDCOMPONENTTYPE, " //15
			+ "r.TYPE " //16
			+ "FROM SNOMEDREFSET_SNOMEDCONCRETEDATATYPEREFSETMEMBER AS m "
			+ "JOIN snomedrefset_snomedconcretedatatyperefset r ON m.REFSET = r.CDO_ID "
			+ "AND r.CDO_BRANCH = 0 "
			+ "AND r.CDO_REVISED = 0 "
			+ "AND r.CDO_VERSION > 0 "					
			+ "WHERE "
			+ "m.CDO_BRANCH = 0 "
			+ "AND m.CDO_REVISED = 0 "
			+ "AND m.CDO_VERSION > 0";
	
	// regular
	String SQL_GET_QUERY_REFSET_MEMBERS = "SELECT " 
			+ "m.UUID, " //1
			+ "m.REFERENCEDCOMPONENTID, " //2
			+ "m.ACTIVE, " //3
			+ "m.MODULEID, " //4
			+ "m.EFFECTIVETIME, " //5
			+ "m.QUERY, " //6
			+ "m.CDO_ID, " //7
			+ "m.REFSET, " //8
			+ "m.RELEASED, " //9
			+ "r.IDENTIFIERID, " //10
			+ "r.REFERENCEDCOMPONENTTYPE, " //11
			+ "r.TYPE " //12
			+ "FROM SNOMEDREFSET_SNOMEDQUERYREFSETMEMBER AS m "
			+ "JOIN snomedrefset_snomedregularrefset r ON m.REFSET = r.CDO_ID "
			+ "AND r.CDO_BRANCH = 0 "
			+ "AND r.CDO_REVISED = 0 "
			+ "AND r.CDO_VERSION > 0 "					
			+ "WHERE "
			+ "m.CDO_BRANCH = 0 "
			+ "AND m.CDO_REVISED = 0 "
			+ "AND m.CDO_VERSION > 0";
	
	// regular
	String SQL_GET_MODULE_DEPENDENCY_REFSET_MEMBERS = "SELECT "
			+ "m.UUID, " //1
			+ "m.REFERENCEDCOMPONENTID, " //2
			+ "m.ACTIVE, " //3
			+ "m.MODULEID, " //4
			+ "m.EFFECTIVETIME, " //5
			+ "m.CDO_ID, " //6
			+ "m.REFSET, " //7
			+ "m.RELEASED, " //8
			+ "m.SOURCEEFFECTIVETIME, " //9
			+ "m.TARGETEFFECTIVETIME, " //10
			+ "r.IDENTIFIERID, " //11
			+ "r.REFERENCEDCOMPONENTTYPE, " //12		
			+ "r.TYPE " //13
			+ "FROM SNOMEDREFSET_SNOMEDMODULEDEPENDENCYREFSETMEMBER AS m "
			+ "JOIN snomedrefset_snomedregularrefset r ON m.REFSET = r.CDO_ID "
			+ "AND r.CDO_BRANCH = 0 "
			+ "AND r.CDO_REVISED = 0 "
			+ "AND r.CDO_VERSION > 0 "			
			+ "WHERE "
			+ "m.CDO_BRANCH = 0 "
			+ "AND m.CDO_REVISED = 0 "
			+ "AND m.CDO_VERSION > 0";

	String SQL_GET_REFSETS = "SELECT "
			+ "r.IDENTIFIERID, " //1
			+ "r.TYPE, " //2
			+ "r.REFERENCEDCOMPONENTTYPE, " //3
			+ "r.CDO_ID, " //4
			+ "c.MODULE0 " //5 identifier concept's module CDO ID
			+ "FROM {0} AS r "
			+ "JOIN SNOMED_CONCEPT AS c ON r.IDENTIFIERID = c.ID "
			+ "AND c.CDO_BRANCH = 0 "
			+ "AND c.CDO_REVISED = 0 "
			+ "AND c.CDO_VERSION > 0 "
			+ "WHERE "
			+ "r.CDO_BRANCH = 0 "
			+ "AND r.CDO_REVISED = 0 "
			+ "AND r.CDO_VERSION > 0";

}