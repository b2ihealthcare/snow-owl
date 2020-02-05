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
package com.b2international.snowowl.core.console;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;
import java.util.stream.Collectors;

import com.b2international.commons.extension.Component;
import com.b2international.snowowl.core.RepositoryInfo;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.request.SearchResourceRequest.SortField;
import com.b2international.snowowl.datastore.CodeSystemEntry;
import com.b2international.snowowl.datastore.CodeSystemVersionEntry;
import com.b2international.snowowl.datastore.CodeSystemVersions;
import com.b2international.snowowl.datastore.CodeSystems;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemRequests;
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

	private static final Ordering<CodeSystemEntry> SHORT_NAME_ORDERING = Ordering.natural().onResultOf(CodeSystemEntry::getShortName);

	@Option(names = { "-c", "--codesystem" }, description = { "A short name of the codeSystem to return" })
	String codeSystem;
	
	@Option(names = { "-v", "--versions" }, description = { "Display version information along with each returned Code System" })
	boolean showVersions;
	
	@Override
	public void run(CommandLineStream out) {
		if (Strings.isNullOrEmpty(codeSystem)) {
			out.println(Joiner.on("\n").join(FluentIterable.from(getCodeSystems()).transform(input -> getCodeSystemInfo(input))));
		} else {
			CodeSystemEntry cs = getCodeSystemById(codeSystem);
			
			if (cs == null) {
				out.println(String.format("Unknown or invalid code system with identifier '%s'", codeSystem));
				return;
			}
			
			out.println(getCodeSystemInfo(cs));
		}
	}

	private String getCodeSystemInfo(CodeSystemEntry codeSystem) {
		return new StringBuilder()
			.append("Name: ").append(codeSystem.getName()).append("\n")
			.append("Short name: ").append(codeSystem.getShortName()).append("\n")
			.append("OID: ").append(codeSystem.getOid()).append("\n")
			.append("Maintaining organization link: ").append(codeSystem.getOrgLink()).append("\n")
			.append("Language: ").append(codeSystem.getLanguage()).append("\n")
			.append("Working branch: ").append(codeSystem.getBranchPath())
			.append(showVersions ? getCodeSystemVersionsInfo(codeSystem) : "")
			.toString();
	}
	
	private String getCodeSystemVersionsInfo(CodeSystemEntry cs) {
		final StringBuilder info = new StringBuilder("\nVersions:\n");
		final CodeSystemVersions versions = CodeSystemRequests
			.prepareSearchCodeSystemVersion()
			.all()
			.filterByCodeSystemShortName(cs.getShortName())
			.sortBy(SortField.ascending(CodeSystemVersionEntry.Fields.EFFECTIVE_DATE))
			.build(cs.getRepositoryUuid())
			.execute(getBus())
			.getSync();
		if (versions.isEmpty()) {
			info.append("\tNo versions have been created yet.");
		} else {
			info.append(versions
					.stream()
					.map(this::getCodeSystemVersionInformation)
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
				.getSync();
	}
	
	private List<CodeSystemEntry> getCodeSystems() {
		final List<Promise<CodeSystems>> getAllCodeSystems = newArrayList();
		for (String repositoryId : getRepositoryIds()) {
			getAllCodeSystems.add(CodeSystemRequests.prepareSearchCodeSystem().all().build(repositoryId).execute(getBus()));
		}
		return Promise.all(getAllCodeSystems)
				.then(results -> {
					final List<CodeSystemEntry> codeSystems = newArrayList();
					for (CodeSystems result : Iterables.filter(results, CodeSystems.class)) {
						codeSystems.addAll(result.getItems());
					}
					return SHORT_NAME_ORDERING.immutableSortedCopy(codeSystems);
				})
				.getSync();
	}
	
	private CodeSystemEntry getCodeSystemById(String shortNameOrOid) {
		final List<Promise<CodeSystems>> getAllCodeSystems = newArrayList();
		for (String repositoryId : getRepositoryIds()) {
			getAllCodeSystems.add(CodeSystemRequests.prepareSearchCodeSystem()
					.all()
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
				.getSync();
	}
	
	private String getCodeSystemVersionInformation(CodeSystemVersionEntry codeSystemVersion) {
		return new StringBuilder()
			.append("\tVersion id: ").append(codeSystemVersion.getVersionId()).append("\n")
			.append("\tDescription: ").append(codeSystemVersion.getDescription()).append("\n")
			.append("\tEffective date: ").append(EffectiveTimes.format(codeSystemVersion.getEffectiveDate(), DateFormats.DEFAULT)).append("\n")
			.append("\tCreation date: ").append(EffectiveTimes.format(codeSystemVersion.getImportDate(), DateFormats.DEFAULT)).append("\n")
			.append("\tLast update: ").append(codeSystemVersion.getLatestUpdateDate() > 0 ? EffectiveTimes.format(codeSystemVersion.getLatestUpdateDate(), DateFormats.DEFAULT) : "-").append("\n")
			.append("\tVersion branch path: ").append(codeSystemVersion.getPath())
			.toString();
	}
	
}
