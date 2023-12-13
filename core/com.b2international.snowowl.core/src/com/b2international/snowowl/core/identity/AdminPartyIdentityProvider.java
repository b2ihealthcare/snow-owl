/*
 * Copyright 2017-2022 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.identity;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.b2international.snowowl.core.events.util.Promise;

/**
 * Overrides roles of all users, so that each user can act as he would be an Administrator in the system.
 * 
 * @since 5.11
 */
class AdminPartyIdentityProvider implements IdentityProvider, IdentityWriter {

	private static final List<Permission> ADMINPARTY_ROLES = Collections.singletonList(Permission.ADMIN);

	private final IdentityProvider delegate;

	public AdminPartyIdentityProvider(IdentityProvider delegate) {
		this.delegate = delegate;
	}

	@Override
	public void addUser(String username, String password) {
		if (delegate instanceof IdentityWriter) {
			((IdentityWriter) delegate).addUser(username, password);
		}
	}

	@Override
	public User auth(String username, String token) {
		return overrideUserRoles(delegate.auth(username, token));
	}
	
	@Override
	public User authJWT(String token) {
		return overrideUserRoles(delegate.authJWT(token));
	}
	
	@Override
	public Promise<Users> searchUsers(Collection<String> usernames, int limit) {
		return delegate.searchUsers(usernames, limit)
					.then(matches -> {
						// override roles
						return new Users(
							matches.stream().map(user -> overrideUserRoles(user)).collect(Collectors.toList()), 
							matches.getLimit(), 
							matches.getTotal()
						);
					});
	}

	private User overrideUserRoles(User user) {
		if (user == null) {
			return null;
		} else {
			return new User(user.getUserId(), ADMINPARTY_ROLES);
		}
	}
	
	@Override
	public String getInfo() {
		return String.format("adminParty[%s]", delegate.getInfo());
	}

}
