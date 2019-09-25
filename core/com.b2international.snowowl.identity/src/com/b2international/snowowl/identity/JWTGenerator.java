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
package com.b2international.snowowl.identity;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.b2international.snowowl.identity.domain.Permission;
import com.b2international.snowowl.identity.domain.Role;
import com.b2international.snowowl.identity.domain.User;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * @since 7.2
 */
public final class JWTGenerator {

	private static final String PERMISSION = "permission";
	
	private final Algorithm algorithm;
	private final String issuer;

	public JWTGenerator(final Algorithm algorithm, final String issuer) {
		this.algorithm = algorithm;
		this.issuer = issuer;
	}
	
	public String generate(String subject, Map<String, Object> claims) {
		Builder builder = JWT.create()
				.withIssuer(issuer)
				.withSubject(subject)
				.withIssuedAt(new Date());
		
		// add claims
		claims.forEach((key, value) -> {
			if (value instanceof String) {
				builder.withClaim(key, (String) value);
			} else if (value instanceof Iterable<?>) {
				builder.withArrayClaim(key, Iterables.toArray((Iterable<String>) value, String.class));
			} else if (value instanceof String[]) {
				builder.withArrayClaim(key, (String[]) value);
			}
		});
		
		try {
			return builder.sign(algorithm);
		} catch (Throwable e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * Generate a JWT security token from the information available in the given {@link User} object.
	 * @param user
	 * @return
	 */
	public String generate(User user) {
		return generate(user.getUsername(), ImmutableMap.of(PERMISSION, user.getPermissions().stream().map(Permission::getPermission).collect(Collectors.toList())));
	}	
	
	/**
	 * Extract subject and claim information to reconstruct a {@link User} object.
	 * @param jwt - the verified token to extract information from
	 * @return
	 */
	public static User toUser(DecodedJWT jwt) {
		final String subject = jwt.getSubject();
		final List<Permission> permissions = jwt.getClaim(PERMISSION).asList(String.class).stream().map(Permission::valueOf).collect(Collectors.toList());
		return new User(subject, ImmutableList.of(new Role("oauth_scopes", permissions)));
	}
	
}
