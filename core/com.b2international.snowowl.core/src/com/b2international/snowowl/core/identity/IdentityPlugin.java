/*
 * Copyright 2017-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.List;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.b2international.commons.extension.Component;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.setup.ConfigurationRegistry;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.core.setup.Plugin;
import com.google.common.collect.Iterables;

/**
 * @since 5.11
 */
@Component
public final class IdentityPlugin extends Plugin {

	@Override
	public void addConfigurations(ConfigurationRegistry registry) {
		registry.add("identity", IdentityConfiguration.class);
	}
	
	@Override
	public void init(SnowOwlConfiguration configuration, Environment env) throws Exception {
		final IdentityConfiguration conf = configuration.getModuleConfig(IdentityConfiguration.class);
		final List<IdentityProvider> providers = IdentityProvider.Factory.createProviders(env, conf.getProviderConfigurations());
		
		IdentityProvider identityProvider = null; 
		if (providers.isEmpty()) {
			identityProvider = IdentityProvider.NOOP;
		} else if (providers.size() == 1) {
			identityProvider = Iterables.getOnlyElement(providers);
		} else {
			identityProvider = new MultiIdentityProvider(providers);
		}
		
		if (conf.isAdminParty()) {
			identityProvider = new AdminPartyIdentityProvider(identityProvider);
		}
		IdentityProvider.LOG.info("Configured identity providers [{}]", identityProvider.getInfo());
		env.services().registerService(IdentityProvider.class, identityProvider);
		
		// configure JWT token generation and verification
		final Algorithm algorithm = Algorithm.HMAC512(conf.getSecret());
		env.services().registerService(JWTGenerator.class, new JWTGenerator(algorithm, conf.getIssuer()));
		env.services().registerService(JWTVerifier.class, JWT.require(algorithm)
				.withIssuer(conf.getIssuer())
				.acceptLeeway(3L) // 3 seconds
				.build());
	}
	
}
