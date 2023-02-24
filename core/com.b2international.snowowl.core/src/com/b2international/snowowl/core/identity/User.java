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

import static com.google.common.base.Preconditions.checkArgument;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.b2international.commons.collections.Collections3;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Strings;

/**
 * Represents a logged in user in the system. A logged in user has access to his own userId and assigned permissions.
 * @since 5.11.0
 */
public final class User implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final User SYSTEM = new User("System", List.of(Permission.ADMIN));
	
	private final String userId;
	private final List<Permission> permissions;
	private final String accessToken;
	
	private Map<String, Object> authorizationContext = Collections.emptyMap();
	
	public User(String userId, List<Permission> permissions) {
		this(userId, permissions, null);
	}
	
	public User(String userId, List<Permission> permissions, String accessToken) {
		checkArgument(!Strings.isNullOrEmpty(userId), "userId may not be null or empty");
		this.userId = userId;
		this.permissions = Collections3.toImmutableList(permissions);
		this.accessToken = accessToken;
	}
	
	public String getUserId() {
		return userId;
	}
	
	@JsonIgnore
	public List<Permission> getPermissions() {
		return permissions;
	}
	
	public String getAccessToken() {
		return accessToken;
	}
	
	/**
	 * @return
	 * @deprecated present only for backward compatibility reasons to return a token value in the login endpoint
	 * @see #getAccessToken()
	 */
	public String getToken() {
		return accessToken;
	}
	
	@JsonIgnore
	public void setAuthorizationContext(Map<String, Object> authorizationContext) {
		this.authorizationContext = authorizationContext == null ? Collections.emptyMap() : Map.copyOf(authorizationContext);
	}
	
	@JsonIgnore
	public Map<String, Object> getAuthorizationContext() {
		return authorizationContext;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(userId);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		User other = (User) obj;
		return Objects.equals(userId, other.userId);
	}

	/**
	 * @return <code>true</code> if this user has a permission that implies all other permissions, <code>false</code> otherwise.
	 */
	@JsonIgnore
	public boolean isAdministrator() {
		return getPermissions().stream()
				.filter(Permission::isAdmin)
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
		return getPermissions().stream().anyMatch(permission -> permission.implies(permissionRequirement));
	}
	
	public User withAccessToken(String accessToken) {
		return new User(userId, permissions, accessToken);
	}
	
	public User withPermissions(List<Permission> permissions) {
		// TODO fix null accessToken here
		return new User(userId, permissions, null);
	}

	/**
	 * @param userId
	 * @return <code>true</code> if this User is the System user.
	 * @see #SYSTEM
	 */
	public static boolean isSystem(String userId) {
		return SYSTEM.getUserId().equals(userId);
	}

}