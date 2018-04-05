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
package com.b2international.snowowl.snomed.importer.rf2.command;

import java.io.File;
import java.net.MalformedURLException;

import org.eclipse.osgi.framework.console.CommandInterpreter;

import com.b2international.commons.ConsoleProgressMonitor;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.server.console.CommandLineAuthenticator;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.ContentSubType;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.importer.ImportException;
import com.b2international.snowowl.snomed.importer.net4j.ImportConfiguration;
import com.b2international.snowowl.snomed.importer.net4j.ImportConfiguration.ImportSourceKind;
import com.b2international.snowowl.snomed.importer.rf2.util.ImportUtil;

public class ImportRefSetCommand extends AbstractRf2ImporterCommand {

	public ImportRefSetCommand() {
		super(
				"rf2_refset",
				"<path> ... -t <type> [-x <excludedId> ...]",
				"Imports reference sets in RF2 format",
				new String[] {
					"<path> ...\t\tSpecifies the file or files to be used for importing.",
					"-t <type>\t\tSets the import type (FULL, SNAPSHOT, or DELTA).",
					"-x <excludedId> ...\tExcludes the specified reference set IDs from the import. All other reference sets will be imported."
				});
	}
	
	@Override
	public void execute(final CommandInterpreter interpreter) {
		
		// TODO should make this command branch path aware as well
		boolean isTerminologyAvailable = SnomedRequests.prepareSearchConcept()
				.setLimit(0)
				.filterById(Concepts.ROOT_CONCEPT)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, Branch.MAIN_PATH)
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.getSync().getTotal() > 0;
		
		if (!isTerminologyAvailable) {
			interpreter.println("SNOMED CT terminology is not present, a core release has to be imported first.");
			return;
		}
		
		final ImportConfiguration configuration = new ImportConfiguration(Branch.MAIN_PATH);
		
		String arg = null;
		
		while ((arg = interpreter.nextArgument()) != null) {
			
			if ("-x".equals(arg) || "-t".equals(arg)) {
				break;
			}
			
			final File refSetFile = new File(arg);
			
			if (refSetFile.getPath().trim().length() < 1) {
				interpreter.println("Reference set path is invalid.");
				return;
			}

			if (!refSetFile.canRead()) {
				interpreter.println("Reference set file '" + refSetFile.getPath() + "' does not exist or is not readable.");
				return;
			}

			try {
				configuration.addRefSetURL(refSetFile.toURI().toURL());
			} catch (final MalformedURLException e) {
				interpreter.println(e);
			}
		}
		
		if (configuration.getRefSetUrls().isEmpty()) {
			printDetailedHelp(interpreter);
			return;
		}
		
		ContentSubType contentSubType = null;
		
		if ("-t".equals(arg)) {
			
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
				interpreter.println("Invalid import type (must be FULL, SNAPSHOT or DELTA).");
				printDetailedHelp(interpreter);
				return;
			}
		} else {
			interpreter.println("Import type needs to be specified (one of FULL, SNAPSHOT or DELTA).");
			printDetailedHelp(interpreter);
			return;
		}
		
		configuration.setContentSubType(contentSubType);
		configuration.setSourceKind(ImportSourceKind.FILES);
		
		if ("-x".equals(arg)) {
			while ((arg = interpreter.nextArgument()) != null) {
				configuration.excludeRefSet(arg);
			}
		}
		
		try {
			
			final CommandLineAuthenticator authenticator = new CommandLineAuthenticator();
			
			if (!authenticator.authenticate(interpreter)) {
				return;
			}
			
			final String userId = authenticator.getUsername();
			new ImportUtil().doImport(userId, configuration, new ConsoleProgressMonitor());
			
		} catch (final ImportException e) {
			interpreter.println("Caught exception during import.");
			interpreter.println(e);
			return;
		}
	}
}