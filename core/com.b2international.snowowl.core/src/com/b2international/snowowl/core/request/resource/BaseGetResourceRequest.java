/*
 * Copyright 2022-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Optional;

import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.domain.PageableCollectionResource;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.request.GetResourceRequest;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.core.request.RevisionIndexReadRequestTimestampProvider;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.core.request.resource.BaseResourceSearchRequest.ResourceHiddenFilter;
import com.b2international.snowowl.core.request.version.VersionSearchRequestBuilder;
import com.b2international.snowowl.core.version.Version;
import com.b2international.snowowl.core.version.VersionDocument;
import com.google.common.base.Strings;

/**
 * Base class for requests that retrieve a terminology resource, eg. bundle or
 * code system by identifier. In addition to searching by ID it also handles
 * point-in-time resolution and resource visibility filtering.
 * 
 * @param <SB> - the search request builder type
 * @param <SR> - the search response type (a {@link PageableCollectionResource} that should have at most one item)
 * @param <R> - the response type
 * 
 * @since 8.7
 */
public abstract class BaseGetResourceRequest<SB extends BaseResourceSearchRequestBuilder<SB, SR>, SR extends PageableCollectionResource<?>, R> 
	extends GetResourceRequest<SB, RepositoryContext, SR, R>
	implements RevisionIndexReadRequestTimestampProvider {

	private static final long serialVersionUID = 1L;
	
	private final ResourceURI resourceUri;
	
	private boolean allowHiddenResources;
	private boolean fetchAlternative = true;
	
	public BaseGetResourceRequest(ResourceURI resourceUri) {
		super(resourceUri.getResourceId());
		this.resourceUri = resourceUri;
	}
	
	void setAllowHiddenResources(boolean allowHiddenResources) {
		this.allowHiddenResources = allowHiddenResources;
	}
	
	protected final void setFetchAlternative(boolean fetchAlternative) {
		this.fetchAlternative = fetchAlternative;
	}
	
	protected final SB configureHiddenFilter(SB searchRequestBuilder) {
		return searchRequestBuilder.filterByHidden(allowHiddenResources ? ResourceHiddenFilter.ALL : ResourceHiddenFilter.VISIBLE_ONLY);
	}
	
	@Override
	protected RepositoryContext alterContextBeforeFetch(RepositoryContext context) {
		// make sure we attach the currently accessed ResourceURI to get the right state
		return context.inject().bind(ResourceURI.class, resourceUri).build();
	}
	
	@Override
	protected final Optional<R> fetchAlternative(RepositoryContext context) {
		if (fetchAlternative && ResourceURI.isSpecialResourceId(id())) {
			return extractFirst(fetch(context, ResourceURI.withoutSpecialResourceIdPart(id())));
		} else {
			return super.fetchAlternative(context);
		}
	}
	
	@Override
	public final Long getReadTimestamp(ServiceProvider context) {
		if (!Strings.isNullOrEmpty(resourceUri.getTimestampPart())) {
			return Long.parseLong(resourceUri.getTimestampPart().substring(1));
		} else if (!resourceUri.isHead() && !resourceUri.isNext()) {
			VersionSearchRequestBuilder versionSearch = ResourceRequests.prepareSearchVersion()
				.one()
				.filterByResource(resourceUri.withoutPath().withoutSpecialResourceIdPart().withoutResourceType())
				.setFields(VersionDocument.Fields.ID, VersionDocument.Fields.CREATED_AT);
			
			if (resourceUri.isLatest()) {
				// fetch the latest resource version if LATEST is specified in the URI
				versionSearch.sortBy(SearchResourceRequest.Sort.fieldDesc(VersionDocument.Fields.EFFECTIVE_TIME));
			} else {
				if (resourceUri.hasSpecialResourceIdPart()) {
					// try to fetch the special ID part as a version
					versionSearch.filterByVersionId(resourceUri.getSpecialIdPart());
				} else {
					// try to fetch the path as exact version if not the special LATEST 
					versionSearch.filterByVersionId(resourceUri.getPath());
				}
			}
			
			// determine the final branch path, if based on the version search we find a version, then use that, otherwise use the defined path as relative branch of the code system working branch
			return versionSearch.buildAsync()
				.execute(context)
				.first()
				.map(Version::getCreatedAt)
				// for now fall back to fetching the HEAD version of the resource, this is okay for most of the cases, but should fail when accessing versioned content
				// TODO this requires configuration of the error handling part
				.orElse(null);
		}
		return null;
	}
	
}
