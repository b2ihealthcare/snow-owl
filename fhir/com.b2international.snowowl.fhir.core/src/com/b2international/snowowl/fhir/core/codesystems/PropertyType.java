/*
 * Copyright 2018-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
 * FHIR property type code system
 * 
 * @since 6.4
 */
@FhirInternalCodeSystem(
	uri = "http://hl7.org/fhir/concept-property-type",
	resourceNarrative = "The type of a property value."
)
public enum PropertyType implements FhirInternalCode {
	
	CODE("code"),
	CODING("Coding"), 
	STRING("string"), 
	INTEGER("integer"),
	DECIMAL("decimal"),
	BOOLEAN("boolean"),
	DATETIME("dateTime");
	
	private String codeName;

	private PropertyType(String codeName) {
		this.codeName = codeName;
	}
	
	@Override
	public String getCodeValue() {
		return codeName;
	}
	
	@Override
	public String getDisplayName() {
		return getCodeValue();
	}
	
}
