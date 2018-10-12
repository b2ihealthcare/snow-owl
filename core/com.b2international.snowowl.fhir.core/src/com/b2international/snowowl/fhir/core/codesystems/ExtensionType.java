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
 * FHIR extension type
 * https://www.hl7.org/fhir/extensibility.html#Extension
 * 
 * @since 6.4
 */
@ResourceNarrative("The type of an extension.")
public enum ExtensionType implements FhirCodeSystem {
	
	CODE("code"),
	STRING("string"), 
	INTEGER("integer"),
	DECIMAL("decimal"),
	BOOLEAN("boolean"),
	DATETIME("dateTime"),
	UNSIGNED_INT("unsignedInt"),
	POSITIVE_INT("positiveInt"),
	DATE("date"),
	TIME("time"),
	INSTANT("instant"),
	URI("uri"),
	OID("oid"),
	UUID("uuid"),
	ID("id"),
	MARKDOWN("markdown"),
	BASE64BINARY("base64Binary"),
	CODING("Coding"),
	CODEABLECONCEPT("CodeableConcept"),
	ATTACHMENT("Attachment"),
	IDENTIFIER("Identifier"),
	QUANTITY("Quantity"),
	SAMPLED_DATA("SampledData"),
	RANGE("Range"),
	PERIOD("Period"),
	RATIO("Ratio"),
	HUMAN_NAME("HumanName"),
	ADDRESS("Address"),
	CONTACTPOINT("ContactPoint"),
	TIMING("Timing"),
	REFERENCE("Reference"),
	ANNOTATION("Annotation"),
	SIGNATURE("Signature"),
	META("Meta");
	
	public final static String CODE_SYSTEM_URI = "http://hl7.org/fhir/extension-value-type"; //not a real FHIR code system for whatever reason
	
	private String codeName;

	private ExtensionType(String codeName) {
		this.codeName = codeName;
	}
	
	public String getCodeValue() {
		return codeName;
	}

	@Override
	public String getCodeSystemUri() {
		return CODE_SYSTEM_URI;
	}

	@Override
	public String getDisplayName() {
		return getCodeValue();
	}

}
