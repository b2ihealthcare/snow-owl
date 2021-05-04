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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.b2international.snowowl.core.Resource;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.branch.Branch;
import com.google.common.annotations.VisibleForTesting;

/**
 * @since 7.12
 */
public interface ResourceURIPathResolver {

	/**
	 * Resolve a List of {@link ResourceURI} instances to actual low-level branch paths.
	 * 
	 * @param context
	 * @param urisToResolve
	 * 
	 * @return a list of branch paths that reference the content of the {@link ResourceURI} instances, never <code>null</code>
	 */
	List<String> resolve(ServiceProvider context, List<ResourceURI> urisToResolve);

	/**
	 * Resolve a single {@link ResourceURI} in the context of the given {@link Resource}.
	 * 
	 * @param context
	 * @param uriToResolve
	 * @param resource
	 * @return
	 */
	String resolve(ServiceProvider context, ResourceURI uriToResolve, Resource resource);
	
	/**
	 * Basic resource URI to branch path resolver, which uses a Resource ID to BranchPath Map to provide branch paths for any Resource.
	 * 
	 * @param resourcesToBranches
	 * @return
	 */
	@VisibleForTesting
	static ResourceURIPathResolver fromMap(Map<String, String> resourcesToBranches) {
		return new ResourceURIPathResolver() {
			@Override
			public List<String> resolve(ServiceProvider context, List<ResourceURI> urisToResolve) {
				return urisToResolve.stream()
					.map(uri -> {
						if (resourcesToBranches.containsKey(uri.getResourceId())) {
							return String.join(Branch.SEPARATOR, resourcesToBranches.get(uri.getResourceId()), uri.getPath());
						} else {
							throw new UnsupportedOperationException("Unrecognized Resource: " + uri);
						}
					}).collect(Collectors.toList());
			}
			
			@Override
			public String resolve(ServiceProvider context, ResourceURI uriToResolve, Resource resource) {
				throw new UnsupportedOperationException("Not implemented yet");
			}
		};
	}

}
