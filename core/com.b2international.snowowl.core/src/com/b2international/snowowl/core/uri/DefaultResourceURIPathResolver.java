/*
 * Copyright 2020-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.uri;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.Resource;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.TerminologyResource;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.core.request.version.VersionSearchRequestBuilder;
import com.b2international.snowowl.core.version.Version;
import com.b2international.snowowl.core.version.VersionDocument;

/**
 * @since 7.12
 */
public final class DefaultResourceURIPathResolver implements ResourceURIPathResolver {
	
	private final boolean allowBranches;
	
	public DefaultResourceURIPathResolver(boolean allowBranches) {
		this.allowBranches = allowBranches;
	}
	
	@Override
	public List<String> resolve(ServiceProvider context, List<ResourceURI> codeSystemURIs) {
		if (CompareUtils.isEmpty(codeSystemURIs)) {
			return Collections.emptyList();
		}
		final Set<String> resourceIds = codeSystemURIs.stream().map(ResourceURI::getResourceId).collect(Collectors.toSet());
		final Map<String, Resource> resourcesById = ResourceRequests.prepareSearch()
				.filterByIds(resourceIds)
				.buildAsync()
				.getRequest()
				.execute(context)
				.stream()
				.collect(Collectors.toMap(Resource::getId, t -> t));
		
		return codeSystemURIs.stream().map(uri -> resolve(context, uri, resourcesById.get(uri.getResourceId()))).collect(Collectors.toList());
	}

	@Override
	public String resolve(ServiceProvider context, ResourceURI uriToResolve, Resource resource) {
		if (resource instanceof TerminologyResource) {
			TerminologyResource terminologyResource = (TerminologyResource) resource;
			if (uriToResolve.isHead()) {
				// use code system working branch directly when HEAD is specified
				return terminologyResource.getBranchPath();
			} else {
				VersionSearchRequestBuilder versionSearch = ResourceRequests.prepareSearchVersion()
						.one()
						.filterByResource(terminologyResource.getResourceURI());
				
				if (uriToResolve.isLatest()) {
					// fetch the latest resource version if LATEST is specified in the URI
					versionSearch.sortBy(SearchResourceRequest.SortField.descending(VersionDocument.Fields.EFFECTIVE_TIME));
				} else {
					// try to fetch the path as exact version if not the special LATEST is specified in the URI
					versionSearch.filterByVersionId(uriToResolve.getPath());
				}
				// determine the final branch path, if based on the version search we find a version, then use that, otherwise use the defined path as relative branch of the code system working branch
				return versionSearch
						.buildAsync()
						.getRequest()
						.execute(context)
						.stream()
						.findFirst()
						.map(Version::getBranchPath)
						.orElseGet(() -> {
							if (uriToResolve.isLatest() || !allowBranches) {
								throw new BadRequestException("No CodeSystem version is present in '%s'. Explicit '%s' can be used to retrieve the latest work in progress version of the CodeSystem.", terminologyResource.getId(), terminologyResource.getId());
							} else {
								return terminologyResource.getRelativeBranchPath(uriToResolve.getPath()); 
							}
						});
			}
		} else {
			return "";
		}
	}

}
