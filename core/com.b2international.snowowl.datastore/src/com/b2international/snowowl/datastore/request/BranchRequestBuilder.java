/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.request;

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.AsyncRequest;
import com.b2international.snowowl.core.events.RequestBuilder;

/**
 * Provides a default method for wrapping {@link BranchContext}-based requests
 * into {@link AsyncRequest}s. The provided {@code BranchContext} does not
 * include a service for accessing the repository index.
 * 
 * @since 7.0
 * @param <R> - the return type
 */
public interface BranchRequestBuilder<R> extends RequestBuilder<BranchContext, R>, AllowedHealthStates {

	default AsyncRequest<R> build(String repositoryId, String branch) {
		return new AsyncRequest<>(
			new RepositoryRequest<>(repositoryId,
				new HealthCheckingRequest<>(
					new IndexReadRequest<>(
						new BranchRequest<>(branch, build())
					), 
					allowedHealthstates()
				)
			)
		);
	}
}
