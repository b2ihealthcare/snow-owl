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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.exceptions.ForbiddenException;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.identity.User;

/**
 * A simple authorization interface that allows plug-ins to customize the default behavior of authorization. 
 * 
 * @since 8.4.0
 */
public interface AuthorizationService {

	/**
	 * Global logger that should be used for authorization related logging. 
	 */
	Logger LOG = LoggerFactory.getLogger("authorization");
	
	/**
	 * The default fallback no-op service when no authorization service has been configured by any of the known plug-ins.
	 */
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
				throwForbiddenException(requiredPermission);
			}
		});
		return user;
	}

	/**
	 * Throws a {@link ForbiddenException} using the given required permission's descriptor.
	 * @param requiredPermission - the permissions that was required but it was missing from the current user's permission list
	 */
	default void throwForbiddenException(Permission requiredPermission) {
		throw new ForbiddenException("Operation not permitted. '%s' permission is required.", requiredPermission.getPermission());
	}
	
	/**
	 * Retrieves the list of accessible resources for the given user. By default this method returns all permission resources unfiltered, which is basically all resources the user has access to.
	 * 
	 * @param context - to use when checking for accessible resources, can be used to register request scoped metrics, or access other services if needed
	 * @param user - the user to return the list of authorized resources
	 * @return a {@link Set} of authorized resource IDs for the user
	 */
	default Set<String> getAccessibleResources(ServiceProvider context, User user) {
		return user.getPermissions()
				.stream()
				.flatMap(p -> p.getResources().stream())
				.collect(Collectors.toSet());
	}

	default boolean isDefault() {
		return AuthorizationService.DEFAULT == this;
	}
	
}
