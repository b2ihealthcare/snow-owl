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
package com.b2international.snowowl.core.identity;

/**
 * An {@link IdentityProvider} can additionally implement this interface to provide a way to change the underlying identities (add/modify/delete/etc.).
 * 
 * @since 5.11
 */
public interface IdentityWriter {

	/**
	 * Add a user to the identity provider, so that the user can authenticate and access resources.
	 * @param username
	 * @param password
	 */
	void addUser(String username, String password);
	
}
