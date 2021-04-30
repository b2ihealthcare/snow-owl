/*
 * Copyright 2018-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.AsyncRequest;
import com.b2international.snowowl.core.events.RequestBuilder;
import com.google.common.base.Strings;

/**
 * Provides a default method for wrapping {@link BranchContext}-based requests
 * into {@link AsyncRequest}s. The provided {@code BranchContext} does not
 * include a service for accessing the repository index.
 * 
 * @since 7.0
 * @param <R> - the return type
 */
public interface BranchRequestBuilder<R> extends RequestBuilder<BranchContext, R>, AllowedHealthStates, TerminologyResourceRequestBuilder<R> {

	/**
	 * @param repositoryId
	 * @param branch
	 * @return
	 * @deprecated - use {@link #build(String)} or {@link #build(ResourceURI)}, this method will be removed in 8.0
	 */
	default AsyncRequest<R> build(String repositoryId, String branch) {
		// if the branch starts with MAIN, then it is an explicit branch path with a repositoryId
		if (Strings.nullToEmpty(branch).startsWith(Branch.MAIN_PATH)) {
			return new AsyncRequest<>(
				new RepositoryRequest<>(repositoryId,
					new HealthCheckingRequest<>(
						new BranchRequest<>(branch, 
							wrap(build())
						),
						allowedHealthstates()
					)
				)
			);
		} else {
			return build(branch);
		}
	}

}
