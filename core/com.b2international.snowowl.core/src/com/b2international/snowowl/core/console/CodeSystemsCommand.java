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

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.plugin.Component;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.core.request.SearchResourceRequest.SortField;
import com.b2international.snowowl.core.version.Version;
import com.b2international.snowowl.core.version.VersionDocument;
import com.b2international.snowowl.core.version.Versions;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import picocli.CommandLine;
import picocli.CommandLine.Option;

/**
 * @since 7.0
 */
@Component
@CommandLine.Command(name = "codesystems", header = "Displays information about the available Code Systems", description = {
		"Displays information about the available Code Systems and their versions" })
public final class CodeSystemsCommand extends Command {

	private static final String CODE_SYSTEM_PROPERTY_DELIMITER = "\n\t";
	private static final String CODE_SYSTEM_SUBPROPERTY_DELIMITER = "\n\t\t";

	@Option(names = { "-c", "--codesystem" }, description = { "A short name of the codeSystem to return" })
	String codeSystem;

	@Option(names = { "-v", "--versions" }, description = {"Display version information along with each returned Code System" })
	boolean showVersions;

	@Override
	public void run(CommandLineStream out) {
		if (Strings.isNullOrEmpty(codeSystem)) {
			out.println(Joiner.on("\n\n").join(getCodeSystems().map(this::getCodeSystemInfo).iterator()));
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

		infos.add(codeSystem.getId());
		infos.add("Title: ".concat(codeSystem.getTitle()));
		infos.add("OID: ".concat(codeSystem.getOid()));
		infos.add("Owner: ".concat(codeSystem.getOwner()));

		if (!CompareUtils.isEmpty(codeSystem.getDescription())) {
			infos.add("Description: ".concat(codeSystem.getDescription()));
		}

		final String availableUpgradesInfo = getAvailableUpgradesInfo(codeSystem);
		if (availableUpgradesInfo != null) {
			infos.add(availableUpgradesInfo);
		}

		if (codeSystem.getUpgradeOf() != null) {
			infos.add("Upgrade Of: ".concat(codeSystem.getUpgradeOf().toString()));
		}

		if (codeSystem.getExtensionOf() != null) {
			infos.add("Extension of: ".concat(codeSystem.getExtensionOf().toString()));
		}

		infos.add("Working branch: ".concat(codeSystem.getBranchPath()));
		infos.add("Tooling: ".concat(codeSystem.getToolingId()));

		final String additionalPropertiesInfo = getAdditionalPropertiesInfo(codeSystem);
		if (additionalPropertiesInfo != null) {
			infos.add(additionalPropertiesInfo);
		}

		if (showVersions) {
			infos.add(getVersionsInfo(codeSystem));
		}
		return String.join(CODE_SYSTEM_PROPERTY_DELIMITER, infos.build());
	}

	private String getAdditionalPropertiesInfo(CodeSystem codeSystem) {
		if (codeSystem.getSettings() == null || codeSystem.getSettings().isEmpty()) {
			return null;
		}

		final ImmutableList.Builder<String> result = ImmutableList.builder();

		result.add("Settings: ");
		codeSystem.getSettings()
				.forEach((key, value) -> result.add(key.concat(": ").concat(value.toString())));

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

	private String getVersionsInfo(CodeSystem cs) {
		final ImmutableList.Builder<String> result = ImmutableList.builder();

		result.add("Versions:");
		final Versions versions = ResourceRequests.prepareSearchVersion()
				.all()
				.filterByResource(cs.getResourceURI())
				.sortBy(SortField.ascending(VersionDocument.Fields.EFFECTIVE_TIME))
				.buildAsync()
				.execute(getBus())
				.getSync(1, TimeUnit.MINUTES);

		if (versions.isEmpty()) {
			result.add("No versions have been created yet.");
		} else {
			result.addAll(versions.stream().map(v -> getVersionInformation(v, cs).concat("\n"))
					.collect(ImmutableList.toImmutableList()));
		}
		return String.join(CODE_SYSTEM_SUBPROPERTY_DELIMITER, result.build());
	}

	private Stream<CodeSystem> getCodeSystems() {
		return CodeSystemRequests.prepareSearchCodeSystem()
				.all()
				// default sorting is by ID
				.buildAsync()
				.execute(getBus())
				.getSync(1, TimeUnit.MINUTES)
				.stream();
	}

	private CodeSystem getCodeSystemById(String shortNameOrOid) {
		return CodeSystemRequests.prepareGetCodeSystem(shortNameOrOid)
				.buildAsync()
				.execute(getBus())
				.getSync(1, TimeUnit.MINUTES);
	}
	
	private String getVersionInformation(Version codeSystemVersion, CodeSystem codeSystem) {
		final ImmutableList.Builder<String> result = ImmutableList.builder();
		result.add("Version: ".concat(codeSystemVersion.getVersion()));
		result.add("Description: ".concat(codeSystemVersion.getDescription()));
		result.add("Effective Time: ".concat(codeSystemVersion.getEffectiveTime().toString()));
		result.add("Branch: ".concat(codeSystemVersion.getBranchPath()));
		return String.join(CODE_SYSTEM_SUBPROPERTY_DELIMITER, result.build());
	}

}
