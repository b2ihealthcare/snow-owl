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
package com.b2international.snowowl.fhir.core.model;

import java.util.Collection;

import com.b2international.snowowl.fhir.core.model.ResponseEntry.Builder;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * Entry to encapsulate a resource in a {@link Bundle}
 * 
 * @since 8.0.0
 */
@JsonDeserialize(using = JsonDeserializer.None.class, builder = ResourceEntry.Builder.class)
public class ResourceEntry extends Entry {
	
	private BatchResponse response;
	
	private FhirResource resource;
	
	protected ResourceEntry(final Collection<String> links, final Uri fullUrl, 
			final BatchResponse response, final FhirResource resource) {
		super(links, fullUrl);
		this.response = response;
		this.resource = resource;
	}

	public BatchResponse getResponse() {
		return response;
	}
	
	public FhirResource getResource() {
		return resource;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder extends Entry.Builder<Builder, ResourceEntry> {
		
		private BatchResponse response;
		
		private FhirResource fhirResource;
		
		@Override
		protected Builder getSelf() {
			return this;
		}
		
		public Builder response(BatchResponse response) {
			this.response = response;
			return getSelf();
		}
		
		public Builder resource(FhirResource fhirResource) {
			this.fhirResource = fhirResource;
			return getSelf();
		}
		
		@Override
		protected ResourceEntry doBuild() {
			return new ResourceEntry(links, fullUrl, response, fhirResource);
		}
		
	}

}
