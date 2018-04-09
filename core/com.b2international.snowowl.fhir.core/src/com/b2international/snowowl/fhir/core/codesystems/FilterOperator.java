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
 * FHIR Filter Operator Code system
 * 
 * @since 6.3
 */
public enum FilterOperator implements FhirCodeSystem {
	
	//The specified property of the code equals the provided value.
	EQUALS("Equals"),
	
	//Includes all concept ids that have a transitive is-a relationship with the concept Id provided as the value, 
	//including the provided concept itself (i.e. include child codes)
	IS_A("Is A (by subsumption)"),
	
	//Includes all concept ids that have a transitive is-a relationship with the concept Id provided as the value, 
	//excluding the provided concept itself (i.e. include child codes)
	DESCENDENT_OF("Descendent Of (by subsumption)"),
	
	//The specified property of the code does not have an is-a relationship with the provided value.
	IS_NOT_A("Not (Is A) (by subsumption)"),
	
	//The specified property of the code matches the regex specified in the provided value.
	REGEX("Regular Expression"),
	
	//The specified property of the code is in the set of codes or concepts specified in the provided value (comma separated list).
	IN("In Set"),
	
	//The specified property of the code is not in the set of codes or concepts specified in the provided value (comma separated list).
	NOT_IN("Not in Set"),
	
	//Includes all concept ids that have a transitive is-a relationship from the concept Id provided as the value, 
	//including the provided concept itself (e.g. include parent codes)
	GENERALIZES("Generalizes (by Subsumption)"),
	
	//The specified property of the code has at least one value (if the specified value is true; 
	//if the specified value is false, then matches when the specified property of the code has no values)
	EXISTS("Exists");
	
	public final static String CODE_SYSTEM_URI = "http://hl7.org/fhir/filter-operator";
	private String displayName;
	
	private FilterOperator(String displayName) {
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
		
		if (EQUALS==this) {
			return "=";
		} else {
			return name().toLowerCase().replaceAll("_", "-");
		}
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
