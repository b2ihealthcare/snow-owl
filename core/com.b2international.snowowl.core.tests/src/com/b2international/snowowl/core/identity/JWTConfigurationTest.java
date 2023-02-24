/*
 * Copyright 2021-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.InstantSource;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.b2international.commons.config.ConfigurationFactory;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.validation.ApiValidation;
import com.b2international.snowowl.core.SnowOwl;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.core.util.PlatformUtil;

/**
 * @since 8.1
 */
public class JWTConfigurationTest {

	private Path path;
	private Environment env;

	@Before
	public void setup() {
		this.path = Paths.get("target");
		this.env = new Environment(path, path.resolve("configuration"), path.resolve("data"));
	}
	
	@After
	public void after() {
	}
	
	@Test
	public void defaultConfig() throws Exception {
		IdentityConfiguration conf = readConfig("default.yml");
		JWTSupport jwtSupport = new IdentityPlugin().initJWT(env, conf);
		assertThatThrownBy(() -> jwtSupport.generate("test@example.com", Map.of()))
			.isInstanceOf(BadRequestException.class)
			.hasMessage("JWT token signing is not configured.");
		// XXX this token is random generated with dummy data
		assertThatThrownBy(() -> jwtSupport.authJWT("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJTbm93IE93bCIsImlhdCI6MTYzNjk5MTc2OCwiZXhwIjoxNjY4NTI3NzY4LCJhdWQiOiJ3d3cuZXhhbXBsZS5jb20iLCJzdWIiOiJleGFtcGxlQGV4YW1wbGUuY29tIn0.rwrbn_n4tFQQk_DPMetRpQEXRnIPp3OKJsmIDIVdLxE"))
			.isInstanceOf(BadRequestException.class)
			.hasMessage("JWT token verification is not configured.");
	}
	
	@Test(expected = SnowOwl.InitializationException.class)
	public void hs256_NoSecret() throws Exception {
		IdentityConfiguration conf = readConfig("hs256_nosecret.yml");
		new IdentityPlugin().initJWT(env, conf);
	}
	
	@Test
	public void hs256() throws Exception {
		IdentityConfiguration conf = readConfig("hs256.yml");
		JWTSupport jwtSupport = new IdentityPlugin().initJWT(env, conf);
		// generate a key then verify it without errors
		String jwt = jwtSupport.generate("test@example.com", Map.of());
		DecodedJWT token = jwtSupport.verify(jwt);
		assertThat(token.getAlgorithm()).isEqualTo("HS256");
		assertThat(token.getExpiresAt()).isNull();
	}
	
	@Test
	public void hs256_withExpiresAt() throws Exception {
		IdentityConfiguration conf = readConfig("hs256.yml");
		InstantSource instantSource = InstantSource.tick(InstantSource.system(), Duration.ofSeconds(1));
		JWTSupport jwtSupport = new IdentityPlugin().initJWT(env, conf, instantSource);
		// generate a key then verify it without errors
		Instant beforeGenerationTime = instantSource.instant();
		String jwt = jwtSupport.generate("test@example.com", Map.of(), "5s");
		DecodedJWT token = jwtSupport.verify(jwt);
		assertThat(token.getAlgorithm()).isEqualTo("HS256");
		assertThat(token.getExpiresAt()).isEqualTo(beforeGenerationTime.plusSeconds(5));
	}
	
	@Test(expected = SnowOwl.InitializationException.class)
	public void hs512_NoSecret() throws Exception {
		IdentityConfiguration conf = readConfig("hs512_nosecret.yml");
		new IdentityPlugin().initJWT(env, conf);
	}
	
	@Test
	public void hs512() throws Exception {
		IdentityConfiguration conf = readConfig("hs512.yml");
		JWTSupport jwtSupport = new IdentityPlugin().initJWT(env, conf);
		// generate a key then verify it without errors
		String jwt = jwtSupport.generate("test@example.com", Map.of());
		DecodedJWT token = jwtSupport.verify(jwt);
		assertThat(token.getAlgorithm()).isEqualTo("HS512");
	}
	
	@Test(expected = SnowOwl.InitializationException.class)
	public void rs256_Nokeys() throws Exception {
		IdentityConfiguration conf = readConfig("rs256_nokeys.yml");
		new IdentityPlugin().initJWT(env, conf);
	}
	
	@Test
	public void rs256() throws Exception {
		// configure support for both signing and verifying
		IdentityConfiguration conf = readConfig("rs256.yml");
		JWTSupport jwtSupport = new IdentityPlugin().initJWT(env, conf);
		String jwt = jwtSupport.generate("test@example.com", Map.of());
		DecodedJWT token = jwtSupport.verify(jwt);
		assertThat(token.getAlgorithm()).isEqualTo("RS256");
	}
	
	@Test
	public void rs256_VerifyOnly_X509() throws Exception {
		// configure support for both signing and verifying first
		IdentityConfiguration conf = readConfig("rs256.yml");
		final JWTSupport jwtSupportRS256 = new IdentityPlugin().initJWT(env, conf);
		// generate a jwt to use for verify only
		String jwt = jwtSupportRS256.generate("test@example.com", Map.of());
		
		// configure the actual verify only config 
		conf = readConfig("rs256_verify_x509.yml");
		final JWTSupport jwtSupportRS256VerifyOnly = new IdentityPlugin().initJWT(env, conf);
		// signing should be disabled
		assertThatThrownBy(() -> jwtSupportRS256VerifyOnly.generate("test@example.com", Map.of()))
			.isInstanceOf(BadRequestException.class)
			.hasMessage("JWT token signing is not configured.");
		// verify should work
		DecodedJWT token = jwtSupportRS256VerifyOnly.verify(jwt);
		assertThat(token.getAlgorithm()).isEqualTo("RS256");
	}
	
	@Test(expected = SnowOwl.InitializationException.class)
	public void rs512_Nokeys() throws Exception {
		IdentityConfiguration conf = readConfig("rs512_nokeys.yml");
		new IdentityPlugin().initJWT(env, conf);
	}
	
	@Test
	public void rs512() throws Exception {
		// configure support for both signing and verifying
		IdentityConfiguration conf = readConfig("rs512.yml");
		JWTSupport jwtSupport = new IdentityPlugin().initJWT(env, conf);
		String jwt = jwtSupport.generate("test@example.com", Map.of());
		DecodedJWT token = jwtSupport.verify(jwt);
		assertThat(token.getAlgorithm()).isEqualTo("RS512");
	}
	
	private IdentityConfiguration readConfig(String configFile) throws Exception {
		return new ConfigurationFactory<>(IdentityConfiguration.class, ApiValidation.getValidator()).build(PlatformUtil.toAbsolutePath(JWTConfigurationTest.class, configFile).toFile());
	}
	
}
