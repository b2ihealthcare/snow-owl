/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.authorization;

import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.DelegatingRequest;
import com.b2international.snowowl.core.events.Request;

/**
 * @since 7.2
 * @param <R> - the return value's type
 */
public final class AuthorizedRequest<R> extends DelegatingRequest<ServiceProvider, ServiceProvider, R> {

	public AuthorizedRequest(Request<ServiceProvider, R> next) {
		super(next);
	}

	@Override
	public R execute(ServiceProvider context) {
		// search for nested requests with Operation annotation
		final Set<String> operations = getNestedRequests()
			.stream()
			.filter(r -> r.getClass().isAnnotationPresent(Operation.class))
			.map(r -> r.getClass().getAnnotation(Operation.class).value())
			.collect(Collectors.toSet());
		
		return next(context);
	}
	
}
