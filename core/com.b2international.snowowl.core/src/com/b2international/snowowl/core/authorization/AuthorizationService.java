/*
 * Copyright 2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.authorization;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.commons.exceptions.ForbiddenException;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.identity.User;

/**
 * A simple authorization interface that allows plug-ins to customize the default behavior of authorization. 
 * 
 * @since 8.4.0
 */
public interface AuthorizationService {

	AuthorizationService DEFAULT = new AuthorizationService() {};
	
	/**
	 * Checks whether the user has all of the required permissions and throws an error if one is missing from the currently assigned permissions.
	 * Implementors should fall back to this algorithm to provide the default authorization checks for automated scripts and other tokens that carry
	 * the permission information inside them.
	 * 
	 * @param user
	 *            - the user who is currently executing a request
	 * @param requiredPermissions
	 *            - the request set of permissions the user must have in order to continue the execution of the request
	 * @return the updated user object with cached permissions
	 * @throws ForbiddenException
	 *             - this method must throw a {@link ForbiddenException} in order to reject execution of the request when the user does not have
	 *             sufficient privileges
	 */
	default User checkPermission(User user, List<Permission> requiredPermissions) {
		requiredPermissions.forEach(requiredPermission -> {
			if (!user.hasPermission(requiredPermission)) {
				throw new ForbiddenException("Operation not permitted. '%s' permission is required. User has '%s'.", requiredPermission.getPermission(), user.getPermissions());
			}
		});
		return user;
	}
	
	/**
	 * Retrieves the list of accessible resources for the given user. By default this method returns all permission resources unfiltered, which is basically all resources the user has access to.
	 * 
	 * @param user - the user to return the list of authorized resources
	 * @return a {@link Set} of authorized resource IDs for the user
	 */
	default Set<String> getAccessibleResources(User user) {
		return user.getPermissions()
				.stream()
				.flatMap(p -> p.getResources().stream())
				.collect(Collectors.toSet());
	}
	
}
