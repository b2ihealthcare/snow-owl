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
package com.b2international.snowowl.snomed.importer.rf2.command;

import java.text.MessageFormat;

import org.eclipse.osgi.framework.console.CommandInterpreter;

/**
 * Represents an abstract OSGi command for importing SNOMED CT components from various sources. 
 *
 */
public abstract class AbstractRf2ImporterCommand {

	private static final String MAIN_COMMAND_NAME = "sctimport";
	
	public static final String HEADER = "---Snow Owl SNOMED CT importer commands---";

	private final String name;
	private final String arguments;
	private final String description;
	private final String[] detailedHelpRows;

	public AbstractRf2ImporterCommand(final String name, final String arguments, final String description, final String[] detailedHelpRows) {
		this.name = name;
		this.arguments = arguments;
		this.description = description;
		this.detailedHelpRows = detailedHelpRows;
	}
	
	public String getName() {
		return name;
	}

	public abstract void execute(CommandInterpreter interpreter);
	
	public String getHelp() {
		return MessageFormat.format("\t{0} {1} {2} - {3}", MAIN_COMMAND_NAME, name, arguments, description);
	}

	public void printCommandHelp(final CommandInterpreter interpreter) {
		interpreter.println(getHelp());
	}

	public void printDetailedHelp(final CommandInterpreter interpreter) {
		interpreter.println();
		interpreter.println(MessageFormat.format("{0}.", description));
		interpreter.println();
		interpreter.println(MessageFormat.format("{0} {1} {2}", MAIN_COMMAND_NAME, name, arguments));
		
		for (final String detailedHelpRow : detailedHelpRows) {
			interpreter.print("  ");
			interpreter.println(detailedHelpRow);
		}
	}
}