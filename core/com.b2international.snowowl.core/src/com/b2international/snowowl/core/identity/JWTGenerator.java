/*
 * Copyright 2021-2023 B2i Healthcare, https://b2ihealthcare.com
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

import java.util.Map;

/**
 * @since 8.1.0
 */
interface JWTGenerator {

	/**
	 * Generates a JWT access token based on the given email, claims and expiration value.
	 * 
	 * @param email - the email address to associate with the generated key, may not be <code>null</code>
	 * @param claims - any additional claims to set for the generated key
	 * @param expiration - expiration to use for the generated key, if <code>null</code> or empty the token will never expire
	 * @return
	 */
	String generate(String email, Map<String, Object> claims, String expiration);

	/**
	 * Generate a JWT security token from the information available in the given {@link User} object.
	 * 
	 * @param user - the user information to gather from this {@link User} object
	 * @param expiration - expiration to use for the generated key, if <code>null</code> or empty the token will never expire
	 * @return
	 */
	String generate(User user, String expiration);

}
