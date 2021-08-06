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

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.fhir.core.ResourceNarrative;
import com.b2international.snowowl.fhir.core.model.capabilitystatement.SearchParam;
import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * FHIR Search parameter type code system
 * 
 * @since 8.0.0
 * @see SearchParam
 */
@ResourceNarrative("Data types allowed to be used for search parameters.")
public enum SearchParamType implements FhirCodeSystem {
	
	NUMBER,
	DATE,
	STRING,
	TOKEN,
	REFERENCE,
	COMPOSITE,
	QUANTITY,
	URI,
	SPECIAL;
	
	
	public final static String CODE_SYSTEM_URI = "http://hl7.org/fhir/search-param-type";
	
	public String getDisplayName() {
		if (this == DATE) {
			return "Date/DateTime";
		} else {
			return StringUtils.capitalizeFirstLetter(name().toLowerCase());
		}
	}
	
	@Override
	public String getCodeValue() {
		return name().toLowerCase();
	}
	
	@Override
	public String getCodeSystemUri() {
		return CODE_SYSTEM_URI;
	}
	
	@JsonCreator
    public static SearchParamType forValue(String value) {
		return SearchParamType.valueOf(value.toUpperCase());
    }

}
