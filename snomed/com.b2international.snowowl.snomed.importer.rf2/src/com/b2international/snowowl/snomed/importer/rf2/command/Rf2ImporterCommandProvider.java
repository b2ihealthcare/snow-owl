/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.List;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

import com.google.common.collect.ImmutableList;

/**
 * Command provider for import RF1 and RF2 files.
 * 
 *
 */
public class Rf2ImporterCommandProvider implements CommandProvider {
	
	private static final String NL = "\r\n";
	
	private final List<AbstractRf2ImporterCommand> commands = ImmutableList.of(
			new ListLanguageRefSetsCommand(),
			new ImportZipCommand(),
			new ImportRefSetCommand(),
			new ImportRefSetSubsetCommand()
//			new ImportRf2Command()
			);
	
	@Override
	public String getHelp() {
		
		final StringBuilder builder = new StringBuilder();
		builder.append(AbstractRf2ImporterCommand.HEADER + NL);
		
		for (final AbstractRf2ImporterCommand subCommand : commands) {
			builder.append(subCommand.getHelp() + NL);
		}
		
		return builder.toString();
	}

	public void _sctimport(final CommandInterpreter interpreter) {
	
		final String subCommandName = interpreter.nextArgument();
		
		if (subCommandName == null) {
			interpreter.print(getHelp());
			return;
		}
		
		for (final AbstractRf2ImporterCommand subCommand : commands) {
			if (subCommandName.equalsIgnoreCase(subCommand.getName())) {
				subCommand.execute(interpreter);
				return;
			}
		}
		
		interpreter.print(getHelp());
		return;
	}
}