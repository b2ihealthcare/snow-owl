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

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.b2international.snowowl.fhir.core.model.dt.Reference;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.search.Mandatory;
import com.b2international.snowowl.fhir.core.search.Summary;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * FHIR Code system capability statement implementation backbone definition.
 * @since 8.0.0
 */
@JsonDeserialize(builder = Implementation.Builder.class)
public class Implementation {

	@NotEmpty
	@Mandatory
	@JsonProperty
	private final String description;
	
	@Valid
	@Summary
	@JsonProperty
	private final Uri url;
	
	@Summary
	@JsonProperty
	private final Reference custodian;
	
	Implementation(final String description, final Uri url, final Reference custodian) {
		this.description = description;
		this.url = url;
		this.custodian = custodian;
	}
	
	public String getDescription() {
		return description;
	}
	
	public Uri getUrl() {
		return url;
	}
	
	public Reference getCustodian() {
		return custodian;
	}
	
	public static Builder builder() {
		return new Builder();
	}

	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder extends ValidatingBuilder<Implementation> {
		
		private String description;
		
		private Uri url;

		private Reference custodian;
		
		public Builder description(final String description) {
			this.description = description;
			return this;
		}

		public Builder url(final String url) {
			this.url = new Uri(url);
			return this;
		}

		public Builder url(final Uri url) {
			this.url = url;
			return this;
		}

		public Builder custodian(final Reference custodian) {
			this.custodian = custodian;
			return this;
		}

		@Override
		protected Implementation doBuild() {
			return new Implementation(description, url, custodian);
		}
		
	}
	
	
}
