/*
 * Copyright 2022-2023 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.request.resource;

import com.b2international.snowowl.core.context.ResourceRepositoryRequestBuilder;
import com.b2international.snowowl.core.domain.PageableCollectionResource;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.request.GetResourceRequestBuilder;
import com.b2international.snowowl.core.request.ResourceRequest;

/**
 * @since 8.7
 */
public abstract class BaseGetResourceRequestBuilder<
	B extends GetResourceRequestBuilder<B, SB, RepositoryContext, SR, R>, 
	SB extends BaseResourceSearchRequestBuilder<SB, SR>, 
	SR extends PageableCollectionResource<R>, 
	R> 
	extends GetResourceRequestBuilder<B, SB, RepositoryContext, SR, R> implements ResourceRepositoryRequestBuilder<R> {

	private boolean allowHiddenResources = true;

	public BaseGetResourceRequestBuilder(BaseGetResourceRequest<SB, SR, R> request) {
		super(request);
	}

	public B setAllowHiddenResources(boolean allowHiddenResources) {
		this.allowHiddenResources = allowHiddenResources;
		return getSelf();
	}

	@Override
	protected void init(ResourceRequest<RepositoryContext, R> req) {
		super.init(req);
		((BaseGetResourceRequest<?, ?, ?>) req).setAllowHiddenResources(allowHiddenResources);
	}
}
