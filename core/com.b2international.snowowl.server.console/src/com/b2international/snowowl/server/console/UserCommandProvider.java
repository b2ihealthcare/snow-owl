/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.server.console;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.identity.IdentityProvider;
import com.b2international.snowowl.identity.IdentityWriter;
import com.google.common.base.Strings;

/**
 * @since 6.17
 */
public class UserCommandProvider implements CommandProvider {

	private static final String ADD_USER_COMMAND = "adduser";

	@Override
	public String getHelp() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("---Snow Owl User commands---\n");
		buffer.append("\tuser adduser [username] [password] - Creates a user for identification inside Snow Owl\n");
		return buffer.toString();
	}

	/**
	 * Reflective template method declaratively registered. Needs to start with "_".
	 * 
	 * @param interpreter
	 */
	public void _user(final CommandInterpreter interpreter) {
		try {

			final String cmd = Strings.nullToEmpty(interpreter.nextArgument());

			if (ADD_USER_COMMAND.equals(cmd)) {
				addUser(interpreter);
			} else {
				interpreter.println(getHelp());
			}

		} catch (final Exception ex) {
			interpreter.println(ex.getMessage());
		}
	}

	private void addUser(CommandInterpreter interpreter) {
		final IdentityProvider identityProvider = ApplicationContext.getServiceForClass(IdentityProvider.class);
		if (!(identityProvider instanceof IdentityWriter)) {
			interpreter.println("Could not find valid IdentityProvider.");
			return;
		}
		
		final String username = interpreter.nextArgument();
		if (Strings.isNullOrEmpty(username)) {
			interpreter.println("Username parameter was not defined.");
			return;
		}
		
		final String password = interpreter.nextArgument();
		if (Strings.isNullOrEmpty(password)) {
			interpreter.println("Password parameter was not defined.");
			return;
		}
		
		((IdentityWriter) identityProvider).addUser(username, password);
		
		interpreter.println(String.format("Registered user with username: '%s', and password: '%s'.", username, password));
		
	}

}
