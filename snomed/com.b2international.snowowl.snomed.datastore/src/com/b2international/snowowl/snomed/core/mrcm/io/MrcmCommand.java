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
package com.b2international.snowowl.snomed.core.mrcm.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.extension.Component;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.console.Command;
import com.b2international.snowowl.core.console.CommandLineAuthenticator;
import com.b2international.snowowl.core.console.CommandLineStream;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.identity.domain.PermissionIdConstant;
import com.b2international.snowowl.identity.domain.User;

import picocli.CommandLine;
import picocli.CommandLine.HelpCommand;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * @since 7.0
 */
@Component
@picocli.CommandLine.Command(
	name = "mrcm",
	header = "Import/Export MRCM rules",
	description = "Imports and exports MRCM content from/to CSV and JSON files",
	subcommands = {
		HelpCommand.class,
		MrcmCommand.ImportCommand.class,
		MrcmCommand.ExportCommand.class
	}
)
public final class MrcmCommand extends Command {

	@Override
	public void run(CommandLineStream out) {
		CommandLine.usage(this, (PrintStream) out);
	}
	
	@picocli.CommandLine.Command(
		name = "import",
		header = "Imports MRCM rules",
		description = "Imports MRCM rules from JSON file"
	)
	private static final class ImportCommand extends Command {

		@Parameters(paramLabel = "FILE", description = "The JSON file with MRCM rules to import")
		String file;
		
		@Override
		public void run(CommandLineStream out) {
			final CommandLineAuthenticator authenticator = new CommandLineAuthenticator();
			
			if (!authenticator.authenticate(out)) {
				return;
			}
			
			final User user = authenticator.getUser();
			if (!user.hasPermission(PermissionIdConstant.IMPORT)) {
				out.println("User is unauthorized to import MRCM rules.");
				return;
			}
			
			try (final InputStream content = Files.newInputStream(Paths.get(file), StandardOpenOption.READ)) {
				ApplicationContext.getServiceForClass(MrcmImporter.class).doImport(user.getUsername(), content);
			} catch (IOException e) {
				out.println("Failed to import MRCM JSON file: " + file);
			}			
			
		}
		
	}

	@picocli.CommandLine.Command(
		name = "export",
		header = "Exports MRCM rules",
		description = "Exports MRCM rules to the selected format. Available formats are: CSV, JSON."
	)
	private static final class ExportCommand extends Command {

		@Parameters(paramLabel = "PATH", description = "Output directory to export the MRCM rules to. The output file will be automatically created.")
		String path;
		
		@Option(names = { "-f", "--format" }, defaultValue = "JSON", description = "MRCM Export Format option. CSV and JSON are supported.")
		MrcmExportFormat format;
		
		@Override
		public void run(CommandLineStream out) {
			final CommandLineAuthenticator authenticator = new CommandLineAuthenticator();
			
			if (!authenticator.authenticate(out)) {
				return;
			}
			
			final User user = authenticator.getUser();
			if (!user.hasPermission(PermissionIdConstant.EXPORT)) {
				out.println("User is unauthorized to export MRCM rules.");
				return;
			}
			
			out.println("Exporting MRCM rules (%s)...", format);
			
			final Path outputFolder = Paths.get(path);
			checkOutputFolder(outputFolder);
			final Path exportPath = outputFolder.resolve("mrcm_" + Dates.now() + "." + format.name().toLowerCase());
			
			try (final OutputStream stream = Files.newOutputStream(exportPath, StandardOpenOption.CREATE)) {
				ApplicationContext.getServiceForClass(MrcmExporter.class).doExport(user.getUsername(), stream, format);
				out.println("Exported MRCM rules to " + exportPath + " in " + format.name() + " format.");
			} catch (IOException e) {
				e.printStackTrace();
				out.println("Failed to export MRCM rules");
			}
			
		}
		
		private void checkOutputFolder(Path outputFolder) {
			final File folder = outputFolder.toFile();
			if (!folder.exists() || !folder.isDirectory()) {
				throw new BadRequestException("Export destination folder cannot be found.");
			}
			if (!folder.canRead()) {
				throw new BadRequestException("Cannot read destination folder.");
			}		
		}
		
	}

}
