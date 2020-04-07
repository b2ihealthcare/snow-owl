/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.codesystem.CodeSystemEntry;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.codesystem.CodeSystemVersionEntry;
import com.b2international.snowowl.core.codesystem.version.CodeSystemVersionSearchRequestBuilder;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.DelegatingRequest;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.uri.CodeSystemURI;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 7.5
 */
public final class CodeSystemResourceRequest<R> extends DelegatingRequest<ServiceProvider, BranchContext, R> {

	private static final long serialVersionUID = 1L;
	
	@JsonProperty
	private final CodeSystemURI uri;
	
	private transient CodeSystemEntry codeSystem;
	
	private transient String branchPath;
	
	CodeSystemResourceRequest(String codeSystemUri, Request<BranchContext, R> next) {
		super(next);
		this.uri = new CodeSystemURI(codeSystemUri);
	}

	@Override
	public R execute(ServiceProvider context) {
		return new RepositoryRequest<R>(getRepositoryId(context),
			new BranchRequest<R>(getBranchPath(context),
				next()
			)
		).execute(context.inject()
				.bind(CodeSystemURI.class, uri)
				.build());
	}

	public CodeSystemEntry getCodeSystem(ServiceProvider context) {
		if (codeSystem == null) {
			codeSystem = CodeSystemRequests.getCodeSystem(context, uri.getCodeSystem());
		}
		return codeSystem;
	}
	
	public String getRepositoryId(ServiceProvider context) {
		return getCodeSystem(context).getRepositoryUuid();
	}
	
	public String getBranchPath(ServiceProvider context) {
		if (branchPath == null) {
			
			if (uri.isHead()) {
				// use code system working branch directly when HEAD is specified
				branchPath = codeSystem.getBranchPath();
			} else {
				CodeSystemVersionSearchRequestBuilder versionSearch = CodeSystemRequests.prepareSearchCodeSystemVersion()
						.one()
						.filterByCodeSystemShortName(codeSystem.getShortName());
				
				if (uri.isLatest()) {
					// fetch the latest code system version if LATEST is specified in the URI
					versionSearch.sortBy(SearchResourceRequest.SortField.descending(CodeSystemVersionEntry.Fields.EFFECTIVE_DATE));
				} else {
					// try to fetch the path as exact version if not the special LATEST is specified in the URI
					versionSearch.filterByVersionId(uri.getPath());
				}
				// determine the final branch path, if based on the version search we find a version, then use that, otherwise use the defined path as relative branch of the code system working branch
				branchPath = versionSearch
						.build(codeSystem.getRepositoryUuid())
						.getRequest()
						.execute(context)
						.stream()
						.findFirst()
						.map(CodeSystemVersionEntry::getPath)
						.orElseGet(() -> {
							if (uri.isLatest()) {
								throw new BadRequestException("No CodeSystem version is present in '%s'. Explicit '%s/HEAD' can be used to retrieve the latest work in progress version of the CodeSystem.", codeSystem.getShortName(), codeSystem.getShortName());
							} else {
								return codeSystem.getRelativeBranchPath(uri.getPath()); 
							}
						});
			}
			
		}
		return branchPath;
	}

}
