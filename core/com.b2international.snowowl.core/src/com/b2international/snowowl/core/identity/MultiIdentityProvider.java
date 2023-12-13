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
import java.util.List;
import java.util.stream.Collectors;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.setup.Environment;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * @since 5.11
 */
public final class MultiIdentityProvider implements IdentityProvider, IdentityWriter {

	private final List<IdentityProvider> providers;

	public MultiIdentityProvider(List<IdentityProvider> providers) {
		this.providers = providers;
	}
	
	@Override
	public void addUser(String username, String password) {
		// add the user to the first internal identity provider
		providers.stream()
			.filter(IdentityWriter.class::isInstance)
			.map(IdentityWriter.class::cast)
			.findFirst()
			.ifPresent(provider -> provider.addUser(username, password));
	}

	@Override
	public User auth(String username, String password) {
		for (IdentityProvider identityProvider : providers) {
			try {
				User user = identityProvider.auth(username, password);
				if (user != null) {
					return user;
				}
			} catch (BadRequestException e) {
				// ignore bad request exceptions coming from providers
			}
		}
		return IdentityProvider.super.auth(username, password);
	}
	
	@Override
	public User authJWT(String token) {
		for (IdentityProvider identityProvider : providers) {
			try {
				User user = identityProvider.authJWT(token);
				if (user != null) {
					return user;
				}
			} catch (BadRequestException e) {
				// ignore bad request exceptions coming from providers
			}
		}
		return IdentityProvider.super.authJWT(token);
	}

	@Override
	public Promise<Users> searchUsers(Collection<String> usernames, int limit) {
		return Promise.all(providers.stream().map(provider -> provider.searchUsers(usernames, limit)).collect(Collectors.toList()))
					.then(responses -> {
						final ImmutableList.Builder<User> users = ImmutableList.builder();
						int total = 0;
						for (Users matches : Iterables.filter(responses, Users.class)) {
							users.addAll(matches); 
							total += matches.getTotal();
						}
						return new Users(users.build(), limit, total);
					});
	}
	
	@Override
	public String getInfo() {
		return String.format("multi[%s]", providers.stream().map(IdentityProvider::getInfo).collect(Collectors.joining(",")));
	}
	
	@Override
	public void init(Environment env) throws Exception {
		for (final IdentityProvider provider : providers) {
			provider.init(env);
		}
		// validate multi-identity provider configuration that each JWT support has its own issuer configured
	}
	
	public List<IdentityProvider> getProviders() {
		return providers;
	}
	
}
