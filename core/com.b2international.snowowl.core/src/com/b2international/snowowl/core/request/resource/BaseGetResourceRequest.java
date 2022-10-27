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

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.request.*;
import com.b2international.snowowl.core.request.version.VersionSearchRequestBuilder;
import com.b2international.snowowl.core.version.Version;
import com.b2international.snowowl.core.version.VersionDocument;
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
			VersionSearchRequestBuilder versionSearch = ResourceRequests.prepareSearchVersion()
				.one()
				.filterByResource(resourceUri.withoutPath());
			
			if (resourceUri.isLatest()) {
				// fetch the latest resource version if LATEST is specified in the URI
				versionSearch.sortBy(SearchResourceRequest.Sort.fieldDesc(VersionDocument.Fields.EFFECTIVE_TIME));
			} else {
				// try to fetch the path as exact version if not the special LATEST is specified in the URI
				versionSearch.filterByVersionId(resourceUri.getPath());
			}
			
			// determine the final branch path, if based on the version search we find a version, then use that, otherwise use the defined path as relative branch of the code system working branch
			return versionSearch.buildAsync()
				.execute(context)
				.first()
				.map(Version::getCreatedAt)
				.orElseGet(() -> {
					// ignore if accessing the LATEST versioned state, but there is no version present
					if (resourceUri.isLatest()) {
						return null;
					} else {
						throw new BadRequestException("Only version paths and timestamp fragments are supported when accessing a resource via an URI. Got: %s", resourceUri.getPath());
					}
				});
		}
		return null;
	}

}
