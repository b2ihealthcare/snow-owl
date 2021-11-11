/*
 * Copyright 2017-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.collect.Lists.newArrayListWithExpectedSize;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureGenerationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.auth0.jwt.interfaces.RSAKeyProvider;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.SnowOwl;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.plugin.ClassPathScanner;
import com.b2international.snowowl.core.plugin.Component;
import com.b2international.snowowl.core.setup.ConfigurationRegistry;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.core.setup.Plugin;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * @since 5.11
 */
@Component
public final class IdentityPlugin extends Plugin {

	private static final Map<String, BiFunction<IdentityConfiguration, RSAKeyProvider, Algorithm>> SUPPORTED_JWS_ALGORITHMS = Map.of(
		"HS256", (config, keyProvider) -> Optional.ofNullable(config.getSecret())
			.map(Algorithm::HMAC256)
			.orElseThrow(() -> new SnowOwl.InitializationException(String.format("'secret' is required to configure '%s' for JWT token signing/verification.", config.getJws()))),
		"HS512", (config, keyProvider) -> Optional.ofNullable(config.getSecret())
			.map(Algorithm::HMAC512)
			.orElseThrow(() -> new SnowOwl.InitializationException(String.format("'secret' is required to configure '%s' for JWT token signing/verification.", config.getJws()))),
		"RS256", (config, keyProvider) -> Optional.ofNullable(keyProvider)
			.map(Algorithm::RSA256)
			.orElseThrow(() -> new SnowOwl.InitializationException(String.format("Either a 'jwksUrl' or 'verificationKey' (PKCS1, PKCS8) and optionally the 'signingKey' (PKCS1, PKCS8) configuration settings are required to use '%s' for JWT token signing/verification.", config.getJws()))),
		"RS512", (config, keyProvider) -> Optional.ofNullable(keyProvider)
			.map(Algorithm::RSA512)
			.orElseThrow(() -> new SnowOwl.InitializationException(String.format("Either a 'jwksUrl' or 'verificationKey' (PKCS1, PKCS8) and optionally the 'signingKey' (PKCS1, PKCS8) configuration settings are required to use '%s' for JWT token signing/verification.", config.getJws())))
	);
	
	@Override
	public void addConfigurations(ConfigurationRegistry registry) {
		registry.add("identity", IdentityConfiguration.class);
	}
	
	@Override
	public void init(SnowOwlConfiguration configuration, Environment env) throws Exception {
		final IdentityConfiguration conf = configuration.getModuleConfig(IdentityConfiguration.class);
		final List<IdentityProvider> providers = createProviders(env, conf.getProviderConfigurations() == null ? List.of() : conf.getProviderConfigurations());
		
		IdentityProvider identityProvider = null;
		if (providers.isEmpty()) {
			// if there are no providers, but the issuer is external and there is a JWS configured, then assume we are using access tokens and Snow Owl is a resource server
			if (conf.isExternalIssuer() || !Strings.isNullOrEmpty(conf.getJws())) {
				identityProvider = IdentityProvider.JWT;
			} else {
				identityProvider = IdentityProvider.NOOP;
			}
		} else if (providers.size() == 1) {
			identityProvider = Iterables.getOnlyElement(providers);
		} else {
			identityProvider = new MultiIdentityProvider(providers);
		}
		
		if (conf.isAdminParty()) {
			identityProvider = new AdminPartyIdentityProvider(identityProvider);
		}
		
		identityProvider.validateSettings();
		IdentityProvider.LOG.info("Configured identity providers [{}]", identityProvider.getInfo());
		env.services().registerService(IdentityProvider.class, identityProvider);
		
		RSAKeyProvider rsaKeyProvider = createRSAKeyProvider(conf);
		Algorithm algorithm;
		if (!Strings.isNullOrEmpty(conf.getJws())) {
			algorithm = SUPPORTED_JWS_ALGORITHMS.getOrDefault(conf.getJws(), this::throwUnsupportedJws).apply(conf, rsaKeyProvider);
		} else {
			IdentityProvider.LOG.warn("'identity.jws' configuration is missing, disabling JWT authorization token signing and verification.");
			algorithm = new Algorithm("disabled", "disabled") {
				@Override
				public void verify(DecodedJWT arg0) throws SignatureVerificationException {
					throw new BadRequestException("JWT token verification is not available.");
				}
				
				@Override
				public byte[] sign(byte[] arg0) throws SignatureGenerationException {
					throw new BadRequestException("JWT token signing is not available.");
				}
			};
		}
		env.services().registerService(JWTGenerator.class, new JWTGenerator(algorithm, conf.getIssuer(), conf.getEmailClaimProperty(), conf.getPermissionsClaimProperty()));
		env.services().registerService(JWTVerifier.class, JWT.require(algorithm)
				.withIssuer(conf.getIssuer())
				.acceptLeeway(3L) // 3 seconds
				.build());
	}
	
	private RSAKeyProvider createRSAKeyProvider(IdentityConfiguration conf) throws MalformedURLException {
		if (!Strings.isNullOrEmpty(conf.getJwksUrl())) {
			// prefer JSON Web Key Set provider URLs (if set) for token verification
			JwkProvider jwkProvider = new JwkProviderBuilder(new URL(conf.getJwksUrl()))
					// TODO do we need configuration support for this?
					.cached(5, 24, TimeUnit.HOURS)
					.rateLimited(10, 1, TimeUnit.MINUTES)
					.build();
			
			return new RSAKeyProvider() {
				
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
		} else if (!Strings.isNullOrEmpty(conf.getVerificationKey())) {
			// if JWKS is not set, then fall back to verification key if set
			RSAPublicKey publicKey = null;
			return new RSAKeyProvider() {
				
				@Override
				public RSAPublicKey getPublicKeyById(String kid) {
					return publicKey;
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
		} else {
			// if neither jwksUrl nor the verificationKey settings are configured then this not an RSA configuration (or an invalid configuration raised when creating the algorithm instance)
			return null;
		}
	}

	private Algorithm throwUnsupportedJws(IdentityConfiguration config, RSAKeyProvider keyProvider) {
		throw new SnowOwl.InitializationException(String.format("Unsupported JWT token signing algorithm: %s", config.getJws()));
	}
	
	private List<IdentityProvider> createProviders(Environment env, List<IdentityProviderConfig> providerConfigurations) {
		final List<IdentityProvider> providers = newArrayListWithExpectedSize(3);
		env.plugins().getPlugins().stream()
			.filter(IdentityProviderFactory.class::isInstance)
			.map(IdentityProviderFactory.class::cast)
			.forEach(factory -> {
				Optional<IdentityProviderConfig> providerConfig = providerConfigurations.stream().filter(conf -> conf.getClass() == factory.getConfigType()).findFirst();
				if (providerConfig.isPresent()) {
					try {
						providers.add(factory.create(env, providerConfig.get()));
					} catch (Exception e) {
						throw new SnowowlRuntimeException(String.format("Couldn't initialize '%s' identity provider", factory), e);
					}
				}
			});
		return providers;
	}

	static Collection<Class<? extends IdentityProviderConfig>> getAvailableConfigClasses(ClassPathScanner scanner) {
		final ImmutableList.Builder<Class<? extends IdentityProviderConfig>> configs = ImmutableList.builder();
		final Iterator<IdentityProviderFactory> it = scanner.getComponentsByInterface(IdentityProviderFactory.class).iterator();
		while (it.hasNext()) {
			configs.add(it.next().getConfigType());
		}
		return configs.build();
	}
	
}
