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
package com.b2international.snowowl.core.users;

import java.util.Collection;
import java.util.Set;

/**
 * 
 * User manager interface to handle users.
 * 
 *
 */
public interface IUserManager {
	
	/**
	 * @return The immutable and unmodifiable set of registered users.
	 */
	public Set<User> getUsers();
		
	/**
	 * Adds a user to the store with the provided credentials.
	 * @param hashSet 
	 * @param username the username.
	 * @param password the password.
	 * @param roles set of roles.
	 */
	public void addUser(User user, Collection<Role> roles);

	/**
	 * Removes a registered user identified by the passed in username.  Returns {@code false} if no user has been registered with this username otherwise it returns {@code true}.
	 * @param username the unique username.
	 * @return {@code true} if the user removal was successful otherwise returns {@code false}.
	 */
	public boolean removeUser(String username);
	
	/**
	 * Returns the {@link User} instance associated and identified by the passed in unique username.
	 * @param username the unique username of the user.
	 * @return a User instance identified by the passed in username. Returns with {@code null} if no user exists with this username.
	 */
	public User getUser(String username);
}