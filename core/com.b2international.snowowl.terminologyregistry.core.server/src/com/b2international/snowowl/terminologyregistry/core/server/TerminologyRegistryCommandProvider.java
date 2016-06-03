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
package com.b2international.snowowl.terminologyregistry.core.server;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.ICodeSystem;
import com.b2international.snowowl.datastore.ICodeSystemVersion;
import com.b2international.snowowl.terminologyregistry.core.index.TerminologyRegistryClientService;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Ordering;

/**
 * OSGI command contribution with Snow Owl terminology registry commands.
 * 
 */
public class TerminologyRegistryCommandProvider implements CommandProvider {

	private static final String VERSIONS_COMMAND = "versions";
	private static final String DETAILS_COMMAND = "details";
	private static final String LISTALL_COMMAND = "listall";

	private static final String OID_SWITCH = "-o";
	private static final String SHORT_NAME_SWITCH = "-s";
	private static final String USAGE = "Command usage: ";
	
	private static final String LISTALL_HELP = "terminologyregistry listall - List all registered terminologies";
	private static final String VERSIONS_HELP = "terminologyregistry versions [codesystem shortname] - List all the available versions for a particular terminology identified by its short name.";
	private static final String DETAILS_HELP = "terminologyregistry details [-s codesystem shortname | -o codesystem OID] - Prints the detailed information of a particular terminology identified by its short name or OID.";

	@Override
	public String getHelp() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("--- Snow Owl terminology registry commands ---\n");
		buffer.append("\t" + LISTALL_HELP);
		buffer.append("\n");
		buffer.append("\t" + DETAILS_HELP);
		buffer.append("\n");
		buffer.append("\t" + VERSIONS_HELP);
		return buffer.toString();
	}

	/**
	 * Reflective template method declaratively registered. Needs to start with "_".
	 * @param interpreter
	 */
	public void _terminologyregistry(CommandInterpreter interpreter) {
		try {
			String cmd = interpreter.nextArgument();
			if (LISTALL_COMMAND.equals(cmd)) {
				listall(interpreter);
				return;
			}
			
			if (DETAILS_COMMAND.equals(cmd)) {
				details(interpreter);
				return;
			}
			
			if (VERSIONS_COMMAND.equals(cmd)) {
				versions(interpreter);
				return;
			}
			
			interpreter.println(getHelp());
		} catch (Exception ex) {
			interpreter.println(ex.getMessage());
		}
	}
	/*
	 * Print the versions
	 */
	private synchronized void versions(CommandInterpreter interpreter) {
		final String codeSystemShortName = interpreter.nextArgument();
		
		if (Strings.isNullOrEmpty(codeSystemShortName)) {
			interpreter.println(USAGE + VERSIONS_HELP);
			return;
		}
		
		final TerminologyRegistryClientService service = getTerminologyRegistryService();
		
		ICodeSystem codeSystem = service.getCodeSystemByShortName(codeSystemShortName);
		
		if (codeSystem == null) {
			interpreter.println(String.format("Unknown or invalid code system with identifier '%s'", codeSystemShortName));
			interpreter.println(USAGE + VERSIONS_HELP);
			return;
		}
		
		List<ICodeSystemVersion> codeSystemVersions = newArrayList(service.getCodeSystemVersions(codeSystemShortName));
		
		Collections.sort(codeSystemVersions, new Comparator<ICodeSystemVersion>() {
			@Override public int compare(ICodeSystemVersion o1, ICodeSystemVersion o2) {
				return Ordering.natural().compare(o1.getEffectiveDate(), o2.getEffectiveDate());
			}
		});
		
		interpreter.print(Joiner.on("\n").join(FluentIterable.from(codeSystemVersions).transform(new Function<ICodeSystemVersion, String>() {
			@Override public String apply(ICodeSystemVersion input) {
				return getCodeSystemVersionInformation(input);
			}
		})));
	}

	/*
	 * Print the details of the code system
	 */
	private synchronized void details(CommandInterpreter interpreter) {
		
		String identifierTypeSwitch = interpreter.nextArgument();
		
		if (!SHORT_NAME_SWITCH.equals(identifierTypeSwitch) && !OID_SWITCH.equals(identifierTypeSwitch)) {
			interpreter.println(USAGE + DETAILS_HELP);
			return;
		}
		
		String codeSystemNameOrId = interpreter.nextArgument();
		if (Strings.isNullOrEmpty(codeSystemNameOrId)) {
			interpreter.println(USAGE + DETAILS_HELP);
			return;
		}
		
		ICodeSystem codeSystem;
		final TerminologyRegistryClientService service = getTerminologyRegistryService();
		if (SHORT_NAME_SWITCH.equals(identifierTypeSwitch)) {
			codeSystem = service.getCodeSystemByShortName(codeSystemNameOrId);
		} else {
			codeSystem = service.getCodeSystemByOid(codeSystemNameOrId);
		}
		
		if (codeSystem == null) {
			interpreter.println(String.format("Unknown or invalid code system with identifier '%s'", codeSystemNameOrId));
			interpreter.println(USAGE + DETAILS_HELP);
			return;
		}
		
		interpreter.println(getCodeSystemInformation(codeSystem, service));
	}

	/*
	 * List all registered terminologies
	 */
	private synchronized void listall(CommandInterpreter interpreter) {
		final TerminologyRegistryClientService service = getTerminologyRegistryService();
		interpreter.print(Joiner.on("\n").join(FluentIterable.from(service.getCodeSystems()).transform(new Function<ICodeSystem, String>() {
			@Override public String apply(ICodeSystem input) {
				return getCodeSystemInformation(input, service);
			}
		})));
	}
	
	private String getCodeSystemInformation(ICodeSystem codeSystem, TerminologyRegistryClientService service) {
		StringBuilder builder = new StringBuilder();
		builder
			.append("Name: ").append(codeSystem.getName()).append("\n")
			.append("Short name: ").append(codeSystem.getShortName()).append("\n")
			.append("Code System OID: ").append(codeSystem.getOid()).append("\n")
			.append("Maintaining organization link: ").append(codeSystem.getOrgLink()).append("\n")
			.append("Language: ").append(codeSystem.getLanguage()).append("\n")
			.append("Last version: ").append(null == service.getVersionId(codeSystem) ? "N/A" : service.getVersionId(codeSystem)).append("\n")
			.append("Current branch path: ").append(codeSystem.getBranchPath()).append("\n");
		return builder.toString();
	}
	
	private String getCodeSystemVersionInformation(ICodeSystemVersion codeSystemVersion) {
		StringBuilder builder = new StringBuilder();
		builder
			.append("Version id: ").append(codeSystemVersion.getVersionId()).append("\n")
			.append("Description: ").append(codeSystemVersion.getDescription()).append("\n")
			.append("Effective date: ").append(EffectiveTimes.format(codeSystemVersion.getEffectiveDate(), DateFormats.DEFAULT)).append("\n")
			.append("Creation date: ").append(EffectiveTimes.format(codeSystemVersion.getImportDate(), DateFormats.DEFAULT)).append("\n")
			.append("Last update: ").append(codeSystemVersion.getLastUpdateDate() > 0 ? EffectiveTimes.format(codeSystemVersion.getLastUpdateDate(), DateFormats.DEFAULT) : "-").append("\n")
			.append("Version branch path: ").append(codeSystemVersion.getPath()).append("\n")
			.append("Parent branch path: ").append(codeSystemVersion.getParentBranchPath()).append("\n")
			.append("Repository id: ").append(codeSystemVersion.getRepositoryUuid()).append("\n");
		return builder.toString();
	}

	private TerminologyRegistryClientService getTerminologyRegistryService() {
		return ApplicationContext.getInstance().getService(TerminologyRegistryClientService.class);
	}
	
}