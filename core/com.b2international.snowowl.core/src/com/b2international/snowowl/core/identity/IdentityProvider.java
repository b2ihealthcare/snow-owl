/*
 * Copyright 2017-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.events.util.Promise;

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
		public User auth(String username, String password) {
			return new User(username, List.of(Permission.ADMIN));
		}
		
		@Override
		public Promise<Users> searchUsers(Collection<String> usernames, int limit) {
			// generate fake Users for given usernames with admin permission
			final List<User> users = usernames.stream()
					.limit(limit)
					.map(username -> new User(username, List.of(Permission.ADMIN)))
					.collect(Collectors.toList());
			return Promise.immediate(new Users(users, limit, usernames.size()));
		}
		
		@Override
		public String getInfo() {
			return "unprotected";
		}

		@Override
		public void validateSettings() {
			// Nothing to do
		}
	};

	/**
	 * External AccessToken based authentication/authorization instance. Always relies on the incoming access token to verify user identity and access.
	 * 
	 * @since 8.1
	 */
	IdentityProvider JWT = new IdentityProvider() {
		
		@Override
		public User auth(String username, String password) {
			throw new UnsupportedOperationException("AccessToken based authentication/authorization does not support username/password");
		}
		
		@Override
		public String getInfo() {
			return "jwt";
		}
		
		@Override
		public Promise<Users> searchUsers(Collection<String> usernames, int limit) {
			throw new UnsupportedOperationException("AccessToken based authentication/authorization does not support searching users");
		}
		
		@Override
		public void validateSettings() throws Exception {
		}
		
	};	
	
	/**
	 * Authenticates a username and password.
	 * @param username - a username to use for authentication
	 * @param password - the user's password to use for authentication
	 * @return an authenticated {@link User} and its permissions or <code>null</code> if the username or password is incorrect.
	 */
	User auth(String username, String password);
	
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
	
	/**
	 * Performs startup-time checks to ensure that the provided parameters are correct.
	 * @throws Exception if a configuration value is invalid, or the
	 *         identity provider can not work correctly under the currently given conditions
	 */
	void validateSettings() throws Exception;
}
