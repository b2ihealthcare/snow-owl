/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.cis.rest;

import java.util.Collections;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.b2international.commons.exceptions.UnauthorizedException;
import com.b2international.snowowl.identity.Credentials;
import com.b2international.snowowl.identity.IdentityProvider;
import com.b2international.snowowl.identity.JWTGenerator;
import com.b2international.snowowl.identity.domain.User;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

/**
 * @since 6.18
 */
public class CisAuthenticationProvider implements AuthenticationProvider {

	private final IdentityProvider identityProvider;
	private final JWTGenerator tokenGenerator;
	private final JWTVerifier jwtVerifier;
	
	public CisAuthenticationProvider(IdentityProvider identityProvider, JWTGenerator tokenGenerator, JWTVerifier jwtVerifier) {
		this.identityProvider = identityProvider;
		this.tokenGenerator = tokenGenerator;
		this.jwtVerifier = jwtVerifier;
	}
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		if (authentication instanceof CisTokenAuthentication) {
			final String token = ((CisTokenAuthentication) authentication).getCredentials();
			// verify token validity and get back username
			String username = verify(token);
			if (Strings.isNullOrEmpty(username)) {
				throw new BadCredentialsException("Provided token is invalid and/or expired.");
			}
			Authentication auth = new PreAuthenticatedAuthenticationToken(username, token, ImmutableList.of(new SimpleGrantedAuthority("ROLE_USER")));
            auth.setAuthenticated(true);
            return auth;
		}
		throw new BadCredentialsException("No authentication token is provided.");
	}

	@Override
	public boolean supports(Class<?> authClass) {
		return CisTokenAuthentication.class.isAssignableFrom(authClass);
	}
	
	public String verify(String token) {
		try {
		    return jwtVerifier.verify(token).getSubject();
		} catch (JWTVerificationException exception){
		    // Invalid signature/claims
			return null;
		}
	}

	public String login(Credentials credentials) {
		User user = identityProvider.auth(credentials.getUsername(), credentials.getPassword());
		if (user != null) {
			return tokenGenerator.generate(credentials.getUsername(), Collections.emptyMap());
		} else {
			throw new UnauthorizedException("Incorrect username or password");
		}
	}

}
