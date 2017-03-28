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
package com.b2international.snowowl.snomed.exporter.server.refset;

/**
 * These queries are executed during the reference set export.
 * <p>
 * <b>Note</b>: Most joins assume a 1:1 mapping from CDO IDs to reference set
 * identifier concept IDs; this means that once a reference set has been
 * created, its identifier concept ID may not change during its lifetime
 * (separate branches may create different reference set instances with the same
 * ID, though).
 * 
 */
public abstract class SnomedRefSetExporterQueries {

	// Simple map type reference sets
	public static final String SQL_SIMPLE_MAP_REFSET_EXPORT_QUERY = "SELECT "
			+ "member.CDO_ID, "
			+ "member.CDO_CREATED, "
			+ "member.CDO_VERSION, "
			+ "member.EFFECTIVETIME, "
			+ "member.UUID, "
			+ "member.ACTIVE, "
			+ "member.MODULEID, "
			+ "member.REFERENCEDCOMPONENTID, "
			+ "member.MAPTARGETCOMPONENTID "
			+ "FROM SNOMEDREFSET_SNOMEDSIMPLEMAPREFSETMEMBER member "
			+ "WHERE member.REFSET = ? ";
	
	// Simple map type reference sets with map target descriptions
	public static final String SQL_SIMPLE_MAP_WITH_TARGET_DESCRIPTION_REFSET_EXPORT_QUERY = "SELECT "
			+ "member.CDO_ID, "
			+ "member.CDO_CREATED, "
			+ "member.CDO_VERSION, "
			+ "member.EFFECTIVETIME, "
			+ "member.UUID, "
			+ "member.ACTIVE, "
			+ "member.MODULEID, "
			+ "member.REFERENCEDCOMPONENTID, "
			+ "member.MAPTARGETCOMPONENTID, "
			+ "member.MAPTARGETCOMPONENTDESCRIPTION "
			+ "FROM SNOMEDREFSET_SNOMEDSIMPLEMAPREFSETMEMBER member "
			+ "WHERE member.REFSET = ? ";
	
	// Complex map type reference sets
	public static final String SQL_COMPLEX_MAP_REFSET_EXPORT_QUERY = "SELECT "
			+ "member.CDO_ID, "
			+ "member.CDO_CREATED, "
			+ "member.CDO_VERSION, "
			+ "member.EFFECTIVETIME, "
			+ "member.UUID, "
			+ "member.ACTIVE, "
			+ "member.MODULEID, "
			+ "member.REFERENCEDCOMPONENTID, "
			+ "member.MAPGROUP, "
			+ "member.MAPPRIORITY, "
			+ "member.MAPRULE, "
			+ "member.MAPADVICE, "
			+ "member.MAPTARGETCOMPONENTID, "
			+ "member.CORRELATIONID "
			+ "FROM SNOMEDREFSET_SNOMEDCOMPLEXMAPREFSETMEMBER member "
			+ "WHERE member.REFSET = ? ";
	
	//extended map type reference sets
	public static final String SQL_EXTENDED_MAP_REFSET_EXPORT_QUERY = "SELECT "
			+ "member.CDO_ID, "
			+ "member.CDO_CREATED, "
			+ "member.CDO_VERSION, "
			+ "member.EFFECTIVETIME, "
			+ "member.UUID, "
			+ "member.ACTIVE, "
			+ "member.MODULEID, "
			+ "member.REFERENCEDCOMPONENTID, "
			+ "member.MAPGROUP, "
			+ "member.MAPPRIORITY, "
			+ "member.MAPRULE, "
			+ "member.MAPADVICE, "
			+ "member.MAPTARGETCOMPONENTID, "
			+ "member.CORRELATIONID, "
			+ "member.MAPCATEGORYID "
			+ "FROM SNOMEDREFSET_SNOMEDCOMPLEXMAPREFSETMEMBER member "
			+ "WHERE member.REFSET = ? ";

	// Simple type reference sets
	public static final String SQL_SIMPLE_TYPE_REFSET_EXPORT_QUERY = "SELECT "
			+ "member.CDO_ID, "
			+ "member.CDO_CREATED, "
			+ "member.CDO_VERSION, "
			+ "member.EFFECTIVETIME, "
			+ "member.UUID, "
			+ "member.ACTIVE, "
			+ "member.MODULEID, "
			+ "member.REFERENCEDCOMPONENTID "
			+ "FROM SNOMEDREFSET_SNOMEDREFSETMEMBER member "
			+ "WHERE member.REFSET = ? ";
	
	// Query reference sets
	public static final String SQL_QUERY_REFSET_EXPORT_QUERY = "SELECT "
			+ "member.CDO_ID, "
			+ "member.CDO_CREATED, "
			+ "member.CDO_VERSION, "
			+ "member.EFFECTIVETIME, "
			+ "member.UUID, "
			+ "member.ACTIVE, "
			+ "member.MODULEID, "
			+ "member.REFERENCEDCOMPONENTID, "
			+ "member.QUERY "
			+ "FROM SNOMEDREFSET_SNOMEDQUERYREFSETMEMBER member "
			+ "WHERE member.REFSET = ? ";

	/**
	 * Input is the CDO ID of the language type reference set.
	 */
	// Language type reference sets
	public static final String SQL_LANGUAGE_REFSET_EXPORT_QUERY = "SELECT "
			+ "member.CDO_ID, "
			+ "member.CDO_CREATED, "
			+ "member.CDO_VERSION, "
			+ "member.EFFECTIVETIME, "
			+ "member.UUID, "
			+ "member.ACTIVE, "
			+ "member.MODULEID, "
			+ "member.REFERENCEDCOMPONENTID, "
			+ "member.ACCEPTABILITYID "
			+ "FROM SNOMEDREFSET_SNOMEDLANGUAGEREFSETMEMBER member "
			+ "WHERE member.REFSET = ? ";

	// Attribute value type reference sets
	public static final String SQL_ATTRIBUTE_VALUE_REFSET_EXPORT_QUERY = "SELECT "
			+ "member.CDO_ID, "
			+ "member.CDO_CREATED, "
			+ "member.CDO_VERSION, "
			+ "member.EFFECTIVETIME, "
			+ "member.UUID, "
			+ "member.ACTIVE, "
			+ "member.MODULEID, "
			+ "member.REFERENCEDCOMPONENTID, "
			+ "member.VALUEID "
			+ "FROM SNOMEDREFSET_SNOMEDATTRIBUTEVALUEREFSETMEMBER member "
			+ "WHERE member.REFSET = ? ";
	
	// description type reference set
	public static final String SQL_DESCRIPTION_TYPE_REFSET_EXPORT_QUERY = "SELECT "
			+ "member.CDO_ID, "
			+ "member.CDO_CREATED, "
			+ "member.CDO_VERSION, "
			+ "member.EFFECTIVETIME, "
			+ "member.UUID, "
			+ "member.ACTIVE, "
			+ "member.MODULEID, "
			+ "member.REFERENCEDCOMPONENTID, "
			+ "member.DESCRIPTIONFORMAT, "
			+ "member.DESCRIPTIONLENGTH "
			+ "FROM SNOMEDREFSET_SNOMEDDESCRIPTIONTYPEREFSETMEMBER member "
			+ "WHERE member.REFSET = ? ";
	
	// concrete domain reference sets
	public static final String SQL_CONCRETE_DATA_TYPE_REFSET_EXPORT_QUERY = "SELECT "
			+ "member.CDO_ID, "
			+ "member.CDO_CREATED, "
			+ "member.CDO_VERSION, "
			+ "member.EFFECTIVETIME, "
			+ "member.UUID, "
			+ "member.ACTIVE, "
			+ "member.MODULEID, "
			+ "member.REFERENCEDCOMPONENTID, "
			+ "member.UOMCOMPONENTID, "
			+ "member.OPERATORCOMPONENTID, "
			+ "member.LABEL0, "
			+ "member.SERIALIZEDVALUE, "
			+ "member.CHARACTERISTICTYPEID "
			+ "FROM SNOMEDREFSET_SNOMEDCONCRETEDATATYPEREFSETMEMBER  member "
			+ "WHERE member.REFSET = ? ";
	
	// concrete domain reference sets
	public static final String SQL_ASSOCIATION_TYPE_REFSET_EXPORT_QUERY = "SELECT "
			+ "member.CDO_ID, "
			+ "member.CDO_CREATED, "
			+ "member.CDO_VERSION, "
			+ "member.EFFECTIVETIME, "
			+ "member.UUID, "
			+ "member.ACTIVE, "
			+ "member.MODULEID, "
			+ "member.REFERENCEDCOMPONENTID, "
			+ "member.TARGETCOMPONENTID "
			+ "FROM SNOMEDREFSET_SNOMEDASSOCIATIONREFSETMEMBER  member "
			+ "WHERE member.REFSET = ? ";
	
	// module dependency reference set
	public static final String SQL_MODULE_DEPENDENCY_REFSET_EXPORT_QUERY = "SELECT " +
			"member.CDO_ID, " +
			"member.CDO_CREATED, " +
			"member.CDO_VERSION, " +
			"member.EFFECTIVETIME, " +
			"member.UUID, " +
			"member.ACTIVE, " +
			"member.MODULEID, " +
			"member.REFERENCEDCOMPONENTID, " +
			"member.SOURCEEFFECTIVETIME, " +
			"member.TARGETEFFECTIVETIME " +
			"FROM SNOMEDREFSET_SNOMEDMODULEDEPENDENCYREFSETMEMBER member " +
			"WHERE member.REFSET = ? ";
	
	// simple type reference set DSV export queries
	
	public static final String SQL_SIMPLE_TYPE_REFSET_DSV_EXPORT_DESCRIPTION_QUERY =" SELECT" 
			 +" description.CDO_ID, description.TERM, description.CDO_BRANCH, description.ID, description.CDO_BRANCH, description.CDO_VERSION"
			 +" FROM SNOMED_DESCRIPTION description" 
			 +" JOIN SNOMEDREFSET_SNOMEDLANGUAGEREFSETMEMBER member"
			 +" ON description.CDO_ID = member.CDO_CONTAINER"
			 
			 +" AND member.CDO_VERSION > 0"
			 +" AND member.ACTIVE = true"
			 +" AND ("
				 +" member.CDO_BRANCH = ?"  
				 +" OR member.CDO_BRANCH = 0" 
			 +" )" 
			 +" AND (" 
				 +" (member.CDO_BRANCH = ?"  
				 +" AND member.CDO_REVISED = 0)"
				 +" OR ("
				 +" member.CDO_BRANCH = 0"
				 +" AND member.CDO_CREATED <= ?"
				 +" AND (" 
					 +" member.CDO_REVISED = 0"  
					 +" OR member.CDO_REVISED >= ?)"
				 +" )"
			+" )"
			 
			+" AND description.CDO_CONTAINER = ?"
			+" AND description.TYPE=("
				 +" SELECT DISTINCT" 
				 +" concept.CDO_ID" 
				 +" FROM SNOMED_CONCEPT concept" 
				 +" WHERE concept.ID = ?"
				 +" AND concept.ACTIVE = true"
				 +" AND concept.CDO_VERSION > 0"
				 +" AND concept.CDO_REVISED = 0"
			 +" )"

			 +" AND description.CDO_VERSION > 0"
			 +" AND description.ACTIVE = true"
			 +" AND ("
				 +" description.CDO_BRANCH = ?"  
				 +" OR description.CDO_BRANCH = 0"
			 +" )" 
			 +" AND (" 
				 +" (description.CDO_BRANCH = ?"  
				 +" AND description.CDO_REVISED = 0)"
				 +" OR ("
				 +" description.CDO_BRANCH = 0"
				 +" AND description.CDO_CREATED <= ?"
				 +" AND (" 
					 +" description.CDO_REVISED = 0"
					 +" OR description.CDO_REVISED >= ?)"
				 +" )"
			+" )"

			+" JOIN SNOMEDREFSET_SNOMEDSTRUCTURALREFSET refset"
				 +" ON member.REFSET = refset.CDO_ID"
				 +" AND refset.CDO_VERSION > 0"
				 +" AND refset.CDO_REVISED = 0"
				 +" AND refset.IDENTIFIERID = ?"
				 +" AND refset.CDO_VERSION > 0"
			 +" AND ("
				 +" refset.CDO_BRANCH = ?"  
				 +" OR refset.CDO_BRANCH = 0" 
			 +" )" 
			 +" AND (" 
				 +" (refset.CDO_BRANCH = ?"
				 +" AND refset.CDO_REVISED = 0)"
				 +" OR ("
				 +" refset.CDO_BRANCH = 0"
				 +" AND refset.CDO_CREATED <= ?"
				 +" AND (" 
					 +" refset.CDO_REVISED = 0"
					 +" OR refset.CDO_REVISED >= ?)"
				+" )"
			+" )"
			+" ORDER BY description.CDO_BRANCH desc, description.CDO_VERSION desc";

	
	public static final String SQL_SIMPLE_TYPE_REFSET_DSV_EXPORT_REMOVED_DESCRIPTION_QUERY = "SELECT "
			+" description.CDO_VERSION"
			+" FROM SNOMED_DESCRIPTION description "
			+" WHERE description.CDO_ID = ?"
			+" AND description.CDO_BRANCH = ?"
			+" ORDER BY description.CDO_VERSION"
			;
	
	public static final String SQL_SIMPLE_TYPE_REFSET_DSV_EXPORT_INACTIVATED_DESCRIPTION_QUERY = "SELECT "
			+" description.ACTIVE, description.CDO_VERSION"
			+" FROM SNOMED_DESCRIPTION description "
			+" WHERE description.CDO_ID = ?"
			+" AND description.CDO_BRANCH = ?"
			+" ORDER BY description.CDO_VERSION desc"
			;
	
	public static final String SQL_SIMPLE_TYPE_REFSET_DSV_EXPORT_RELATIONSHIP_QUERY = "SELECT DISTINCT "
			+" relationship.CDO_ID, concept.ID, relationship.CDO_BRANCH, relationship.GROUP0, concept.CDO_ID"
			+" from SNOMED_CONCEPT concept" 
			+" JOIN SNOMED_RELATIONSHIP relationship" 
			+" ON concept.CDO_ID = relationship.DESTINATION"
			+" WHERE relationship.CDO_CONTAINER = ?" 
			+" AND relationship.TYPE = ("
				+" SELECT DISTINCT" 
				+" concept.CDO_ID" 
				+" FROM SNOMED_CONCEPT concept" 
				+" WHERE concept.ID=?"
				+" AND concept.ACTIVE = true"
				+" AND concept.CDO_VERSION > 0"
				+" AND concept.CDO_REVISED = 0"
			+" )"
			+" AND relationship.active = true" 
			+" AND relationship.cdo_version > 0"
			+" AND ("
	        +" relationship.cdo_branch = ?"  
	        +" OR relationship.cdo_branch = 0" 
	        +" )" 
	        +" AND (" 
	        		+" (relationship.cdo_branch = ?"  
	        		+" AND relationship.cdo_revised = 0)"
		        +" OR ("
		        	+" relationship.cdo_branch = 0"
		        	+" AND relationship.cdo_created <= ?"
		        	+" AND (" 
		        		+" relationship.cdo_revised = 0"  
		        		+" OR relationship.cdo_revised >= ?)"
		        	+" )"
		    +" )"
		    +" AND concept.ACTIVE = true"
		    +" AND concept.CDO_REVISED = 0"
		    ;
	
	public static final String SQL_SIMPLE_TYPE_REFSET_DSV_EXPORT_REMOVED_RELATIONSHIP_QUERY = "SELECT "
			+" relationship.CDO_VERSION"
			+" FROM SNOMED_RELATIONSHIP relationship"
			+" WHERE  relationship.CDO_ID = ?"
			+" AND relationship.CDO_BRANCH = ?"
			+" ORDER BY relationship.CDO_VERSION"
			;
	
	public static final String SQL_SIMPLE_TYPE_REFSET_DSV_EXPORT_INACTIVATED_RELATIONSHIP_QUERY = "SELECT "
			+" relationship.ACTIVE, relationship.CDO_VERSION"
			+" FROM SNOMED_RELATIONSHIP relationship"
			+" WHERE  relationship.CDO_ID = ?"
			+" AND relationship.CDO_BRANCH = ?"
			+" ORDER BY relationship.CDO_VERSION desc"
			;
	
	public static final String SQL_SIMPLE_TYPE_REFSET_DSV_EXPORT_DATATYPE_QUERY =  "SELECT "
			+" member.CDO_ID, member.SERIALIZEDVALUE, member.CDO_BRANCH"
			+" FROM SNOMEDREFSET_SNOMEDCONCRETEDATATYPEREFSETMEMBER member " 
			+" WHERE member.CDO_CONTAINER = ?"
			+" AND member.LABEL0 = ?"
			+" AND member.cdo_version > 0"
			+" AND ("
				+" member.cdo_branch = ? " 
				+" OR member.cdo_branch = 0" 
			+" )" 
			+" AND (" 
				+" (member.cdo_branch = ? " 
				+" AND member.cdo_revised = 0)"
				+" OR ("
				+" member.cdo_branch = 0"
				+" AND member.cdo_created <= ?"
				+" AND (" 
					+" member.cdo_revised = 0 " 
					+" OR member.cdo_revised >= ?)"
				+" )"
			+")" 
			+" AND member.ACTIVE = true"	
			;
	
	public static final String SQL_SIMPLE_TYPE_REFSET_DSV_EXPORT_REMOVED_DATATYPE_QUERY = "SELECT "
			+" member.CDO_VERSION"
			+" FROM SNOMEDREFSET_SNOMEDCONCRETEDATATYPEREFSETMEMBER member "
			+" WHERE member.CDO_ID = ?"
			+" AND member.CDO_BRANCH = ?"
			+" ORDER BY member.CDO_VERSION"
			;
	
	public static final String SQL_SIMPLE_TYPE_REFSET_DSV_EXPORT_INACTIVATED_DATATYPE_QUERY = "SELECT "
			+" member.ACTIVE, member.CDO_VERSION"
			+" FROM SNOMEDREFSET_SNOMEDCONCRETEDATATYPEREFSETMEMBER member "
			+" WHERE member.CDO_ID = ?"
			+" AND member.CDO_BRANCH = ?"
			+" ORDER BY member.CDO_VERSION desc"
			;
	
	public static final String SQL_SIMPLE_TYPE_REFSET_DSV_EXPORT_EFFECTIVE_TIME_QUERY = "SELECT "
			+" concept.EFFECTIVETIME"
			+" FROM SNOMED_CONCEPT concept"
			+" WHERE concept.CDO_ID = ?"
			+" AND concept.cdo_version > 0"
			+" AND ("
				+" concept.cdo_branch = ? " 
				+" OR concept.cdo_branch = 0" 
			+" )" 
			+" AND (" 
				+" (concept.cdo_branch = ? " 
				+" AND concept.cdo_revised = 0)"
				+" OR ("
				+" concept.cdo_branch = 0"
				+" AND concept.cdo_created <= ?"
				+" AND (" 
					+" concept.cdo_revised = 0 " 
					+" OR concept.cdo_revised >= ?)"
				+" )"
			+")" 
			+" ORDER BY concept.EFFECTIVETIME DESC"
			;
	
	public static final String SQL_SIMPLE_TYPE_REFSET_DSV_EXPORT_DEFINITION_STATUS_QUERY = "SELECT "
			+" concept.DEFINITIONSTATUS"
			+" FROM SNOMED_CONCEPT concept"
			+" WHERE concept.CDO_ID = ?"
			+" AND concept.cdo_version > 0"
			+" AND ("
				+" concept.cdo_branch = ? " 
				+" OR concept.cdo_branch = 0" 
			+" )" 
			+" AND (" 
				+" (concept.cdo_branch = ? " 
				+" AND concept.cdo_revised = 0)"
				+" OR ("
				+" concept.cdo_branch = 0"
				+" AND concept.cdo_created <= ?"
				+" AND (" 
					+" concept.cdo_revised = 0 " 
					+" OR concept.cdo_revised >= ?)"
				+" )"
			+")" 
			;
	
	public static final String SQL_SIMPLE_TYPE_REFSET_DSV_EXPORT_MODULE_QUERY = "SELECT "
			+" concept.MODULE0"
			+" FROM SNOMED_CONCEPT concept"
			+" WHERE concept.CDO_ID = ?"
			+" AND concept.cdo_version > 0"
			+" AND ("
				+" concept.cdo_branch = ? " 
				+" OR concept.cdo_branch = 0" 
			+" )" 
			+" AND (" 
				+" (concept.cdo_branch = ? " 
				+" AND concept.cdo_revised = 0)"
				+" OR ("
				+" concept.cdo_branch = 0"
				+" AND concept.cdo_created <= ?"
				+" AND (" 
					+" concept.cdo_revised = 0 " 
					+" OR concept.cdo_revised >= ?)"
				+" )"
			+")" 
			;
	
	public static final String SQL_SIMPLE_TYPE_REFSET_DSV_EXPORT_REFSET_MEMBER_QUERY = "SELECT"
			+" concept.CDO_ID, concept.ID"
			+" FROM SNOMED_CONCEPT concept"
			+" JOIN SNOMEDREFSET_SNOMEDREFSETMEMBER member"
			+" ON concept.ID = member.REFERENCEDCOMPONENTID"
			+" AND member.REFSET ="
			+" (SELECT DISTINCT refset.CDO_ID FROM SNOMEDREFSET_SNOMEDREGULARREFSET refset"
			+" WHERE refset.IDENTIFIERID = ?"
			+" )"
			+" AND member.cdo_version > 0"
			+" AND ("
				+" member.cdo_branch = ? " 
				+" OR member.cdo_branch = 0" 
			+" )" 
			+" AND (" 
				+" (member.cdo_branch = ? " 
				+" AND member.cdo_revised = 0)"
				+" OR ("
				+" member.cdo_branch = 0"
				+" AND member.cdo_created <= ?"
				+" AND (" 
					+" member.cdo_revised = 0 " 
					+" OR member.cdo_revised >= ?)"
				+" )"
			+")" 
			+" AND concept.cdo_version > 0"
			+" AND ("
				+" concept.cdo_branch = ? " 
				+" OR concept.cdo_branch = 0" 
			+" )" 
			+" AND (" 
				+" (concept.cdo_branch = ? " 
				+" AND concept.cdo_revised = 0)"
				+" OR ("
				+" concept.cdo_branch = 0"
				+" AND concept.cdo_created <= ?"
				+" AND (" 
					+" concept.cdo_revised = 0 " 
					+" OR concept.cdo_revised >= ?)"
				+" )"
			+")"
			+" AND concept.ACTIVE = true"
			+" WHERE ("
				+" member.ACTIVE = true"
			+" )";
	
	public static final String SQL_SIMPLE_TYPE_REFSET_DSV_EXPORT_INACTIVATED_REFSET_MEMBER_CONCEPT_QUERY = "SELECT "
			+" concept.ACTIVE, concept.CDO_VERSION"
			+" FROM SNOMED_CONCEPT concept"
			+" WHERE concept.CDO_ID = ?"
			+" AND concept.CDO_BRANCH = ?"
			+" ORDER BY concept.CDO_VERSION DESC";
	
	public static final String SQL_SIMPLE_TYPE_REFSET_DSV_EXPORT_IS_ACTIVE_CONCEPT_QUERY = "SELECT"
			+" concept.ACTIVE"
			+" FROM SNOMED_CONCEPT concept"
			+" WHERE concept.CDO_ID = ?"
			+" AND concept.cdo_version > 0"
			+" AND ("
				+" concept.cdo_branch = ? " 
				+" OR concept.cdo_branch = 0" 
			+" )" 
			+" AND (" 
				+" (concept.cdo_branch = ? " 
				+" AND concept.cdo_revised = 0)"
				+" OR ("
				+" concept.cdo_branch = 0"
				+" AND concept.cdo_created <= ?"
				+" AND (" 
					+" concept.cdo_revised = 0 " 
					+" OR concept.cdo_revised >= ?)"
				+" )"
			+")" ;
	
	public static String buildPreferredTermQuery(int size) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT");	 
		sb.append("  description.TERM, description.CDO_ID, description.ID, description.CDO_BRANCH, description.CDO_VERSION");
					 sb.append(" FROM SNOMED_DESCRIPTION description");
					 sb.append(" JOIN SNOMEDREFSET_SNOMEDLANGUAGEREFSETMEMBER member");
					 sb.append(" ON description.CDO_ID = member.CDO_CONTAINER");
					 sb.append(" AND member.ACCEPTABILITYID = ?");
					 sb.append(" AND member.CDO_VERSION > 0");
					 sb.append(" AND member.ACTIVE = true");
					 sb.append(" AND (");
						 sb.append(" member.CDO_BRANCH = ?");
						 sb.append(" OR member.CDO_BRANCH = 0");
					 sb.append(" )");
					 sb.append(" AND (");
						 sb.append(" (member.CDO_BRANCH = ?");
						 sb.append(" AND member.CDO_REVISED = 0)");
						 sb.append(" OR (");
						 sb.append(" member.CDO_BRANCH = 0");
						 sb.append(" AND member.CDO_CREATED <= ?");
						 sb.append(" AND (");
							 sb.append(" member.CDO_REVISED = 0");
							 sb.append(" OR member.CDO_REVISED >= ?)");
						 sb.append(" )");
					sb.append(" )");
					 
					sb.append(" AND description.CDO_CONTAINER = ?");
					 sb.append(" AND description.CDO_VERSION > 0");
					 sb.append(" AND description.ACTIVE = true");
					 sb.append(" AND (");
						 sb.append(" description.CDO_BRANCH = ?");
						 sb.append(" OR description.CDO_BRANCH = 0");
					 sb.append(" )");
					 sb.append(" AND (");
						 sb.append(" (description.CDO_BRANCH = ?"); 
						 sb.append(" AND description.CDO_REVISED = 0)");
						 sb.append(" OR (");
						 sb.append(" description.CDO_BRANCH = 0");
						 sb.append(" AND description.CDO_CREATED <= ?");
						 sb.append(" AND (");
							 sb.append(" description.CDO_REVISED = 0");
							 sb.append(" OR description.CDO_REVISED >= ?)");
						 sb.append(" )");
					sb.append(" )");
					sb.append(" AND description.TYPE IN (");
				
					// The number of synonym and it's all descendant types is variable.
				
					boolean first = true;
					for (int i = 0 ; i < size; ++i) {
						if (first) {
							first = false;
						} else {
							sb.append(", ");
						}
						sb.append("? ");
					}
					sb.append(")");

					sb.append(" JOIN SNOMEDREFSET_SNOMEDSTRUCTURALREFSET refset");
						 sb.append(" ON member.REFSET = refset.CDO_ID");
						 sb.append(" AND refset.CDO_VERSION > 0");
						 sb.append(" AND refset.CDO_REVISED = 0");
						 sb.append(" AND refset.IDENTIFIERID = ?");
						 sb.append(" AND refset.CDO_VERSION > 0");
					 sb.append(" AND (");
						 sb.append(" refset.CDO_BRANCH = ?");
						 sb.append(" OR refset.CDO_BRANCH = 0");
					 sb.append(" )");
					 sb.append(" AND (");
						 sb.append(" (refset.CDO_BRANCH = ?");
						 sb.append(" AND refset.CDO_REVISED = 0)");
						 sb.append(" OR (");
						 sb.append(" refset.CDO_BRANCH = 0");
						 sb.append(" AND refset.CDO_CREATED <= ?");
						 sb.append(" AND (");
							 sb.append(" refset.CDO_REVISED = 0");
							 sb.append(" OR refset.CDO_REVISED >= ?)");
						sb.append(" )");
					sb.append(" )");

		sb.append(" ORDER BY description.CDO_BRANCH desc, description.CDO_VERSION desc");
		return sb.toString();
	}
	
	public static String buildDSVExportRelationshipQuery(boolean filterOnGroupId) {
		if (filterOnGroupId) {
			return SQL_SIMPLE_TYPE_REFSET_DSV_EXPORT_RELATIONSHIP_QUERY + " AND relationship.GROUP0 = ?";
		} else {
			return SQL_SIMPLE_TYPE_REFSET_DSV_EXPORT_RELATIONSHIP_QUERY;
		}
	}
	
	private SnomedRefSetExporterQueries() {
		// Prevent instantiation
	}
}