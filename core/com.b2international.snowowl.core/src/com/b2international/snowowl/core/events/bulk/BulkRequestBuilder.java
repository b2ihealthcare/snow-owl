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
package com.b2international.snowowl.core.events.bulk;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.RequestBuilder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

/**
 * @since 4.5
 */
public final class BulkRequestBuilder<C extends ServiceProvider> implements RequestBuilder<C, BulkResponse> {
	
	private Builder<Request<C, ?>> requests = ImmutableList.builder();
	
	BulkRequestBuilder() {}

	public BulkRequestBuilder<C> add(Request<C, ?> req) {
		this.requests.add(req);
		return this;
	}
	
	public BulkRequestBuilder<C> add(RequestBuilder<C, ?> req) {
		return add(req.build());
	}
	
	@Override
	public BulkRequest<C> build() {
		return new BulkRequest<>(requests.build());
	}
}
