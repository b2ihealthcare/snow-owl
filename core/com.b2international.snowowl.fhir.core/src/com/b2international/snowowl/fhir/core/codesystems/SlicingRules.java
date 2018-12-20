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
 * FHIR Slicing Rules
 * https://www.hl7.org/fhir/codesystem-resource-slicing-rules.html#SlicingRules
 * 
 * @since 7.1
 */
@ResourceNarrative("How slices are interpreted when evaluating an instance.")
public enum SlicingRules implements FhirCodeSystem {
	
	//No additional content is allowed other than that described by the slices in this profile.
	CLOSED("Closed"),
	
	//Additional content is allowed anywhere in the list.
	OPEN("Open"), 
	
	/*
	 * Additional content is allowed, but only at the end of the list. 
	 * Note that using this requires that the slices be ordered, which makes it hard to share uses. 
	 * This should only be done where absolutely required.
	 */
	OPENATEND("Open at End");
	
	public final static String CODE_SYSTEM_URI = "http://hl7.org/fhir/resource-slicing-rules"; //$NON-NLS-N$
	
	private String displayName;

	private SlicingRules(String displayName) {
		this.displayName = displayName;
	}
	
	public String getCodeValue() {
		return StringUtils.camelCase(displayName, " ");
	}

	@Override
	public String getCodeSystemUri() {
		return CODE_SYSTEM_URI;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

}
