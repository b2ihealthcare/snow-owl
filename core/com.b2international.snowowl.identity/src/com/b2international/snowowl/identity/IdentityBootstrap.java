/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.commons.platform.PlatformUtil;
import com.b2international.snowowl.core.CoreActivator;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.setup.DefaultBootstrapFragment;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.core.setup.ModuleConfig;

/**
 * @since 5.11
 */
@ModuleConfig(fieldName = "identity", type = IdentityConfiguration.class)
public class IdentityBootstrap extends DefaultBootstrapFragment {

	@Override
	public void init(SnowOwlConfiguration configuration, Environment env) throws Exception {
		final IdentityConfiguration conf = configuration.getModuleConfig(IdentityConfiguration.class);
		IdentityProvider identityProvider = IdentityProvider.Factory.createInstance(env, conf.getType(), conf.getProperties());
		if (conf.isAdminParty() && PlatformUtil.isDevVersion(CoreActivator.PLUGIN_ID)) {
			identityProvider = new AdminPartyIdentityProvider(identityProvider);
		}
		IdentityProvider.LOG.info("Configured '{}' identity provider", conf.getType());
		env.services().registerService(IdentityProvider.class, identityProvider);
	}
	
}
