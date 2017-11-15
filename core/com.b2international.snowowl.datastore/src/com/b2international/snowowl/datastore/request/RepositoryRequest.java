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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.domain.DelegatingContext;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.domain.RepositoryContextProvider;
import com.b2international.snowowl.core.events.DelegatingRequest;
import com.b2international.snowowl.core.events.Request;
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
		DelegatingContext.Builder<? extends RepositoryContext> repositoryContext = context.service(RepositoryContextProvider.class).get(repositoryId).inject();
		
		// by default add a NullProgressMonitor binding to the context
		// if the previous context is a delegate context, injecting all services can override this safely 
		repositoryContext.bind(IProgressMonitor.class, new NullProgressMonitor());
		
		if (context instanceof DelegatingContext) {
			repositoryContext.bindAll((DelegatingContext) context);
		}
		
		return next(repositoryContext.build());
	}
}
