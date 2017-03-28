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

import java.util.List;

import com.b2international.snowowl.core.Repository;
import com.b2international.snowowl.core.Repository.Health;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.domain.RepositoryContextProvider;
import com.b2international.snowowl.core.events.DelegatingRequest;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.exceptions.NotAvailableException;
import com.google.common.collect.Lists;

/**
 * A delegating request that provides repository state assertion for a given allowed {@link Repository.Health health} states, before the request proceeds to executing the delegate.
 * @param <C>
 *            - the required context type for this {@link Request}
 * @param <B>
 *            - the type of the result
 * @since 5.8
 */
public class HealthCheckingRequest<C extends ServiceProvider, B> extends DelegatingRequest<C, ServiceProvider, B> {

	private static final long serialVersionUID = 1L;
	
	private List<Health> allowedHealthStates;
	private String repositoryId;

	protected HealthCheckingRequest(String repositoryId, Request<ServiceProvider, B> next, Repository.Health... healths) {
		super(next);
		this.repositoryId = repositoryId;
		this.allowedHealthStates = Lists.newArrayList(healths);
	}

	@Override
	public B execute(C context) {
		assertRepositoryHealth(context);
		return next(context.service(RepositoryContextProvider.class).get(repositoryId));
	}

	private void assertRepositoryHealth(C context) {
		Repository repository = getRepository(context);
		Health repositoryHealth = repository.getHealth();

		if (!getAllowedHealthStates().contains(repositoryHealth)) {
			throw new NotAvailableException("Requests for this repository [{}] are not allowed to execute with health state: {}.", repository.id(), repositoryHealth);
		}
	}

	private Repository getRepository(C context) {
		return context.service(RepositoryManager.class).get(repositoryId);
	}
	
	private List<Health> getAllowedHealthStates() {
		return allowedHealthStates;
	}
}
