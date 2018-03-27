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
package com.b2international.snowowl.fhir.core.model;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.fhir.core.model.conversion.Order;
import com.b2international.snowowl.fhir.core.model.conversion.SerializableParametersConverter;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.lookup.ParametersModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

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
@JsonDeserialize(converter=SerializableParametersConverter.class)
@JsonInclude(Include.NON_EMPTY) //covers nulls as well
public class Designation extends ParametersModel {
	
	//The language code this designation is defined for (0..1)
	@Order(value=1)
	private Code language;
	
	//A code that details how this designation would be used (0..1)
	@Order(value=2)
	private Coding use;
	
	//The text value for this designation (1..1)
	@NotEmpty
	@Order(value=3)
	private String value;
	
	Designation(final Code language, final Coding use, final String value) {
		this.language = language;
		this.use = use;
		this.value = value;
	}
	
	public Code getLanguage() {
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
	
	public static class Builder extends ValidatingBuilder<Designation>{
		
		private Code languageCode;
		private Coding use;
		private String value;

		public Builder languageCode(final String languageCode) {
			this.languageCode = new Code(languageCode);
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

		@Override
		protected Designation doBuild() {
			return new Designation(languageCode, use, value);
		}
	}
	
}
