/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.base.Preconditions.checkNotNull;

import com.b2international.snowowl.core.Repository;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.domain.RepositoryContextProvider;
import com.b2international.snowowl.core.events.DelegatingRequest;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 4.5
 */
public final class RepositoryRequest<B> extends DelegatingRequest<ServiceProvider, RepositoryContext, B> {

	@JsonProperty
	private final String repositoryId;

	public RepositoryRequest(String repositoryId, Request<RepositoryContext, B> next) {
		super(next);
		this.repositoryId = checkNotNull(repositoryId, "repositoryId");
	}
	
	@Override
	public B execute(final ServiceProvider context) {
		checkRepositoryConsistency(context);
		return next(context.service(RepositoryContextProvider.class).get(repositoryId));
	}

	private void checkRepositoryConsistency(final ServiceProvider context) {
		if (!next().needsConsistencyCheck())
			return;
		
		Repository repository = context.service(RepositoryManager.class).get(repositoryId);
		
		if (repository.getRepositoryState() == Repository.RepositoryState.INCONSISTENT) {
			throw new BadRequestException(String.format("Repository [%s] is in %s state. Until consistency is restored requests to this repository will fail. Consistency can be restored by restoring a consistent state or re-indexing this repository.", repository.id(), repository.getRepositoryState().toString().toLowerCase()));
		}
	}
	
}
