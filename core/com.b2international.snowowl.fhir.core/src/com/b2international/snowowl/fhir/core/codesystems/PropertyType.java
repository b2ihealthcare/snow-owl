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
 * FHIR property type code system
 * 
 * @since 6.3
 */
public enum PropertyType implements FhirCodeSystem {
	
	CODE("code"),
	CODING("Coding"), 
	STRING("string"), 
	INTEGER("integer"),
	BOOLEAN("boolean"),
	DATETIME("dateTime");
	
	public final static String CODE_SYSTEM_URI = "http://hl7.org/fhir/concept-property-type";
	
	private String codeName;

	private PropertyType(String codeName) {
		this.codeName = codeName;
	}
	
	@Override
	public Code getCode() {
		return new Code(getCodeValue());
	}
	
	@Override
	public String getCodeValue() {
		return codeName;
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
