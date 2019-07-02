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
package com.b2international.snowowl.snomed.importer.rf2.command;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Set;

import org.eclipse.osgi.framework.console.CommandInterpreter;

import com.b2international.commons.ConsoleProgressMonitor;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.server.console.CommandLineAuthenticator;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.ContentSubType;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.importer.net4j.ImportConfiguration;
import com.b2international.snowowl.snomed.importer.net4j.ImportConfiguration.ImportSourceKind;
import com.b2international.snowowl.snomed.importer.rf2.util.ImportUtil;
import com.google.common.collect.ImmutableSet;

public class ImportRefSetCommand extends AbstractRf2ImporterCommand {

	private static final String BRANCH = "-b";
	private static final String TYPE = "-t";
	private static final String CODESYSTEM = "-c";
	private static final String EXCLUSION = "-e";
	
	private static final Set<String> OPTION_KEYS = ImmutableSet.of(BRANCH, TYPE, CODESYSTEM, EXCLUSION);
	
	public ImportRefSetCommand() {
		super(
				"rf2_refset",
				"<path> ... [-t <type>] [-b <branch>] [-c <code_system_short_name>] [-x <excludedId> ...]",
				"Imports reference sets in RF2 format",
				new String[] {
					"<path> ...\t\tSpecifies the file or files to be used for importing.",
					BRANCH + " <branch>\t\tSets the branch path to use for the import. Defaults to the 'MAIN' branch.",
					TYPE + " <type>\t\tSets the import type (FULL, SNAPSHOT, or DELTA). Defaults to 'DELTA'.",
					CODESYSTEM + " <code_system_short_name>\t\tThe selected code system short name where you would like to import the refset(s). Defaults to 'SNOMEDCT'.",
					EXCLUSION + " <excludedId> ...\tExcludes the specified reference set IDs from the import. All other reference sets will be imported. Defaults to no exclusion IDs."
				});
	}
	
	@Override
	public void execute(final CommandInterpreter interpreter) {
		
		final ImportConfiguration configuration = new ImportConfiguration();
		configuration.setBranchPath(Branch.MAIN_PATH);
		configuration.setCodeSystemShortName(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME);
		configuration.setContentSubType(ContentSubType.DELTA);
		configuration.setSourceKind(ImportSourceKind.FILES);
		
		String arg = null;
		
		while ((arg = interpreter.nextArgument()) != null) {
			
			if (OPTION_KEYS.contains(arg)) {
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
		
		while (arg != null) {
			if (TYPE.equals(arg)) {
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
				} else {
					configuration.setContentSubType(contentSubType);
				}
			}

			if (CODESYSTEM.equals(arg)) {
				configuration.setCodeSystemShortName(interpreter.nextArgument());
			}
			
			if (EXCLUSION.equals(arg)) {
				configuration.excludeRefSet(interpreter.nextArgument());
			}
			
			if (BRANCH.equals(arg)) {
				configuration.setBranchPath(interpreter.nextArgument());
			}
			
			arg = interpreter.nextArgument();
		}
		
		try {
			
			boolean isTerminologyAvailable = SnomedRequests.prepareSearchConcept()
					.setLimit(0)
					.filterById(Concepts.ROOT_CONCEPT)
					.build(SnomedDatastoreActivator.REPOSITORY_UUID, configuration.getBranchPath())
					.execute(ApplicationContext.getServiceForClass(IEventBus.class))
					.getSync().getTotal() > 0;
			
			if (!isTerminologyAvailable) {
				interpreter.println("SNOMED CT terminology is not present, a core release has to be imported first.");
				return;
			}
			
			final CommandLineAuthenticator authenticator = new CommandLineAuthenticator();
			
			if (!authenticator.authenticate(interpreter)) {
				return;
			}
			
			final String userId = authenticator.getUsername();
			new ImportUtil().doImport(userId, configuration, new ConsoleProgressMonitor());
			
		} catch (final Exception e) {
			interpreter.println("Caught exception during import.");
			interpreter.printStackTrace(e);
		}
	}
}