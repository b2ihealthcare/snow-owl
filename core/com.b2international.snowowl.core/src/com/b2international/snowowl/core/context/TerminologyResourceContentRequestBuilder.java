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
package com.b2international.snowowl.core.context;

import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.AsyncRequest;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.RequestBuilder;
import com.b2international.snowowl.core.request.AllowedHealthStates;
import com.b2international.snowowl.core.request.RevisionIndexReadRequest;

/**
 * @since 8.0
 */
public interface TerminologyResourceContentRequestBuilder<R> extends RequestBuilder<BranchContext, R>, AllowedHealthStates {

	default AsyncRequest<R> build(String toolingId, String resourceUriOrPath) {
		return new AsyncRequest<>(
			new TerminologyResourceRequest<>(toolingId, resourceUriOrPath,
				new TerminologyResourceContentRequest<>(
					wrap(build())
				)
			)
		);
	}
	
	default AsyncRequest<R> build(String resourceUriOrPath) {
		return build(getToolingId(), resourceUriOrPath);
	}
	
	default AsyncRequest<R> build(ResourceURI resourceUri) {
		return build(resourceUri.toString());
	}
	
	default Request<BranchContext, R> wrap(Request<BranchContext, R> req) {
		return new RevisionIndexReadRequest<>(req, snapshot());
	}
	
	default boolean snapshot() {
		return true;
	}
	
	/**
	 * When required subclasses can provide reflective access by providing their unique tooling id. This method by default returns <code>null</code>.
	 * 
	 * @return the unique tooling id of the API that allows reflective access to its content
	 */
	default String getToolingId() {
		return null;
	}
	
}
