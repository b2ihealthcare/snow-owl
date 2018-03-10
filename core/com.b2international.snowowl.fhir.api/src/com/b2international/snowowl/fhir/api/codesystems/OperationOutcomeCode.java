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
package com.b2international.snowowl.fhir.api.codesystems;

import com.b2international.snowowl.fhir.api.model.dt.Code;

/**
 * Operation outcome codesystem.
 * 
 * @see <a href="http://hl7.org/fhir/operationoutcome-definitions.html#OperationOutcome.issue">FHIR:Operation Outcome:issue</a>
 *  
 * @since 6.3
 */
public enum OperationOutcomeCode implements FhirCodeSystem {
	
	MSG_AUTH_REQUIRED("You must authenticate before you can use this service");
	
	public final String CODE_SYSTEM_URI = "http://hl7.org/fhir/operation-outcome";
	private String displayName;
	
	private OperationOutcomeCode(String displayName) {
		this.displayName = displayName;
	}
	
	@Override
	public Code getCode() {
		return new Code(name().toLowerCase());
	}
	
	@Override
	public String getCodeValue() {
		return name().toLowerCase();
	}

	
	public String displayName() {
		return displayName;
	}

}
