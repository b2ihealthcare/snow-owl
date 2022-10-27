/*
 * Copyright 2022 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.request.GetResourceRequest;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.core.request.RevisionIndexReadRequestTimestampProvider;
import com.b2international.snowowl.core.request.SearchResourceRequestBuilder;
import com.b2international.snowowl.core.version.Version;
import com.google.common.base.Strings;

/**
 * @since 8.7
 * 
 * @param <SB>
 * @param <SR>
 * @param <R>
 */
public abstract class BaseGetResourceRequest<SB extends SearchResourceRequestBuilder<SB, RepositoryContext, SR>, SR, R> 
		extends GetResourceRequest<SB, RepositoryContext, SR, R>
		implements RevisionIndexReadRequestTimestampProvider {

	private static final long serialVersionUID = 1L;
	
	private final ResourceURI resourceUri;
	
	public BaseGetResourceRequest(ResourceURI resourceUri) {
		super(resourceUri.getResourceId());
		this.resourceUri = resourceUri;
	}
	
	@Override
	public final Long getReadTimestamp(ServiceProvider context) {
		if (!Strings.isNullOrEmpty(resourceUri.getTimestampPart())) {
			return Long.parseLong(resourceUri.getTimestampPart().substring(1));
		} else if (!resourceUri.isHead() && !resourceUri.isNext()) {
			return ResourceRequests.prepareSearchVersion()
					.one()
					.filterByResource(resourceUri.withoutPath())
					.filterByVersionId(resourceUri.getPath())
					.buildAsync()
					.execute(context)
					.first()
					.map(Version::getCreatedAt)
					.orElse(null);
		}
		return null;
	}

}
