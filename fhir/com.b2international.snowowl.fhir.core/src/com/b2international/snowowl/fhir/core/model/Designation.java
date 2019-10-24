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
package com.b2international.snowowl.fhir.core.model;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

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
 * @since 6.4
 */
@JsonDeserialize(builder = Designation.Builder.class)
@JsonPropertyOrder({"language", "use", "value"})
public final class Designation {
	
	//The language code this designation is defined for (0..1)
	private Code language;
	
	//A code that details how this designation would be used (0..1)
	private Coding use;
	
	//The text value for this designation (1..1)
	@NotEmpty
	private String value;
	
	Designation(final Code languageCode, final Coding use, final String value) {
		this.language = languageCode;
		this.use = use;
		this.value = value;
	}
	
	public Code getLanguageCode() {
		return language;
	}
	
	public String getLanguage() {
		return language.getCodeValue();
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
	
	@JsonPOJOBuilder(withPrefix="")
	public static class Builder extends ValidatingBuilder<Designation>{
		
		private Code language;
		private Coding use;
		private String value;

		public Builder languageCode(final String languageCode) {
			this.language = new Code(languageCode);
			return this;
		}
		
		public Builder language(final Code languageCode) {
			this.language = languageCode;
			return this;
		}
		
		public Builder language(final String language) {
			this.language = new Code(language);
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
			return new Designation(language, use, value);
		}
	}
	
}
