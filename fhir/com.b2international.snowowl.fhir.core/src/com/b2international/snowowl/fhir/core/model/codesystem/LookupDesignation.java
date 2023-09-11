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

import java.util.List;

import org.hl7.fhir.r5.model.Coding;

import com.b2international.commons.collections.Collections3;
import com.google.common.collect.ImmutableList;

import ca.uhn.fhir.model.primitive.CodeDt;

/**
 * This class represents a FHIR designation in a lookup result object.
 * <p>
 * The class is capable of providing a bean that will be serialized into:
 * <pre>
 * {
 *   {"name" : "languageCode", "valueCode" : "uk"},
 *   {"name" : "value", "valueString" : "whatever string this is"},
 *   {"name": "use", "valueCoding" : {
 *     "code" : "code",
 *     "systemUri" : "systemUri",
 *     "version" : "version",
 *     "display" : null,
 *     "userSelected" : false
 *   }
 * }
 * </pre>
 *
 * @see <a href="https://www.hl7.org/fhir/codesystem-operations.html#4.7.15.2.1">FHIR:CodeSystem:Operations</a>
 * @since 6.4
 */
public final class LookupDesignation {
	
	// The language code this designation is defined for (0..1)
	private CodeDt language;
	
	// A code that details how this designation would be used (0..1)
	private Coding use;
	
	// Additional codes that detail how this designation would be used (if there is more than one) (0..*)
	private List<Coding> additionalUse = ImmutableList.of();
	
	//The text value for this designation (1..1)
	private String value;

	public CodeDt getLanguage() {
		return language;
	}

	public void setLanguage(CodeDt language) {
		this.language = language;
	}

	public Coding getUse() {
		return use;
	}

	public void setUse(Coding use) {
		this.use = use;
	}

	public List<Coding> getAdditionalUse() {
		return additionalUse;
	}

	public void setAdditionalUse(Iterable<Coding> additionalUse) {
		this.additionalUse = Collections3.toImmutableList(additionalUse);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
