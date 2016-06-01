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

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.datastore.ICodeSystem;
import com.b2international.snowowl.datastore.ICodeSystemVersion;
import com.b2international.snowowl.terminologyregistry.core.index.TerminologyRegistryClientService;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;

/**
 * OSGI command contribution with Snow Owl terminology registry commands.
 * 
 */
public class TerminologyRegistryCommandProvider implements CommandProvider {

	private static final String LISTALL_HELP = "\tterminologyregistry listall - List all registered terminologies\n";
	private static final String VERSIONS_HELP = "\tterminologyregistry versions [codesystem shortname] - List all the available versions for a particular terminology identified by its shortname.\n";
	private static final String DETAILS_HELP = "\tterminologyregistry details [-s codesystem shortname|-o codesystem OID] - Prints the detailed information of a particular terminology identified by its short name or OID.\n";

	@Override
	public String getHelp() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("--- Snow Owl terminology registry commands ---\n");
		buffer.append(LISTALL_HELP);
		buffer.append(DETAILS_HELP);
		buffer.append(VERSIONS_HELP);
		return buffer.toString();
	}

	/**
	 * Reflective template method declaratively registered. Needs to start with "_".
	 * @param interpreter
	 */
	public void _terminologyregistry(CommandInterpreter interpreter) {
		try {
			String cmd = interpreter.nextArgument();
			if ("listall".equals(cmd)) {
				listall(interpreter);
				return;
			}
			
			if ("details".equals(cmd)) {
				details(interpreter);
				return;
			}
			
			if ("versions".equals(cmd)) {
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
		String codeSystemShortName = interpreter.nextArgument();
		if (Strings.isNullOrEmpty(codeSystemShortName)) {
			interpreter.print("Command usage:" + VERSIONS_HELP);
			return;
		}
		
		final TerminologyRegistryClientService service = ApplicationContext.getInstance().getService(TerminologyRegistryClientService.class);
		Collection<ICodeSystemVersion> codeSystemVersions = service.getCodeSystemVersions(codeSystemShortName);
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
		
		if (!"-s".equals(identifierTypeSwitch) && !"-o".equals(identifierTypeSwitch)) {
			interpreter.print(DETAILS_HELP);
			return;
		}
		
		String codeSystemId = interpreter.nextArgument();
		if (Strings.isNullOrEmpty(codeSystemId)) {
			interpreter.print(DETAILS_HELP);
			return;
		}
		
		ICodeSystem codeSystem;
		final TerminologyRegistryClientService service = ApplicationContext.getInstance().getService(TerminologyRegistryClientService.class);
		if ("-s".equals(identifierTypeSwitch)) {
			codeSystem = service.getCodeSystemByShortName(codeSystemId);
		} else {
			codeSystem = service.getCodeSystemByOid(codeSystemId);
		}
		interpreter.println(getCodeSystemInformation(codeSystem, service));
	}

	/*
	 * List all registered terminologies
	 */
	private synchronized void listall(CommandInterpreter interpreter) {
		final TerminologyRegistryClientService service = ApplicationContext.getInstance().getService(TerminologyRegistryClientService.class);
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
	
	/**
	 * @param input
	 * @param service
	 * @return
	 */
	protected String getCodeSystemVersionInformation(ICodeSystemVersion codeSystemVersion) {
		
		StringBuilder builder = new StringBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy/MM/dd");
        
		Date effectiveDate = new Date(codeSystemVersion.getEffectiveDate());
		Date importDate = new Date(codeSystemVersion.getImportDate());
		Date lastUpdateDate = new Date(codeSystemVersion.getLastUpdateDate());
		
		builder
			.append("Version id: ").append(codeSystemVersion.getVersionId()).append("\n")
			.append("Description: ").append(codeSystemVersion.getDescription()).append("\n")
			.append("Effective date: ").append(dateFormat.format(effectiveDate)).append("\n")
			.append("Creation date: ").append(importDate).append("\n")
			.append("Last update: ").append(lastUpdateDate).append("\n")
			.append("Version branch path: ").append(codeSystemVersion.getPath())
			.append("Parent branch path: ").append(codeSystemVersion.getParentBranchPath()).append("\n")
			.append("Repository id: ").append(codeSystemVersion.getRepositoryUuid());
		return builder.toString();
	}
	
}