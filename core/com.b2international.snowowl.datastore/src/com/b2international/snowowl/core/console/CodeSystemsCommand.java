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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.osgi.framework.console.CommandInterpreter;

import com.b2international.commons.extension.Component;
import com.b2international.snowowl.core.RepositoryInfo;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.datastore.CodeSystemEntry;
import com.b2international.snowowl.datastore.CodeSystemVersionEntry;
import com.b2international.snowowl.datastore.CodeSystems;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemRequests;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;

/**
 * @since 7.0
 */
@Component
public final class CodeSystemsCommand extends Command {

	private static final Ordering<CodeSystemEntry> SHORT_NAME_ORDERING = Ordering.natural().onResultOf(CodeSystemEntry::getShortName);
	
	@Override
	public void run(CommandInterpreter interpreter) {
		String codeSystemShortName = interpreter.nextArgument();
		if (Strings.isNullOrEmpty(codeSystemShortName)) {
			interpreter.print(Joiner.on("\n").join(FluentIterable.from(getCodeSystems()).transform(input -> getCodeSystemInformation(input))));
		} else {
			CodeSystemEntry codeSystem = getCodeSystemById(codeSystemShortName);
			
			if (codeSystem == null) {
				interpreter.println(String.format("Unknown or invalid code system with identifier '%s'", codeSystemShortName));
				return;
			}
			
			interpreter.println(getCodeSystemInformation(codeSystem));
		}
	}
	
	@Override
	public String getCommand() {
		return "codesystems [codeSystemShortName]";
	}
	
	@Override
	public String getDescription() {
		return "lists all/single available terminologies/terminology in the system";
	}
	
	private String getCodeSystemInformation(CodeSystemEntry codeSystem) {
		StringBuilder builder = new StringBuilder();
		builder
			.append("Name: ").append(codeSystem.getName()).append("\n")
			.append("Short name: ").append(codeSystem.getShortName()).append("\n")
			.append("Code System OID: ").append(codeSystem.getOid()).append("\n")
			.append("Maintaining organization link: ").append(codeSystem.getOrgLink()).append("\n")
			.append("Language: ").append(codeSystem.getLanguage()).append("\n")
			.append("Current branch path: ").append(codeSystem.getBranchPath()).append("\n");
		return builder.toString();
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
		checkNotNull(shortNameOrOid, "Shortname Or OID parameter may not be null.");
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
		StringBuilder builder = new StringBuilder();
		builder
			.append("Version id: ").append(codeSystemVersion.getVersionId()).append("\n")
			.append("Description: ").append(codeSystemVersion.getDescription()).append("\n")
			.append("Effective date: ").append(EffectiveTimes.format(codeSystemVersion.getEffectiveDate(), DateFormats.DEFAULT)).append("\n")
			.append("Creation date: ").append(EffectiveTimes.format(codeSystemVersion.getImportDate(), DateFormats.DEFAULT)).append("\n")
			.append("Last update: ").append(codeSystemVersion.getLatestUpdateDate() > 0 ? EffectiveTimes.format(codeSystemVersion.getLatestUpdateDate(), DateFormats.DEFAULT) : "-").append("\n")
			.append("Version branch path: ").append(codeSystemVersion.getPath()).append("\n")
			.append("Parent branch path: ").append(codeSystemVersion.getParentBranchPath()).append("\n")
			.append("Repository id: ").append(codeSystemVersion.getRepositoryUuid()).append("\n");
		return builder.toString();
	}
	
}
