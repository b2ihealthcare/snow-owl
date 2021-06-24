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

import com.b2international.snowowl.fhir.core.model.dt.Parameters.Fhir;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * Entry to encapsulate a response in a {@link Bundle}
 * @since 8.0.0
 */
@JsonDeserialize(using = JsonDeserializer.None.class, builder = ResponseEntry.Builder.class)
public class ResponseEntry extends Entry {
	
	private BatchResponse response;
	
	private Fhir responseResource;
	
	protected ResponseEntry(final Collection<String> links, final Uri fullUrl, 
			final BatchResponse response, final Fhir responseResource) {
		super(links, fullUrl);
		this.response = response;
		this.responseResource = responseResource;
	}
	
	public BatchResponse getResponse() {
		return response;
	}
	
	@JsonProperty("resource")
	public Fhir getResponseResource() {
		return responseResource;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder extends Entry.Builder<Builder, ResponseEntry> {
		
		private BatchResponse response;
		
		private Fhir responseResource;
		
		@Override
		protected Builder getSelf() {
			return this;
		}
		
		public Builder response(BatchResponse response) {
			this.response = response;
			return getSelf();
		}
		
		public Builder resource(Fhir requestResource) {
			this.responseResource = requestResource;
			return getSelf();
		}
		
		@Override
		protected ResponseEntry doBuild() {
			return new ResponseEntry(links, fullUrl, response, responseResource);
		}
		
	}

}
