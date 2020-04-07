/*
 * Copyright 2018-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.extension.Component;

import picocli.CommandLine.HelpCommand;

/**
 * @since 7.0
 */
@Component
@picocli.CommandLine.Command(
	name = "users",
	header = "Displays and manages user sessions",
	description = "Displays information about user sessions and provides functionality to disconnect them from the server or prevent them from logging in, when required",
	subcommands = {
		HelpCommand.class
	}
)
public final class UsersCommand extends Command {

	@Override
	public void run(CommandLineStream out) {
		final Map<Long, String> connectedSessions = Collections.emptyMap();
		
		if (CompareUtils.isEmpty(connectedSessions)) {
			out.println("No users are connected to the server.");
			return;
		}
		
		for (final Entry<Long, String> session : connectedSessions.entrySet()) {
			out.println("User: " + session.getValue() + " | session ID: " + session.getKey());
		}
	}

}
