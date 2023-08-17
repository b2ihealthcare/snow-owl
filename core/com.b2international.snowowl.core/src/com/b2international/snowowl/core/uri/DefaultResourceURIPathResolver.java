/*
 * Copyright 2020-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.*;
import java.util.stream.Collectors;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.collections.Collections3;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.Resource;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.TerminologyResource;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.core.request.resource.BaseResourceSearchRequest.ResourceHiddenFilter;
import com.b2international.snowowl.core.request.version.VersionSearchRequestBuilder;
import com.b2international.snowowl.core.version.VersionDocument;
import com.b2international.snowowl.core.version.Versions;

/**
 * @since 7.12
 */
public final class DefaultResourceURIPathResolver implements ResourceURIPathResolver {
	
	private final List<TerminologyResourceURIPathResolver> terminologyResourceURIPathResolvers;
	private final boolean allowBranches;
	
	public DefaultResourceURIPathResolver(Collection<TerminologyResourceURIPathResolver> terminologyResourceURIPathResolvers, boolean allowBranches) {
		this.terminologyResourceURIPathResolvers = Collections3.toImmutableList(terminologyResourceURIPathResolvers);
		this.allowBranches = allowBranches;
	}
	
	@Override
	public boolean isSpecialURI(ResourceURI resourceUri) {
		return resourceUri.isLatest() || terminologyResourceURIPathResolvers.stream().anyMatch(r -> r.canResolve(resourceUri));
	}
	
	@Override
	public List<String> resolve(ServiceProvider context, List<ResourceURI> codeSystemURIs) {
		if (CompareUtils.isEmpty(codeSystemURIs)) {
			return Collections.emptyList();
		}
		final Set<String> resourceIds = codeSystemURIs.stream().map(ResourceURI::getResourceId).collect(Collectors.toSet());
		final Map<String, Resource> resourcesById = ResourceRequests.prepareSearch()
				.filterByIds(resourceIds)
				// make sure we fetch both visible and hidden resources as well, so that URI resolution works correctly
				.filterByHidden(ResourceHiddenFilter.ALL)
				.buildAsync()
				.getRequest()
				.execute(context)
				.stream()
				.collect(Collectors.toMap(Resource::getId, t -> t));
		
		return codeSystemURIs.stream().map(uri -> resolve(context, uri, resourcesById.get(uri.getResourceId()))).collect(Collectors.toList());
	}

	@Override
	public PathWithVersion resolveWithVersion(ServiceProvider context, ResourceURI uriToResolve, Resource resource) {
		if (resource instanceof TerminologyResource) {
			TerminologyResource terminologyResource = (TerminologyResource) resource;
			
			// use code system working branch directly when HEAD is specified
			if (uriToResolve.isHead()) {
				return getResourceHeadBranch(uriToResolve, terminologyResource);
			}
			
			// prevent running special URI resolution if path is not a single path segment
			final String relativeBranchPath = terminologyResource.getRelativeBranchPath(uriToResolve.getPath());
			if (uriToResolve.getPath().contains(Branch.SEPARATOR)) {
				final String absoluteBranchPath = relativeBranchPath + uriToResolve.getTimestampPart();
				return new PathWithVersion(absoluteBranchPath);
			}
			
			// perform URI resolution for special single path segments
			// first use the plugged in URI resolvers
			for (TerminologyResourceURIPathResolver resolver : terminologyResourceURIPathResolvers) {
				if (resolver.canResolve(uriToResolve)) {
					return resolver.resolve(context, uriToResolve, terminologyResource);
				}
			}
			
			// then fall back to version search
			VersionSearchRequestBuilder versionSearch = ResourceRequests.prepareSearchVersion()
				.one()
				.filterByResource(terminologyResource.getResourceURI());
			
			if (uriToResolve.isLatest()) {
				// fetch the latest resource version if LATEST is specified in the URI
				versionSearch.sortBy(SearchResourceRequest.Sort.fieldDesc(VersionDocument.Fields.EFFECTIVE_TIME));
			} else {
				// try to fetch the path as exact version if not the special LATEST is specified in the URI
				versionSearch.filterByVersionId(uriToResolve.getPath());
			}
			
			// determine the final branch path, if based on the version search we find a version, then use that, otherwise use the defined path as relative branch of the code system working branch
			Versions versions = versionSearch.buildAsync()
				.getRequest()
				.execute(context);
			
			return versions.first()
				.map(v -> {
					final String versionBranchPath = v.getBranchPath() + uriToResolve.getTimestampPart();
					final ResourceURI versionResourceURI = v.getVersionResourceURI().withTimestampPart(uriToResolve.getTimestampPart());
					return new PathWithVersion(versionBranchPath, versionResourceURI);
				})
				.orElseGet(() -> {
					// for draft resources allow HEAD to be queried via LATEST
					if (uriToResolve.isLatest() && Resource.DRAFT_STATUS.equals(terminologyResource.getStatus())) {
						return getResourceHeadBranch(uriToResolve, terminologyResource);
					}
					if (uriToResolve.isLatest() || !allowBranches) {
						throw new BadRequestException("No Resource version is present in '%s'. Explicit '%s' can be used to retrieve the latest work in progress version of the Resource.", 
							terminologyResource.getId(), terminologyResource.getId());
					}
					
					return new PathWithVersion(relativeBranchPath); 
				});
		}
		
		return new PathWithVersion("");
	}

	private PathWithVersion getResourceHeadBranch(ResourceURI uriToResolve, TerminologyResource terminologyResource) {
		final String workingBranchPath = terminologyResource.getBranchPath() + uriToResolve.getTimestampPart();
		return new PathWithVersion(workingBranchPath);
	}
}
