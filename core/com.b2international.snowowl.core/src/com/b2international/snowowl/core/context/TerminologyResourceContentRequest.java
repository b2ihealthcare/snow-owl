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
package com.b2international.snowowl.core.context;

import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.TerminologyResource;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.DelegatingRequest;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.request.BranchRequest;
import com.b2international.snowowl.core.request.RepositoryRequest;
import com.b2international.snowowl.core.uri.ResourceURIPathResolver;

/**
 * @since 7.5
 */
public final class TerminologyResourceContentRequest<R> extends DelegatingRequest<TerminologyResourceContext, BranchContext, R> {

	private static final long serialVersionUID = 1L;
	
	public TerminologyResourceContentRequest(final Request<BranchContext, R> next) {
		super(next);
	}

	@Override
	public R execute(TerminologyResourceContext context) {
		final ResourceURI resourceURI = context.resourceURI();
		final TerminologyResource resource = context.resource();
		final String branchPath = context.service(ResourceURIPathResolver.class).resolve(context, resourceURI, resource);
		return new RepositoryRequest<R>(resource.getToolingId(),
			new BranchRequest<R>(branchPath,
				next()
			)
		).execute(context);
	}

}
