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
package com.b2international.snowowl.authentication;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import com.b2international.commons.Pair;

/**
 * Abstract superclass for Snow Owl login modules.
 * 
 */
public abstract class AbstractLoginModule implements LoginModule {

	private CallbackHandler callbackHandler;

	@Override
	public void initialize(final Subject subject, final CallbackHandler callbackHandler, final Map<String, ?> sharedState, final Map<String, ?> options) {
		this.callbackHandler = checkNotNull(callbackHandler, "callbackHandler");
	}

	@Override
	public final boolean login() throws LoginException {
		final Pair<String, String> userNameAndPassword = getUsernameAndPasswordFromCallback();
		doLogin(userNameAndPassword);
		return true;
	}

	@Override
	public final boolean logout() {
		return true;
	}

	@Override
	public final boolean commit() {
		return true;
	}

	@Override
	public final boolean abort() {
		return true;
	}

	private Pair<String, String> getUsernameAndPasswordFromCallback() throws LoginException {
		checkNotNull(callbackHandler, "Callback handler is null.");

		final Callback[] callbacks = new Callback[2];
		callbacks[0] = new NameCallback("username");
		callbacks[1] = new PasswordCallback("password", false);

		try {

			callbackHandler.handle(callbacks);

			final String userName = ((NameCallback) callbacks[0]).getName();
			final String password =  new String(((PasswordCallback) callbacks[1]).getPassword());
			((PasswordCallback) callbacks[1]).clearPassword();

			return Pair.of(userName, password);

		} catch (final IOException e) {
			throw new LoginException(e.toString());
		} catch (final UnsupportedCallbackException e) {
			throw new LoginException(MessageFormat.format("Callback ''{0}'' is not recognized.", e.getCallback())); 
		}
	}

	protected abstract void doLogin(Pair<String, String> userNameAndPassword) throws LoginException;
}