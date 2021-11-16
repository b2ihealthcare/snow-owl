/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.auth0.jwt.interfaces.DecodedJWT;

/**
 * @since 8.1.0
 */
public interface JWTGenerator {

	String generate(String email, Map<String, Object> claims);

	/**
	 * Generate a JWT security token from the information available in the given {@link User} object.
	 * @param user
	 * @return
	 */
	String generate(User user);

	/**
	 * Extract subject and claim information to reconstruct a {@link User} object.
	 * @param jwt - the verified token to extract information from
	 * @return
	 */
	User toUser(DecodedJWT jwt);
	
}
