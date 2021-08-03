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

import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.search.Mandatory;
import com.b2international.snowowl.fhir.core.search.Summary;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * FHIR Capability statement Rest backbone definition.
 * @since 8.0.0
 */
@JsonDeserialize(builder = Rest.Builder.class)
public class Rest {
	
	@Mandatory
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
	@JsonProperty
	private final Collection<Messaging> messaging;
	
	Rest(final Code mode, 
			final String documentation, 
			final Security security,
			final Collection<Resource> resources,
			final Collection<Interaction> interactions,
			final Collection<Messaging> messaging) {
		
		this.mode = mode;
		this.documentation = documentation;
		this.security = security;
		this.resources = resources;
		this.interactions = interactions;
		this.messaging = messaging;
	}
	
	public static Builder builder() {
		return new Builder();
	}

	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder extends ValidatingBuilder<Rest> {
		
		@Override
		protected Rest doBuild() {
			//return new Rest(name, version, releaseDate);
			return null;
		}
	}
}
