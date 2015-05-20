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
package com.b2international.snowowl.authentication.file;

import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.FailedLoginException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.Pair;
import com.b2international.snowowl.authentication.AbstractLoginModule;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.users.IUserManager;
import com.b2international.snowowl.core.users.User;

/**
 * JAAS login module, backed by a simple properties file.
 * 
 */
public class FileLoginModule extends AbstractLoginModule {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileLoginModule.class);

	@Override
	public void initialize(final Subject subject, final CallbackHandler callbackHandler, final Map<String, ?> state, final Map<String, ?> options) {
		LOGGER.info("Initializing file-based login module.");
		super.initialize(subject, callbackHandler, state, options);
	}

	@Override
	protected void doLogin(final Pair<String, String> userNameAndPassword) throws FailedLoginException {
		final IUserManager userManager = ApplicationContext.getInstance().getService(IUserManager.class);
		final User user = userManager.getUser(userNameAndPassword.getA());

		if (null == user) {
			throw new FailedLoginException("Incorrect user name or password.");
		}

		final char[] storedPassword = user.getPassword().toCharArray();
		final char[] enteredPassword = userNameAndPassword.getB().toCharArray();
		final int commonSegmentLength = Math.min(storedPassword.length, enteredPassword.length);

		boolean match = true;

		for (int i = 0; i < commonSegmentLength; i++) {
			// XXX: using non-eagerly evaluated version of 'and' operator, so that it takes the same amount of time
			match = (storedPassword[i] == enteredPassword[i]) & match;
		}

		if (!match || (storedPassword.length != enteredPassword.length)) {
			throw new FailedLoginException("Incorrect user name or password.");
		}
	}
}