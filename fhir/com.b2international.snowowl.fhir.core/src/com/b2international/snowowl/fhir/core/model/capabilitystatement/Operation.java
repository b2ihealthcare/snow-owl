/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.model.capabilitystatement;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.search.Mandatory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * FHIR Capability statement Operation backbone definition.
 * @since 8.0.0
 */
@JsonDeserialize(builder = Operation.Builder.class)
public class Operation {
	
	@NotEmpty
	@Mandatory
	@JsonProperty
	private final String name;
	
	@JsonProperty
	private final Uri definition;
	
	@JsonProperty
	private final String documenation;
	
	Operation(final String name, final Uri definition, final String documenation) {
		this.name = name;
		this.definition = definition;
		this.documenation = documenation;
	}
	
	public String getName() {
		return name;
	}
	
	public Uri getDefinition() {
		return definition;
	}
	
	public String getDocumenation() {
		return documenation;
	}
	
	public static Builder builder() {
		return new Builder();
	}

	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder extends ValidatingBuilder<Operation> {
		
		private String name;
		private Uri definition;
		private String documenation;
		
		public Builder name(final String name) {
			this.name = name;
			return this;
		}

		public Builder definition(final String definition) {
			this.definition = new Uri(definition);
			return this;
		}
		
		public Builder definition(final Uri definition) {
			this.definition = definition;
			return this;
		}
		
		public Builder documenation(final String documenation) {
			this.documenation = documenation;
			return this;
		}
		
		@Override
		protected Operation doBuild() {
			return new Operation(name, definition, documenation);
		}
	}
}
