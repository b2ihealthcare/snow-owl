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

/**
 * Client side interface for Authorization service.
 * <p>
 * <b>Note:</b> Implementation should use cache lookup before delegating further to the server. Frequent calls to server
 * (and possible LDAP beyond the server) ause performance degradation.
 * </p>
 * 
 */
public interface IClientAuthorizationService {

	/**
	 * Checks whether the current user is authorized to perform a certain action.
	 * 
	 * @param permission the permission to check
	 * @return {@code true} if the user can proceed with the action, {@code false} if the user is not permitted to
	 * perform the action
	 */
	boolean isAuthorized(Permission permission);

	/**
	 * Returns a collection of roles the user belongs to.
	 * 
	 * @return the current user's roles
	 */
	Collection<Role> getRoles();
}