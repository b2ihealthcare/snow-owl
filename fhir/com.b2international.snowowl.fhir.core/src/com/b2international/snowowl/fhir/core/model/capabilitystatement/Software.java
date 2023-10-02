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

import java.util.Date;
import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.fhir.core.model.Extension;
import com.b2international.snowowl.fhir.core.search.Mandatory;
import com.b2international.snowowl.fhir.core.search.Summary;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * FHIR Capability statement Software backbone definition.
 * @since 8.0.0
 */
@JsonDeserialize(builder = Software.Builder.class)
public class Software extends BackboneElement {
	
	@NotEmpty
	@Mandatory
	@JsonProperty
	private final String name;

	@Summary
	@JsonProperty
	private final String version;
	
	@Summary
	@JsonProperty
	private final Date releaseDate;
	
	Software(final String id, final List<Extension<?>> extensions,
			final List<Extension<?>> modifierExtensions,
			final String name, final String version, final Date releaseDate) {
		
		super(id, extensions, modifierExtensions);
		this.name = name;
		this.version = version;
		this.releaseDate = releaseDate;
	}
	
	public String getName() {
		return name;
	}
	
	public String getVersion() {
		return version;
	}
	
	public Date getReleaseDate() {
		return releaseDate;
	}
	
	public static Builder builder() {
		return new Builder();
	}

	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder extends BackboneElement.Builder<Builder, Software> {
		
		private String name;
		private String version;
		private Date releaseDate;
		
		public Builder name(final String name) {
			this.name = name;
			return this;
		}

		public Builder version(final String version) {
			this.version = version;
			return this;
		}
		
		public Builder releaseDate(final Date releaseDate) {
			this.releaseDate = releaseDate;
			return this;
		}
		
		@Override
		protected Software doBuild() {
			return new Software(id, extensions, modifierExtensions, name, version, releaseDate);
		}

		@Override
		protected Builder getSelf() {
			return this;
		}
	}
}
