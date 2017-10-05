/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.identity;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.identity.domain.Role;
import com.b2international.snowowl.identity.domain.User;
import com.b2international.snowowl.identity.domain.Users;

/**
 * Overrides roles of all users, so that each user can act as he would be an Administrator in the system.
 * 
 * @since 5.11
 */
class AdminPartyIdentityProvider implements InternalIdentityProvider {

	private static final List<Role> ADMINPARTY_ROLES = Collections.singletonList(Role.ADMINISTRATOR);

	private final IdentityProvider delegate;

	public AdminPartyIdentityProvider(IdentityProvider delegate) {
		this.delegate = delegate;
	}

	@Override
	public void addUser(String username, String password) {
		if (delegate instanceof InternalIdentityProvider) {
			((InternalIdentityProvider) delegate).addUser(username, password);
		}
	}

	@Override
	public boolean auth(String username, String token) {
		return delegate.auth(username, token);
	}

	@Override
	public Promise<Users> searchUsers(Collection<String> usernames, int offset, int limit) {
		return delegate.searchUsers(usernames, offset, limit)
					.then(matches -> {
						// override roles
						return new Users(
							matches.stream().map(user -> new User(user.getUsername(), ADMINPARTY_ROLES)).collect(Collectors.toList()), 
							matches.getOffset(), 
							matches.getLimit(), 
							matches.getTotal()
						);
					});
	}
	
	@Override
	public String getInfo() {
		return String.format("adminParty[%s]", delegate.getInfo());
	}

}
