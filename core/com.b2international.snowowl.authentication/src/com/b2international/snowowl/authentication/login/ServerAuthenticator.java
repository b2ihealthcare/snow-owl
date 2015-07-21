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
package com.b2international.snowowl.authentication.login;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;

import org.eclipse.equinox.security.auth.ILoginContext;
import org.eclipse.equinox.security.auth.LoginContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.StringUtils;
import com.b2international.commons.platform.PlatformUtil;
import com.b2international.snowowl.authentication.AuthenticationConfiguration;
import com.b2international.snowowl.core.LogUtils;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;

/**
 * JAAS authenticator, which can be configured with different login modules.
 * 
 * 
 */
public class ServerAuthenticator {

	private static final Logger LOG = LoggerFactory.getLogger(ServerAuthenticator.class);
	private static final String JAAS_CONFIG_FILE = "snowowl_jaas_configuration.conf";

	private ILoginContext loginContext;

	private String userName;
	private String password;

	private File jaasConfiguration;
	private AuthenticationConfiguration authenticationConfiguration;

	public ServerAuthenticator(SnowOwlConfiguration configuration) {
		checkNotNull(configuration, "configuration");
		this.jaasConfiguration = new File(configuration.getConfigurationDirectory(), JAAS_CONFIG_FILE);
		this.authenticationConfiguration = configuration.getModuleConfig(AuthenticationConfiguration.class);
		this.loginContext = initLoginContext(new SimpleCallBackHandler());
		LOG.info("Created {} based authenticator.", authenticationConfiguration.getType());
	}

	private ILoginContext initLoginContext(CallbackHandler callbackHandler) {
		final URL url = getFileAsUrl(this.jaasConfiguration,
				PlatformUtil.toFileURL(ServerAuthenticator.class, JAAS_CONFIG_FILE));
		return LoginContextFactory.createContext(authenticationConfiguration.getType(), url, callbackHandler);
	}

	private URL getFileAsUrl(File file, URL fallback) {
		try {
			if (file != null && file.canRead()) {
				/*
				 * We need to decode the URL, otherwise the underlying framework
				 * can't load the configuration file if it contains escaped
				 * characters. See:
				 * https://github.com/b2ihealthcare/snowowl/issues/653
				 */
				return new URL(URLDecoder.decode(file.toURI().toString(), "UTF-8"));
			}
			return fallback;
		} catch (final MalformedURLException e) {
			throw new SnowowlRuntimeException("Cannot locate JAAS configuration file.", e);
		} catch (UnsupportedEncodingException e) {
			throw new SnowowlRuntimeException("Cannot locate JAAS configuration file.", e);
		}
	}

	/**
	 * Attempts to log in the specified user with the specified password. Throws
	 * LoginException in case of a failed login (unathenticated).
	 * 
	 * @param userName
	 * @param password
	 * @throws LoginException
	 */
	public void login(String userName, String password) throws LoginException {
		synchronized (loginContext) {
			this.userName = userName;
			this.password = password;
			try {
				LogUtils.logUserAccess(LOG, userName, "Authenticating: " + userName);
				loginContext.login();
				LogUtils.logUserAccess(LOG, userName, "Authentication succeeded");
			} catch (LoginException loginException) {
				
				String reason = "";
				if (loginException.getCause() instanceof LoginException) {
					reason = !StringUtils.isEmpty(loginException.getCause().getMessage()) ? " Reason: "
							+ loginException.getCause().getMessage() : "";
				}
				
				final String message = userName + " could not log in." + reason;
				LOG.error(message);
				LogUtils.logUserAccess(LOG, userName, message);
				throw loginException;
			}
		}
	}

	private class SimpleCallBackHandler implements CallbackHandler {

		@Override
		public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
			for (int i = 0; i < callbacks.length; i++) {
				if (callbacks[i] instanceof NameCallback) {
					((NameCallback) callbacks[i]).setName(ServerAuthenticator.this.userName);
				} else if (callbacks[i] instanceof PasswordCallback) {
					((PasswordCallback) callbacks[i]).setPassword(ServerAuthenticator.this.password.toCharArray());
				}
			}
		}

	}

}