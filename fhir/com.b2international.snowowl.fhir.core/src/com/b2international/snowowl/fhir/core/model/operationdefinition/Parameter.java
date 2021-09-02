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
import com.b2international.snowowl.fhir.core.search.Mandatory;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.Lists;

/**
 * FHIR {@link Parameter} definition for {@link OperationDefinition}s.
 * Parameters for the operation/query.
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
	private final String max;
	
	@JsonProperty
	private final String documentation;
	
	@Valid
	@JsonProperty
	private final Code type;
	
	@Valid
	@JsonProperty("targetProfile")
	private final Collection<Uri> targetProfiles;
	
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
			final String max,
			final String documentation,
			final Code type,
			final Collection<Uri> targetProfiles,
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
		this.targetProfiles = targetProfiles;
		this.searchType = searchType;
		this.binding = binding;
		this.parameters = parameters;
	}
	
	public Code getName() {
		return name;
	}
	
	public Code getUse() {
		return use;
	}
	
	public Integer getMin() {
		return min;
	}
	
	public String getMax() {
		return max;
	}
	
	public String getDocumentation() {
		return documentation;
	}
	
	public Code getType() {
		return type;
	}
	
	public Collection<Uri> getTargetProfiles() {
		return targetProfiles;
	}
	
	public Code getSearchType() {
		return searchType;
	}
	
	public Binding getBinding() {
		return binding;
	}
	
	public Collection<Parameter> getParameters() {
		return parameters;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder extends Element.Builder<Builder, Parameter> {
		
		private Code name;
		private Code use;
		private Integer min;
		private String max;
		private String documentation;
		private Code type;
		private Collection<Uri> targetProfiles;
		private Code searchType;
		private Binding binding;
		private Collection<Parameter> parameters;
		
		public Builder name(final Code name) {
			this.name = name;
			return getSelf();
		}
		
		public Builder name(final String name) {
			this.name = new Code(name);
			return getSelf();
		}

		public Builder use(final Code use) {
			this.use = use;
			return getSelf();
		}
		
		public Builder use(final String use) {
			this.use = new Code(use);
			return getSelf();
		}
		
		public Builder min(final Integer min) {
			this.min = min;
			return getSelf();
		}

		public Builder max(final Integer max) {
			this.max = String.valueOf(max);
			return getSelf();
		}
		
		public Builder max(final String max) {
			this.max = max;
			return getSelf();
		}

		public Builder maxInfinite() {
			this.max = "*";
			return getSelf();
		}
		
		public Builder documentation(final String documentation) {
			this.documentation = documentation;
			return getSelf();
		}

		public Builder type(final Code type) {
			this.type = type;
			return getSelf();
		}
		
		public Builder type(final String type) {
			this.type = new Code(type);
			return getSelf();
		}
		
		@JsonProperty("targetProfile")
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public Builder targetProfiles(Collection<Uri> targetProfiles) {
			this.targetProfiles = targetProfiles;
			return getSelf();
		}
		
		public Builder addTargetProfile(Uri targetProfile) {
			
			if (targetProfiles == null) {
				targetProfiles = Lists.newArrayList();
			}
			targetProfiles.add(targetProfile);
			return getSelf();
		}
		
		public Builder searchType(final Code searchType) {
			this.searchType = searchType;
			return getSelf();
		}
		
		public Builder searchType(final String searchType) {
			this.searchType = new Code(searchType);
			return getSelf();
		}

		public Builder binding(final Binding binding) {
			this.binding = binding;
			return getSelf();
		}
		
		@JsonProperty("part")
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public Builder parameters(Collection<Parameter> parameters) {
			this.parameters = parameters;
			return getSelf();
		}
		
		public Builder addParameter(Parameter parameter) {
			
			if (parameters == null) {
				parameters = Lists.newArrayList();
			}
			parameters.add(parameter);
			return getSelf();
		}
		
		
		@Override
		protected Builder getSelf() {
			return this;
		}
		
		@Override
		protected Parameter doBuild() {
			return new Parameter(id, extensions, 
					name, use, min, max, documentation, type, targetProfiles, searchType, binding, parameters);	
		}
	}
}
