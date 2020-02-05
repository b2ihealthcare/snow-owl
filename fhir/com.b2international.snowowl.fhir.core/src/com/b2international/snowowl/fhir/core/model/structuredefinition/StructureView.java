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

import java.util.Collection;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

/**
 * FHIR {@link StructureDefinition} snapshot/differential backBone definition.
 * @since 7.1
 */
public class StructureView {
	
	@NotEmpty
	@JsonProperty("element")
	private final Collection<ElementDefinition> elementDefinitions;
	
	StructureView(final Collection<ElementDefinition> elementDefinitions) {
		this.elementDefinitions = elementDefinitions;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder extends ValidatingBuilder<StructureView> {
		
		private Collection<ElementDefinition> elementDefinitions = Lists.newArrayList();
		
		public Builder elementDefinitions(Collection<ElementDefinition> elementDefinitions) {
			this.elementDefinitions = elementDefinitions;
			return this;
		}

		public Builder addElementDefinition(final ElementDefinition elementDefinition) {
			this.elementDefinitions.add(elementDefinition);
			return this;
		}
		
		@Override
		protected StructureView doBuild() {
			return new StructureView(elementDefinitions);
		}
	}
}
