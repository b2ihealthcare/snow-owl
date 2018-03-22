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

/**
 * FHIR Publication status code system
 * 
 * @since 6.3
 */
public enum PublicationStatus implements FhirCodeSystem {
	
	//This resource is still under development and is not yet considered to be ready for normal use.
	DRAFT,
	
	//This resource is ready for normal use.
	ACTIVE,
	
	//This resource has been withdrawn or superseded and should no longer be used.
	RETIRED,
	
	//The authoring system does not know which of the status values currently applies for this resource.
	//Note: This concept is not to be used for "other" - one of the listed statuses is presumed to apply, 
	//it's just not known which one.
	UNKNOWN;
	
	public static final String CODE_SYSTEM_URI = "http://hl7.org/fhir/publication-status";

	@Override
	public Code getCode() {
		return new Code(name().toLowerCase());
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
