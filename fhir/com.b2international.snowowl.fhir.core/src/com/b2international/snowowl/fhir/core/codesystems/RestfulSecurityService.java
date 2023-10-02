/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * FHIR RESTful security code system
 * 
 * @since 8.0.0
 */
@ResourceNarrative("Types of security services used with FHIR.")
public enum RestfulSecurityService implements FhirCodeSystem {
	
	OAUTH("OAuth"),
	
	SMART_ON_FHIR("SMART-on-FHIR"),
	
	NTLM("NTLM"),
	
	BASIC("Basic"),
	
	KERBEROS("Kerberos"),
	
	CERTIFICATES("Certificates");
	
	public final static String CODE_SYSTEM_URI = "http://terminology.hl7.org/CodeSystem/restful-security-service";
	
	private String codeValue;
	
	private RestfulSecurityService(final String codeValue) {
		this.codeValue = codeValue;
	}
	
	public String getDisplayName() {
		return codeValue;
	}
	
	@Override
	public String getCodeValue() {
		return codeValue;
	}
	
	@Override
	public String getCodeSystemUri() {
		return CODE_SYSTEM_URI;
	}
	
	@JsonCreator
    public static RestfulSecurityService forValue(String value) {
		return RestfulSecurityService.valueOf(value.toUpperCase().replaceAll("-", "_"));
    }
	
}
