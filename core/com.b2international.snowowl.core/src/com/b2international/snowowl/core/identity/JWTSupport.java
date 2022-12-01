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
package com.b2international.snowowl.core.identity;

import java.util.Map;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.b2international.commons.exceptions.BadRequestException;

/**
 * @since 8.8.0
 */
public interface JWTSupport extends JWTGenerator, JWTVerifier {

	/**
	 * @since 8.8.0
	 */
	JWTSupport DISABLED = new JWTSupport() {
		@Override
		public DecodedJWT verify(DecodedJWT arg0) throws JWTVerificationException {
			throw new BadRequestException("JWT token verification is not configured.");
		}
		
		@Override
		public DecodedJWT verify(String arg0) throws JWTVerificationException {
			throw new BadRequestException("JWT token verification is not configured.");
		}
		
		@Override
		public String generate(User user) {
			throw new BadRequestException("JWT token signing is not configured.");
		}
		
		@Override
		public String generate(String email, Map<String, Object> claims) {
			throw new BadRequestException("JWT token signing is not configured.");
		}
		
		@Override
		public JWTConfiguration config() {
			throw new BadRequestException("JWT token signing and verification is not configured.");
		}
	};

	JWTConfiguration config();

}
