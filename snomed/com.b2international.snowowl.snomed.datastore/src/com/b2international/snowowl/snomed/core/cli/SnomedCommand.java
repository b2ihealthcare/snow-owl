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
package com.b2international.snowowl.snomed.core.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.UUID;

import com.b2international.commons.extension.Component;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.attachments.AttachmentRegistry;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.console.Command;
import com.b2international.snowowl.core.console.CommandLineStream;
import com.b2international.snowowl.identity.domain.PermissionIdConstant;
import com.b2international.snowowl.identity.domain.User;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.ISnomedImportConfiguration.ImportStatus;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.datastore.request.rf2.Rf2ImportResponse;

import picocli.CommandLine;
import picocli.CommandLine.HelpCommand;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * @since 7.0
 */
@Component
@picocli.CommandLine.Command(
	name = "snomed",
	header = "Provides subcommands to manage SNOMED CT content",
	description = "Provides subcommands to manage SNOMED CT content",
	subcommands = {
		HelpCommand.class,
		SnomedCommand.ImportCommand.class
	}
)
public final class SnomedCommand extends Command {

	@Override
	public void run(CommandLineStream out) {
		CommandLine.usage(this, (PrintStream) out);
	}

	@picocli.CommandLine.Command(
		name = "import",
		header = "Imports SNOMED CT content",
		description = "Imports SNOMED CT content"
	)
	private static final class ImportCommand extends Command {
		
		private static final String SUPPORTED_FORMAT = "rf2";
		
		@Option(names = { "-c", "--codesystem" }, description = "The target codesystem where versions should be created if createVersions optionKey is enabled.", defaultValue = SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME)
		String codeSystem = SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME;
		
		@Option(names = { "-b", "--branch" }, description = "The target branch. After a successful import all importable content will be accessible from this branch.", defaultValue = Branch.MAIN_PATH)
		String branch = Branch.MAIN_PATH;
		
		@Option(names = { "-f", "--format" }, description = "The import file format. Currently 'rf2' is supported only.", defaultValue = SUPPORTED_FORMAT)
		String format = SUPPORTED_FORMAT;
		
		@Option(names = { "-t" }, description = "The importable release type from an RF2 compatible file.", defaultValue = "FULL")
		Rf2ReleaseType rf2ReleaseType = Rf2ReleaseType.FULL;
		
		@Option(names = { "-v" }, description = "Whether to create versions for the underlying code system or just import the content.", defaultValue = "true")
		boolean createVersions;
		
		@Parameters(paramLabel = "PATH", description = "The absolute path to the importable file")
		String path;
		
		@Override
		public void run(CommandLineStream out) {
			if (!SUPPORTED_FORMAT.equalsIgnoreCase(format)) {
				out.println("Unrecognized import format: '%s'. Supported formats are: %s", format, SUPPORTED_FORMAT);
			}
			
			final User user = out.authenticate();
			
			if (user == null || !user.hasPermission(PermissionIdConstant.IMPORT)) {
				out.println("User is unauthorized to import SNOMED CT content.");
				return;
			}
			
			UUID rf2ArchiveId = UUID.randomUUID();
			try (FileInputStream in = new FileInputStream(new File(path))) {
				ApplicationContext.getServiceForClass(AttachmentRegistry.class).upload(rf2ArchiveId, in);
			} catch (IOException e) {
				if (e instanceof FileNotFoundException) {
					out.println("Cannot find the path specified. '%s'", path);
				} else {
					out.println("Error reading the path specified. '%s'. Message: '%s'", path, e.getMessage());
				}
				return;
			}
			
			final Rf2ImportResponse response = SnomedRequests.rf2().prepareImport()
					.setCodeSystemShortName(codeSystem)
					.setCreateVersions(createVersions)
					.setRf2ArchiveId(rf2ArchiveId)
					.setUserId(user.getUsername())
					.setReleaseType(rf2ReleaseType)
					.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch)
					.execute(getBus())
					.getSync();
			
			if (response.getStatus() == ImportStatus.COMPLETED) {
				out.println("Successfully imported SNOMED CT content from file: %s", path);
			} else {
				if (!response.getIssues().isEmpty()) {
					out.println("There are %s validation errors in the provided '%s' file.", response.getIssues().size(), path);
				} else {
					out.println("Failed to import SNOMED CT content from file: %s", path);
				}
			}
			
		}
		
	}
 	
	

}
