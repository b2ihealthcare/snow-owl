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
import java.util.List;
import java.util.stream.Collectors;

import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.identity.domain.User;
import com.b2international.snowowl.identity.domain.Users;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * @since 5.11
 */
final class MultiIdentityProvider implements InternalIdentityProvider {

	private final List<IdentityProvider> providers;

	public MultiIdentityProvider(List<IdentityProvider> providers) {
		this.providers = providers;
	}
	
	@Override
	public void addUser(String username, String password) {
		// add the user to the first internal identity provider
		providers.stream()
			.filter(InternalIdentityProvider.class::isInstance)
			.map(InternalIdentityProvider.class::cast)
			.findFirst()
			.ifPresent(provider -> provider.addUser(username, password));
	}

	@Override
	public boolean auth(String username, String token) {
		for (IdentityProvider identityProvider : providers) {
			if (identityProvider.auth(username, token)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Promise<Users> searchUsers(Collection<String> usernames, int offset, int limit) {
		return Promise.all(providers.stream().map(provider -> provider.searchUsers(usernames, offset, limit)).collect(Collectors.toList()))
					.then(responses -> {
						final ImmutableList.Builder<User> users = ImmutableList.builder();
						int total = 0;
						for (Users matches : Iterables.filter(responses, Users.class)) {
							users.addAll(matches); 
							total += matches.getTotal();
						}
						return new Users(users.build(), offset, limit, total);
					});
	}
	
	@Override
	public String getInfo() {
		return String.format("multi[%s]", providers.stream().map(IdentityProvider::getInfo).collect(Collectors.joining(",")));
	}

}
