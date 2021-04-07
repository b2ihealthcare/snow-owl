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

import com.b2international.commons.CompareUtils;
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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;

import picocli.CommandLine;
import picocli.CommandLine.Option;

/**
 * @since 7.0
 */
@Component
@CommandLine.Command(name = "codesystems", header = "Displays information about the available Code Systems", description = {
		"Displays information about the available Code Systems and their versions" })
public final class CodeSystemsCommand extends Command {

	private static final Ordering<CodeSystem> SHORT_NAME_ORDERING = Ordering.natural().onResultOf(CodeSystem::getShortName);

	private static final String CODE_SYSTEM_PROPERTY_DELIMITER = "\n\t";
	private static final String CODE_SYSTEM_SUBPROPERTY_DELIMITER = "\n\t\t";

	@Option(names = { "-c", "--codesystem" }, description = { "A short name of the codeSystem to return" })
	String codeSystem;

	@Option(names = { "-v", "--versions" }, description = {"Display version information along with each returned Code System" })
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

		final ImmutableList.Builder<String> infos = ImmutableList.builder();

		infos.add(codeSystem.getShortName());
		infos.add("Name: ".concat(codeSystem.getName()));
		infos.add("Short name: ".concat(codeSystem.getShortName()));
		infos.add("OID: ".concat(codeSystem.getOid()));
		infos.add("Maintaining organization link: ".concat(codeSystem.getOrganizationLink()));

		if (!CompareUtils.isEmpty(codeSystem.getCitation())) {
			infos.add("Citation: ".concat(codeSystem.getCitation()));
		}

		final String availableUpgradesInfo = getAvailableUpgradesInfo(codeSystem);
		if (availableUpgradesInfo != null) {
			infos.add(availableUpgradesInfo);
		}

		infos.add("Code System URI: ".concat(codeSystem.getCodeSystemURI().toString()));
		infos.add("Icon path: ".concat(codeSystem.getIconPath()));

		if (codeSystem.getUpgradeOf() != null) {
			infos.add("Upgrade of: ".concat(codeSystem.getUpgradeOf().toString()));
		}

		if (codeSystem.getExtensionOf() != null) {
			infos.add("Extension of: ".concat(codeSystem.getExtensionOf().toString()));
		}

		infos.add("Repository: ".concat(codeSystem.getRepositoryId()));
		infos.add("Working branch: ".concat(codeSystem.getBranchPath()));
		infos.add("Terminology ID: ".concat(codeSystem.getTerminologyId()));

		final String additionalPropertiesInfo = getAdditionalPropertiesInfo(codeSystem);
		if (additionalPropertiesInfo != null) {
			infos.add(additionalPropertiesInfo);
		}

		final String localesInfo = getLocalesInfo(codeSystem);
		if (localesInfo != null) {
			infos.add(localesInfo);
		}

		if (showVersions) {
			infos.add(getCodeSystemVersionsInfo(codeSystem));
		}
		return String.join(CODE_SYSTEM_PROPERTY_DELIMITER, infos.build());
	}

	private String getAdditionalPropertiesInfo(CodeSystem codeSystem) {
		if (codeSystem.getAdditionalProperties() == null || codeSystem.getAdditionalProperties().isEmpty()) {
			return null;
		}

		final ImmutableList.Builder<String> result = ImmutableList.builder();

		result.add("Additional Properties: ");
		codeSystem.getAdditionalProperties()
				.forEach((key, value) -> result.add(key.concat(": ").concat(value.toString())));

		return String.join(CODE_SYSTEM_SUBPROPERTY_DELIMITER, result.build());
	}

	private String getLocalesInfo(CodeSystem codeSystem) {
		if (codeSystem.getLocales() == null || codeSystem.getLocales().isEmpty()) {
			return null;
		}
		final ImmutableList.Builder<String> result = ImmutableList.builder();

		result.add("Locales: ");
		codeSystem.getLocales().forEach(l -> result.add(l.toString()));

		return String.join(CODE_SYSTEM_SUBPROPERTY_DELIMITER, result.build());
	}

	private String getAvailableUpgradesInfo(CodeSystem codeSystem) {

		if (codeSystem.getAvailableUpgrades() == null || codeSystem.getAvailableUpgrades().isEmpty()) {
			return null;
		}

		final ImmutableList.Builder<String> result = ImmutableList.builder();

		result.add("Available Upgrades: ");
		codeSystem.getAvailableUpgrades().forEach(update -> result.add(update.toString()));

		return String.join(CODE_SYSTEM_SUBPROPERTY_DELIMITER, result.build());
	}

	private String getCodeSystemVersionsInfo(CodeSystem cs) {
		final ImmutableList.Builder<String> result = ImmutableList.builder();

		result.add("Versions:");
		final CodeSystemVersions versions = CodeSystemRequests.prepareSearchCodeSystemVersion().all()
				.filterByCodeSystemShortName(cs.getShortName())
				.sortBy(SortField.ascending(CodeSystemVersionEntry.Fields.EFFECTIVE_DATE)).build(cs.getRepositoryId())
				.execute(getBus()).getSync(1, TimeUnit.MINUTES);

		if (versions.isEmpty()) {
			result.add("No versions have been created yet.");
		} else {
			result.addAll(versions.stream().map(v -> getCodeSystemVersionInformation(v, cs).concat("\n"))
					.collect(ImmutableList.toImmutableList()));
		}
		return String.join(CODE_SYSTEM_SUBPROPERTY_DELIMITER, result.build());
	}

	private List<String> getRepositoryIds() {
		return RepositoryRequests.prepareSearch().all().buildAsync().execute(getBus())
				.then(repos -> repos.stream().map(RepositoryInfo::id).collect(Collectors.toList()))
				.getSync(1, TimeUnit.MINUTES);
	}

	private List<CodeSystem> getCodeSystems() {
		final List<Promise<CodeSystems>> getAllCodeSystems = newArrayList();
		for (String repositoryId : getRepositoryIds()) {
			getAllCodeSystems
					.add(CodeSystemRequests.prepareSearchCodeSystem().all().build(repositoryId).execute(getBus()));
		}
		return Promise.all(getAllCodeSystems).then(results -> {
			final List<CodeSystem> codeSystems = newArrayList();
			for (CodeSystems result : Iterables.filter(results, CodeSystems.class)) {
				codeSystems.addAll(result.getItems());
			}
			return SHORT_NAME_ORDERING.immutableSortedCopy(codeSystems);
		}).getSync(1, TimeUnit.MINUTES);
	}

	private CodeSystem getCodeSystemById(String shortNameOrOid) {
		final List<Promise<CodeSystems>> getAllCodeSystems = newArrayList();
		for (String repositoryId : getRepositoryIds()) {
			getAllCodeSystems.add(CodeSystemRequests.prepareSearchCodeSystem().one().filterById(shortNameOrOid)
					.build(repositoryId).execute(getBus()));
		}
		return Promise.all(getAllCodeSystems).then(results -> {
			for (CodeSystems result : Iterables.filter(results, CodeSystems.class)) {
				if (!result.getItems().isEmpty()) {
					return Iterables.getOnlyElement(result.getItems());
				}
			}
			return null;
		}).getSync(1, TimeUnit.MINUTES);
	}
	
	private String getCodeSystemVersionInformation(CodeSystemVersion codeSystemVersion, CodeSystem codeSystem) {
		final ImmutableList.Builder<String> result = ImmutableList.builder();

		result.add("Version id: ".concat(codeSystemVersion.getVersion()));
		result.add("Description: ".concat(codeSystemVersion.getDescription()));
		result.add("Effective date: ".concat(EffectiveTimes.format(codeSystemVersion.getEffectiveTime(), DateFormats.DEFAULT)));
		result.add("Creation date: ".concat(Dates.formatByHostTimeZone(codeSystemVersion.getImportDate(), DateFormats.DEFAULT)));
		result.add("Last update: ".concat(codeSystemVersion.getLastModificationDate() != null? 
				StdDateFormat.getDateInstance().format(codeSystemVersion.getLastModificationDate())
				: "-"));
		result.add("Version branch path: ".concat(codeSystemVersion.getPath()));
		final ImmutableList<String> extensionsForGivenVersion = getExtensionsForGivenVersionOfCodeSystem(codeSystemVersion, codeSystem);
		result.add("Extensions: ".concat(CompareUtils.isEmpty(extensionsForGivenVersion)? "-" : String.join(", ", extensionsForGivenVersion)));

		return String.join(CODE_SYSTEM_SUBPROPERTY_DELIMITER, result.build());
	}

	private ImmutableList<String> getExtensionsForGivenVersionOfCodeSystem(CodeSystemVersion version,
			CodeSystem extendedCodeSystem) {
		return CodeSystemRequests.prepareSearchAllCodeSystems().buildAsync().execute(getBus())
				.then(results -> results.stream().filter(cs -> {
					// Filters the code systems that are the direct extensions of the
					// extendedCodeSystem and the given version
					return cs.getExtensionOf() != null
							&& cs.getExtensionOf().getCodeSystem()
									.equals(extendedCodeSystem.getCodeSystemURI().getCodeSystem())
							&& cs.getExtensionOf().getPath().equals(version.getVersion());
				}).map(css -> css.getShortName()).collect(ImmutableList.toImmutableList()))
				.getSync(1, TimeUnit.MINUTES);
	}

}
