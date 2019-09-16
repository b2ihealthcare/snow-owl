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

import java.util.Map;

import com.b2international.commons.exceptions.ForbiddenException;
import com.b2international.commons.exceptions.UnauthorizedException;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.DelegatingRequest;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.identity.IdentityProvider;
import com.b2international.snowowl.identity.domain.User;
import com.google.common.base.Strings;

/**
 * @since 7.2
 * @param <R> - the return value's type
 */
public final class AuthorizedRequest<R> extends DelegatingRequest<ServiceProvider, ServiceProvider, R> {

	public static final String AUTHORIZATION_HEADER = "Authorization";
	
	private final Map<String, String> headers;

	public AuthorizedRequest(final Map<String, String> headers, Request<ServiceProvider, R> next) {
		super(next);
		this.headers = headers;
	}

	@Override
	public R execute(ServiceProvider context) {
		final String authorizationToken = headers.get(AUTHORIZATION_HEADER);

		final IdentityProvider identityProvider = context.service(IdentityProvider.class);
		final User user;
		// if there is no authentication configured
		if (IdentityProvider.NOOP == identityProvider) {
			// allow execution as SYSTEM user
			user = User.SYSTEM;
		} else if (Strings.isNullOrEmpty(authorizationToken)) {
			// if there is authentication configured, but no authorization token found prevent execution and throw UnauthorizedException
			throw new UnauthorizedException("Missing authorization token");
		} else {
			// authenticate security token
			user = identityProvider.auth(authorizationToken);
			if (user == null) {
				throw new UnauthorizedException("Incorrect authorization token");
			}
			
			// authorize user whether it is permitted to execute the operation or not
			getNestedRequests()
			.stream()
			.filter(AccessControl.class::isInstance)
			.map(AccessControl.class::cast)
			.map(AccessControl::getPermission)
			.forEach(permissionRequirement -> {
				if (!user.hasPermission(permissionRequirement)) {
					throw new ForbiddenException("Operation not permitted. '%s' permission is required.", permissionRequirement.getPermission());
				}
			});
		}

		// inject the User for later access
		return next(context.inject()
				.bind(User.class, user)
				.bind(IEventBus.class, new AuthorizedEventBus(context.service(IEventBus.class), headers))
				.build());
	}
	
}
