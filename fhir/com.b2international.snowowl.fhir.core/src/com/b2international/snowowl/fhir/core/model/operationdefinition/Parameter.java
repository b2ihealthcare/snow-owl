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
package com.b2international.snowowl.fhir.core.model.operationdefinition;

import java.util.Collection;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.b2international.snowowl.fhir.core.model.Element;
import com.b2international.snowowl.fhir.core.model.Extension;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.model.structuredefinition.Binding;
import com.b2international.snowowl.fhir.core.search.Mandatory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * FHIR {@link Parameter} definition.
 * Defines an appropriate combination of parameters to use when invoking this operation, 
 * to help code generators when generating overloaded parameter sets for this operation.
 * @since 8.0.0
 */
@JsonDeserialize(builder = Parameter.Builder.class)
public class Parameter extends Element {

	@NotNull
	@Valid
	@Mandatory
	@JsonProperty
	private final Code name;

	@NotNull
	@Valid
	@Mandatory
	@JsonProperty
	private final Code use;
	
	@NotNull
	@Mandatory
	@JsonProperty
	private final Integer min;

	@NotNull
	@Mandatory
	@JsonProperty
	private final Integer max;
	
	@JsonProperty
	private String documentation;
	
	
	@Valid
	@JsonProperty
	private final Code type;
	
	@Valid
	@JsonProperty
	private final Uri targetProfile;
	
	@Valid
	@JsonProperty
	private final Code searchType;
	
	@Valid
	@JsonProperty
	private final Binding binding;
	
	@JsonProperty("part")
	private final Collection<Parameter> parameters;
	
	Parameter(final String id, 
			@SuppressWarnings("rawtypes") final List<Extension> extensions,
			final Code name, 
			final Code use,
			final Integer min,
			final Integer max,
			final String documentation,
			final Code type,
			final Uri targetProfile,
			final Code searchType,
			final Binding binding,
			final Collection<Parameter> parameters) {
		
		super(id, extensions);
		
		this.name = name;
		this.use = use;
		this.min = min;
		this.max = max;
		this.documentation = documentation;
		this.type = type;
		this.targetProfile = targetProfile;
		this.searchType = searchType;
		this.binding = binding;
		this.parameters = parameters;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder extends Element.Builder<Builder, Parameter> {
		
		
		@Override
		protected Builder getSelf() {
			return this;
		}
		
		
		@Override
		protected Parameter doBuild() {
			return null;	
		}
	}
}
