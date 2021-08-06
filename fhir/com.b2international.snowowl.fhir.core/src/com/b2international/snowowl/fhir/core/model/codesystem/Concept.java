/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.model.codesystem;

import java.util.Collection;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.b2international.snowowl.fhir.core.model.Designation;
import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.property.ConceptProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.Lists;

/**
 * The concept in the code system
 * @since 6.3
 */
@JsonDeserialize(builder = Concept.Builder.class)
public class Concept {
	
	@Valid
	@NotNull
	@JsonProperty
	private final Code code;
	
	@JsonProperty
	private final String display;
	
	@JsonProperty
	private final String definition;
	
	@Valid
	@JsonProperty("designation")
	@JsonInclude(Include.NON_EMPTY)
	private final Collection<Designation> designations;
	
	@SuppressWarnings("rawtypes")
	@Valid
	@JsonProperty("property")
	@JsonInclude(Include.NON_EMPTY)
	private final Collection<ConceptProperty> properties;
	
	@Valid
	@JsonProperty("concept")
	@JsonInclude(Include.NON_EMPTY)
	private final Collection<Concept> children;
	
	Concept(Code code, String display, String definition, Collection<Designation> designations, @SuppressWarnings("rawtypes") Collection<ConceptProperty> properties, Collection<Concept> children) {
		this.code = code;
		this.display = display;
		this.definition = definition;
		this.designations = designations;
		this.properties = properties;
		this.children = children;
	}
	
	public Code getCode() {
		return code;
	}
	
	public String getDisplay() {
		return display;
	}
	
	public String getDefinition() {
		return definition;
	}
	
	public Collection<Designation> getDesignations() {
		return designations;
	}
	
	public Collection<ConceptProperty> getProperties() {
		return properties;
	}
	
	public Collection<Concept> getChildren() {
		return children;
	}
	
	public static Builder builder() {
		return new Builder();
	}

	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder extends ValidatingBuilder<Concept> {
		
		private Code code;
		
		private String display;
		
		private String definition;
		
		private Collection<Designation> designations;
		
		@SuppressWarnings("rawtypes")
		private Collection<ConceptProperty> properties;
		
		private Collection<Concept> children;
		
		public Builder code(final String codeValue) {
			this.code = new Code(codeValue);
			return this;
		}

		public Builder display(final String display) {
			this.display = display;
			return this;
		}
		
		public Builder definition(final String definition) {
			this.definition = definition;
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
		
		@JsonProperty("property")
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public Builder properties(@SuppressWarnings("rawtypes") Collection<ConceptProperty> properties) {
			this.properties = properties;
			return this;
		}
		
		public Builder addProperty(final ConceptProperty<?> conceptProperty) {
			
			if (properties == null) {
				properties = Lists.newArrayList();
			}
			
			properties.add(conceptProperty);
			return this;
		}
		
		@JsonProperty("concept")
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public Builder children(Collection<Concept> children) {
			this.children = children;
			return this;
		}
		
		public Builder addChildConcept(final Concept childConcept) {

			if (children == null) {
				children = Lists.newArrayList();
			}
			
			children.add(childConcept);
			return this;
		}

		@Override
		protected Concept doBuild() {
			return new Concept(code, display, definition, designations, properties, children);
		}
	}

}
