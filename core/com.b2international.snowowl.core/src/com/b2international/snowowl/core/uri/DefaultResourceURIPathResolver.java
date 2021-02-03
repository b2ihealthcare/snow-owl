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
import java.util.stream.Collectors;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.codesystem.CodeSystemVersion;
import com.b2international.snowowl.core.codesystem.CodeSystemVersionEntry;
import com.b2international.snowowl.core.codesystem.version.CodeSystemVersionSearchRequestBuilder;
import com.b2international.snowowl.core.request.SearchResourceRequest;

/**
 * @since 7.12
 */
public final class DefaultResourceURIPathResolver implements ResourceURIPathResolver {

	@Override
	public List<String> resolve(ServiceProvider context, List<CodeSystemURI> codeSystemURIs) {
		if (CompareUtils.isEmpty(codeSystemURIs)) {
			return Collections.emptyList();
		}
		final Map<String, CodeSystem> codeSystemsByShortName = CodeSystemRequests.prepareSearchAllCodeSystems()
				.filterByIds(codeSystemURIs.stream().map(CodeSystemURI::getCodeSystem).collect(Collectors.toSet()))
				.build()
				.execute(context)
				.stream()
				.collect(Collectors.toMap(CodeSystem::getShortName, t -> t));
		
		return codeSystemURIs.stream().map(uri -> resolve(context, uri, codeSystemsByShortName.get(uri.getCodeSystem()))).collect(Collectors.toList());
	}

	private String resolve(ServiceProvider context, CodeSystemURI uriToResolve, CodeSystem codeSystem) {
		if (uriToResolve.isHead()) {
			// use code system working branch directly when HEAD is specified
			return codeSystem.getBranchPath();
		} else {
			CodeSystemVersionSearchRequestBuilder versionSearch = CodeSystemRequests.prepareSearchCodeSystemVersion()
					.one()
					.filterByCodeSystemShortName(codeSystem.getShortName());
			
			if (uriToResolve.isLatest()) {
				// fetch the latest code system version if LATEST is specified in the URI
				versionSearch.sortBy(SearchResourceRequest.SortField.descending(CodeSystemVersionEntry.Fields.EFFECTIVE_DATE));
			} else {
				// try to fetch the path as exact version if not the special LATEST is specified in the URI
				versionSearch.filterByVersionId(uriToResolve.getPath());
			}
			// determine the final branch path, if based on the version search we find a version, then use that, otherwise use the defined path as relative branch of the code system working branch
			return versionSearch
					.build(codeSystem.getRepositoryId())
					.getRequest()
					.execute(context)
					.stream()
					.findFirst()
					.map(CodeSystemVersion::getPath)
					.orElseGet(() -> {
						if (uriToResolve.isLatest()) {
							throw new BadRequestException("No CodeSystem version is present in '%s'. Explicit '%s/HEAD' can be used to retrieve the latest work in progress version of the CodeSystem.", codeSystem.getShortName(), codeSystem.getShortName());
						} else {
							return codeSystem.getRelativeBranchPath(uriToResolve.getPath()); 
						}
					});
		}
	}
	
}
