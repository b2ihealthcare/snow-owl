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
 * FHIR Code system hierarchy meaning code system
 * @since 6.3
 */
public enum CodeSystemHierarchyMeaning implements FhirCodeSystem {
	
	GROUPED_BY("Grouped By"),
	IS_A("Is-A"), 
	PART_OF("Part Of"), 
	CLASSIFIED_WITH("Classified With");
	
	public final static String CODE_SYSTEM_URI = "https://www.hl7.org/fhir/codesystem-hierarchy-meaning.html";
	
	private String displayName;

	private CodeSystemHierarchyMeaning(String displayName) {
		this.displayName = displayName;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	@Override
	public Code getCode() {
		return new Code(getCodeValue());
	}
	
	@Override
	public String getCodeValue() {
		return name().toLowerCase().replace("_", "-");
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
