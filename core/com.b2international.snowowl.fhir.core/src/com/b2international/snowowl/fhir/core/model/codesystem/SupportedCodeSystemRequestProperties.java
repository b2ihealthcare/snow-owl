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
package com.b2international.snowowl.fhir.core.model.codesystem;

import com.b2international.snowowl.fhir.core.codesystems.ConceptPropertyType;

/**
 * FHIR Code system request properties
 * @since 6.6
 */
public enum SupportedCodeSystemRequestProperties implements ConceptProperties {
	
	SYSTEM,
	NAME,
	VERSION,
	DISPLAY,
	DESIGNATION, 
	PARENT, //this is also part of the common concept properties, what gives?
	CHILD; //this is also part of the common concept properties, what gives?
	//LANG.X? where X is the designation language code

	//this is not really a code system
	@Override
	public String getCodeSystemUri() {
		return null;
	}

	@Override
	public String getCodeValue() {
		return name().toLowerCase();
	}

	@Override
	public String getDisplayName() {
		return name().toLowerCase();
	}

	@Override
	public ConceptPropertyType getConceptPropertyType() {
		return null;
	}
	
	
}