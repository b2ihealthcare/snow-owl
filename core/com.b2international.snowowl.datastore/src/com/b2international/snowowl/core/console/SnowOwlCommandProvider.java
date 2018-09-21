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

import static com.google.common.collect.Lists.newArrayList;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

import com.b2international.commons.extension.ClassPathScanner;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.eventbus.IEventBus;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;

import picocli.CommandLine;
import picocli.CommandLine.IVersionProvider;
import picocli.CommandLine.Option;

/**
 * @since 7.0
 */
public final class SnowOwlCommandProvider implements CommandProvider {

	private static final int USAGE_WIDTH = 150;
	private final String usage;

	public SnowOwlCommandProvider() {
		this.usage = getHelp(cli());
	}
	
	@Override
	public String getHelp() {
		return String.format("---Snow Owl Commands---\n%s", usage);
	}

	public void _snowowl(CommandInterpreter interpreter) throws Exception {
		// first read all args into an array
		List<String> args = newArrayList();
		String arg;
		while ((arg = interpreter.nextArgument()) != null) {
			args.add(arg);
		}
		final List<CommandLine> commands = cli().parse(args.toArray(new String[]{}));
		
		try (InterpreterStream out = new InterpreterStream(interpreter)) {
			// print help if requested for any command
			if (CommandLine.printHelpIfRequested(commands, out, out, CommandLine.Help.Ansi.AUTO)) {
				return;
			}
			
			// get the last command used in the cli
			CommandLine cli = Iterables.getLast(commands, null);
			if (cli == null) {
				return;
			}
			// we should get an executable Snow Owl Command, so execute it
			((BaseCommand) cli.getCommand()).run(out);
		} catch (Exception e) {
			interpreter.println("Unknown error occured");
			interpreter.printStackTrace(e);
		} 
	}
	
	private String getHelp(CommandLine cmd) {
		try (StringWriter sw = new StringWriter();
				PrintWriter writer = new PrintWriter(sw)) {
			cmd.usage(writer);
			return sw.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private CommandLine cli() {
		final CommandLine cli = new CommandLine(new SnowOwlCommand());
		cli.setUsageHelpWidth(USAGE_WIDTH);
		cli.addSubcommand("help", new CommandLine.HelpCommand());
		ClassPathScanner.INSTANCE.getComponentsBySuperclass(Command.class)
			.stream()
			.sorted(Ordering.natural().onResultOf(Command::getCommand))
			.forEach(cmd -> {
				if (cmd.getClass().isAnnotationPresent(picocli.CommandLine.Command.class)) {
					cli.addSubcommand(cmd.getCommand(), cmd);
				}
			});
		return cli;
	}
	
	@CommandLine.Command(
		name = "snowowl", 
		versionProvider = SnowOwlVersionProvider.class
		
	)
	private final class SnowOwlCommand extends BaseCommand {
		
		@Option(names = {"-v", "--version"}, versionHelp = true, description = "Show this help message and exit.")
		boolean versionInfoRequested;

		@Option(names = {"-h", "--help"}, usageHelp = true, description = "Print version information and exit.")
		boolean usageHelpRequested;
		
		@Override
		public void run(CommandLineStream out) {
			out.println(usage);
		}
		
	}
	
	private static final class SnowOwlVersionProvider implements IVersionProvider {
		
		@Override
		public String[] getVersion() throws Exception {
			return new String[] {
				RepositoryRequests.prepareGetServerInfo()
					.buildAsync()
					.execute(ApplicationContext.getServiceForClass(IEventBus.class))
					.getSync()
					.version()
			};
		}
		
	}
	
	private static final class InterpreterStream extends PrintStream implements CommandLineStream {
	
		private final CommandInterpreter interpreter;

		public InterpreterStream(CommandInterpreter interpreter) {
			super(new ByteArrayOutputStream()); // should not receive any output, we delegate all print calls to the interpreter
			this.interpreter = interpreter;
		}
		
		@Override
		public void print(String o) {
			interpreter.print(o);
		}
		
		@Override
		public void print(Object o) {
			interpreter.print(o);
		}
		
		@Override
		public void println() {
			interpreter.println();
		}
		
		@Override
		public void println(String o) {
			interpreter.println(o);
		}
		
		@Override
		public void println(Object o) {
			interpreter.println(o);
		}
		
	}

}
