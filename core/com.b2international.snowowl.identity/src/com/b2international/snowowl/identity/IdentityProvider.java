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
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.identity.domain.Role;
import com.b2international.snowowl.identity.domain.User;
import com.b2international.snowowl.identity.domain.Users;
import com.google.common.collect.ImmutableList;

/**
 * @since 5.11
 */
public interface IdentityProvider {

	/**
	 * Global logger that should be used for identity provider related logging. 
	 */
	Logger LOG = LoggerFactory.getLogger("identity");
	
	/**
	 * @since 7.0 
	 */
	IdentityProvider NOOP = new IdentityProvider() {
		
		@Override
		public boolean auth(String username, String token) {
			return true;
		}
		
		@Override
		public Promise<Users> searchUsers(Collection<String> usernames, int limit) {
			// generate fake Users for given usernames with admin permission
			final List<User> users = usernames.stream()
					.limit(limit)
					.map(username -> new User(username, ImmutableList.of(Role.ADMINISTRATOR)))
					.collect(Collectors.toList());
			return Promise.immediate(new Users(users, limit, usernames.size()));
		}
		
		@Override
		public String getInfo() {
			return "unprotected";
		}
		
	};
	
	/**
	 * @since 5.11
	 */
	final class Factory {
		
		private static ServiceLoader<IdentityProviderFactory> FACTORIES;
		
		static {
			FACTORIES = ServiceLoader.load(IdentityProviderFactory.class, IdentityProviderFactory.class.getClassLoader());
		}
		
		private Factory() {}
		
		/**
		 * Creates a new {@link IdentityProvider} instance based on the currently available {@link IdentityProviderFactory} instances provided by the fragments of this bundle.
		 * @return
		 */
		public static List<IdentityProvider> createProviders(Environment env, Collection<IdentityProviderConfig> providerConfigurations) {
			final ImmutableList.Builder<IdentityProvider> providers = ImmutableList.builder();
			Iterator<IdentityProviderFactory> it = FACTORIES.iterator();
			while (it.hasNext()) {
				IdentityProviderFactory<IdentityProviderConfig> factory = it.next();
				Optional<IdentityProviderConfig> providerConfig = providerConfigurations.stream().filter(conf -> conf.getClass() == factory.getConfigType()).findFirst();
				if (providerConfig.isPresent()) {
					try {
						providers.add(factory.create(env, providerConfig.get()));
					} catch (Exception e) {
						throw new SnowowlRuntimeException(String.format("Couldn't initialize '%s' identity provider", factory), e);
					}
				}
			}
			return providers.build();
		}

		public static Collection<Class<? extends IdentityProviderConfig>> getAvailableConfigClasses() {
			final ImmutableList.Builder<Class<? extends IdentityProviderConfig>> configs = ImmutableList.builder();
			final Iterator<IdentityProviderFactory> it = FACTORIES.iterator();
			while (it.hasNext()) {
				configs.add(it.next().getConfigType());
			}
			return configs.build();
		}
		
	}
	
	/**
	 * Authenticates the given user and his security token (eg. password, JWT, etc. depending on the implementation).
	 * 
	 * @param username
	 *            - the user who would like to log in to the system
	 * @param token
	 *            - any kind of security token (simplest form is a password, but can be a JWT)
	 * @return <code>true</code> if the user and his security token both valid, otherwise return <code>false</code>
	 */
	boolean auth(String username, String token);
	
	/**
	 * Filters and return users based on the given filters. In case of no filters returns all users (paged response). 
	 * @param usernames - filter by user name
	 * @param limit - paging limit to specify how many users should we read from the users collection
	 * @return
	 */
	Promise<Users> searchUsers(Collection<String> usernames, int limit);
	
	/**
	 * Returns a summary like information about this {@link IdentityProvider}, usually it contains the type and some non-sensitive configuration values
	 * @return 
	 */
	String getInfo();
	
}
