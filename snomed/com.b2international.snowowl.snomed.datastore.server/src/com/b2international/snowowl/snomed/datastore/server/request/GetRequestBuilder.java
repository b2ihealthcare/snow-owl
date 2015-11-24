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
package com.b2international.snowowl.snomed.datastore.server.request;

import java.util.Collections;
import java.util.List;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.RequestBuilder;
import com.b2international.snowowl.datastore.request.RepositoryRequests;

/**
 * @since 4.5
 */
public abstract class GetRequestBuilder<B extends GetRequestBuilder<B, R>, R> implements RequestBuilder<BranchContext, R> {

	private final String repositoryId;
	
	private String componentId;
	private List<String> expand = Collections.emptyList();
	private List<ExtendedLocale> locales = Collections.emptyList();

	protected GetRequestBuilder(String repositoryId) {
		this.repositoryId = repositoryId;
	}
	
	public final B setComponentId(String componentId) {
		this.componentId = componentId;
		return getSelf();
	}
	
	public final B setExpand(List<String> expand) {
		this.expand = expand;
		return getSelf();
	}
	
	public final B setLocales(List<ExtendedLocale> locales) {
		this.locales = locales;
		return getSelf();
	}
	
	public final Request<ServiceProvider, R> build(String branch) {
		return RepositoryRequests.wrap(repositoryId, branch, RepositoryRequests.toIndexReadRequest(build()));
	}
	
	@Override
	public final Request<BranchContext, R> build() {
		final GetRequest<R> req = create();
		req.setComponentId(componentId);
		req.setExpand(expand);
		req.setLocales(locales);
		return req;
	}
	
	protected abstract GetRequest<R> create();
	
	protected abstract B getSelf();
	
}
