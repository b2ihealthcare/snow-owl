/*
 * Copyright 2018-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.b2international.snowowl.core.RepositoryInfo;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.codesystem.CodeSystemVersion;
import com.b2international.snowowl.core.codesystem.CodeSystemVersionEntry;
import com.b2international.snowowl.core.codesystem.CodeSystemVersions;
import com.b2international.snowowl.core.codesystem.CodeSystems;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.plugin.Component;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.core.request.SearchResourceRequest.SortField;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;

import picocli.CommandLine;
import picocli.CommandLine.Option;

/**
 * @since 7.0
 */
@Component
@CommandLine.Command(
	name = "codesystems", 
	header = "Displays information about the available Code Systems",
	description = {"Displays information about the available Code Systems and their versions"}
)
public final class CodeSystemsCommand extends Command {

	private static final Ordering<CodeSystem> SHORT_NAME_ORDERING = Ordering.natural().onResultOf(CodeSystem::getShortName);

	@Option(names = { "-c", "--codesystem" }, description = { "A short name of the codeSystem to return" })
	String codeSystem;
	
	@Option(names = { "-v", "--versions" }, description = { "Display version information along with each returned Code System" })
	boolean showVersions;
	
	@Override
	public void run(CommandLineStream out) {
		if (Strings.isNullOrEmpty(codeSystem)) {
			out.println(Joiner.on("\n\n").join(FluentIterable.from(getCodeSystems()).transform(input -> getCodeSystemInfo(input))));
		} else {
			CodeSystem cs = getCodeSystemById(codeSystem);
			
			if (cs == null) {
				out.println(String.format("Unknown or invalid code system with identifier '%s'", codeSystem));
				return;
			}
			
			out.println(getCodeSystemInfo(cs));
		}
	}

	private String getCodeSystemInfo(CodeSystem codeSystem) {
		//Added the three spaces for better readability
		return new StringBuilder()
			.append(codeSystem.getShortName()).append("\n")
			.append("\tName: ").append(codeSystem.getName()).append("\n")
			.append("\tShort name: ").append(codeSystem.getShortName()).append("\n")
			.append("\tOID: ").append(codeSystem.getOid()).append("\n")
			.append("\tMaintaining organization link: ").append(codeSystem.getOrganizationLink()).append("\n")
			.append(codeSystem.getCitation() == null? "" :
					"\tCitation: " + codeSystem.getCitation() + "\n")
			.append(getAvailableUpgradesInfo(codeSystem))
			.append("\tCode System URI: ").append(codeSystem.getCodeSystemURI()).append("\n")
			.append("\tIcon Path: ").append(codeSystem.getIconPath()).append("\n")
			.append(getDependenciesInfo(codeSystem))
			.append(codeSystem.getUpgradeOf() == null? "" : 
					"\tUpgrade of: " + codeSystem.getUpgradeOf().toString() + "\n")
			.append(codeSystem.getExtensionOf() == null? "" : 
					"\tExtension of: " + codeSystem.getExtensionOf().toString() + "\n")
			.append("\tRepository: ").append(codeSystem.getRepositoryId()).append("\n")
			.append("\tWorking branch: ").append(codeSystem.getBranchPath()).append("\n")
			.append("\tTerminology ID: ").append(codeSystem.getTerminologyId())
			.append(getAdditionalPropertiesInfo(codeSystem))
			.append(getLocalesInfo(codeSystem))
			.append(showVersions ? "\t" + getCodeSystemVersionsInfo(codeSystem) : "")
			.toString();
	}
	
	private String getAdditionalPropertiesInfo(CodeSystem codeSystem) {
		if(codeSystem.getAdditionalProperties() == null || codeSystem.getAdditionalProperties().isEmpty()){
			return "";
		}
		
		StringBuilder result = new StringBuilder("\n\tAdditional Properties:\n");
		codeSystem.getAdditionalProperties().forEach((key, value) -> result.append("\t\t").append(key).append(": ").append(value.toString()).append("\n"));;
		
		return result.toString();
	}

	private String getDependenciesInfo(CodeSystem codeSystem) {
		if(codeSystem.getDependencies() == null || codeSystem.getDependencies().isEmpty()){
			return "";
		}
		
		StringBuilder result = new StringBuilder("\tDependencies:\n");
		codeSystem.getDependencies().forEach(dependency -> result.append("\t\t").append(dependency).append("\n"));
		
		return result.toString();
	}

	private String getLocalesInfo(CodeSystem codeSystem) {
		if(codeSystem.getLocales() == null || codeSystem.getLocales().isEmpty()){
			return "";
		}
		StringBuilder result = new StringBuilder("\tLocales:\n");
		result.append(codeSystem.getLocales().stream().map(e -> "\t\t" + e.toString()).collect(Collectors.joining("\n")));
		
		return result.toString();
	}

	private String getAvailableUpgradesInfo(CodeSystem codeSystem) {
		
		if(codeSystem.getAvailableUpgrades() == null || codeSystem.getAvailableUpgrades().isEmpty()){
			return "";
		}
		StringBuilder result = new StringBuilder("\tAvailable Upgrades:\n");
		codeSystem.getAvailableUpgrades().forEach(update -> result.append("\t\t").append(update).append("\n"));
		
		return result.toString();
	}

	private String getCodeSystemVersionsInfo(CodeSystem cs) {
		final StringBuilder info = new StringBuilder("\n\tVersions:\n");
		final CodeSystemVersions versions = CodeSystemRequests
			.prepareSearchCodeSystemVersion()
			.all()
			.filterByCodeSystemShortName(cs.getShortName())
			.sortBy(SortField.ascending(CodeSystemVersionEntry.Fields.EFFECTIVE_DATE))
			.build(cs.getRepositoryId())
			.execute(getBus())
			.getSync(1, TimeUnit.MINUTES);
		if (versions.isEmpty()) {
			info.append("\t\tNo versions have been created yet.");
		} else {
			info.append(versions
					.stream()
					.map(v -> getCodeSystemVersionInformation(v, cs))
					.collect(Collectors.joining("\n")));
		}
		return info.toString();
	}
	
	private List<String> getRepositoryIds() {
		return RepositoryRequests.prepareSearch()
				.all()
				.buildAsync()
				.execute(getBus())
				.then(repos -> repos.stream().map(RepositoryInfo::id).collect(Collectors.toList()))
				.getSync(1, TimeUnit.MINUTES);
	}
	
	private List<CodeSystem> getCodeSystems() {
		final List<Promise<CodeSystems>> getAllCodeSystems = newArrayList();
		for (String repositoryId : getRepositoryIds()) {
			getAllCodeSystems.add(CodeSystemRequests.prepareSearchCodeSystem().all().build(repositoryId).execute(getBus()));
		}
		return Promise.all(getAllCodeSystems)
				.then(results -> {
					final List<CodeSystem> codeSystems = newArrayList();
					for (CodeSystems result : Iterables.filter(results, CodeSystems.class)) {
						codeSystems.addAll(result.getItems());
					}
					return SHORT_NAME_ORDERING.immutableSortedCopy(codeSystems);
				})
				.getSync(1, TimeUnit.MINUTES);
	}
	
	private CodeSystem getCodeSystemById(String shortNameOrOid) {
		final List<Promise<CodeSystems>> getAllCodeSystems = newArrayList();
		for (String repositoryId : getRepositoryIds()) {
			getAllCodeSystems.add(CodeSystemRequests.prepareSearchCodeSystem()
					.one()
					.filterById(shortNameOrOid)
					.build(repositoryId)
					.execute(getBus()));
		}
		return Promise.all(getAllCodeSystems)
				.then(results -> {
					for (CodeSystems result : Iterables.filter(results, CodeSystems.class)) {
						if (!result.getItems().isEmpty()) {
							return Iterables.getOnlyElement(result.getItems());
						}
					}
					return null;
				})
				.getSync(1, TimeUnit.MINUTES);
	}
	
	//I added the CodeSystem argument so i don't have to search for it in a request. It saves a lot of time
	private String getCodeSystemVersionInformation(CodeSystemVersion codeSystemVersion, CodeSystem codeSystem) {
		return new StringBuilder()
			.append("\t\tVersion id: ").append(codeSystemVersion.getVersion()).append("\n")
			.append("\t\tDescription: ").append(codeSystemVersion.getDescription()).append("\n")
			.append("\t\tEffective date: ").append(EffectiveTimes.format(codeSystemVersion.getEffectiveTime(), DateFormats.DEFAULT)).append("\n")
			.append("\t\tCreation date: ").append(Dates.formatByHostTimeZone(codeSystemVersion.getImportDate(), DateFormats.DEFAULT)).append("\n")
			.append("\t\tLast update: ").append(codeSystemVersion.getLastModificationDate() != null ? StdDateFormat.getDateInstance().format(codeSystemVersion.getLastModificationDate()) : "-").append("\n")
			.append("\t\tVersion branch path: ").append(codeSystemVersion.getPath()).append("\n")
			.append("\t\tExtensions: ").append(getExtensionsForGivenVersionOfCodeSystem(codeSystemVersion, codeSystem))
			.append("\n")
			.toString();
	}
	
	private String getExtensionsForGivenVersionOfCodeSystem(CodeSystemVersion version, CodeSystem extendedCodeSystem) {
		final List<String> extensionsOfThisVersion =
		CodeSystemRequests
		.getAllCodeSystems(getContext())
		.stream()
		.filter(cs -> 
				{
					//Filters the code systems that are the direct extensions of the extendedCodeSystem and the given version
					return cs.getExtensionOf() != null 
							&& 
							cs.getExtensionOf().getCodeSystem().equals(extendedCodeSystem.getCodeSystemURI().getCodeSystem())
							&&
							cs.getExtensionOf().getPath().equals(version.getVersion());
					}
		)
		.map(css -> css.getShortName())
		.collect(Collectors.toList());
		return extensionsOfThisVersion.isEmpty()? "-" : String.join(", ", extensionsOfThisVersion);
	}
	
}
