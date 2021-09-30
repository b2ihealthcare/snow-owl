package com.b2international.snowowl.fhir.core.model.capabilitystatement;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.b2international.snowowl.fhir.core.codesystems.EventCapabilityMode;
import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.search.Mandatory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * FHIR Capability statement SupportedMessage backbone definition.
 * @since 8.0.0
 */
@JsonDeserialize(builder = SupportedMessage.Builder.class)
public class SupportedMessage {
	
	@Mandatory
	@Valid
	@NotNull
	@JsonProperty
	private final Code mode;
	
	@Mandatory
	@Valid
	@JsonProperty
	private final Uri definition;
	
	SupportedMessage(final Code mode, final Uri definition) {
		this.mode = mode;
		this.definition = definition;
	}
	
	public Code getMode() {
		return mode;
	}
	
	public Uri getDefinition() {
		return definition;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder extends ValidatingBuilder<SupportedMessage> {
		
		private Code mode;
		private Uri definition;
		
		@JsonProperty
		public Builder mode(final Code mode) {
			this.mode = mode;
			return this;
		}
		
		@JsonIgnore
		public Builder mode(final EventCapabilityMode mode) {
			this.mode = mode.getCode();
			return this;
		}
		 
		@JsonIgnore
		public Builder mode(final String mode) {
			this.mode = new Code(mode);
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
		
		@Override
		protected SupportedMessage doBuild() {
			return new SupportedMessage(mode, definition);
		}
	}


}
