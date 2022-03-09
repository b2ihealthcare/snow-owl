/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.b2international.commons.config.ConfigurationFactory;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.validation.ApiValidation;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.SnowOwl;
import com.b2international.snowowl.core.util.PlatformUtil;

/**
 * @since 8.1
 */
public class JWTConfigurationTest {

	private ApplicationContext services = ApplicationContext.getInstance();
	private IdentityProvider identityProvider = IdentityProvider.NOOP;

	@Before
	public void setup() {
		// remove any JWTGenerator, JWTVerifier services before test
		services.unregisterService(JWTGenerator.class);
		services.unregisterService(JWTVerifier.class);
	}
	
	@After
	public void after() {
		// remove any JWTGenerator, JWTVerifier services after test
		services.unregisterService(JWTGenerator.class);
		services.unregisterService(JWTVerifier.class);
	}
	
	
	@Test
	public void defaultConfig() throws Exception {
		IdentityConfiguration conf = readConfig("default.yml");
		new IdentityPlugin().configureJWT(services, identityProvider, conf);
		assertThatThrownBy(() -> services.getService(JWTGenerator.class).generate("test@example.com", Map.of()))
			.isInstanceOf(BadRequestException.class)
			.hasMessage("JWT token signing is not configured.");
		// XXX this token is random generated with dummy data
		assertThatThrownBy(() -> services.getService(JWTVerifier.class).verify("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJTbm93IE93bCIsImlhdCI6MTYzNjk5MTc2OCwiZXhwIjoxNjY4NTI3NzY4LCJhdWQiOiJ3d3cuZXhhbXBsZS5jb20iLCJzdWIiOiJleGFtcGxlQGV4YW1wbGUuY29tIn0.rwrbn_n4tFQQk_DPMetRpQEXRnIPp3OKJsmIDIVdLxE"))
			.isInstanceOf(BadRequestException.class)
			.hasMessage("JWT token verification is not configured.");
	}
	
	@Test(expected = SnowOwl.InitializationException.class)
	public void hs256_NoSecret() throws Exception {
		IdentityConfiguration conf = readConfig("hs256_nosecret.yml");
		new IdentityPlugin().configureJWT(services, identityProvider, conf);
	}
	
	@Test
	public void hs256() throws Exception {
		IdentityConfiguration conf = readConfig("hs256.yml");
		new IdentityPlugin().configureJWT(services, identityProvider, conf);
		// generate a key then verify it without errors
		String jwt = services.getService(JWTGenerator.class).generate("test@example.com", Map.of());
		DecodedJWT decoded = services.getService(JWTVerifier.class).verify(jwt);
		assertThat(decoded.getAlgorithm()).isEqualTo("HS256");
	}
	
	@Test(expected = SnowOwl.InitializationException.class)
	public void hs512_NoSecret() throws Exception {
		IdentityConfiguration conf = readConfig("hs512_nosecret.yml");
		new IdentityPlugin().configureJWT(services, identityProvider, conf);
	}
	
	@Test
	public void hs512() throws Exception {
		IdentityConfiguration conf = readConfig("hs512.yml");
		new IdentityPlugin().configureJWT(services, identityProvider, conf);
		// generate a key then verify it without errors
		String jwt = services.getService(JWTGenerator.class).generate("test@example.com", Map.of());
		DecodedJWT decoded = services.getService(JWTVerifier.class).verify(jwt);
		assertThat(decoded.getAlgorithm()).isEqualTo("HS512");
	}
	
	@Test(expected = SnowOwl.InitializationException.class)
	public void rs256_Nokeys() throws Exception {
		IdentityConfiguration conf = readConfig("rs256_nokeys.yml");
		new IdentityPlugin().configureJWT(services, identityProvider, conf);
	}
	
	@Test
	public void rs256() throws Exception {
		// configure support for both signing and verifying
		IdentityConfiguration conf = readConfig("rs256.yml");
		new IdentityPlugin().configureJWT(services, identityProvider, conf);
		String jwt = services.getService(JWTGenerator.class).generate("test@example.com", Map.of());
		DecodedJWT decoded = services.getService(JWTVerifier.class).verify(jwt);
		assertThat(decoded.getAlgorithm()).isEqualTo("RS256");
	}
	
	@Test
	public void rs256_VerifyOnly_X509() throws Exception {
		// configure support for both signing and verifying first
		IdentityConfiguration conf = readConfig("rs256.yml");
		new IdentityPlugin().configureJWT(services, identityProvider, conf);
		// generate a jwt to use for verify only
		String jwt = services.getService(JWTGenerator.class).generate("test@example.com", Map.of());
		
		// configure the actual verify only config 
		conf = readConfig("rs256_verify_x509.yml");
		new IdentityPlugin().configureJWT(services, identityProvider, conf);
		// signing should be disabled
		assertThatThrownBy(() -> services.getService(JWTGenerator.class).generate("test@example.com", Map.of()))
			.isInstanceOf(BadRequestException.class)
			.hasMessage("JWT token signing is not configured.");
		// verify should work
		DecodedJWT decoded = services.getService(JWTVerifier.class).verify(jwt);
		assertThat(decoded.getAlgorithm()).isEqualTo("RS256");
	}
	
	@Test(expected = SnowOwl.InitializationException.class)
	public void rs512_Nokeys() throws Exception {
		IdentityConfiguration conf = readConfig("rs512_nokeys.yml");
		new IdentityPlugin().configureJWT(services, identityProvider, conf);
	}
	
	@Test
	public void rs512() throws Exception {
		// configure support for both signing and verifying
		IdentityConfiguration conf = readConfig("rs512.yml");
		new IdentityPlugin().configureJWT(services, identityProvider, conf);
		String jwt = services.getService(JWTGenerator.class).generate("test@example.com", Map.of());
		DecodedJWT decoded = services.getService(JWTVerifier.class).verify(jwt);
		assertThat(decoded.getAlgorithm()).isEqualTo("RS512");
	}
	
	private IdentityConfiguration readConfig(String configFile) throws Exception {
		return new ConfigurationFactory<>(IdentityConfiguration.class, ApiValidation.getValidator()).build(PlatformUtil.toAbsolutePath(getClass(), configFile).toFile());
	}
	
}
