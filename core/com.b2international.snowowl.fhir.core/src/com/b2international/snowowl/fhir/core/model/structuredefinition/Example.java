/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.fhir.core.model.Element;
import com.b2international.snowowl.fhir.core.model.Extension;
import com.b2international.snowowl.fhir.core.model.typedproperty.StringProperty;
import com.b2international.snowowl.fhir.core.model.typedproperty.TypedProperty;
import com.b2international.snowowl.fhir.core.search.Summary;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A sample value for this element demonstrating the type of information that would typically be found in the element.
 * 
 * @since 7.1
 */
public class Example extends Element {
	
	@NotNull
	@Summary
	@JsonProperty
	private final String label;
	
	@NotNull
	@Valid
	@Summary
	@JsonProperty
	private TypedProperty<?> value;
	
	protected Example(final String id, 
			@SuppressWarnings("rawtypes") final Collection<Extension> extensions, 
			final String label,
			final TypedProperty<?> value) {
		
		super(id, extensions);
		this.label = label;
		this.value = value;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder extends Element.Builder<Builder, Example> {
		
		private String label;
		private TypedProperty<?> value;
		
		@Override
		protected Builder getSelf() {
			return this;
		}
		
		public Builder label(String label) {
			this.label = label;
			return getSelf();
		}
		
		public Builder value(String stringValue) {
			this.value = new StringProperty(stringValue);
			return getSelf();
		}
		
		@Override
		protected Example doBuild() {
			return new Example(id, extensions, label, value);
		}
	}

}
