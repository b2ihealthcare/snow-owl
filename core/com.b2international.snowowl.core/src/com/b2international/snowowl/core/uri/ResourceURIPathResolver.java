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
	 * @param resourceURIs
	 * 
	 * @return a list of branch paths that reference the content of the {@link ResourceURI} instances, never <code>null</code>
	 */
	List<String> resolve(ServiceProvider context, List<ResourceURI> resourceURIs);

	/**
	 * Basic resource URI to branch path resolver, which uses a CodeSystem ShortName to BranchPath Map to provide branch paths for CodeSystems.
	 * 
	 * @param codeSystemsToBranches
	 * @return
	 */
	@VisibleForTesting
	static ResourceURIPathResolver fromMap(Map<String, String> codeSystemsToBranches) {
		return (context, uris) -> {
			return uris.stream()
					.map(uri -> {
						if (codeSystemsToBranches.containsKey(uri.getResourceId())) {
							return String.join(Branch.SEPARATOR, codeSystemsToBranches.get(uri.getResourceId()), uri.getPath());
						} else {
							throw new UnsupportedOperationException("Unrecognized CodeSystemURI: " + uri);
						}
					}).collect(Collectors.toList());
		};
	}

}
