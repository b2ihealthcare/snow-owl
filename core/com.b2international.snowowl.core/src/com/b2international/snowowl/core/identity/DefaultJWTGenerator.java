/*
 * Copyright 2019-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.common.collect.Iterables;

/**
 * @since 7.2
 */
final class DefaultJWTGenerator implements JWTGenerator {

	private final Algorithm algorithm;
	private final String issuer;
	private final String emailClaimProperty;
	private final String permissionsClaimProperty;

	public DefaultJWTGenerator(final Algorithm algorithm, final IdentityConfiguration conf) {
		this.algorithm = checkNotNull(algorithm);
		this.issuer = conf.getIssuer();
		this.emailClaimProperty = conf.getEmailClaimProperty();
		this.permissionsClaimProperty = conf.getPermissionsClaimProperty();
	}
	
	@Override
	public String generate(String email, Map<String, Object> claims) {
		Builder builder = JWT.create()
				.withIssuer(issuer)
				.withSubject(email)
				.withIssuedAt(new Date())
				.withClaim(emailClaimProperty, email);
		
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

	@Override
	public String generate(User user) {
		final List<String> permissions = user.getPermissions().stream().map(Permission::getPermission).collect(Collectors.toList());
		return generate(user.getUsername(), Map.of(permissionsClaimProperty, permissions));
	}
	
}
