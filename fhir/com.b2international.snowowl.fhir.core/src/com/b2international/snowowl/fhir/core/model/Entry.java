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
import com.b2international.snowowl.fhir.core.search.Summary;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * FHIR Entry BackBone element in the Bundle domain object
 * @since 8.0.0
 */
@JsonDeserialize(using = BundleEntryDeserializer.class)
public class Entry {
	
	@Summary
	private Collection<String> links;
	
	//This can be null
	@Summary
	private Uri fullUrl;
	
	protected Entry(final Collection<String> links, final Uri fullUrl) {
		this.links = links;
		this.fullUrl = fullUrl;
	}

	@JsonProperty("link")
	public Collection<String> getLinks() {
		return links;
	}
	
	public Uri getFullUrl() {
		return fullUrl;
	}
	
	public static abstract class Builder<B extends Builder<B, T>, T extends Entry> extends ValidatingBuilder<T> {
		
		protected Collection<String> links;
		
		protected Uri fullUrl;
		
		protected abstract B getSelf();
		
		public B fullUrl(Uri fullUrl) {
			this.fullUrl = fullUrl;
			return getSelf();
		}
		
		public B fullUrl(String uriString) {
			this.fullUrl = new Uri(uriString);
			return getSelf();
		}
		
		
		@JsonProperty("link")
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public B links(Collection<String> links) {
			this.links = links;
			return getSelf();
		}
		
	}
	
}
