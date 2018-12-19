/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.reasoner.console;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.snomed.reasoner.domain.ReasonerExtensions;
import com.b2international.snowowl.snomed.reasoner.request.ClassificationRequests;

/**
 * A {@link CommandProvider} implementation for the administration of the OWL ontology and reasoner services on the OSGi
 * console.
 */
public class SnomedOntologyCommandProvider implements CommandProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(SnomedOntologyCommandProvider.class);
	
	private enum Command {
		
		LIST("- Lists all registered reasoners on the server.") {
			@Override public void execute(final SnomedOntologyCommandProvider provider, final CommandInterpreter interpreter) throws Exception {
				final ReasonerExtensions reasonerExtensions = ClassificationRequests.prepareSearchReasonerExtensions()
					.buildAsync()
					.get();
				
				interpreter.println("extensionId\tname\tversion");
				interpreter.println("-----------\t----\t-------");
				
				reasonerExtensions.forEach(e -> {
					interpreter.print(e.getExtensionId());
					interpreter.print("\t");
					interpreter.print(e.getName());
					interpreter.print("\t");
					interpreter.println(e.getVersion());
				});
			}
		},
		
		// Note: The name of this enum can be anything as long as it *is* invalid
		INVALID("") { 
			@Override public void execute(final SnomedOntologyCommandProvider provider, final CommandInterpreter interpreter) throws Exception { 
				interpreter.println(provider.getHelp());
			}
		};
		
		private final String helpText;
		
		private Command(final String helpText) {
			this.helpText = helpText;
		}
		
		public static Command fromCommandText(final String commandText) {
			
			for (final Command command : values()) {
				if (command.name().equalsIgnoreCase(commandText)) {
					return command;
				}
			}
			
			return INVALID;
		}
		
		public String getHelpText() {
			return helpText;
		}
		
		public abstract void execute(final SnomedOntologyCommandProvider provider, final CommandInterpreter interpreter) throws Exception;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.osgi.framework.console.CommandProvider#getHelp()
	 */
	@Override
	public String getHelp() {
		final StringBuilder helpStringBuilder = new StringBuilder();
		helpStringBuilder.append("--- SNOMED CT OWL ontology commands ---\n");
		
		for (final Command command : Command.values()) {
			
			if (Command.INVALID.equals(command)) {
				continue;
			}
			
			helpStringBuilder.append('\t');
			helpStringBuilder.append("ontology ");
			helpStringBuilder.append(command.name().toLowerCase());
			helpStringBuilder.append(' ');
			helpStringBuilder.append(command.getHelpText());
			helpStringBuilder.append('\n');
		}
		
		return helpStringBuilder.toString();
	}

	public void _ontology(final CommandInterpreter interpreter) {
		try {
			final Command cmd = Command.fromCommandText(interpreter.nextArgument());
			cmd.execute(this, interpreter);
		} catch (final Exception ex) {
			LOGGER.error("Caught runtime exception while executing command.", ex);
		}
	}
}
