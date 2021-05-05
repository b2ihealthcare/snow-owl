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

import com.b2international.snowowl.core.Resource;
import com.b2international.snowowl.core.domain.RepositoryContext;

/**
 * @since 8.0
 */
public final class ResourceGetRequest extends GetResourceRequest<ResourceSearchRequestBuilder, RepositoryContext, Resource> {

	private static final long serialVersionUID = 1L;
	
	public ResourceGetRequest(String resourceId) {
		super(resourceId);
	}

	@Override
	protected ResourceSearchRequestBuilder createSearchRequestBuilder() {
		return new ResourceSearchRequestBuilder();
	}

}
