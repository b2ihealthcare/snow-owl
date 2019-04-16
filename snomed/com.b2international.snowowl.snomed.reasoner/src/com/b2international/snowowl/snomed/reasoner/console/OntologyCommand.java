/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.io.PrintStream;

import com.b2international.commons.extension.Component;
import com.b2international.snowowl.core.console.Command;
import com.b2international.snowowl.core.console.CommandLineStream;
import com.b2international.snowowl.snomed.reasoner.domain.ReasonerExtensions;
import com.b2international.snowowl.snomed.reasoner.request.ClassificationRequests;

import picocli.CommandLine;
import picocli.CommandLine.HelpCommand;

/**
 * @since 7.1
 */
@Component
@CommandLine.Command(
	name = "ontology",
	header = "Displays information about branches in a repository",
	description = "",
	subcommands = {
		HelpCommand.class,
		OntologyCommand.ListCommand.class
	}
)
public class OntologyCommand extends Command {

	@Override
	public void run(CommandLineStream out) {
		CommandLine.usage(this, (PrintStream) out);
	}
	
	@CommandLine.Command(
		name = "list",
		header = "Lists all registered reasoners on the server.",
		description = ""
	)
	public class ListCommand extends Command {

		@Override
		public void run(CommandLineStream out) {
			final ReasonerExtensions reasonerExtensions = ClassificationRequests.prepareSearchReasonerExtensions()
				.buildAsync()
				.get();
			
			out.println("extensionId\tname\tversion");
			out.println("-----------\t----\t-------");
			
			reasonerExtensions.forEach(e -> {
				out.print(e.getExtensionId());
				out.print("\t");
				out.print(e.getName());
				out.print("\t");
				out.println(e.getVersion());
			});
		}
		
	}
	
}
