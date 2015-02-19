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
import java.text.MessageFormat;

import org.eclipse.osgi.framework.console.CommandInterpreter;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.ConsoleProgressMonitor;
import com.b2international.snowowl.importer.ImportException;
import com.b2international.snowowl.server.console.CommandLineAuthenticator;
import com.b2international.snowowl.snomed.common.ContentSubType;
import com.b2international.snowowl.snomed.importer.net4j.SnomedImportResult;
import com.b2international.snowowl.snomed.importer.net4j.SnomedValidationDefect;
import com.b2international.snowowl.snomed.importer.rf2.util.ImportUtil;

public class ImportZipCommand extends AbstractRf2ImporterCommand {

	private ImportUtil importUtil;

	public ImportZipCommand() {
		super("rf2_release", "-l <languageRefSetId> -t <type> <path>", "Imports core terminology and reference sets from a release archive", new String[] {
				"-l <languageRefSetId>\tThe language reference set identifier to use for component labels.",
				"-t <type>\t\tThe import type (FULL, SNAPSHOT or DELTA).",
				"<path>\t\tSpecifies the release archive to import (must be a .zip file with a supported internal structure, such as the release archive of the International Release)."
		});
		
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
		} else {
			interpreter.println("Import type needs to be specified (one of FULL, SNAPSHOT or DELTA).");
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

		try {
			
			final CommandLineAuthenticator authenticator = new CommandLineAuthenticator();
			
			if (!authenticator.authenticate(interpreter)) {
				return;
			}
			
			final String userId = authenticator.getUsername();
			
			final SnomedImportResult result = importUtil.doImport(userId, languageRefSetId, contentSubType, archiveFile, new ConsoleProgressMonitor());
			if (!CompareUtils.isEmpty(result.getValidationDefects())) {
				interpreter.println("SNOMED CT import has been canceled due to validation errors in the RF2 release.");
				for (final SnomedValidationDefect defect : result.getValidationDefects()) {
					for (final String offendingId : defect.getDefects()) {
						final String message = MessageFormat.format("\t* {0} {1}", defect.getDefectType(), offendingId);
						interpreter.println(message);
					}
				}
			}
			
		} catch (final ImportException e) {
			interpreter.printStackTrace(e);
		}
	}
}