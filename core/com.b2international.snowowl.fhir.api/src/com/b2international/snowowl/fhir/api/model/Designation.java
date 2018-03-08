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
package com.b2international.snowowl.fhir.api.model;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.core.annotation.Order;

import com.b2international.snowowl.fhir.api.model.dt.Coding;

/**
 * This class represents a FHIR designation.
 * The class is capable of providing a bean that will be serialized into:
 * <pre>{@code
 *	{"name" : "languageCode", "valueCode" : "uk"},
 *	   	{"name" : "value", "valueString" : "whatever string this is"},
 *		{"name": "use", 
 *		"valueCoding" : {
 *			"code" : "code",
 *			"systemUri" : "systemUri",
 *			"version" : "version",
 *			"display" : null,
 *			"userSelected" : false
 *		}
 *	</pre>
 *
 * @see <a href="https://www.hl7.org/fhir/codesystem-operations.html#4.7.15.2.1">FHIR:CodeSystem:Operations</a>
 * @since 6.3
 */
public class Designation extends FhirModel {
	
	//The language code this designation is defined for (0..1)
	@Order(value=1)
	@FhirDataType(type = FhirType.CODE)
	private String language;
	
	//A code that details how this designation would be used (0..1)
	@Order(value=2)
	private Coding use;
	
	//The text value for this designation (1..1)
	@NotEmpty
	@Order(value=3)
	private String value;
	
	Designation(final String language, final Coding use, final String value) {
		this.language = language;
		this.use = use;
		this.value = value;
	}
	
	public String getLanguage() {
		return language;
	}

	public Coding getUse() {
		return use;
	}

	public String getValue() {
		return value;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder extends ModelValidator<Designation>{
		
		private String languageCode;
		private Coding use;
		private String value;

		public Builder languageCode(final String languageCode) {
			this.languageCode = languageCode;
			return this;
		}
		
		public Builder use(final Coding use) {
			this.use = use;
			return this;
		}
		
		public Builder value(final String value) {
			this.value = value;
			return this;
		}

		protected Designation doBuild() {
			return new Designation(languageCode, use, value);
		}
	}
	
}
