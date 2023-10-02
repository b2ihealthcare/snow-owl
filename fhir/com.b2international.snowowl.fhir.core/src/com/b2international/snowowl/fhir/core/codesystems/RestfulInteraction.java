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
 * FHIR RESTful interaction code system
 * 
 * @since 8.0.0
 */
@ResourceNarrative("The set of interactions defined by the RESTful part of the FHIR specification.")
public enum RestfulInteraction implements FhirCodeSystem {
	
	READ,
	
	VREAD,
	
	UPDATE,
	
	PATCH,
	
	DELETE,
	
	HISTORY,
	
	HISTORY_INSTANCE,
	
	HISTORY_TYPE,
	
	HISTORY_SYSTEM,
	
	CREATE,
	
	SEARCH,
	
	SEARCH_TYPE,
	
	SEARCH_SYSTEM,
	
	CAPABILITIES,
	
	TRANSACTION,
	
	BATCH,
	
	OPERATION;
	
	public final static String CODE_SYSTEM_URI = "http://hl7.org/fhir/restful-interaction";
	
	public String getDisplayName() {
		return name().toLowerCase().replaceAll("_", "-");
	}
	
	@Override
	public String getCodeValue() {
		return name().toLowerCase().replaceAll("_", "-");
	}
	
	@Override
	public String getCodeSystemUri() {
		return CODE_SYSTEM_URI;
	}
	
	@JsonCreator
    public static RestfulInteraction forValue(String value) {
		return RestfulInteraction.valueOf(value.toUpperCase().replaceAll("-", "_"));
    }
	
}
