/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.events.bulk;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.RequestBuilder;

/**
 * @since 4.5
 */
public class BulkRequestBuilder<C extends ServiceProvider> implements RequestBuilder<C, BulkResponse> {
	
	private List<Request<C, ?>> requests = newArrayList();
	
	BulkRequestBuilder() {}

	public BulkRequestBuilder<C> add(Request<C, ?> req) {
		this.requests.add(req);
		return this;
	}
	
	public BulkRequestBuilder<C> add(RequestBuilder<C, ?> req) {
		return add(req.build());
	}
	
	@Override
	public Request<C, BulkResponse> build() {
		return new BulkRequest<>(requests);
	}
}
