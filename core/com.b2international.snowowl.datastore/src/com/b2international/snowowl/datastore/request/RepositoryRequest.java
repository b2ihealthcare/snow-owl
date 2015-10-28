/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.request;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.domain.DefaultRepositoryContext;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.domain.exceptions.CodeSystemNotFoundException;
import com.b2international.snowowl.core.events.DelegatingRequest;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.IBranchPathMap;
import com.b2international.snowowl.datastore.ICodeSystem;
import com.b2international.snowowl.datastore.TerminologyRegistryService;
import com.b2international.snowowl.datastore.UserBranchPathMap;

/**
 * @since 4.5
 */
public final class RepositoryRequest<B> extends DelegatingRequest<ServiceProvider, RepositoryContext, B> {

	private static final IBranchPathMap MAIN_BRANCH_PATH_MAP = new UserBranchPathMap();
	
	private final String codeSystemShortName;

	// TODO replace short name with repositoryId or define which is the one true ID
	public RepositoryRequest(String codeSystemShortName, Request<RepositoryContext, B> next) {
		super(next);
		this.codeSystemShortName = codeSystemShortName;
	}
	
	@Override
	public B execute(final ServiceProvider context) {
		final String repositoryId = ensureAvailability(context);
		return next(new DefaultRepositoryContext(context, repositoryId));
	}
	
	private String ensureAvailability(ServiceProvider context) {
		// XXX: in case of a non-MAIN-registered code system, we would need a repository UUID to get the code system to get the repository UUID
		final ICodeSystem codeSystem = context.service(TerminologyRegistryService.class).getCodeSystemByShortName(MAIN_BRANCH_PATH_MAP, codeSystemShortName);
		if (codeSystem == null) {
			throw new CodeSystemNotFoundException(codeSystemShortName);
		}
		return codeSystem.getRepositoryUuid();
	}

}
