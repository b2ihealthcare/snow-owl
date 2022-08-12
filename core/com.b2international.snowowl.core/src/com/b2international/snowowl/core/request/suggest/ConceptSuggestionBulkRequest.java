/*
 * Copyright 2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.request.suggest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.elasticsearch.core.Map;
import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.authorization.AuthorizationService;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.identity.User;
import com.b2international.snowowl.eventbus.IEventBus;
import com.google.common.collect.Iterables;

/**
 * @since 8.5.1
 */
public final class ConceptSuggestionBulkRequest implements Request<ServiceProvider, List<Suggestions>> {

	private static final long serialVersionUID = 1L;

	@NotEmpty
	private List<ConceptSuggestionRequest> requests;
	
	@NotNull
	@Min(1)
	private Integer batchSize;
	
	@NotNull
	@Min(1)
	private Integer batchTimeout;

	ConceptSuggestionBulkRequest() {
	}
	
	void setRequests(List<ConceptSuggestionRequest> requests) {
		this.requests = requests;
	}
	
	void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}
	
	void setBatchTimeout(int batchTimeout) {
		this.batchTimeout = batchTimeout;
	}
	
	@Override
	public List<Suggestions> execute(ServiceProvider context) {
		// cache accessible resources before executing any of the specified suggestion requests, so that we won't hit the authorization service multiple times from multiple threads
		User user = context.service(User.class);
		context
			.optionalService(AuthorizationService.class)
			.orElse(AuthorizationService.DEFAULT)
			.getAccessibleResources(context, user);
		
		final List<Suggestions> response = new ArrayList<>(requests.size());
		for (Iterable<ConceptSuggestionRequest> batch : Iterables.partition(requests, batchSize)) {
			final List<Promise<Suggestions>> batchResponse = new ArrayList<>(batchSize);
			batch.forEach(request -> {
				// pass the prefetched User with accessible resource information forward, so that nested suggestion requests can use the cached info
				batchResponse.add(request.async(Map.of(User.class, user)).execute(context.service(IEventBus.class)));
			});
			// wait for batch completion then proceed to next batch, a batch must complete in under the specified minutes (default 2 min, configurable)
			Promise.all(batchResponse)
				.getSync(batchTimeout, TimeUnit.SECONDS)
				.stream()
				.filter(Suggestions.class::isInstance)
				.map(Suggestions.class::cast)
				.forEach(response::add);
		}
		return response;
	}
	
}
