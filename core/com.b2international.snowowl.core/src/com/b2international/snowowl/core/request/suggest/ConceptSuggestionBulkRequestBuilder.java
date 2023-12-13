/*
 * Copyright 2022 B2i Healthcare, https://b2ihealthcare.com
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

import java.util.List;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.request.SystemRequestBuilder;
import com.google.common.collect.ImmutableList;

/**
 * @since 8.5.1
 */
public final class ConceptSuggestionBulkRequestBuilder 
		extends BaseRequestBuilder<ConceptSuggestionBulkRequestBuilder, ServiceProvider, List<Suggestions>>
		implements SystemRequestBuilder<List<Suggestions>> {

	private ImmutableList.Builder<ConceptSuggestionRequest> requests = ImmutableList.builder();
	private Integer batchSize = 10;
	private Integer batchTimeout = 120;
	
	public ConceptSuggestionBulkRequestBuilder add(ConceptSuggestionRequestBuilder req) {
		return add(req == null ? null : (ConceptSuggestionRequest) req.build());
	}
	
	public ConceptSuggestionBulkRequestBuilder add(ConceptSuggestionRequest req) {
		synchronized (this.requests) {
			this.requests.add(req);
		}
		return getSelf();
	}
	
	public ConceptSuggestionBulkRequestBuilder setBatchSize(Integer batchSize) {
		this.batchSize = batchSize;
		return getSelf();
	}
	
	public ConceptSuggestionBulkRequestBuilder setBatchTimeout(Integer batchTimeout) {
		this.batchTimeout = batchTimeout;
		return getSelf();
	}
	
	@Override
	protected Request<ServiceProvider, List<Suggestions>> doBuild() {
		ConceptSuggestionBulkRequest req = new ConceptSuggestionBulkRequest();
		req.setRequests(requests.build());
		req.setBatchSize(batchSize);
		req.setBatchTimeout(batchTimeout);
		return req;
	}

}
