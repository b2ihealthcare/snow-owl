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
package com.b2international.snowowl.fhir.core.model.structuredefinition;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.b2international.snowowl.fhir.core.model.dt.Id;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * FHIR Mapping BackBone definition in the {@link StructureDefinition} domain object.
 * @since 7.1
 */
public class Mapping {

	@Valid
	@NotNull
	@JsonProperty
	private final Id identity;
	
	@Valid
	@JsonProperty
	private final Uri uri;
	
	@JsonProperty
	private final String name;
	
	@JsonProperty
	private final String comment;
	
	Mapping(final Id identity, final Uri uri, final String name, final String comment) {
		this.identity = identity;
		this.uri = uri;
		this.name = name;
		this.comment = comment;
	}
	
	@AssertTrue(message = "Name or/and URI needs to be set.")
	private boolean isValid() {

		if (name == null && uri == null) {
			return false;
		}
		return true;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder extends ValidatingBuilder<Mapping> {
		
		private Id identity;
		
		private Uri uri;
		
		private String name;
		
		private String comment;
		
		public Builder identity(final String identity) {
			this.identity = new Id(identity);
			return this;
		}
		
		public Builder identity(Id identityId) {
			this.identity = identityId;
			return this;
		}

		public Builder uri(final String uri) {
			this.uri = new Uri(uri);
			return this;
		}
		
		public Builder uri(final Uri uri) {
			this.uri = uri;
			return this;
		}
		
		public Builder name(final String name) {
			this.name = name;
			return this;
		}
		
		public Builder comment(final String comment) {
			this.comment = comment;
			return this;
		}

		@Override
		protected Mapping doBuild() {
			return new Mapping(identity, uri, name, comment);
		}
		
	}
	
}
