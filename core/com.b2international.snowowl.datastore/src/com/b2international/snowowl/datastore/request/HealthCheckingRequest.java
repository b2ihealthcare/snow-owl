/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.RepositoryInfo;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.DelegatingRequest;
import com.b2international.snowowl.core.events.Request;
import com.google.common.collect.ImmutableList;

/**
 * A delegating request that provides repository state assertion for a given allowed {@link RepositoryInfo.Health health} states, before the request proceeds to executing the delegate.
 * @param <B>
 *            - the type of the result
 * @since 5.8
 */
public final class HealthCheckingRequest<B> extends DelegatingRequest<RepositoryContext, RepositoryContext, B> {

	private static final long serialVersionUID = 1L;
	
	private final List<RepositoryInfo.Health> allowedHealthStates;

	public HealthCheckingRequest(Request<RepositoryContext, B> next, RepositoryInfo.Health... healths) {
		super(next);
		this.allowedHealthStates = ImmutableList.copyOf(healths);
	}

	@Override
	public B execute(RepositoryContext context) {
		if (allowedHealthStates.contains(context.health())) {
			return next(context);
		}
		throw new BadRequestException("Requests for repository '%s' are not allowed to execute with health state '%s'.", context.id(), context.health());
	}

}
