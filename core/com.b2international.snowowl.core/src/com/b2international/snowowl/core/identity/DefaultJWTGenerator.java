/*
 * Copyright 2019-2023 B2i Healthcare, https://b2ihealthcare.com
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

import java.time.InstantSource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.elasticsearch.core.TimeValue;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.algorithms.Algorithm;
import com.b2international.commons.exceptions.BadRequestException;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

/**
 * @since 7.2
 */
final class DefaultJWTGenerator implements JWTGenerator {

	private final Algorithm algorithm;
	private final String issuer;
	private final String emailClaimProperty;
	private final String permissionsClaimProperty;
	private final InstantSource instantSource;

	public DefaultJWTGenerator(final Algorithm algorithm, final String issuer, final String emailClaimProperty, final String permissionsClaimProperty, InstantSource instantSource) {
		this.algorithm = checkNotNull(algorithm);
		this.issuer = issuer;
		this.emailClaimProperty = emailClaimProperty;
		this.permissionsClaimProperty = permissionsClaimProperty;
		this.instantSource = checkNotNull(instantSource);
	}
	
	@Override
	public String generate(String email, Map<String, Object> claims, String expiration) {
		
		Builder builder = JWT.create()
				.withIssuer(issuer)
				.withSubject(email)
				.withIssuedAt(new Date())
				.withClaim(emailClaimProperty, email);
		
		if (!Strings.isNullOrEmpty(expiration)) {
			try {
				TimeValue expirationTimeValue = TimeValue.parseTimeValue(expiration, "expiration");
				builder.withExpiresAt(Date.from(instantSource.instant().plusSeconds(expirationTimeValue.getSeconds())));
 			} catch (IllegalArgumentException e) {
 				throw new BadRequestException("Invalid 'expiration' value: %s", expiration).withDeveloperMessage(e.getMessage());
 			}
		}
		
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
	public String generate(User user, String expiration) {
		final List<String> permissions = user.getPermissions().stream().map(Permission::getPermission).collect(Collectors.toList());
		return generate(user.getUserId(), Map.of(permissionsClaimProperty, permissions), expiration);
	}
	
}
