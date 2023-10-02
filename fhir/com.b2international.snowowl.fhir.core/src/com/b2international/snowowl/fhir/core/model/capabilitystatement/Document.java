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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.b2international.snowowl.fhir.core.codesystems.DocumentMode;
import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.search.Mandatory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * FHIR Capability statement Document backbone definition.
 * @since 8.0.0
 */
@JsonDeserialize(builder = Document.Builder.class)
public class Document {

	@Valid
	@NotNull
	@Mandatory
	@JsonProperty
	private final Code mode;
	
	@JsonProperty
	private final String documentation;
	
	
	@JsonProperty
	private final Uri profile;
	
	Document(final Code mode, final String documentation, final Uri profile) {
		this.mode = mode;
		this.profile = profile;
		this.documentation = documentation;
	}
	
	public Code getMode() {
		return mode;
	}
	
	public String getDocumentation() {
		return documentation;
	}
	
	public Uri getProfile() {
		return profile;
	}
	
	public static Builder builder() {
		return new Builder();
	}

	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder extends ValidatingBuilder<Document> {
		
		private Code mode;
		private String documentation;
		private Uri profile;
		
		@JsonProperty
		public Builder mode(final Code mode) {
			this.mode = mode;
			return this;
		}
		
		@JsonIgnore
		public Builder mode(final String mode) {
			this.mode = new Code(mode);
			return this;
		}
		
		@JsonIgnore
		public Builder mode(final DocumentMode mode) {
			this.mode = mode.getCode();
			return this;
		}
		
		public Builder documentation(final String documentation) {
			this.documentation = documentation;
			return this;
		}

		public Builder profile(final String profile) {
			this.profile = new Uri(profile);
			return this;
		}
		
		public Builder profile(final Uri profile) {
			this.profile = profile;
			return this;
		}
		
		
		@Override
		protected Document doBuild() {
			return new Document(mode, documentation, profile);
		}
	}
}
