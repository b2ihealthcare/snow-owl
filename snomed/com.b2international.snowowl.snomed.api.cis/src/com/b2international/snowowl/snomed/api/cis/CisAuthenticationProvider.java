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
package com.b2international.snowowl.snomed.api.cis;

import java.util.Date;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.b2international.snowowl.identity.IdentityProvider;
import com.b2international.snowowl.snomed.api.cis.exceptions.UnauthorizedException;
import com.b2international.snowowl.snomed.cis.client.Credentials;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

/**
 * @since 6.18
 */
public class CisAuthenticationProvider implements AuthenticationProvider {

	private static final String ISSUER = "snow-owl";
	
	private final Algorithm algorithm;
	private final IdentityProvider identityProvider;
	private final long tokenTtl = 1 * 60 * 60 * 1000L;
	
	public CisAuthenticationProvider(IdentityProvider identityProvider, String secret) {
		this.identityProvider = identityProvider;
		this.algorithm = Algorithm.HMAC256(secret);
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
		    JWTVerifier verifier = JWT.require(algorithm)
		        .withIssuer(ISSUER)
		        .build(); //Reusable verifier instance
		    return verifier.verify(token).getSubject();
		} catch (JWTVerificationException exception){
		    // Invalid signature/claims
			return null;
		}
	}

	public String login(Credentials credentials) {
		if (identityProvider.auth(credentials.getUsername(), credentials.getPassword())) {
			return generateToken(credentials.getUsername());
		} else {
			throw new UnauthorizedException("Incorrect username or password");
		}
	}

	private String generateToken(String username) {
		try {
			final long now = System.currentTimeMillis();
			return JWT.create()
		        .withIssuer(ISSUER)
		        .withSubject(username)
		        .withIssuedAt(new Date())
		        .withExpiresAt(new Date(now + tokenTtl))
		        .sign(algorithm);
		} catch (JWTCreationException e){
			throw new RuntimeException(e);
		}
	}
	
}
