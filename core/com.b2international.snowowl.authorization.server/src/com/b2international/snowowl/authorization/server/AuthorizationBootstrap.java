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
package com.b2international.snowowl.authorization.server;

import com.b2international.commons.platform.PlatformUtil;
import com.b2international.snowowl.authentication.AuthenticationConfiguration;
import com.b2international.snowowl.authorization.server.providers.IAuthorizationStrategy;
import com.b2international.snowowl.authorization.server.providers.file.FileBasedAuthorizationStrategy;
import com.b2international.snowowl.authorization.server.providers.ldap.LdapAuthorizationStrategy;
import com.b2international.snowowl.authorization.server.service.AdminPartyAuthorizationService;
import com.b2international.snowowl.authorization.server.service.AuthorizationService;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.setup.DefaultBootstrapFragment;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.core.users.IAuthorizationService;
import com.b2international.snowowl.rpc.RpcProtocol;
import com.b2international.snowowl.rpc.RpcUtil;

/**
 * @since 5.10.13
 */
public class AuthorizationBootstrap extends DefaultBootstrapFragment {

	@Override
	public void init(SnowOwlConfiguration configuration, Environment env) throws Exception {
		final IAuthorizationService service;
		if (!env.isEmbedded()) {
			// register proxy authorization service in embedded mode
			final RpcProtocol protocol = RpcUtil.getRpcClientProtocol(env.container());
			protocol.registerClassLoader(IAuthorizationService.class, IAuthorizationService.class.getClassLoader());
			service = protocol.getServiceProxy(IAuthorizationService.class);
		} else {
			final AuthenticationConfiguration authenticationConfiguration = configuration.getModuleConfig(AuthenticationConfiguration.class);
			service = createAuthorizationService(authenticationConfiguration);
		}
		ApplicationContext.getInstance().registerService(IAuthorizationService.class, service);
	}

	/**
	 * Returns the {@link IAuthorizationService} for the given JAAS type.
	 * 
	 * @param type
	 *            - the JAAS type
	 * @return
	 * @throws SnowowlServiceException
	 *             - if the given JAAS type is not available
	 */
	private IAuthorizationService createAuthorizationService(AuthenticationConfiguration conf) throws SnowowlServiceException {
		if (conf.isAdminParty() && PlatformUtil.isDevVersion(AuthorizationServerActivator.PLUGIN_ID)) {
			return new AdminPartyAuthorizationService();
		} else {
			// TODO refactor this switch to be OO
			IAuthorizationStrategy strategy = null;
			switch (conf.getType()) {
			case "PROP_FILE":
				strategy = new FileBasedAuthorizationStrategy();
				break;
			case "LDAP":
				strategy = new LdapAuthorizationStrategy();
				break;
			default:
				throw new SnowowlServiceException("Unknown authorization type: " + conf.getType());
			}
			return new AuthorizationService(strategy);
		}
	}
	
}
