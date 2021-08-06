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
import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * FHIR Reference handling policy code system
 * 
 * @since 8.0.0
 */
@ResourceNarrative("A set of flags that defines how references are supported.")
public enum ReferenceHandlingPolicy implements FhirCodeSystem {
	
	LITERAL("Literal References"),
	LOGICAL("Logical References"), 
	RESOLVES("Resolves References"), 
	ENFORCED("Reference Integrity Enforced"), 
	LOCAL("Local References Only");
	
	
	public final static String CODE_SYSTEM_URI = "http://hl7.org/fhir/reference-handling-policy";
	
	private final String displayName;

	private ReferenceHandlingPolicy(String displayName) {
		this.displayName = displayName;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	@Override
	public String getCodeValue() {
		return name().toLowerCase();
	}
	
	@Override
	public String getCodeSystemUri() {
		return CODE_SYSTEM_URI;
	}
	
	@JsonCreator
    public static ReferenceHandlingPolicy forValue(String value) {
		return ReferenceHandlingPolicy.valueOf(value.toUpperCase());
    }

}
