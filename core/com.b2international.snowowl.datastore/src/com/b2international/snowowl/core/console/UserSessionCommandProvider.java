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
package com.b2international.snowowl.core.console;

import static com.google.common.collect.Lists.newArrayList;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.Pair;
import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.datastore.session.IApplicationSessionManager;
import com.google.common.base.Strings;

/**
 * OSGI command contribution with Snow Owl commands to manage user sessions.
 * 
 */
public class UserSessionCommandProvider implements CommandProvider {

	private static Logger USER_ACTIVITY_LOGGER = LoggerFactory.getLogger(UserSessionCommandProvider.class);
	
	@Override
	public String getHelp() {
		final StringBuffer buffer = new StringBuffer();
		buffer.append("---Snow Owl user session commands---\n");
		buffer.append("\tsession users - List the users that are currently logged in\n");
		buffer.append("\tsession disconnect [userName1,userName2,userNameN|ALL] - Disconnect active user(s) or all users.  Do not use space between the users.\n");
		buffer.append("\tsession login [enabled|disabled|status] - Enables/disables login for new, non-administrator sessions.\n");
		return buffer.toString();
	}

	/**
	 * Reflective template method declaratively registered. Needs to start with
	 * "_".
	 * 
	 * @param interpreter
	 */
	public void _session(final CommandInterpreter interpreter) {

		try {
			
			final String cmd = Strings.nullToEmpty(interpreter.nextArgument());
			
			switch (cmd) {
				case "login":
					login(interpreter);
					break;
				default:
					interpreter.println(getHelp());
					break;
			}

		} catch (final Exception ex) {
			interpreter.println(ex.getMessage());
		}
	}

	/**
	 * List the active users of the session.
	 * 
	 * @param interpreter
	 */
	public synchronized void users(final CommandInterpreter interpreter) {
		
		final List<Pair<String, String>> info = newArrayList(ApplicationContext.getInstance().getService(IApplicationSessionManager.class).getConnectedSessionInfo());
		
		if (CompareUtils.isEmpty(info)) {
			
			interpreter.println("No users are connected to the server.");
			
		} else {
		
			Collections.sort(info, new Comparator<Pair<String, String>>() {
				@Override public int compare(final Pair<String, String> o1, final Pair<String, String> o2) {
					return Strings.nullToEmpty(o1.getA()).compareTo(Strings.nullToEmpty(o2.getA()));
				}
			});
			
			for (final Pair<String, String> pair : info) {
					interpreter.println("User: " + pair.getA() + " | session ID: " + pair.getB());
			}
			
		}

	}

	private static final List<String> ALLOWED_SUBCOMMANDS = Arrays.asList("enabled", "disabled", "status"); 
	
	public synchronized void login(final CommandInterpreter interpreter) {

		final String subCommand = interpreter.nextArgument();
		
		if (StringUtils.isEmpty(subCommand) || !ALLOWED_SUBCOMMANDS.contains(subCommand.toLowerCase())) {
			interpreter.print("Command usage: session login [enabled|disabled|status]");
			return;
		}
		
		IApplicationSessionManager applicationSessionManager = ApplicationContext.getInstance().getService(IApplicationSessionManager.class);
		if (subCommand.equalsIgnoreCase("status")) {
			interpreter.println(MessageFormat.format("Non-administrative logins are currently {0}.", (applicationSessionManager.isLoginEnabled() ? "enabled" : "disabled")));
			return;
		}

		final boolean loginEnabled = subCommand.equalsIgnoreCase("enabled");
		applicationSessionManager.enableLogins(loginEnabled);
		interpreter.println(MessageFormat.format("{0} non-administrative logins.", (loginEnabled ? "Enabled" : "Disabled")));
	}

	/*
	 * There should be no spaces present in the input string.
	 */
	private List<String> tokenizeParameter(final String userName) {
		final List<String> userList = new ArrayList<String>();
		
		final StringTokenizer st = new StringTokenizer(userName, ",");
		while (st.hasMoreElements()) {
			userList.add(st.nextToken());
		}
		return userList;
	}

}