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

import com.b2international.snowowl.fhir.core.ResourceNarrative;

/**
 * FHIR Reference Version Rules code system
 * https://www.hl7.org/fhir/codesystem-reference-version-rules.html#ReferenceVersionRules
 * 
 * @since 7.1
 */
@ResourceNarrative("Whether a reference needs to be version specific or version independent, or whether either can be used.")
public enum ReferenceVersionRules implements FhirCodeSystem {
	
	//The reference may be either version independent or version specific
	EITHER("Either Specific or Independent"),
	
	//The reference must be version independent
	INDEPENDENT("Version independent"),
	
	//The reference must be version specific
	SPECIFIC("Version Specific");
	
	public final static String CODE_SYSTEM_URI = "http://hl7.org/fhir/reference-version-rules"; //$NON-NLS-N$
	
	private String displayName;
	
	private ReferenceVersionRules(String displayName) {
		this.displayName = displayName;
	}
	
	public String getCodeValue() {
		return name().toLowerCase();
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
