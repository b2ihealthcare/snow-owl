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

import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * Entry to encapsulate a resource-based request in a {@link Bundle}
 * @since 8.0.0
 */
@JsonDeserialize(using = JsonDeserializer.None.class, builder = ParametersRequestEntry.Builder.class)
public class ResourceRequestEntry extends Entry {

	private BatchRequest request;
	
	private FhirResource requestResource;
	
	protected ResourceRequestEntry(final Collection<String> links, final Uri fullUrl, 
			final BatchRequest request, final FhirResource requestResource) {
		
		super(links, fullUrl);
		this.request = request;
		this.requestResource = requestResource;
	}
	
	public BatchRequest getRequest() {
		return request;
	}
	
	@JsonProperty("resource")
	public FhirResource getRequestResource() {
		return requestResource;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder extends Entry.Builder<Builder, ResourceRequestEntry> {
		
		private BatchRequest request;
		
		private FhirResource requestResource;
		
		@Override
		protected Builder getSelf() {
			return this;
		}
		
		public Builder request(BatchRequest request) {
			this.request = request;
			return getSelf();
		}
		
		public Builder resource(FhirResource requestResource) {
			this.requestResource = requestResource;
			return getSelf();
		}
		
		@Override
		protected ResourceRequestEntry doBuild() {
			return new ResourceRequestEntry(links, fullUrl, request, requestResource);
		}
	}

}
