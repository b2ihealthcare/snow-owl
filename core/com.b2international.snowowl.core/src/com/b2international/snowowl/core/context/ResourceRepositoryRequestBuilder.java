/*
 * Copyright 2021-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.context;

import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.AsyncRequest;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.RequestBuilder;

/**
 * @since 8.0
 */
public interface ResourceRepositoryRequestBuilder<R> extends RequestBuilder<RepositoryContext, R> {

	default AsyncRequest<R> buildAsync() {
		return buildAsync(null);
	}
	
	default AsyncRequest<R> buildAsync(Long timestamp) {
		return new AsyncRequest<>(
			new ResourceRepositoryRequest<>(timestamp, 
				wrap(build())
			)
		);
	}

	default Request<RepositoryContext, R> wrap(Request<RepositoryContext, R> req) {
		return req;
	}
}
