/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.authentication.ldap;

import static com.google.common.base.Preconditions.checkState;

import com.b2international.snowowl.authentication.AuthenticationConfiguration;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.setup.DefaultBootstrapFragment;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.core.users.IUserManager;

/**
 * Bootstrap fragment implementation for LDAP based authentication module.
 * 
 * @since 3.3
 */
public class LdapAuthenticationBootstrap extends DefaultBootstrapFragment {

	@Override
	public void init(SnowOwlConfiguration configuration, Environment env) throws Exception {
		if ("LDAP".equals(configuration.getModuleConfig(AuthenticationConfiguration.class).getType())) {
			checkState(env.services().getService(IUserManager.class) == null, "Another IUserManager implemententation is already registered");
			env.services().registerService(IUserManager.class, new LdapUserManager());
		}
	}

}