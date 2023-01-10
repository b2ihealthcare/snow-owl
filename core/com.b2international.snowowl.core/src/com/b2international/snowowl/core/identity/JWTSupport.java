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

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.auth0.jwt.interfaces.RSAKeyProvider;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.SnowOwl;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.hash.Hashing;

/**
 * @since 8.8.0
 */
public class JWTSupport implements JWTGenerator {

	// known public/private RSA key pair headers (PEM file format, Base64 encoded DER keys)
	private static final String PUBLIC_HEADER = "-----BEGIN PUBLIC KEY-----";
	private static final String PUBLIC_FOOTER = "-----END PUBLIC KEY-----";
	private static final String PKCS8_HEADER = "-----BEGIN PRIVATE KEY-----";
	private static final String PKCS8_FOOTER = "-----END PRIVATE KEY-----";

	// Configuration Map that create supported JWT token signing/verification algorithms
	public static final Map<String, BiFunction<JWTConfiguration, RSAKeyProvider, Algorithm>> SUPPORTED_JWS_ALGORITHMS = Map
			.of("HS256",
					(config, keyProvider) -> Optional.ofNullable(config.getSecret()).map(Algorithm::HMAC256)
							.orElseThrow(() -> new SnowOwl.InitializationException(
									String.format("'secret' is required to configure '%s' for JWT token signing/verification.", config.getJws()))),
					"HS512",
					(config, keyProvider) -> Optional.ofNullable(config.getSecret()).map(Algorithm::HMAC512)
							.orElseThrow(() -> new SnowOwl.InitializationException(
									String.format("'secret' is required to configure '%s' for JWT token signing/verification.", config.getJws()))),
					"RS256",
					(config, keyProvider) -> Optional.ofNullable(keyProvider).map(Algorithm::RSA256)
							.orElseThrow(() -> new SnowOwl.InitializationException(String.format(
									"'verificationKey' and optionally 'signingKey' (PKCS#8 PEM) configuration settings are required to use '%s' for JWT token signing/verification.",
									config.getJws()))),
					"RS512",
					(config, keyProvider) -> Optional.ofNullable(keyProvider).map(Algorithm::RSA512)
							.orElseThrow(() -> new SnowOwl.InitializationException(String.format(
									"'verificationKey' and optionally 'signingKey' (PKCS#8 PEM) configuration settings are required to use '%s' for JWT token signing/verification.",
									config.getJws()))));

	// Disabled JWT Verifier implementation that throws an exception if any verification related logic would be required
	private static final JWTVerifier JWT_VERIFIER_DISABLED = new JWTVerifier() {
		@Override
		public DecodedJWT verify(DecodedJWT arg0) throws JWTVerificationException {
			throw new BadRequestException("JWT token verification is not configured.");
		}

		@Override
		public DecodedJWT verify(String arg0) throws JWTVerificationException {
			throw new BadRequestException("JWT token verification is not configured.");
		}
	};

	// Disabled JWT Generator implementation that throws an exception if any token signing related logic would be required
	private static final JWTGenerator JWT_GENERATOR_DISABLED = new JWTGenerator() {
		@Override
		public String generate(User user) {
			throw new BadRequestException("JWT token signing is not configured.");
		}

		@Override
		public String generate(String email, Map<String, Object> claims) {
			throw new BadRequestException("JWT token signing is not configured.");
		}
	};

	private final IdentityConfiguration config;

	private JWTGenerator generator;
	private JWTVerifier verifier;

	JWTSupport(IdentityConfiguration config) {
		this.config = config;
	}

	public void init() throws Exception {
		RSAKeyProvider rsaKeyProvider = createRSAKeyProvider(config);
		Algorithm algorithm;
		// if the old deprecated JWKS URL is set, then fall back to disabled mode without any warning
		if (!Strings.isNullOrEmpty(config.getJwksUrl())) {
			algorithm = null;
		} else if (!Strings.isNullOrEmpty(config.getJws())) {
			algorithm = SUPPORTED_JWS_ALGORITHMS.getOrDefault(config.getJws(), JWTSupport::throwUnsupportedJws).apply(config, rsaKeyProvider);
		} else {
			IdentityProvider.LOG.warn("'identity.jws' configuration is missing, disabling JWT authorization token signing and verification.");
			algorithm = null;
		}

		if (algorithm == null) {
			// both signing and verification is disabled
			generator = JWT_GENERATOR_DISABLED;
			verifier = JWT_VERIFIER_DISABLED;
		} else if (rsaKeyProvider != null && rsaKeyProvider.getPrivateKey() == null) {
			generator = JWT_GENERATOR_DISABLED;
			verifier = createJWTVerifier(algorithm, config);
		} else {
			generator = new DefaultJWTGenerator(algorithm, config.getIssuer(), config.getEmailClaimProperty(), config.getPermissionsClaimProperty());
			verifier = createJWTVerifier(algorithm, config);
		}
	}

	@VisibleForTesting
	/* package */ DecodedJWT verify(String token) {
		return verifier.verify(token);
	}

	public User authJWT(String token) {
		return toUser(verify(token), config);
	}

	@Override
	public String generate(String email, Map<String, Object> claims) {
		return generator.generate(email, claims);
	}

	@Override
	public String generate(User user) {
		return generator.generate(user);
	}

	private RSAKeyProvider createRSAKeyProvider(JWTConfiguration conf) throws Exception {
		final String privateKeyId;
		final RSAPrivateKey privateKey;

		// read private key if provided
		if (!Strings.isNullOrEmpty(conf.getSigningKey())) {
			privateKeyId = Hashing.goodFastHash(16).hashString(conf.getSigningKey(), Charsets.UTF_8).toString();
			privateKey = readPrivateKey(conf.getSigningKey());
		} else {
			privateKeyId = null;
			privateKey = null;
		}

		if (!Strings.isNullOrEmpty(conf.getVerificationKey())) {
			RSAPublicKey publicKey = readPublicKey(conf.getVerificationKey());
			return new RSAKeyProvider() {

				@Override
				public RSAPublicKey getPublicKeyById(String kid) {
					return publicKey;
				}

				@Override
				public String getPrivateKeyId() {
					return privateKeyId;
				}

				@Override
				public RSAPrivateKey getPrivateKey() {
					return privateKey;
				}

			};
		} else {
			// if verification key is not configured then this not an RSA configuration (or an invalid configuration raised when creating the
			// algorithm instance)
			// token signing on its own cannot be configured
			return null;
		}
	}

	private RSAPrivateKey readPrivateKey(String value) {
		if (value.startsWith(PKCS8_HEADER)) {
			try {
				String extractedKey = value
						// replace header
						.replace(PKCS8_HEADER, "")
						// replace any line endings if present
						.replaceAll("\\r", "").replaceAll("\\n", "")
						// replace footer
						.replace(PKCS8_FOOTER, "");
				PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(extractedKey));
				return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(keySpec);
			} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
				throw new SnowOwl.InitializationException(
						"Invalid signingKey. Only Base64 encoded PKCS#1, PKCS#8 or JWK keys are supported. Error: " + e.getMessage());
			}
		} else {
			// TODO support PKCS#1
			// TODO support JWK strings
			throw new SnowOwl.InitializationException(
					"Unsupported private key header in 'identity.jws.signingKey'. Snow Owl supports the PKCS8 private key format.");
		}
	}

	private RSAPublicKey readPublicKey(String value) {
		try {
			String extractedKey = value
					// replace header
					.replace(PUBLIC_HEADER, "")
					// replace any line endings if present
					.replaceAll("\\r", "").replaceAll("\\n", "")
					// replace footer
					.replace(PUBLIC_FOOTER, "");
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(extractedKey));
			return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(keySpec);
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			throw new SnowOwl.InitializationException(
					"Invalid verificationKey. Only Base64 encoded X509 Certificate keys are supported. Error: " + e.getMessage());
		}
	}

	public static final com.auth0.jwt.JWTVerifier createJWTVerifier(Algorithm algorithm, final JWTConfiguration conf) {
		return JWT.require(algorithm).withIssuer(conf.getIssuer()).acceptLeeway(3L) // 3 seconds
				.build();
	}

	public static final Algorithm throwUnsupportedJws(JWTConfiguration config, RSAKeyProvider keyProvider) {
		throw new SnowOwl.InitializationException(String.format("Unsupported JWT token signing algorithm: %s", config.getJws()));
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
	public static User toUser(DecodedJWT jwt, JWTConfiguration config) {
		final String userId;

		// XXX emailClaimProperty should be renamed to userIdProperty
		String userIdProperty = config.getEmailClaimProperty();
		if (Strings.isNullOrEmpty(userIdProperty)) {
			userId = jwt.getSubject();
		} else {
			final Claim emailClaim = jwt.getClaim(userIdProperty);
			if (emailClaim == null || emailClaim.isNull()) {
				throw new BadRequestException("'%s' JWT access token field is required for userId access, but it was missing.", userIdProperty);
			}
			userId = emailClaim.asString();
		}

		final List<String> permissions;
		final String permissionsClaimProperty = config.getPermissionsClaimProperty();
		if (!Strings.isNullOrEmpty(permissionsClaimProperty)) {
			Claim permissionsClaim = jwt.getClaim(permissionsClaimProperty);
			if (permissionsClaim == null || permissionsClaim.isNull()) {
				throw new BadRequestException("'%s' JWT access token field is required for permissions access, but it was missing.",
						permissionsClaimProperty);
			}
			permissions = permissionsClaim.asList(String.class);
		} else {
			permissions = Collections.emptyList();
		}

		return new User(userId, permissions.stream().map(Permission::valueOf).collect(Collectors.toList()), jwt.getToken());
	}

}
