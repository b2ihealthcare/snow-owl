/*
 * Copyright 2021-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.exceptions.UnauthorizedException;
import com.google.common.base.Charsets;

/**
 * Verifies HTTP Authorization header values and delegates verification to the correct service based on the header value's prefix. Supported prefixes
 * are: basic and bearer.
 * 
 * @since 8.1
 */
public final class AuthorizationHeaderVerifier {

	private final JWTVerifier verifier;
	private final IdentityProvider identityProvider;
	private final String emailClaimProperty;
	private final String permissionsClaimProperty;

	public AuthorizationHeaderVerifier(JWTVerifier verifier, IdentityProvider identityProvider, String emailClaimProperty,
			String permissionsClaimProperty) {
		this.verifier = verifier;
		this.identityProvider = identityProvider;
		this.emailClaimProperty = emailClaimProperty;
		this.permissionsClaimProperty = permissionsClaimProperty;
	}

	/**
	 * Authenticates an authorization token. 
	 * Supported formats are: - Basic: Base64 encoded username:password 
	 * - Bearer: JWT token issued by Snow Owl or an external OAuth Authorization Server
	 * 
	 * @param authorizationHeaderValue
	 *            - the value of the HTTP Authorization header
	 * @return the {@link User} if the header is valid, never <code>null</code>
	 * @throws UnauthorizedException
	 *             - if the header value is incorrect
	 */
	public User auth(String authorizationHeaderValue) {
		final String[] parts = authorizationHeaderValue.trim().split(" ");
		if (parts.length == 2) {
			// standard two part authorization header values have a type prefix specified, use it to determine the algorithm
			switch (parts[0].toLowerCase()) {
			case "basic":
				return authBase64(parts[1]);
			case "bearer":
				return authJWT(parts[1]);
			default:
				throw new UnauthorizedException("Incorrect authorization token");
			}
		} else if (parts.length == 1) {
			// any other authorization token is treated and verified as a bearer JWT
			return authJWT(parts[0]);
		} else {
			// empty value or more than two parts, which is an incorrect header value
			throw new UnauthorizedException("Incorrect authorization token");
		}
	}

	/**
	 * Authenticates a token as JWT and returns the authenticated {@link User} object or throws an {@link UnauthorizedException}.
	 * 
	 * @param context
	 * @param token
	 * @return
	 * @throws UnauthorizedException
	 */
	public User authJWT(final String token) {
		try {
			final DecodedJWT jwt = verifier.verify(token);
			return toUser(jwt);
		} catch (JWTVerificationException e) {
			throw new UnauthorizedException("Incorrect authorization token");
		}
	}

	/**
	 * Converts the given JWT access token to a {@link User} representation using the configured email and permission claims. This method does not
	 * verify the given access token, it only decodes it and uses the publicly available claims to construct the {@link User} object. To verify a
	 * token and create a user object use the {@link #authJWT(String)} method.
	 * 
	 * @param token
	 * @return a {@link User} instance created from the given token
	 */
	public User toUser(String token) {
		return toUser(JWT.decode(token));
	}

	/**
	 * Converts the given JWT access token to a {@link User} representation using the configured email and permission claims.
	 * 
	 * @param jwt
	 *            - the JWT to convert to a {@link User} object
	 * @return
	 * @throws BadRequestException
	 *             - if either the configured email or permissions property is missing from the given JWT
	 */
	public User toUser(DecodedJWT jwt) {
		final Claim emailClaim = jwt.getClaim(emailClaimProperty);
		if (emailClaim == null || emailClaim.isNull()) {
			throw new BadRequestException("'%s' JWT access token field is required for email access, but it was missing.", emailClaimProperty);
		}

		Claim permissionsClaim = jwt.getClaim(permissionsClaimProperty);
		if (permissionsClaim == null || permissionsClaim.isNull()) {
			throw new BadRequestException("'%s' JWT access token field is required for permissions access, but it was missing.",
					permissionsClaimProperty);
		}

		final List<Permission> permissions = jwt.getClaim(permissionsClaimProperty).asList(String.class).stream().map(Permission::valueOf)
				.collect(Collectors.toList());
		return new User(emailClaim.asString(), permissions);
	}

	/**
	 * Authenticates a token as Base64 encoded user:pass String (HTTP Basic) and returns a {@link User} object or throws an
	 * {@link UnauthorizedException}.
	 * 
	 * @param base64EncodedHeader
	 * @return
	 * @throws UnauthorizedException
	 * @throws {@link
	 *             IllegalArgumentException} - if the given base64EncodedHeader value is not a Base64 encoded value
	 */
	public User authBase64(final String base64EncodedHeader) {
		final String decoded = new String(Base64.getDecoder().decode(base64EncodedHeader), Charsets.UTF_8);
		final String[] base64Parts = decoded.split(":");
		if (base64Parts.length != 2) {
			throw new UnauthorizedException("Incorrect username or password");
		}
		return identityProvider.auth(base64Parts[0], base64Parts[1]);
	}

}
