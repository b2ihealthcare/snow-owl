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
package com.b2international.snowowl.core.rate;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.DelegatingRequest;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.identity.domain.User;

/**
 * @since 7.2
 * @param <R> - the response body type
 */
public final class RateLimitingRequest<R> extends DelegatingRequest<ServiceProvider, ServiceProvider, R> {

	public RateLimitingRequest(Request<ServiceProvider, R> next) {
		super(next);
	}

	@Override
	public R execute(ServiceProvider context) {
		final User user = context.service(User.class);
		if (user != User.SYSTEM) {
			// rate limit only non-system user requests
			final String username = user.getUsername();
			RateLimitConsumption consumption = context.service(RateLimiter.class).consume(username);
			if (consumption.isConsumed()) {
				// TODO set remaining header
			} else {
				// TODO throw error and set wait time header
			}
		}
		
		return next(context);
	}
	
}
