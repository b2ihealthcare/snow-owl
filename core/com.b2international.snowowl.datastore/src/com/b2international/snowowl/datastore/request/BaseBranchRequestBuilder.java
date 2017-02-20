/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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

import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.AsyncRequest;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.BranchPathUtils;

/**
 * @since 4.5
 */
public abstract class BaseBranchRequestBuilder<B extends BaseBranchRequestBuilder<B, R>, R> extends BaseRequestBuilder<B, BranchContext, R> {

	/**
	 * Builds the request.
	 * @param repositoryId the repository id to execute the request against.
	 * @param branch the branch to execute the request on
	 * @return
	 * @see BranchPathUtils
	 * @see SnomedDatastoreActivator.REPOSITORY_UUID
	 */
	public final AsyncRequest<R> build(final String repositoryId, final String branch) {
		return toAsync(
			new RepositoryRequest<>(repositoryId, 
				new BranchRequest<>(branch, 
					extend(build())
				)
			)
		);
	}
	
	/**
	 * Extend the given {@link Request} with additional functionality.
	 * 
	 * @param req
	 * @return
	 */
	@OverridingMethodsMustInvokeSuper
	protected Request<BranchContext, R> extend(Request<BranchContext, R> req) {
		return req;
	}

}
