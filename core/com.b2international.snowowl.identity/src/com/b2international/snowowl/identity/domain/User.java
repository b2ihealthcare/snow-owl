/*
 * Copyright 2017-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Represents a logged in user in the system. A logged in user has access to his own username and assigned roles (and permissions).
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
	
	@JsonIgnore
	public List<Permission> getPermissions() {
		return getRoles().stream().flatMap(role -> role.getPermissions().stream()).distinct().collect(Collectors.toList());
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

	/**
	 * @return <code>true</code> if this user has a permission that implies all other permissions, <code>false</code> otherwise.
	 */
	public boolean isAdministrator() {
		return getPermissions().stream()
				.filter(p -> {
					return Permission.ALL.equals(p.getOperation()) && Permission.ALL.equals(p.getResource());
				})
				.findFirst()
				.isPresent();
	}
	
	/**
	 * Returns <code>true</code> if the user has the necessary permission to allow performing an operation on the given resource.
	 *  
	 * @param permissionRequirement
	 * @return
	 */
	public boolean hasPermission(Permission permissionRequirement) {
		return getRoles().stream()
			.map(Role::getPermissions)
			.flatMap(Collection::stream)
			.anyMatch(permission -> permission.implies(permissionRequirement));
	}

	/**
	 * @param userId
	 * @return <code>true</code> if this User is the System user.
	 * @see #SYSTEM
	 */
	public static boolean isSystem(String userId) {
		return SYSTEM.getUsername().equals(userId);
	}

}