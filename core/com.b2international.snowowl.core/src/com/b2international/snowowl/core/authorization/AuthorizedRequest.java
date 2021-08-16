/*
 * Copyright 2019-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Collection;

import com.b2international.commons.exceptions.ForbiddenException;
import com.b2international.commons.exceptions.UnauthorizedException;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.DelegatingRequest;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.util.RequestHeaders;
import com.b2international.snowowl.core.identity.IdentityProvider;
import com.b2international.snowowl.core.identity.User;
import com.b2international.snowowl.core.util.PlatformUtil;
import com.b2international.snowowl.eventbus.IEventBus;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

/**
 * @since 7.2
 * @param <R> - the return value's type
 */
public final class AuthorizedRequest<R> extends DelegatingRequest<ServiceProvider, ServiceProvider, R> {

	private static final long serialVersionUID = 1L;
	
	public static final String AUTHORIZATION_HEADER = "Authorization";
	
	public AuthorizedRequest(Request<ServiceProvider, R> next) {
		super(next);
	}

	@Override
	public R execute(ServiceProvider context) {
		final RequestHeaders requestHeaders = context.service(RequestHeaders.class);
		final String authorizationToken = requestHeaders.header(AUTHORIZATION_HEADER);

		final IdentityProvider identityProvider = context.service(IdentityProvider.class);
		final Collection<Request<?, ?>> requests = getNestedRequests();
		
		final User user;
		// if there is no authentication configured
		if (IdentityProvider.NOOP == identityProvider) {
			// allow execution as SYSTEM user
			user = User.SYSTEM;
		} else if (Strings.isNullOrEmpty(authorizationToken)) {
			// allow login requests in
			if (requests.stream().allMatch(req -> req.getClass().isAnnotationPresent(Unprotected.class))) {
				user = User.SYSTEM;
			} else {
				// if there is authentication configured, but no authorization token found prevent execution and throw UnauthorizedException
				if (PlatformUtil.isDevVersion()) {
					Request<?, ?> request = Iterables.getFirst(requests, null);
					System.err.println(request);
				}
				throw new UnauthorizedException("Missing authorization token");
			}
		} else {
			// authenticate security token
			user = identityProvider.auth(authorizationToken);
			if (user == null) {
				throw new UnauthorizedException("Incorrect authorization token");
			}
			
			// authorize user whether it is permitted to execute the request(s) or not
			requests
				.stream()
				.filter(AccessControl.class::isInstance)
				.map(AccessControl.class::cast)
				.map(ac -> ac.getPermission(context))
				.forEach(permissionRequirement -> {
					if (!user.hasPermission(permissionRequirement)) {
						throw new ForbiddenException("Operation not permitted. '%s' permission is required.", permissionRequirement.getPermission());
					}
				});
		}

		// inject the User for later access
		return next(context.inject()
				.bind(User.class, user)
				.bind(IEventBus.class, new AuthorizedEventBus(context.service(IEventBus.class), requestHeaders.headers()))
				.build());
	}

}
