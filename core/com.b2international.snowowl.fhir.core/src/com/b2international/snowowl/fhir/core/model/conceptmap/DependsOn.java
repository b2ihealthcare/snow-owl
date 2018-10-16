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
package com.b2international.snowowl.fhir.core.model.conceptmap;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.search.Summary;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * FHIR Concept map dependsOn backbone element
 * <br> Other elements required for the mapping (from context)
 * @since 6.10
 */
public class DependsOn {

	@Valid
	@JsonProperty
	@NotNull
	private final Uri property;
	
	@Valid
	@JsonProperty
	private final Uri system;
	
	@Valid
	@NotNull
	@JsonProperty
	private final Code code;
	
	@Summary
	@JsonProperty
	private final String display;

	public DependsOn(Uri property, Uri system, Code code, String display) {
		
		this.property = property;
		this.system = system;
		this.code = code;
		this.display = display;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder extends ValidatingBuilder<DependsOn> {

		private Uri property;
		private Uri system;
		private Code code;
		private String display;

		public Builder property(final Uri property) {
			this.property = property;
			return this;
		}
		
		public Builder property(final String propertyString) {
			this.property = new Uri(propertyString);
			return this;
		}
		
		public Builder system(final Uri system) {
			this.system = system;
			return this;
		}
		
		public Builder system(final String systemString) {
			this.system = new Uri(systemString);
			return this;
		}
		
		public Builder code(final Code code) {
			this.code = code;
			return this;
		}
		
		public Builder code(final String codeString) {
			this.code = new Code(codeString);
			return this;
		}
		
		public Builder display(final String display) {
			this.display = display;
			return this;
		}
		
		@Override
		protected DependsOn doBuild() {
			
			return new DependsOn(property, system, code, display);
		}
	}
	
}
