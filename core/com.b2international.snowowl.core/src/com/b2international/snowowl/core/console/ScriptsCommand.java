/*
 * Copyright 2018-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.collect.Maps.newHashMap;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;

import javax.script.ScriptEngine;

import com.b2international.commons.extension.Component;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.core.setup.Plugins;
import com.b2international.snowowl.eventbus.IEventBus;

import picocli.CommandLine;
import picocli.CommandLine.HelpCommand;
import picocli.CommandLine.Parameters;

/**
 * @since 7.0
 */
@Component
@picocli.CommandLine.Command(
	name = "scripts",
	header = "Scripting shell commands",
	description = "Provides shell commands to work with Groovy scripts",
	subcommands = {
		HelpCommand.class,
		ScriptsCommand.RunCommand.class
	}
)
public class ScriptsCommand extends Command {

	@Override
	public void run(CommandLineStream out) {
		CommandLine.usage(this, (PrintStream) out);
	}
	
	@picocli.CommandLine.Command(
		name = "run",
		header = "Scripting shell commands",
		description = ""
	)	
	private static final class RunCommand extends Command {

		@Parameters(paramLabel = "PATH", description = "Absolute path to the script file to run.")
		String path;
		
		@Override
		public void run(CommandLineStream out) {
			try {
				
				final String script = Files.lines(Paths.get(path)).collect(Collectors.joining(System.getProperty("line.separator")));
				final ClassLoader classLoader = ApplicationContext.getServiceForClass(Plugins.class).getCompositeClassLoader();
				
				// Include the CLI-authorized IEventBus when running scripts
				final ServiceProvider context = ApplicationContext.getServiceForClass(Environment.class)
						.inject()
						.bind(IEventBus.class, getBus())
						.build();
				
				final Map<String, Object> binding = newHashMap();
				binding.put("ctx", context);
				ScriptEngine.run("groovy", classLoader, script, binding);
				
			} catch (Exception e) {
				e.printStackTrace();
				out.println(e.getMessage());
			}
		}
	}
}
