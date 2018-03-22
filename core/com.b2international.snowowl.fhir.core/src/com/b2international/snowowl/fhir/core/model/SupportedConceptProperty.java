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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.b2international.snowowl.fhir.core.codesystems.ConceptProperties;
import com.b2international.snowowl.fhir.core.model.conversion.Order;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * FHIR Code System supported property
 * 
 * @since 6.3
 */
public class SupportedConceptProperty {
	
	//Identifies the property returned (1..1)
	@Order(value=1)
	@Valid
	@NotNull
	private final Code code;
	
	/*
	 * The value of the property returned (0..1)
	 * code | Coding | string | integer | boolean | dateTime
	 */
	@Order(value=2)
	private final Uri uri;
	
	//Human Readable representation of the property value (e.g. display for a code) 0..1
	@Order(value=3)
	private final String description;
	
	@Order(value=4)
	@Valid
	@NotNull
	private final Code type;
	
	SupportedConceptProperty(final Code code, final Uri uri, final String description, final Code type) {
		this.code = code;
		this.uri = uri;
		this.description = description;
		this.type = type;
	}
	
	public Code getCode() {
		return code;
	}
	
	@JsonIgnore
	public String getCodeValue() {
		return code.getCodeValue();
	}

	public Uri getUri() {
		return uri;
	}

	public String getDescription() {
		return description;
	}
	
	public Code getType() {
		return type;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static Builder builder(ConceptProperties conceptProperty) {
		return new Builder()
			.code(conceptProperty.getCode())
			.uri(conceptProperty.getUri())
			.description(conceptProperty.getDisplayName())
			.type(conceptProperty.getType());
	}
	
	public static class Builder extends ValidatingBuilder<SupportedConceptProperty> {
		
		private Code code;
		private Uri uri;
		private String description;
		private Code type;

		public Builder code(final Code code) {
			this.code = code;
			return this;
		}
		
		public Builder uri(final Uri uri) {
			this.uri = uri;
			return this;
		}
		
		public Builder type(final Code type) {
			this.type = type;
			return this;
		}

		public Builder description(final String description) {
			this.description = description;
			return this;
		}
		
		@Override
		protected SupportedConceptProperty doBuild() {
			return new SupportedConceptProperty(code, uri, description, type);
		}
	}

}
