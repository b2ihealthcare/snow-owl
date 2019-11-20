/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.codesystems;

/**
 * FHIR Concept Map Group Unmapped Mode
 * @since 7.1
 */
@FhirInternalCodeSystem(
	uri = "http://hl7.org/fhir/conceptmap-unmapped-mode",
	resourceNarrative = "Defines which action to take if there is no match in the group."
)
public enum ConceptMapGroupUnmappedMode implements FhirInternalCode {
	
	PROVIDED("Provided Code"),
	FIXED("Fixed Code"),
	OTHER_MAP("Other Map");

	private String displayName;
	
	private ConceptMapGroupUnmappedMode(String displayName) {
		this.displayName = displayName;
	}
	
	@Override
	public String getCodeValue() {
		return name().toLowerCase().replaceAll("_", "-");
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

}
