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

import java.util.List;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.DelegatingRequest;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.uri.CodeSystemURI;
import com.b2international.snowowl.core.uri.ResourceURIPathResolver;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Iterables;

/**
 * @since 7.5
 */
public final class CodeSystemResourceRequest<R> extends DelegatingRequest<ServiceProvider, BranchContext, R> {

	private static final long serialVersionUID = 1L;
	
	@JsonProperty
	private final CodeSystemURI uri;
	
	private transient CodeSystem codeSystem;
	
	private transient String branchPath;
	
	public CodeSystemResourceRequest(CodeSystemURI codeSystemUri, Request<BranchContext, R> next) {
		super(next);
		this.uri = codeSystemUri;
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

	public CodeSystem getCodeSystem(ServiceProvider context) {
		if (codeSystem == null) {
			codeSystem = CodeSystemRequests.getCodeSystem(context, uri.getCodeSystem());
		}
		return codeSystem;
	}
	
	public String getRepositoryId(ServiceProvider context) {
		return getCodeSystem(context).getRepositoryId();
	}
	
	public String getBranchPath(ServiceProvider context) {
		if (branchPath == null) {
			branchPath = Iterables.getOnlyElement(context.service(ResourceURIPathResolver.class).resolve(context, List.of(uri)));
		}
		return branchPath;
	}

}
