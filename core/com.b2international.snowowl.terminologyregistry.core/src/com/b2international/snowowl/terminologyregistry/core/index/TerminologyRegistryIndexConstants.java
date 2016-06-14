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
package com.b2international.snowowl.terminologyregistry.core.index;

/**
 * Constants for index based terminology registry metadata.
 *
 */
public abstract class TerminologyRegistryIndexConstants {

	public static final String VERSION_IMPORT_DATE = "versionImportDate";
	public static final String VERSION_EFFECTIVE_DATE = "versionVersionEffectiveDate";
	public static final String VERSION_DESCRIPTION = "versionVersionDescription";
	public static final String VERSION_VERSION_ID = "versionVersionId";
	public static final String VERSION_PARENT_BRANCH_PATH = "parentBranchPath";
	public static final String VERSION_LATEST_UPDATE_DATE = "versionLatestUpdateDate";
	public static final String VERSION_STORAGE_KEY = "versionStorageKey";
	public static final String VERSION_REPOSITORY_UUID = "versionRepositoryUuid";
	public static final String VERSION_SYSTEM_SHORT_NAME = "versionSystemShortName";
	
	public static final String SYSTEM_OID = "systemOid";
	public static final String SYSTEM_NAME = "systemName"; 
	public static final String SYSTEM_SHORT_NAME = "systemShortName"; 
	public static final String SYSTEM_ORG_LINK = "systemOrgLink"; 
	public static final String SYSTEM_LANGUAGE = "systemLanguage"; 
	public static final String SYSTEM_CITATION = "systemCitation"; 
	public static final String SYSTEM_ICON_PATH = "systemIconPth"; 
	public static final String SYSTEM_TERMINOLOGY_COMPONENT_ID = "systemTerminologyComponentId";
	public static final String SYSTEM_STORAGE_KEY = "systemStorageKey";
	public static final String SYSTEM_REPOSITORY_UUID = "systemRepositoryUuid";
	public static final String SYSTEM_BRANCH_PATH = "systemBranchPath";
	public static final String SYSTEM_EXTENSION_OF = "extensionOf";
	
	private TerminologyRegistryIndexConstants() { /*not here*/ }
	
}