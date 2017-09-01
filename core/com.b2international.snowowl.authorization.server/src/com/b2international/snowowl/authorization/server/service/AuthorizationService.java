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
package com.b2international.snowowl.authorization.server.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;

import com.b2international.snowowl.authorization.server.providers.IAuthorizationStrategy;
import com.b2international.snowowl.core.users.IAuthorizationService;
import com.b2international.snowowl.core.users.Permission;
import com.b2international.snowowl.core.users.Role;

/**
 * Service for authorizations related purposes.
 * 
 */
public class AuthorizationService implements IAuthorizationService {

	private final IAuthorizationStrategy strategy;

	public AuthorizationService(final IAuthorizationStrategy strategy) {
		this.strategy = checkNotNull(strategy, "strategy");
	}

	@Override
	public boolean isAuthorized(final String userId, final Permission permission) {
		return strategy.hasPermission(userId, permission.getId());
	}

	@Override
	public Collection<Role> getRoles(final String userId) {
		return strategy.getRoles(userId);
	}
}