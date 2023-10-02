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

import javax.validation.constraints.AssertTrue;

import com.b2international.snowowl.fhir.core.codesystems.HttpVerb;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * Entry to encapsulate a GET request without a resource in a {@link Bundle}
 * @since 8.0.0
 */
@JsonDeserialize(using = JsonDeserializer.None.class, builder = RequestEntry.Builder.class)
public class RequestEntry extends AbstractRequestEntry {

	protected RequestEntry(final Collection<String> links, final Uri fullUrl, 
			final BatchRequest request) {
		
		super(links, fullUrl, request);
	}
	
	@JsonIgnore
	@AssertTrue(message = "Only GET requests can be without request body")
	public boolean isGet() {
		return HttpVerb.GET.getCode().equals(getRequest().getMethod());
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder extends AbstractRequestEntry.Builder<Builder, RequestEntry> {
		
		@Override
		protected Builder getSelf() {
			return this;
		}
		
		@Override
		protected RequestEntry doBuild() {
			return new RequestEntry(links, fullUrl, request);
		}
	}

}
