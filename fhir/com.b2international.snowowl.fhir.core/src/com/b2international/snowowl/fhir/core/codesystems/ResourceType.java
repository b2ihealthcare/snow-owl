/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.fhir.core.ResourceNarrative;
import com.b2international.snowowl.fhir.core.model.FhirResource;
import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * FHIR resource types code system
 * 
 * @since 8.0.0
 * @see FhirResource
 */
@ResourceNarrative("The supported FHIR resource types.")
public enum ResourceType implements FhirCodeSystem {
	
	BUNDLE("Bundle"),
	
	CODESYSTEM("CodeSystem"),
	
	VALUESET("ValueSet"),
	
	CONCEPTMAP("ConceptMap"),
	
	CAPABILITYSTATEMENT("CapabilityStatement"),
	
	STRUCTUREDEFINITION("StructureDefinition");
	
	public final static String CODE_SYSTEM_URI = "http://hl7.org/fhir/resource-types";
	
	private String codeName;
	
	private ResourceType(String codeName) {
		this.codeName = codeName;
	}
	
	public String getDisplayName() {
		return codeName;
	}
	
	@Override
	public String getCodeValue() {
		return codeName;
	}
	
	@Override
	public String getCodeSystemUri() {
		return CODE_SYSTEM_URI;
	}
	
	@JsonCreator
    public static ResourceType forValue(String value) {
		return ResourceType.valueOf(value.toUpperCase());
    }

}
