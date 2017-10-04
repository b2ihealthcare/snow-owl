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

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.identity.domain.Users;
import com.google.common.base.Strings;

/**
 * @since 5.11
 */
public interface IdentityProvider {

	/**
	 * Global logger that should be used for identity provider related logging. 
	 */
	Logger LOG = LoggerFactory.getLogger("identity");
	
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
		public static IdentityProvider createInstance(Environment env, String type, Map<String, Object> configuration) {
			checkArgument(!Strings.isNullOrEmpty(type), "Type argument cannot be null or empty");
			Iterator<IdentityProviderFactory> it = FACTORIES.iterator();
			while (it.hasNext()) {
				IdentityProviderFactory factory = it.next();
				if (type.equals(factory.getType())) {
					try {
						return factory.create(env, configuration);
					} catch (Exception e) {
						throw new SnowowlRuntimeException(String.format("Couldn't initialize '%s' identity provider", type), e);
					}
				}
			}
			throw new IllegalArgumentException("No identity manager factory found with type: " + type);
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
	 * Add a user to the identity provider, so that the user can authenticate and access resources.
	 * @param username
	 * @param password
	 */
	void addUser(String username, String password);
	
	/**
	 * Filters and return users based on the given filters. In case of no filters returns all users (paged response). 
	 * @param usernames - filter by user name
	 * @param offset - paging offset to specify where to start reading the users collection
	 * @param limit - paging limit to specify how many users should we read from the users collection
	 * @return
	 */
	Promise<Users> searchUsers(Collection<String> usernames, int offset, int limit);
	
}
