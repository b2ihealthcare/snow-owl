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
package com.b2international.snowowl.fhir.core.model.capabilitystatement;

import java.util.Collection;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.b2international.snowowl.fhir.core.codesystems.RestfulCapabilityMode;
import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.search.Mandatory;
import com.b2international.snowowl.fhir.core.search.Summary;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.Lists;

/**
 * FHIR Capability statement Rest backbone definition.
 * @since 8.0.0
 */
@JsonDeserialize(builder = Rest.Builder.class)
public class Rest {
	
	@Mandatory
	@NotNull
	@Valid
	@JsonProperty
	private final Code mode;

	@Summary
	@JsonProperty
	private final String documentation;
	
	@Summary
	@JsonProperty
	private final Security security;
	
	@Summary
	@Valid
	@JsonProperty("resource")
	private final Collection<Resource> resources;
	
	@Valid
	@JsonProperty("interaction")
	private final Collection<Interaction> interactions;
	
	@Valid
	@JsonProperty("searchParam")
	private final Collection<SearchParam> searchParams;

	@Valid
	@JsonProperty("operation")
	private final Collection<Operation> operations;

	@Valid
	@JsonProperty("compartment")
	private final Collection<Uri> compartments;
	
	Rest(final Code mode, 
			final String documentation, 
			final Security security,
			final Collection<Resource> resources,
			final Collection<Interaction> interactions,
			final Collection<SearchParam> searchParams,
			final Collection<Operation> operations,
			final Collection<Uri> compartments) {
		
		this.mode = mode;
		this.documentation = documentation;
		this.security = security;
		this.resources = resources;
		this.interactions = interactions;
		this.searchParams = searchParams;
		this.operations = operations;
		this.compartments = compartments;
	}
	
	public Code getMode() {
		return mode;
	}
	
	public String getDocumentation() {
		return documentation;
	}
	
	public Security getSecurity() {
		return security;
	}
	
	public Collection<Resource> getResources() {
		return resources;
	}
	
	public Collection<Interaction> getInteractions() {
		return interactions;
	}
	
	public Collection<SearchParam> getSearchParams() {
		return searchParams;
	}
	
	public Collection<Operation> getOperations() {
		return operations;
	}
	
	public Collection<Uri> getCompartments() {
		return compartments;
	}
	
	public static Builder builder() {
		return new Builder();
	}

	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder extends ValidatingBuilder<Rest> {
		
		private Code mode;
		private String documentation;
		private Security security;
		private Collection<Resource> resources;
		private Collection<Interaction> interactions;
		private Collection<SearchParam> searchParams;
		private Collection<Operation> operations;
		private Collection<Uri> compartments;
		
		@JsonProperty
		public Builder mode(final Code mode) {
			this.mode = mode;
			return this;
		}

		@JsonIgnore
		public Builder mode(final RestfulCapabilityMode mode) {
			this.mode = mode.getCode();
			return this;
		}

		@JsonIgnore
		public Builder mode(final String mode) {
			this.mode = new Code(mode);
			return this;
		}
		
		public Builder documentation(final String documentation) {
			this.documentation = documentation;
			return this;
		}

		public Builder security(final Security security) {
			this.security = security;
			return this;
		}
		
		@JsonProperty("resource")
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public Builder resources(Collection<Resource> resources) {
			this.resources = resources;
			return this;
		}
		
		public Builder addResource(final Resource resource) {
			if (resources == null) {
				resources = Lists.newArrayList();
			}
			resources.add(resource);
			return this;
		}
		
		@JsonProperty("interaction")
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public Builder interactions(Collection<Interaction> interactions) {
			this.interactions = interactions;
			return this;
		}
		
		public Builder addInteraction(final Interaction interaction) {
			if (interactions == null) {
				interactions = Lists.newArrayList();
			}
			interactions.add(interaction);
			return this;
		}

		@JsonProperty("searchParam")
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public Builder searchParams(Collection<SearchParam> searchParams) {
			this.searchParams = searchParams;
			return this;
		}
		
		public Builder addSearchParam(final SearchParam searchParam) {
			if (searchParams == null) {
				searchParams = Lists.newArrayList();
			}
			searchParams.add(searchParam);
			return this;
		}

		@JsonProperty("operation")
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public Builder operations(Collection<Operation> operations) {
			this.operations = operations;
			return this;
		}
		
		public Builder addOperation(final Operation operation) {
			if (operations == null) {
				operations = Lists.newArrayList();
			}
			operations.add(operation);
			return this;
		}

		@JsonProperty("compartment")
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public Builder compartments(Collection<Uri> compartments) {
			this.compartments = compartments;
			return this;
		}
		
		public Builder addCompartment(final Uri compartment) {
			if (compartments == null) {
				compartments = Lists.newArrayList();
			}
			compartments.add(compartment);
			return this;
		}
		
		@Override
		protected Rest doBuild() {
			return new Rest(mode, documentation, security, resources, interactions, 
					searchParams, operations, compartments);
		}
	}
}
