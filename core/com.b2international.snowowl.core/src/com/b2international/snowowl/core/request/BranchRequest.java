/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.base.Preconditions.checkNotNull;

import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.DelegatingRequest;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.uri.ResourceURIPathResolver.PathWithVersion;

/**
 * @since 4.5
 */
public final class BranchRequest<B> extends DelegatingRequest<RepositoryContext, BranchContext, B> {

	private final String branchPath;
	private final ResourceURI versionResourceURI;
	
	public BranchRequest(String branchPath, Request<BranchContext, B> next) {
		super(next);
		this.branchPath = checkNotNull(branchPath, "branchPath");
		this.versionResourceURI = null;
	}
	
	public BranchRequest(PathWithVersion branchPathWithVersion, Request<BranchContext, B> next) {
		super(next);
		this.branchPath = branchPathWithVersion.getPath();
		this.versionResourceURI = branchPathWithVersion.getVersionResouceURI();
	}

	public String getBranchPath() {
		return branchPath;
	}
	
	@Override
	public B execute(RepositoryContext context) {
		BranchContext branchContext = context.openBranch(context, branchPath);
		if (versionResourceURI != null) {
			branchContext = branchContext.inject()
				.bind(ResourceURI.class, versionResourceURI)
				.build();
		}
		
		return next(branchContext);
	}
}
