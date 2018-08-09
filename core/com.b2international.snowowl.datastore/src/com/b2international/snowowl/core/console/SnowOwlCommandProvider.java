/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

import com.b2international.commons.extension.ClassPathScanner;

/**
 * @since 7.0
 */
public final class SnowOwlCommandProvider implements CommandProvider {

	private final SortedMap<String, Command> availableCommands = new TreeMap<>();
	
	public SnowOwlCommandProvider() {
		ClassPathScanner.INSTANCE.getComponentsBySuperclass(Command.class)
			.forEach(cmd -> {
				this.availableCommands.put(cmd.getCommand(), cmd);
			});
	}
	
	@Override
	public String getHelp() {
		final StringBuilder help = new StringBuilder();
		help.append("--- Snow Owl Commands ---\n");
		availableCommands.values()
			.stream()
			.map(cmd -> String.format("\tsnowowl %s\n", cmd.getHelp()))
			.forEach(help::append);
		return help.toString();
	}
	
	public void _snowowl(CommandInterpreter interpreter) {
		final String cmd = interpreter.nextArgument();
		final Command command = availableCommands.get(cmd);
		if (command == null) {
			interpreter.println("snowowl: '%s' is not a snowowl command. See 'snowowl --help' for the available commands.");
			return;
		}
			
		try {
			command.run(interpreter);
		} catch (Exception e) {
			if (command != null) {
				interpreter.println(String.format("Failed to execute command " + command.getCommand()));
			}
			interpreter.printStackTrace(e);
		}
	}

}
