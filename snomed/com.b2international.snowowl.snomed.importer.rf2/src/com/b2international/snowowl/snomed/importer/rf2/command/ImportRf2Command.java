/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.collect.Lists.newArrayList;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.UUID;

import org.eclipse.osgi.framework.console.CommandInterpreter;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.CodeSystemEntry;
import com.b2international.snowowl.datastore.file.FileRegistry;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.server.console.CommandLineAuthenticator;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemRequests;
import com.google.common.base.Strings;

/**
 * @since 6.0.0
 */
final class ImportRf2Command extends AbstractRf2ImporterCommand {

	private static final String TYPE_SWITCH = "-t";
	private static final String BRANCH_OR_VERSION_SWITCH = "-b";
	private static final String CREATE_VERSION_SWITCH = "-v";
	
	private static final String IMPORT_ARCHIVE_PATH_IS_MISSING = "Import archive path is missing.";
	
	public ImportRf2Command() {
		super(
			"rf2", 
			"-t <type> -b <branch> -v <path to rf2 archive> <code_system_short_name>",
			"Imports SNOMED CT RF2 release from a release archive",
			new String[] { 
				"-t <type>\t\tThe import type (FULL, SNAPSHOT or DELTA).",
				"-b <branch>\t\tThe existing branch to import the content onto. In case of extension import, an effective time from the base SNOMED CT release (e.g. 2016-01-31). If omitted 'MAIN' will be used.",
				"-v\t\t\tCreates versions for each effective time found in the release archive. If omitted no versions will be created.",
				"<path>\t\tSpecifies the release archive to import (must be a .zip file with a supported internal structure, such as the release archive of the International Release).",
				"<code_system_short_name>\t\tThe selected code system short name where you would like to import the content from the archive",
				"E.g:",
				"\tImporting the international release on MAIN:",
				"\tsctimport rf2 -t full -v C:/SnomedCT_RF2Release_INT_20160131.zip SNOMEDCT",
				"\tImport and extension on a branch:",
				"\tsctimport rf2 -t full -b 2016-01-31 -v C:/SnomedCT_Release_B2i_20160201.zip SNOMEDCT-B2I"
			}
		);
	}
	
	private List<String> getParameters(final CommandInterpreter interpreter) {
		List<String> parameters = newArrayList();
		String param = null;
		do {
			param = interpreter.nextArgument();
			if (param != null) {
				parameters.add(param);
			}
		} while (param != null);
		return parameters;
	}

	@Override
	public void execute(CommandInterpreter interpreter) {
		
		List<String> parameters = getParameters(interpreter);
		
		if (parameters.isEmpty()) {
			printDetailedHelp(interpreter);
			return;
		}
		
		if (parameters.size() < 4) {
			interpreter.println("Invalid number of arguments");
			printDetailedHelp(interpreter);
			return;
		}
		
		Rf2ReleaseType releaseType = null;
		String branchPath = IBranchPath.MAIN_BRANCH;
		boolean createVersions = false;
		String archiveFilePath = null;
		
		// release type
		
		if (!TYPE_SWITCH.equals(parameters.get(0))) {
			interpreter.println("Import type must be defined.");
			printDetailedHelp(interpreter);
			return;
		} else {
			String importType = parameters.get(1);
			try {
				releaseType = Rf2ReleaseType.getByNameIgnoreCase(importType);
			} catch (IllegalArgumentException e) {
				interpreter.println("Invalid or unknown import type '" + importType + ".");
				printDetailedHelp(interpreter);
				return;
			}
		}

		// create version flag
		
		if (parameters.contains(CREATE_VERSION_SWITCH)) {
			createVersions = true;
		}
		
		// archive path
		if (parameters.contains(BRANCH_OR_VERSION_SWITCH) && parameters.contains(CREATE_VERSION_SWITCH)) {
			if (parameters.size() > 5) {
				archiveFilePath = parameters.get(5);
				if (Strings.isNullOrEmpty(archiveFilePath)) {
					interpreter.println(IMPORT_ARCHIVE_PATH_IS_MISSING);
					printDetailedHelp(interpreter);
					return;
				}
			} else {
				interpreter.println(IMPORT_ARCHIVE_PATH_IS_MISSING);
				printDetailedHelp(interpreter);
				return;
			}
		} else if (parameters.contains(BRANCH_OR_VERSION_SWITCH) && !parameters.contains(CREATE_VERSION_SWITCH)) {
			if (parameters.size() > 4) {
				archiveFilePath = parameters.get(4);
				if (Strings.isNullOrEmpty(archiveFilePath)) {
					interpreter.println(IMPORT_ARCHIVE_PATH_IS_MISSING);
					printDetailedHelp(interpreter);
					return;
				}
			} else {
				interpreter.println(IMPORT_ARCHIVE_PATH_IS_MISSING);
				printDetailedHelp(interpreter);
				return;
			}
		} else if (!parameters.contains(BRANCH_OR_VERSION_SWITCH) && parameters.contains(CREATE_VERSION_SWITCH)) {
			archiveFilePath = parameters.get(3);
			if (Strings.isNullOrEmpty(archiveFilePath)) {
				interpreter.println(IMPORT_ARCHIVE_PATH_IS_MISSING);
				printDetailedHelp(interpreter);
				return;
			}
		} else if (!parameters.contains(BRANCH_OR_VERSION_SWITCH) && !parameters.contains(CREATE_VERSION_SWITCH)) {
			archiveFilePath = parameters.get(2);
			if (Strings.isNullOrEmpty(archiveFilePath)) {
				interpreter.println(IMPORT_ARCHIVE_PATH_IS_MISSING);
				printDetailedHelp(interpreter);
				return;
			}
		}
		
		final File archiveFile = new File(archiveFilePath);
		
		if (!archiveFile.isFile()) {
			interpreter.println("Invalid import archive path.");
			printDetailedHelp(interpreter);
			return;
		}
		
		// code system
		String codeSystemShortName = null;
		if (!archiveFilePath.equals(parameters.get(parameters.size() - 1))) {
			codeSystemShortName = parameters.get(parameters.size() - 1);
			if (Strings.isNullOrEmpty(codeSystemShortName)) {
				interpreter.println("Code System Short Name must be specified");
				printDetailedHelp(interpreter);
				return;
			}
		} else {
			interpreter.println("Code System Short Name must be specified");
			printDetailedHelp(interpreter);
			return;
		}

		final CodeSystemEntry codeSystem = CodeSystemRequests.prepareGetCodeSystem(codeSystemShortName).build(SnomedDatastoreActivator.REPOSITORY_UUID).execute(ApplicationContext.getServiceForClass(IEventBus.class)).getSync();
		
		// branchPath
		
		if (parameters.contains(BRANCH_OR_VERSION_SWITCH)) {
			branchPath = parameters.get(3);
			if (codeSystem.getExtensionOf() == null) {
				if (!BranchPathUtils.exists(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath)) {
					interpreter.println(String.format("Invalid branch path '%s'", branchPath));
					printDetailedHelp(interpreter);
					return;
				}
			} else {
				if (!branchPath.equalsIgnoreCase(IBranchPath.MAIN_BRANCH)) {

					IBranchPath parentBranchPath = BranchPathUtils.createPath(BranchPathUtils.createMainPath(), branchPath);
					IBranchPath extensionBranchPath = BranchPathUtils.createPath(parentBranchPath, codeSystem.getShortName());

					if (!BranchPathUtils.exists(SnomedDatastoreActivator.REPOSITORY_UUID, extensionBranchPath.getPath())) {
						IEventBus eventBus = ApplicationContext.getServiceForClass(IEventBus.class);
						RepositoryRequests.branching().prepareCreate()
							.setParent(parentBranchPath.getPath())
							.setName(codeSystem.getShortName())
							.build(SnomedDatastoreActivator.REPOSITORY_UUID)
							.execute(eventBus)
							.getSync();
					}
					
					branchPath = extensionBranchPath.getPath();
				}
			}
		}

		try {

			final CommandLineAuthenticator authenticator = new CommandLineAuthenticator();

			if (!authenticator.authenticate(interpreter)) {
				return;
			}
			
			UUID rf2ArchiveId = UUID.randomUUID();
			ApplicationContext.getServiceForClass(FileRegistry.class).upload(rf2ArchiveId, new FileInputStream(archiveFile));
			
			SnomedRequests.rf2().prepareImport()
				.setRf2ArchiveId(rf2ArchiveId)
				.setReleaseType(releaseType)
				.setCreateVersions(createVersions)
				.setUserId(authenticator.getUsername())
				.build(codeSystem.getRepositoryUuid(), branchPath)
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.getSync();

			interpreter.println("SNOMED CT import has successfully finished.");

		} catch (final Exception e) {
			interpreter.printStackTrace(e);
		}
		
	}

}
