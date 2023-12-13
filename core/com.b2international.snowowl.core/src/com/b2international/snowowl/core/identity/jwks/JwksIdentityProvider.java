/*
 * Copyright 2022 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.identity.jwks;

import java.net.URL;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.RSAKeyProvider;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.identity.*;
import com.b2international.snowowl.core.setup.Environment;

/**
 * @since 8.8.0
 */
public final class JwksIdentityProvider implements IdentityProvider {

	static final String TYPE = "jwks";
	private final JwksIdentityProviderConfig config;
	private final JWTConfiguration jwtConfiguration;
	
	private JWTVerifier verifier;
	
	public JwksIdentityProvider(JwksIdentityProviderConfig config) {
		this.config = config;
		this.jwtConfiguration = config.toJWTConfiguration();
	}

	@Override
	public void init(Environment env) throws Exception {
		// prefer JSON Web Key Set provider URLs (if set) for token verification
		final JwkProvider jwkProvider = new JwkProviderBuilder(new URL(config.getJwksUrl()))
				// TODO do we need configuration support for this?
				.cached(5, 24, TimeUnit.HOURS)
				.rateLimited(10, 1, TimeUnit.MINUTES)
				.build();
		
		final RSAKeyProvider rsaKeyProvider = new RSAKeyProvider() {
			
			@Override
			public RSAPublicKey getPublicKeyById(String kid) {
				try {
					return (RSAPublicKey) jwkProvider.get(kid).getPublicKey();
				} catch (JwkException e) {
					throw new SnowowlRuntimeException(e.getMessage(), e);
				}
			}
			
			@Override
			public String getPrivateKeyId() {
				return null;
			}
			
			@Override
			public RSAPrivateKey getPrivateKey() {
				return null;
			}

		};
		
		Algorithm algorithm = JWTSupport.SUPPORTED_JWS_ALGORITHMS.getOrDefault(config.getJws(), JWTSupport::throwUnsupportedJws).apply(jwtConfiguration, rsaKeyProvider);
		
		this.verifier = JWTSupport.createJWTVerifier(algorithm, jwtConfiguration);
	}
	
	@Override
	public User authJWT(String token) {
		return JWTSupport.toUser(verifier.verify(token), jwtConfiguration);
	}
	
	@Override
	public Promise<Users> searchUsers(Collection<String> usernames, int limit) {
		return Promise.immediate(new Users(limit, 0));
	}

	@Override
	public String getInfo() {
		return String.join("@", TYPE, config.getJwksUrl());
	}
	
}
