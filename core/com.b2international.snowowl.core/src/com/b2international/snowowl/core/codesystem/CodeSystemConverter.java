/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.codesystem;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Comparator;
import java.util.List;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.request.BaseResourceConverter;
import com.b2international.snowowl.core.uri.CodeSystemURI;
import com.google.common.collect.Multimaps;
import com.google.common.collect.TreeMultimap;

public final class CodeSystemConverter extends BaseResourceConverter<CodeSystemEntry, CodeSystem, CodeSystems> {

	public CodeSystemConverter(RepositoryContext context, Options expand, List<ExtendedLocale> locales) {
		super(context, expand, locales);
	}

	@Override
	protected CodeSystems createCollectionResource(List<CodeSystem> results, String searchAfter, int limit, int total) {
		return new CodeSystems(results, searchAfter, limit, total);
	}

	@Override
	protected CodeSystem toResource(CodeSystemEntry entry) {
		return CodeSystem.builder(entry).build();
	}
	
	@Override
	protected void expand(List<CodeSystem> results) {
		if (expand().isEmpty()) {
			return;
		}
		
		expandAvailableUpgrades(results);
	}

	private void expandAvailableUpgrades(List<CodeSystem> results) {
		if (!expand().containsKey(CodeSystem.Expand.AVAILABLE_UPGRADES)) {
			return;
		}
		
		final Set<String> parentCodeSystems = results.stream()
			.map(CodeSystem::getExtensionOf)
			.filter(uri -> uri != null)
			.map(CodeSystemURI::getCodeSystem)
			.collect(Collectors.toSet());
		
		final CodeSystemVersions parentVersions = CodeSystemRequests.prepareSearchCodeSystemVersion()
			.all()
			.filterByCodeSystemShortNames(parentCodeSystems)
			.build()
			.execute(context());
		
		final TreeMultimap<String, CodeSystemVersionEntry> versionsByShortName = TreeMultimap.create(
				Comparator.naturalOrder(), 
				Comparator.comparing(CodeSystemVersionEntry::getEffectiveDate));
		
		versionsByShortName.putAll(Multimaps.index(parentVersions, CodeSystemVersionEntry::getCodeSystemShortName));
		
		for (final CodeSystem result : results) {
			final CodeSystemURI extensionOf = result.getExtensionOf();
			
			if (extensionOf == null) {
				continue;
			}
			
			final String shortName = extensionOf.getCodeSystem();
			final NavigableSet<CodeSystemVersionEntry> candidates = versionsByShortName.get(shortName);
			final String versionId = extensionOf.getPath();
			
			final Optional<CodeSystemVersionEntry> currentExtensionVersion = candidates.stream()
					.filter(v -> v.getVersionId().equals(versionId))
					.findFirst();
			
			final Optional<List<CodeSystemURI>> upgradeUris = currentExtensionVersion.map(currentVersion -> {
				final SortedSet<CodeSystemVersionEntry> upgradeVersions = candidates.tailSet(currentVersion, false);
				return upgradeVersions.stream()
						.map(upgradeVersion -> createCodeSystemUri(upgradeVersion))
						.collect(Collectors.toList());
			});
	
			result.setAvailableUpgrades(upgradeUris.orElseGet(() -> newArrayList()));
		}			
	}

	private CodeSystemURI createCodeSystemUri(CodeSystemVersionEntry version) {
		return new CodeSystemURI(String.format("%s/%s", version.getCodeSystemShortName(), version.getVersionId()));
	}
}
