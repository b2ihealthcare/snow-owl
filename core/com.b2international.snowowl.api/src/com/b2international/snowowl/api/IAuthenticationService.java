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
package com.b2international.snowowl.api;

/**
 * Implementations of this interface are responsible for checking incoming HTTP request credentials for the terminology
 * server's REST services.
 */
public interface IAuthenticationService {

	/**
	 * Authenticates the request's originating user using the supplied user identifier and password.
	 * 
	 * @param username the identifier of the user to authenticate
	 * @param password the user's supplied password
	 * 
	 * @return {@code true} if the user exists and the entered password matches the stored one, {@code false} otherwise
	 */
	boolean authenticate(String username, String password);
}
