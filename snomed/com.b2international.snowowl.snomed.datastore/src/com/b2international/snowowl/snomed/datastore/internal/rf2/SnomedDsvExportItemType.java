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
package com.b2international.snowowl.snomed.datastore.internal.rf2;

/**
 * This enumeration denotes the type of the exported property.
 * 
 */
public enum SnomedDsvExportItemType {

	DESCRIPTION,  
	RELATIONSHIP, 
	DATAYPE, 

	PREFERRED_TERM("Preferred term"), 
	CONCEPT_ID("Concept ID"), 
	MODULE("Concept module"), 
	EFFECTIVE_TIME("Concept effective time"), 
	STATUS_ID("Status code"), 
	STATUS_LABEL("Concept status label"), 
	DEFINITION_STATUS("Definition status"), 
	REFERENCED_COMPONENT("Referenced component"), 
	REFERENCED_COMPONENT_ID("Referenced component ID"), 
	MAP_TARGET_DESCRIPTION("Map target description"),
	MAP_TARGET_ID("Map target ID"), 
	MODULE_LABEL("Module label"), 
	MODULE_ID("Module ID"), 
	MEMBER_ID("Member ID"), 
	MAP_GROUP("Map group"), 
	MAP_PRIORITY("Map priority"), 
	MAP_RULE("Map rule"), 
	MAP_ADVICE("Map advice"), 
	CORRELATION("Correlation ID"), 
	MAP_CATEGORY("Map category"), 
	SDD_CLASS("SDD Class");

	private final String displayName;

	private SnomedDsvExportItemType() {
		this("");
	}
	
	private SnomedDsvExportItemType(final String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}
}
