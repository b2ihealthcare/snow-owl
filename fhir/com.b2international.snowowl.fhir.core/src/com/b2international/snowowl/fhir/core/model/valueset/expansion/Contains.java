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
package com.b2international.snowowl.fhir.core.model.valueset.expansion;

import java.util.Collection;

import javax.validation.Valid;

import com.b2international.snowowl.fhir.core.model.Designation;
import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Sets;

/**
 * The codes that are contained in the value set expansion.
 * 
 * @since 6.7
 */
public class Contains {
	
	@Valid
	@JsonProperty
	private final Uri system;
	
	@JsonProperty("abstract")
	private final Boolean isAbstract;
	
	@JsonProperty
	private final Boolean inactive;
	
	@JsonProperty
	private final String version;
	
	@Valid
	@JsonProperty
	private final Code code;
	
	@JsonProperty
	private final String display;
	
	@Valid
	@JsonProperty("designation")
	private Collection<Designation> designations;
	
	@Valid
	@JsonProperty
	private Collection<Contains> contains;

	Contains(Uri system, Boolean isAbstract, Boolean inactive, String version, Code code, String display, Collection<Designation> designations,
			Collection<Contains> contains) {
		this.system = system;
		this.isAbstract = isAbstract;
		this.inactive = inactive;
		this.version = version;
		this.code = code;
		this.display = display;
		this.designations = designations;
		this.contains = contains;
	}
	
	public static Builder builder() {
		return new Builder();
	}

	public static class Builder extends ValidatingBuilder<Contains> {
		
		private Uri system;
		
		private Boolean isAbstract;
		
		private Boolean inactive;
		
		private String version;
		
		private Code code;
		
		private String display;
		
		private Collection<Designation> designations =  Sets.newHashSet();;
		
		private Collection<Contains> contains = Sets.newHashSet();;
		
		public Builder system(final String systemString) {
			this.system = new Uri(systemString);
			return this;
		}
		
		public Builder system(final Uri system) {
			this.system = system;
			return this;
		}
		
		public Builder isAbstract(Boolean isAbstract) {
			this.isAbstract = isAbstract;
			return this;
		}
		
		public Builder inactive(Boolean inactive) {
			this.inactive = inactive;
			return this;
		}
		
		public Builder version(String version) {
			this.version = version;
			return this;
		}
		
		public Builder code(String codeString) {
			this.code = new Code(codeString);
			return this;
		}
		
		public Builder code(Code code) {
			this.code = code;
			return this;
		}
		
		public Builder display(String display) {
			this.display = display;
			return this;
		}
		
		public Builder addDesignation(final Designation designation) {
			this.designations.add(designation);
			return this;
		}
		
		public Builder addContains(Contains content) {
			contains.add(content);
			return this;
		}
		
		@Override
		protected Contains doBuild() {
			return new Contains(system, isAbstract, inactive, version, code, display, designations, contains);
		}
	}

}
