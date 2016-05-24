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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Set;

import org.eclipse.osgi.framework.console.CommandInterpreter;

import com.b2international.commons.ConsoleProgressMonitor;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.importer.ImportException;
import com.b2international.snowowl.server.console.CommandLineAuthenticator;
import com.b2international.snowowl.snomed.SnomedRelease;
import com.b2international.snowowl.snomed.common.ContentSubType;
import com.b2international.snowowl.snomed.core.store.SnomedReleases;
import com.b2international.snowowl.snomed.importer.net4j.SnomedImportResult;
import com.b2international.snowowl.snomed.importer.net4j.SnomedValidationDefect;
import com.b2international.snowowl.snomed.importer.rf2.util.ImportUtil;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

/**
 * Import release command for the OSGi console
 */
public class ImportZipCommand extends AbstractRf2ImporterCommand {

	private ImportUtil importUtil;

	public ImportZipCommand() {
		super("rf2_release", "-l <languageRefSetId> -t <type> -b <branch> -cv <true|false> <path> [release descriptor file path]",
				"Imports core terminology and reference sets from a release archive",
				new String[] { "-l <languageRefSetId>\tThe language reference set identifier to use for component labels.",
						"-t <type>\t\tThe import type (FULL, SNAPSHOT or DELTA).",
						"-b <branch>\t\tThe existing branch to import the content onto. Use 'MAIN' for the MAIN branch.",
						"-cv <true|false>\t\tCreates versions for each effective time found in the release.",
						"<path>\t\tSpecifies the release archive to import (must be a .zip file with a supported internal structure, such as the release archive of the International Release).",
						"[release descriptor file path]\tThe path to the optional release descriptor file."});

		importUtil = new ImportUtil();
	}

	@Override
	public void execute(final CommandInterpreter interpreter) {

		if (!"-l".equals(interpreter.nextArgument())) {
			printDetailedHelp(interpreter);
			return;
		}

		final String languageRefSetId = interpreter.nextArgument();

		if (languageRefSetId == null || languageRefSetId.isEmpty()) {
			interpreter.println("Language reference set identifier expected.");
			printDetailedHelp(interpreter);
			return;
		}

		ContentSubType contentSubType = null;

		if ("-t".equals(interpreter.nextArgument())) {

			final String subType = interpreter.nextArgument();
			boolean subTypeSet = false;

			for (final ContentSubType candidate : ContentSubType.values()) {
				if (candidate.name().equalsIgnoreCase(subType)) {
					contentSubType = candidate;
					subTypeSet = true;
					break;
				}
			}

			if (!subTypeSet) {
				interpreter.println("Invalid import type '" + subType + ".");
				printDetailedHelp(interpreter);
				return;
			}
		}

		final String branchPath;

		String branchSwitch = interpreter.nextArgument();
		if ("-b".equals(branchSwitch)) {
			branchPath = interpreter.nextArgument();

			if (!BranchPathUtils.exists(SNOMED_STORE, branchPath)) {
				interpreter.println("Invalid branch path '" + branchPath + "'.");
				printDetailedHelp(interpreter);
				return;
			}
		} else {
			interpreter.println("Expected paramater type '-b', received instead: '" + branchSwitch + "'.");
			printDetailedHelp(interpreter);
			return;
		}

		String createVersionSwitch = interpreter.nextArgument();

		boolean toCreateVersion;
		if ("-cv".equals(createVersionSwitch)) {
			String toCreateVersionString = interpreter.nextArgument();

			if (toCreateVersionString.equalsIgnoreCase("true")) {
				toCreateVersion = true;
			} else if (toCreateVersionString.equalsIgnoreCase("false")) {
				toCreateVersion = false;

			} else {
				interpreter.println("Could not parse " + toCreateVersionString + " as a boolean.");
				printDetailedHelp(interpreter);
				return;
			}
		} else {
			interpreter.println("Expected paramater type '-cv', received instead: '" + createVersionSwitch + "'.");
			printDetailedHelp(interpreter);
			return;
		}

		final String archivePath = interpreter.nextArgument();
		if (archivePath == null) {
			interpreter.println("No archive path specified.");
			printDetailedHelp(interpreter);
			return;
		}

		final File archiveFile = new File(archivePath);
		
		SnomedRelease snomedRelease = null;
		
		final String metadataFilePath = interpreter.nextArgument();
		if (metadataFilePath == null) {
			String fileName = archiveFile.getName();
			interpreter.println("SNOMED CT release descriptor is not found.  Falling back to archive file name: " + fileName);
			
			//TODO: INT for now, Gabor can grab info from some files...
			
			snomedRelease = SnomedReleases.newSnomedInternationalRelease().build();
		} else {
			Path configLocation = Paths.get(metadataFilePath);
			try (InputStream stream = Files.newInputStream(configLocation)) {
				Properties config = new Properties();
				config.load(stream);
				snomedRelease = createSnomedRelease(config);
			} catch (IOException e) {
				interpreter.printStackTrace(e);
				return;
			}
		}

		try {

			final CommandLineAuthenticator authenticator = new CommandLineAuthenticator();

			if (!authenticator.authenticate(interpreter)) {
				return;
			}

			final String userId = authenticator.getUsername();
			
			final SnomedImportResult result = importUtil.doImport(snomedRelease, userId, languageRefSetId, contentSubType, branchPath, archiveFile, toCreateVersion,
					new ConsoleProgressMonitor());
			Set<SnomedValidationDefect> validationDefects = result.getValidationDefects();
			boolean criticalFound = FluentIterable.from(validationDefects).anyMatch(new Predicate<SnomedValidationDefect>() {

				@Override
				public boolean apply(SnomedValidationDefect defect) {
					return defect.getDefectType().isCritical();
				}
			});
			
			if (criticalFound) {
				interpreter.println("SNOMED CT import has been canceled due to critical errors found in the RF2 release.");
			} else {
				interpreter.println("SNOMED CT import has successfully finished.");
			}

		} catch (final ImportException e) {
			interpreter.printStackTrace(e);
		}
	}

}