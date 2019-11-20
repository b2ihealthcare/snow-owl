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
 * FHIR Concept property type code system
 * @since 6.4
 */
@FhirInternalCodeSystem(
	uri = "http://hl7.org/fhir/concept-property-type",
	resourceNarrative = "The type of a property value."
)
public enum ConceptPropertyType implements FhirInternalCode {
	
	CODE(PropertyType.CODE.getCodeValue(), "code (internal reference)"),
	CODING(PropertyType.CODING.getCodeValue(), "Coding (external reference)"), 
	STRING(PropertyType.STRING.getCodeValue(), "string"), 
	INTEGER(PropertyType.INTEGER.getCodeValue(), "integer"),
	BOOLEAN(PropertyType.BOOLEAN.getCodeValue(), "boolean"),
	DATETIME(PropertyType.DATETIME.getCodeValue(), "dateTime");
	
	private String code;
	private String displayName;

	private ConceptPropertyType(String code, String displayName) {
		this.code = code;
		this.displayName = displayName;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	@Override
	public String getCodeValue() {
		return code;
	}
	
}
