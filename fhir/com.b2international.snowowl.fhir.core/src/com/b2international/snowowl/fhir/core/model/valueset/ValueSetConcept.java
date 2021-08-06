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
package com.b2international.snowowl.fhir.core.model.valueset;

import java.util.Collection;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.b2international.snowowl.fhir.core.model.Designation;
import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * The concept in the value set resource
 * @since 6.7
 */
@JsonDeserialize(builder = ValueSetConcept.Builder.class)
public class ValueSetConcept {
	
	@Valid
	@NotNull
	@JsonProperty
	private final Code code;
	
	@JsonProperty
	private final String display;
	
	@Valid
	@JsonProperty("designation")
	private final Collection<Designation> designations;
	
	ValueSetConcept(Code code, String display, Collection<Designation> designations) {
		this.code = code;
		this.display = display;
		this.designations = designations;
	}
	
	public Code getCode() {
		return code;
	}
	
	public String getDisplay() {
		return display;
	}
	
	public Collection<Designation> getDesignations() {
		return designations;
	}
	
	public static Builder builder() {
		return new Builder();
	}

	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder extends ValidatingBuilder<ValueSetConcept> {
		
		private Code code;
		
		private String display;
		
		private Collection<Designation> designations;
		
		public Builder code(final String codeValue) {
			this.code = new Code(codeValue);
			return this;
		}

		public Builder display(final String display) {
			this.display = display;
			return this;
		}
		
		@JsonProperty("designation")
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public Builder designations(Collection<Designation> designations) {
			this.designations = designations;
			return this;
		}
		
		public Builder addDesignation(final Designation designation) {
			
			if (designations == null) {
				designations = Lists.newArrayList();
			}
			designations.add(designation);
			return this;
		}
		
		@Override
		protected ValueSetConcept doBuild() {
			return new ValueSetConcept(code, display, designations);
		}
	}

}
