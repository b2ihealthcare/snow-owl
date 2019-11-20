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
 * usual	 The identifier recommended for display and use in real-world interactions.
 * official  The identifier considered to be most trusted for the identification of this item.
 * temp temporary identifier.
 * secondary An identifier that was assigned in secondary use - it serves to identify the object in a relative context, but cannot be consistently assigned to the same object again in a different context.
 * 
 * @see <a href="https://www.hl7.org/fhir/codesystem-identifier-use.html">FHIR:CodeSystem:Terminology</a>
 * @since 6.4
 */
@FhirInternalCodeSystem(
	uri = "http://hl7.org/fhir/identifier-use",
	resourceNarrative = "Identifies the purpose for this identifier, if known."
)
public enum IdentifierUse implements FhirInternalCode {
	
	USUAL,
	OFFICIAL,
	TEMP,
	SECONDARY;
	
}
