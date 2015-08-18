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
package com.b2international.snowowl.core.users;

/**
 *  Central place for Permission IDs
 */
public abstract class PermissionIdConstant {

	private PermissionIdConstant(){ /* Suppressing instantiation */ }
	
	public static final String ALLOW_ALL = "all:all";
	
	public static final String ADMINISTRATIVE_EDITING = "all:administrativeediting";
	
	public static final String ADMINISTRATIVE_LOGIN = "none:administrativelogin";
	
	public static final String NEW_TEXT_FILE_WIZARD = "none:newtextfilewizard";
	
	public static final String NEW_PROJECT = "none:newproject";
	
	public static final String NEW_FOLDER = "none:newfolder";
	
	public static final String NEW_FILE = "none:newfile";
	
	public static final String B2I_EXAMPLES = "none:b2iexampleswizard";
	
	public static final String EXPORT_FILESYSTEM = "none:exportfilesystem";
	
	public static final String EXPORT_ZIP = "none:exportzipfile";
	
	public static final String IMPORT_PREFERENCES = "none:importpreferences";
	
	public static final String IMPORT_FILESYSTEM = "none:importfilesystem";
	
	public static final String IMPORT_EXTERNAL_PROJECT = "none:importexternalProject";
	
	public static final String IMPORT_ZIPFILE = "none:importzipfile";
	
	public static final String PROGRESS_VIEW = "none:viewprogressView";
	
	public static final String PROJECT_EXPLORER = "none:viewprojectexplorer";
	
	public static final String INTRO_VIEW = "none:viewintro";
			
	public static final String CHEAT_SHEET_VIEW = "none:viewcheatsheet";
		
	public static final String PROBLEM_VIEW = "none:viewproblems";
	
	public static final String OUT_LINE_VIEW = "none:viewoutline";
	
	public static final String CONSOLE_VIEW = "none:viewconsole";
	
	public static final String PROPERTY_SHEET = "none:viewproperties";
	
	public static final String HISTORY_VIEW = "none:viewhistory";
	
	public static final String HELP_VIEW = "none:viewhelp";
	
	public static final String LOG_VIEW = "none:viewlog";
	
	public static final String SEARCH_VIEW = "none:viewsearch";
	
	public static final String SCRIPTING_PROJECT = "none:createscriptingproject";
	
	public static final String PARENT_VIEW = "none:viewparent";

	public static final String PROBLEMS_VIEW = "none:viewproblems";

	public static final String REMOTE_JOBS_VIEW = "none:viewremotejobs";
	
	public static final String BOOKMARKS = "none:viewbookmarks";

	public static final String COMMIT_INFO_VIEW = "none:viewcommitinformation";

	public static final String STUDY_PROTOCOL_WIZARD = "none:createstudyprotocol";

	public static final String EHR_VIEW = "none:viewehr";
	
	public static final String VISUALIZATION_VIEW = "none:viewvisualization";

	public static final String AST_VISUALIZATION = "none:viewastvisualization";

	public static final String MYLYN_NEW_CATEGORY = "none:mylyn:newcategory";

	public static final String MYLYN_NEW_QUERY = "none:mylyn:newquery";

	public static final String TASK_EXPORT = "none:mylyn:taskexport";

	public static final String TASK_IMPORT = "none:mylyn:taskimport";

	public static final String REPOSITORIES_VIEW = "none:mylyn:viewrepositories";

	public static final String TASK_LIST_VIEW = "none:mylyn:viewtask";
	
	public static final String ATC_SEARCH = "atc:search";
	
	public static final String ATC_CLAML_EXPORT = "atc:export:claml"; 
	
	public static final String ATC_CLAML_IMPORT  = "atc:import:claml";
	
	public static final String ATC_NATIVE_IMPORT  = "atc:import:native";
	
	public static final String ATC_NAVIGATOR = "atc:view";
	
	public static final String ATC_CREATE_CONCEPT = "atc:create:concept";

	public static final String ATC_DELETE_CONCEPT = "atc:delete:concept";

	public static final String ICD_SEARCH = "icd10:search";

	public static final String ICD_10_AM_SEARCH = "icd10am:search";
	
	public static final String ICD_10_IMPORT = "icd10:import:claml";
	
	public static final String ICD_10_NAVIGATOR = "icd10:view";
	
	public static final String ICD_10_AM_IMPORT = "icd10am:import";
	
	public static final String ICD_10_AM_NAVIGATOR = "icd10am:view";
	
	public static final String ICD_10_CM_IMPORT = "icd10cm:import";
	
	public static final String ICD_10_CM_SEARCH = "icd10cm:search";
	
	public static final String ICD_10_CM_NAVIGATOR = "icd10cm:view";
	
	public static final String LCS_EXPORT = "lcs:export";
	
	public static final String LCS_IMPORT = "lcs:import";
	
	public static final String LCS_NAVIGATOR = "lcs:view";
	
	public static final String LCS_CREATE_CONCEPT = "lcs:create:concept";

	public static final String LCS_DELETE_CONCEPT = "lcs:delete:concept";
	
	public static final String LCS_CREATE_TERMINOLOGY = "lcs:create:terminology";

	public static final String LCS_DELETE_TERMINOLOGY = "lcs:delete:terminology";

	public static final String LCS_SEARCH = "lcs:search";
	
	public static final String LOINC_IMPORT = "loinc:import";
	
	public static final String LOINC_VIEW = "loinc:view";
	
	public static final String LOINC_CREATE_CONCEPT = "loinc:create:concept";

	public static final String LOINC_DELETE_CONCEPT = "loinc:delete:concept";

	public static final String LOINC_SEARCH = "loinc:search";

	public static final String MAPPINGSET_EXPORT = "mappingset:export";
	
	public static final String MAPPINGSET_IMPORT = "mappingset:import";
	
	public static final String MAPPINGSET_NAVIGATOR  = "mappingset:view";
	
	public static final String MAPPINGSET_CREATE_FOLDER = "mappingset:create:folder";

	public static final String MAPPINGSET_CREATE_MAPPINGSET = "mappingset:create:mappingset";
	
	public static final String MAPPINGSET_DELETE_MAPPINGSET = "mappingset:delete:mappingset";

	public static final String MAPPINGSET_DELETE_FOLDER = "mappingset:delete:folder";

	public static final String MAPPINGSET_RENAME_FOLDER = "mappingset:rename:folder";

	public static final String MAPPINGSET_SEARCH = "mappingset:search";

	public static final String MRCM_EXPORT = "snomed:mrcm:export";
	
	public static final String MRCM_IMPORT = "snomed:mrcm:import";
	
	public static final String MRCM_EDIT = "snomed:mrcmedit";
	
	public static final String SNOMED_REFSET_EDITING = "snomed:edit:refset";
	
	public static final String IMPORT_CATEGORY = "snomed:categoryimport";

	public static final String SNOMED_IMPORT = "snomed:sctImport";

	public static final String SNOMED_REFSET_RF2_IMPORT = "snomed:import:refsetrf2";

	public static final String SNOMED_REFSET_RF1_DSV_IMPORT = "snomed:import:refsetrf1dsv";

	public static final String CLASSIFY = "snomed:classify";

	public static final String SNOMED_REFSET_CREATE = "snomed:create:refset";

	public static final String SNOMED_CREATE_AUTOMAP = "snomed:create:automap";
	
	public static final String SNOMED_COMPARE_AUTOMAP = "snomed:compare:automap";

	public static final String REFSET_NAVIGATOR = "snomed:viewrefset";

	public static final String CONCEPT_DIAGRAM_NAVIGATOR = "snomed:viewconceptdiagramnavigator";

	public static final String SNOMED_NAVIGATOR = "snomed:view";

	public static final String SNOMED_EDIT_CONCEPT = "snomed:edit:concept";
	
	public static final String SNOMED_REFSET_EXPORT_DSV = "snomed:refset:export:dsv";

	public static final String SNOMED_REFSET_EXPORT_EXCEL = "snomed:refset:export:excel";

	public static final String SNOMED_REFSET_EXPORT_RF = "snomed:refset:export:rf";

	public static final String SNOMED_EXPORT_RF2 = "snomed:sctExport:terminology";

	public static final String SNOMED_EXPORT_OWL = "snomed:export:owl";

	public static final String SNOMED_REFSET_CLONE = "snomed:clone:refset";

	public static final String SNOMED_REFSET_SEARCH = "snomed:refset:search";

	public static final String SNOMED_CREATE_CONCEPT = "snomed:create:concept";

	public static final String SNOMED_DELETE_CONCEPT = "snomed:delete:concept";

	public static final String SNOMED_SEARCH = "snomed:search";

	public static final String UMLS_NAVIGATOR = "umls:view";

	public static final String UMLS_SEARCH = "umls:search";
	
	public static final String UMLS_IMPORT = "umls:import";

	public static final String VALUESET_EXPORT_EXCEL = "valuedomain:export:excel";

	public static final String VALUESET_EXPORT_UMLS = "valuedomain:export:umls";

	public static final String VALUESET_IMPORT_EXCEL = "valuedomain:import:excel";

	public static final String VALUESET_IMPORT_UMLS = "valuedomain:import:umls";

	public static final String VALUESET_NAVIGATOR = "valuedomain:view";
	
	public static final String VALUESET_CREATE_FOLDER = "valuedomain:create:folder";

	public static final String VALUESET_CREATE_VALUESET = "valuedomain:create:valuedomain";

	public static final String VALUESET_DELETE_FOLDER = "valuedomain:delete:folder";
	
	public static final String VALUESET_DELETE_VALUESET = "valuedomain:delete:valuedomain";

	public static final String VALUESET_RENAME_FOLDER = "valuedomain:rename:folder";

	public static final String VALUESET_SEARCH = "valuedomain:search";

	public static final String CREATE_TASK = "all:createtask";

	public static final String GENERATE_ONTOLOGY_ON_BRANCH = "ogf:branch:generateOntology";

	public static final String GENERATE_ONTOLOGY_ON_MAIN = "ogf:main:generateOntology";

	public static final String GENERATE_ONTOLOGY_MULTIPLE_BRANCHES = "ogf:multiple:generateOntology";
	
	public static final String VERSION_CREATE = "all:createversion";

	public static final String PROMOTE = "all:promote";

	public static final String REMOTE_JOBS_VIEW_ALL = "remotejobs:viewalljobs";
}