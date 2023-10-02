/*
 * Copyright 2018-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.model.dt;

import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * FHIR reference
 * 
 * @see <a href="https://www.hl7.org/fhir/references.html">FHIR:References</a>
 * @since 6.6
 */
@JsonDeserialize(builder = Reference.Builder.class)
public class Reference {
	
	@JsonProperty
	private String reference;
	
	@JsonProperty
	private Identifier identifier;
	
	@JsonProperty
	private String display;

	/**
	 * @param reference
	 * @param identifier
	 * @param display
	 */
	Reference(String reference, Identifier identifier, String display) {
		this.reference = reference;
		this.identifier = identifier;
		this.display = display;
	}

	/**
	 * @return the reference
	 */
	public String getReference() {
		return reference;
	}

	/**
	 * @return the identifier
	 */
	public Identifier getIdentifier() {
		return identifier;
	}

	/**
	 * @return the display
	 */
	public String getDisplay() {
		return display;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder extends ValidatingBuilder<Reference> {
		
		private String reference;
		
		private Identifier identifier;
		
		private String display;
		
		public Builder reference(final String reference) {
			this.reference = reference;
			return this;
		}

		public Builder identifier(final Identifier identifier) {
			this.identifier = identifier;
			return this;
		}
		
		public Builder display(final String display) {
			this.display = display;
			return this;
		}

		@Override
		protected Reference doBuild() {
			return new Reference(reference, identifier, display);
		}
	}
	
}
