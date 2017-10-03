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
package com.b2international.snowowl.identity.domain;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Stores user credentials, which includes the user's name, the password and the user's roles.
 * @since 5.11.0
 */
public final class User implements Serializable {

	public static final User SYSTEM = new User("System", Collections.singletonList(Role.ADMINISTRATOR));
	
	private final String username;
	private final List<Role> roles;

	public User(String username, List<Role> roles) {
		this.username = username;
		this.roles = roles;
	}
	
	public String getUsername() {
		return username;
	}
	
	public List<Role> getRoles() {
		return roles;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(username);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		User other = (User) obj;
		return Objects.equals(username, other.username);
	}

	public boolean isAdministrator() {
		return getRoles().contains(Role.ADMINISTRATOR);
	}
	
	public boolean hasPermission(String permission) {
		return getRoles().stream()
			.map(Role::getPermissions)
			.flatMap(Collection::stream)
			.collect(Collectors.toSet())
			.contains(permission);
	}

	public static boolean isSystem(String userId) {
		return SYSTEM.getUsername().equals(userId);
	}
	
}