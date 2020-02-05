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
package com.b2international.snowowl.fhir.core.codesystems;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.fhir.core.ResourceNarrative;

/**
 * FHIR Issue Severity Code system
 * 
 * @since 6.4
 */
@ResourceNarrative("How the issue affects the success of the action.")
public enum IssueSeverity implements FhirCodeSystem {
	
	//The issue caused the action to fail, and no further checking could be performed.
	FATAL,
	
	//The issue is sufficiently important to cause the action to fail.
	ERROR,
	
	//Warning The issue is not important enough to cause the action to fail, but may cause it to be performed suboptimally or in a way that is not as desired.
	WARNING,
	
	//The issue has no relation to the degree of success of the action.
	INFORMATION;
	
	public final static String CODE_SYSTEM_URI = "http://hl7.org/fhir/issue-severity";
	
	public String getDisplayName() {
		return StringUtils.capitalizeFirstLetter(name().toLowerCase());
	}
	
	@Override
	public String getCodeValue() {
		return name().toLowerCase();
	}
	
	@Override
	public String getCodeSystemUri() {
		return CODE_SYSTEM_URI;
	}

}
