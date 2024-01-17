/*
 * Copyright 2022 B2i Healthcare, https://b2ihealthcare.com
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

import java.util.Collection;
import java.util.SortedSet;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.TerminologyResource;
import com.b2international.snowowl.core.events.DelegatingRequest;
import com.b2international.snowowl.core.events.Request;
import com.google.common.collect.ImmutableSortedSet;

/**
 * @since 8.2
 * 
 * @param <C>
 * @param <T>
 * @param <R>
 */
public final class TerminologyResourceStatusCheckRequest<C extends ServiceProvider, R> extends DelegatingRequest<C, C, R> {

	private static final long serialVersionUID = 1L;
	private final SortedSet<String> forbiddenStatuses;

	public TerminologyResourceStatusCheckRequest(Request<C, R> next, Collection<String> forbiddenStatuses) {
		super(next);
		this.forbiddenStatuses = forbiddenStatuses == null ? ImmutableSortedSet.of() : ImmutableSortedSet.copyOf(forbiddenStatuses);
	}

	@Override
	public R execute(C context) {
		TerminologyResource resource = context.service(TerminologyResource.class);
		
		if (forbiddenStatuses.contains(resource.getStatus())) {
			throw new BadRequestException("Executing this request is forbidden on resources with one of the following states '%s'. Resource '%s' is in '%s' status.", this.forbiddenStatuses, resource.getTitle(), resource.getStatus());
		} else {
			return next(context);
		}
		
	}

}
