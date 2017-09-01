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
package com.b2international.snowowl.authorization.server.providers;

import java.text.MessageFormat;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.users.Permission;
import com.b2international.snowowl.core.users.Role;

/**
 * 
 */
public abstract class AbstractAuthorizationStrategy implements IAuthorizationStrategy {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAuthorizationStrategy.class);

	@Override
	public boolean hasPermission(final String userId, final String permissionId) {
		final Collection<Role> roles = getRoles(userId);

		for (final Role role : roles) {
			final Collection<Permission> permissions = role.getPermissions();

			for (final Permission permission : permissions) {

				if (permission.getId().equals(permissionId)) {
					LOGGER.debug(MessageFormat.format("User ({0}) granted permission [{1}]", userId, permissionId));
					return true;
				}
			}
		}

		LOGGER.debug(MessageFormat.format("User ({0}) denied of permission [{1}]", userId, permissionId));
		return false;
	}
}