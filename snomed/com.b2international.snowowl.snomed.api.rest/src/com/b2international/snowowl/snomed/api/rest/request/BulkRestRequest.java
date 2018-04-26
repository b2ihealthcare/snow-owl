/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.rest.request;

import java.util.Collection;
import java.util.stream.Stream;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.Request;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 4.5
 */
public final class BulkRestRequest {

	private final Collection<RestRequest> requests;

	@JsonCreator
	public BulkRestRequest(@JsonProperty("requests") Collection<RestRequest> requests) {
		this.requests = requests;
	}
	
	public <C extends ServiceProvider> Stream<Request<C, ?>> resolve(RequestResolver<C> resolver) {
		return requests.stream().map(request -> request.resolve(resolver));
	}
	
	public Collection<RestRequest> getRequests() {
		return requests;
	}
}
