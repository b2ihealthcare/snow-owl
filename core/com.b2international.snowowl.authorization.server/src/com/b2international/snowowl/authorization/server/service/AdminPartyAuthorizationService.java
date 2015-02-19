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

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.b2international.snowowl.core.users.IAuthorizationService;
import com.b2international.snowowl.core.users.Permission;
import com.b2international.snowowl.core.users.Role;
import com.b2international.snowowl.core.users.SpecialRole;

/**
 * {@link IAuthorizationService} which recognizes all users as admin using the
 * role {@link SpecialRole#ADMINISTRATOR}.
 * 
 * @since 3.4
 */
public class AdminPartyAuthorizationService implements IAuthorizationService {

	private static final Set<Role> ADMINPARTY_ROLES = Collections.singleton(SpecialRole.ADMINISTRATOR);

	@Override
	public boolean isAuthorized(String userId, Permission permission) {
		return true;
	}

	@Override
	public Collection<Role> getRoles(String userId) {
		return ADMINPARTY_ROLES;
	}
}