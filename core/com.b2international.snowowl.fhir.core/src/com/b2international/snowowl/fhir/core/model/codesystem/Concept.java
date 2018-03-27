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
package com.b2international.snowowl.fhir.core.model.codesystem;

import java.util.Collection;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.b2international.snowowl.fhir.core.model.Designation;
import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.property.ConceptProperty;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Sets;

/**
 * The concept in the code system
 * @since 6.3
 */
public class Concept {
	
	@Valid
	@NotNull
	@JsonProperty
	private final Code code;
	
	@JsonProperty
	private final String display;
	
	@JsonProperty
	private final String definition;
	
	//TODO probably differently serialized :-(
	@Valid
	@JsonProperty
	private final Collection<Designation> designation;
	
	@Valid
	@JsonProperty
	private final Collection<ConceptProperty<?>> properties;
	
	@Valid
	@JsonProperty("concept")
	private final Collection<Concept> children;
	
	/**
	 * @param children
	 */
	Concept(Code code, String display, String definition, Collection<Designation> designation, Collection<ConceptProperty<?>> properties, Collection<Concept> children) {
		this.code = code;
		this.display = display;
		this.definition = definition;
		this.designation = designation;
		this.properties = properties;
		this.children = children;
	}

	public static class Builder extends ValidatingBuilder<Concept> {
		
		private Code code;
		
		private String display;
		
		private String definition;
		
		private Collection<Designation> designations = Sets.newHashSet();
		
		private Collection<ConceptProperty<?>> properties = Sets.newHashSet();
		
		private Collection<Concept> children = Sets.newHashSet();
		
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
		
		public Builder addDesignation(final Designation designation) {
			this.designations.add(designation);
			return this;
		}
		
		public Builder addProperties(final ConceptProperty<?> conceptProperty) {
			this.properties.add(conceptProperty);
			return this;
		}
		
		public Builder addChildConcept(final Concept childConcept) {
			this.children.add(childConcept);
			return this;
		}

		@Override
		protected Concept doBuild() {
			return new Concept(code, display, definition, designations, properties, children);
		}
	}

}
