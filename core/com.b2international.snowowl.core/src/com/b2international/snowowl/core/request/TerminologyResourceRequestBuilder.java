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
package com.b2international.snowowl.core.request;

import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.AsyncRequest;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.RequestBuilder;

/**
 * @since 8.0
 */
public interface TerminologyResourceRequestBuilder<R> extends RequestBuilder<BranchContext, R> {

	default AsyncRequest<R> build(String resourceUri) {
		return build(new ResourceURI(resourceUri));
	}
	
	default AsyncRequest<R> build(ResourceURI resourceUri) {
		return new AsyncRequest<>(
			new TerminologyResourceRequest<>(
				resourceUri,
				wrap(build())
			)
		);
	}
	
	default Request<BranchContext, R> wrap(Request<BranchContext, R> req) {
		return req;
	}
	
}
