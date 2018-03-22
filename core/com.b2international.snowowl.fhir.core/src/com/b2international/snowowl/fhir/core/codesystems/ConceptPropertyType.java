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
package com.b2international.snowowl.fhir.core.codesystems;

import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Uri;

/**
 * FHIR Concept property type code system
 * @since 6.3
 */
public enum ConceptPropertyType implements FhirCodeSystem {
	
	CODE("code", "code (internal reference)"),
	CODING("Coding", "Coding (external reference)"), 
	STRING("string", "string"), 
	INTEGER("integer", "integer"),
	BOOLEAN("boolean", "boolean"),
	DATETIME("dateTime", "dateTime");
	
	public final static String CODE_SYSTEM_URI = "https://www.hl7.org/fhir/concept-property-type.html";
	
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
	public Code getCode() {
		return new Code(code);
	}
	
	@Override
	public String getCodeValue() {
		return code;
	}
	
	@Override
	public Uri getUri() {
		return new Uri(CODE_SYSTEM_URI + "/" + getCodeValue());
	}
	
	@Override
	public String getCodeSystemUri() {
		return CODE_SYSTEM_URI;
	}

}
