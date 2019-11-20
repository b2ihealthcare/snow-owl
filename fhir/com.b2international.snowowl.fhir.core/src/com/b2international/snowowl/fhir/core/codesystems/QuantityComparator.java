/*
 * Copyright 2018-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

/**
 * FHIR property type code system
 * 
 * @since 6.4
 */
@FhirInternalCodeSystem(
	uri = "http://hl7.org/fhir/quantity-comparator",
	resourceNarrative = "How the Quantity should be understood and represented."
)
public enum QuantityComparator implements FhirInternalCode {
	
	LESS_THAN("<", "Less than"),
	LESS_OR_EQUAL_TO("<=", "Less or Equal to"), 
	GREATER_OR_EQUAL_TO(">=", "Greater or Equal to"), 
	GREATER_THAN(">", "Greater than");
	
	private final String code;
	private final String displayName;

	private QuantityComparator(String code, String displayName) {
		this.code = code;
		this.displayName = displayName;
	}
	
	@Override
	public String getCodeValue() {
		return code;
	}
	
	@Override
	public String getDisplayName() {
		return displayName;
	}

}
