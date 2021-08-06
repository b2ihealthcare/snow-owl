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

import javax.validation.constraints.NotNull;

import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * Entry to encapsulate a GET request without a resource in a {@link Bundle}
 * @since 8.0.0
 */
//@JsonDeserialize(using = JsonDeserializer.None.class, builder = RequestEntry.Builder.class)
public abstract class AbstractRequestEntry extends Entry {

	@NotNull
	private BatchRequest request;
	
	protected AbstractRequestEntry(final Collection<String> links, final Uri fullUrl, 
			final BatchRequest request) {
		
		super(links, fullUrl);
		this.request = request;
	}
	
	public BatchRequest getRequest() {
		return request;
	}
	
	@JsonPOJOBuilder(withPrefix = "")
	public static abstract class Builder<B extends Builder<B, T>, T extends AbstractRequestEntry> extends Entry.Builder<B, T> {
		
		protected BatchRequest request;
		
		public B request(BatchRequest request) {
			this.request = request;
			return getSelf();
		}
	}

}
