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

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;

/**
 * @since 4.7
 * @param <B> - the builder type
 * @param <C> - the execution context type
 * @param <R> - the response type
 */
public abstract class BaseRepositoryRequestBuilder<B extends BaseRepositoryRequestBuilder<B, R>, R> extends BaseRequestBuilder<B, RepositoryContext, R> {

	private final String repositoryId;
	
	protected BaseRepositoryRequestBuilder(String repositoryId) {
		this.repositoryId = repositoryId;
	}

	public final Request<ServiceProvider, R> buildFor() {
		return RepositoryRequests.wrap(repositoryId, build());
	}
	
}
