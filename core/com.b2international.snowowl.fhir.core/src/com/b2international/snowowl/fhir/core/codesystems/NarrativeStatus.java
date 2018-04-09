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
 * FHIR Narrative status code system
 * 
 * @since 6.3
 */
public enum NarrativeStatus implements FhirCodeSystem {

	//The contents of the narrative are entirely generated from the structured data in the content.
	GENERATED,
	
	//The contents of the narrative are entirely generated from the structured data in the content 
	//and some of the content is generated from extensions
	EXTENSIONS,
	
	//The contents of the narrative may contain additional information not found in the structured data. 
	//Note that there is no computable way to determine what the extra information is, other than by human inspection
	ADDITIONAL,
	
	//The contents of the narrative are some equivalent of "No human-readable text provided in this case"
	EMPTY;

	public static final String CODE_SYSTEM_URI = "http://hl7.org/fhir/narrative-status";
	
	@Override
	public Code getCode() {
		return new Code(name().toLowerCase());
	}
	
	@Override
	public String getCodeValue() {
		return name().toLowerCase();
	}
	
	@Override
	public String getDisplayName() {
		return getCodeValue();
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
