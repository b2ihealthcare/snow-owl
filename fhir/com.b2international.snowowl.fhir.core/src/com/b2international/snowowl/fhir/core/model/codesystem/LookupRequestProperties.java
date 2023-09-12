/*
 * Copyright 2018-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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

import org.hl7.fhir.r5.model.CodeType;

/**
 * FHIR properties that the client wishes to be returned in the output.
 * <p>
 * In addition to these codes, any value from the common properties defined in
 * code system "Concept Properties" as well as any custom properties declared by
 * the code system itself can be requested.
 * 
 * @since 6.6
 */
public enum LookupRequestProperties {

	// The code system URI that was sent in for convenience (does not appear in the specification)
	SYSTEM("system"),
	// Code system name
	NAME("name"),
	// Code system version
	VERSION("version"),

	// Code display name
	DISPLAY("display"),
	// Designations
	DESIGNATION("designation"),
	// Designations by language code prefix (a complete property code should look like eg. "lang.en")
	@Deprecated
	LANG_X("lang");

	private CodeType codeElement;

	private LookupRequestProperties(final String code) {
		this.codeElement = new CodeType(code);
	}

	public CodeType getCodeElement() {
		return codeElement;
	}
	
	public String getCode() {
		return codeElement.getCode();
	}

	@Override
	public String toString() {
		return getCode();
	}
}
