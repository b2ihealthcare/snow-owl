/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request.rf2.validation;

/**
 * since 7.0
 */
public enum Rf2ValidationDefects {
	
	INCORRECT_COLUMN_NUMBER("Incorrect column number in release file"),
	INVALID_ID("Component identifier is not of the expected type, malformed, or has an incorrect Verhoeff check digit."),
	MISSING_DEPENDANT_ID("Component refers to a non-existing concept"),
	MODULE_CONCEPT_NOT_EXIST("Module concept does not exist"),
	NOT_UNIQUE_DESCRIPTION_ID("Description identifier is not unique"),
	CONCEPT_DEFINITION_STATUS_NOT_EXIST("Concept refers to a non-existing concept in column 'definitionStatusId'"),
	EMPTY_REFSET_MEMBER_FIELD("Reference set member field is empty"),
	ENCOUNTER_UNKNOWN_RELEASE_FILE("Encountered unknown release file"), 
	UNEXPECTED_COMPONENT_CATEGORY("Unexpected component category found in column of the release file"),
	MISSING_DESCRIPTION_TERM("Description term is missing from release file"), 
	RELATIONSHIP_SOURCE_DESTINATION_EQUALS("Relationships source and target id are equivalent"), 
	MISSING_ACTIVE_FLAG("Missing active flag from release file"), 
	INVALID_UUID("Invalid UUID in release file");
	
	private final String label;
	
	private Rf2ValidationDefects(final String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}
	
	@Override
	public String toString() {
		return getLabel();
	}
	
}
