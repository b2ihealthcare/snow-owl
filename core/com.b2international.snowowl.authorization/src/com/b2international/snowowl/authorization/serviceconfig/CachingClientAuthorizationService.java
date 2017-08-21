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
package com.b2international.snowowl.authorization.serviceconfig;

import java.text.MessageFormat;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.users.IAuthorizationService;
import com.b2international.snowowl.core.users.IClientAuthorizationService;
import com.b2international.snowowl.core.users.Permission;
import com.b2international.snowowl.core.users.Role;
import com.b2international.snowowl.core.users.User;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Client side authorization service, caching the roles for currently logged in user. 
 * 
 */
public class CachingClientAuthorizationService implements IClientAuthorizationService {

	private final LoadingCache<String, Collection<Role>> userRolesCache;

	private static final Logger LOGGER = LoggerFactory.getLogger(CachingClientAuthorizationService.class);

	public CachingClientAuthorizationService(final IAuthorizationService delegateAuthorizationService) {
		userRolesCache = CacheBuilder.newBuilder().build(new CacheLoader<String, Collection<Role>>() {
			@Override
			public Collection<Role> load(final String userNameKey) {
				return delegateAuthorizationService.getRoles(userNameKey);
			}
		});
	}

	@Override
	public boolean isAuthorized(final Permission authorizable) {
		final Collection<Role> roles = getRoles();
		for (final Role role : roles) {
			final Collection<Permission> permissions = role.getPermissions();
			for (final Permission permission : permissions) {
				if (permission.getId().equals(authorizable.getId())) {
					LOGGER.debug(MessageFormat.format("User ({0}) granted permission [{1}]", getLoggedInUser().getUserName(), authorizable.getId()));
					return true;
				}
			}
		}
		LOGGER.debug(MessageFormat.format("User ({0}) denied of permission [{1}]", getLoggedInUser().getUserName(), authorizable.getId()));
		return false;
	}

	@Override
	public Collection<Role> getRoles() {
		return userRolesCache.getUnchecked(getLoggedInUser().getUserName());
	}

	private User getLoggedInUser() {
		return ApplicationContext.getInstance().getService(ICDOConnectionManager.class).getUser();
	}
}