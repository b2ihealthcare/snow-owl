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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.b2international.snowowl.fhir.core.codesystems.DiscriminatorType;
import com.b2international.snowowl.fhir.core.model.Element;
import com.b2international.snowowl.fhir.core.model.Extension;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.search.Mandatory;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * FHIR {@link ElementDefinition} slicing discriminator definition.
 * Designates which child elements are used to discriminate between the slices when processing an instance. 
 * If one or more discriminators are provided, the value of the child elements in the instance data SHALL completely distinguish 
 * which slice the element in the resource matches based on the allowed values for those elements in each of the slices.
 * @since 7.1
 */
public class Discriminator extends Element {

	//How the element value is interpreted when discrimination is evaluated.
	@NotNull
	@Valid
	@Mandatory
	@JsonProperty
	private final Code type;
	
	//A FHIRPath expression, using a restricted subset of FHIRPath, 
	//that is used to identify the element on which discrimination is based.
	@NotNull
	@Mandatory
	@JsonProperty
	private final String path;
	
	protected Discriminator(final String id, 
			@SuppressWarnings("rawtypes") final Collection<Extension> extensions,
			final Code type, 
			final String path) {
		
		super(id, extensions);
		
		this.type = type;
		this.path = path;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder extends Element.Builder<Builder, Discriminator> {
		
		private Code type;
		private String path;
		
		@Override
		protected Builder getSelf() {
			return this;
		}
		
		/**
		 * Type specifies how the element value is interpreted when discrimination is evaluated
		 * @param type
		 * @return builder
		 */
		public Builder type(DiscriminatorType type) {
			this.type = type.getCode();
			return getSelf();
		}
		
		/**
		 * FHIRPath expression, using a restricted subset of FHIRPath,
		 * used to identify the element on which discrimination is based.
		 * @param path
		 * @return builder
		 */
		public Builder path(String path) {
			this.path = path;
			return getSelf();
		}
		
		@Override
		protected Discriminator doBuild() {
			return new Discriminator(id, extensions, type, path);
		}
	}

}
