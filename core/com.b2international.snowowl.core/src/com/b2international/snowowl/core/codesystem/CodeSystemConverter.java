/*
 * Copyright 2020-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.index.revision.BaseRevisionBranching;
import com.b2international.index.revision.RevisionBranch;
import com.b2international.index.revision.RevisionBranch.BranchState;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.branch.BranchInfo;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.core.request.resource.BaseMetadataResourceConverter;
import com.b2international.snowowl.core.uri.ResourceURIPathResolver;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @since 7.6
 */
final class CodeSystemConverter extends BaseMetadataResourceConverter<CodeSystem, CodeSystems> {

	public CodeSystemConverter(RepositoryContext context, Options expand, List<ExtendedLocale> locales) {
		super(context, expand, locales);
	}
	
	@Override
	protected CodeSystems createCollectionResource(List<CodeSystem> results, String searchAfter, int limit, int total) {
		return new CodeSystems(results, searchAfter, limit, total);
	}

	@Override
	public void expand(List<CodeSystem> results) {
		super.expand(results);
		expandExtensionOfBranchState(results);
		expandUpgradeOfInfo(results);
	}
	
	private void expandExtensionOfBranchState(List<CodeSystem> results) {
		if (!expand().containsKey(CodeSystem.Expand.EXTENSION_OF_BRANCH_INFO)) {
			return;
		}
		
		// extensionOf branches are the parent branches of the CodeSystem, so simple branch state calculation is enough
		final RepositoryManager repositoryManager = context().service(RepositoryManager.class);
		final Set<String> toolingIds = results.stream()
			.map(CodeSystem::getToolingId)
			.collect(Collectors.toSet());
		
		final Map<String, BaseRevisionBranching> branchingMap = toolingIds.stream()
			.collect(ImmutableMap.toImmutableMap(
				id -> id, 
				id -> repositoryManager.get(id).service(BaseRevisionBranching.class)));
				
		for (CodeSystem result : results) {
			BaseRevisionBranching branching = branchingMap.get(result.getToolingId());
			RevisionBranch branch = branching.getBranch(result.getBranchPath());
			BranchState branchState = branching.getBranchState(branch);
			result.setExtensionOfBranchInfo(new BranchInfo(branch.getPath(), branchState, branch.getBaseTimestamp(), branch.getHeadTimestamp()));
		}
	}
	
	private void expandUpgradeOfInfo(List<CodeSystem> results) {
		if (!expand().containsKey(CodeSystem.Expand.UPGRADE_INFO)) {
			return;
		}
		
		final List<ResourceURI> upgradeOfURIs = results.stream()
				.filter(codeSystem -> codeSystem.getUpgradeOf() != null)
				.map(codeSystem -> codeSystem.getUpgradeOf().withoutPath())
				.collect(Collectors.toList());
		
		// nothing to expand, quit early
		if (upgradeOfURIs.isEmpty()) {
			return;
		}
		
		final List<String> upgradeOfBranches = context().service(ResourceURIPathResolver.class).resolve(context(), upgradeOfURIs);
		
		final Map<ResourceURI, String> branchesByUpgradeOf = Maps.newHashMap();
		Iterator<ResourceURI> uriIterator = upgradeOfURIs.iterator();
		Iterator<String> branchIterator = upgradeOfBranches.iterator();
		while (uriIterator.hasNext() && branchIterator.hasNext()) {
			ResourceURI uri = uriIterator.next();
			String branch = branchIterator.next();
			branchesByUpgradeOf.put(uri, branch);
		}

		for (CodeSystem result : results) {
			BaseRevisionBranching branching = context().service(RepositoryManager.class).get(result.getToolingId()).service(BaseRevisionBranching.class);
			String upgradeOfCodeSystemBranchPath = branchesByUpgradeOf.get(result.getUpgradeOf().withoutPath());
			
			if (!Strings.isNullOrEmpty(upgradeOfCodeSystemBranchPath)) {
				
				RevisionBranch branch = branching.getBranch(result.getBranchPath());
				BranchState branchState = branching.getBranchState(result.getBranchPath(), upgradeOfCodeSystemBranchPath);
				BranchInfo mainInfo = new BranchInfo(branch.getPath(), branchState, branch.getBaseTimestamp(), branch.getHeadTimestamp());
				
				List<ResourceURI> availableVersions = Lists.newArrayList();
				List<BranchInfo> versionBranchInfo = Lists.newArrayList();

				if (!result.getUpgradeOf().isHead()) {
					long startTimestamp;
					final String upgradeOfVersionBranch = context().service(ResourceURIPathResolver.class).resolve(context(), List.of(result.getUpgradeOf())).stream().findFirst().orElse("");

					if (!Strings.isNullOrEmpty(upgradeOfVersionBranch)) {
						startTimestamp = branching.getBranch(upgradeOfVersionBranch).getBaseTimestamp() + 1;
					} else {
						startTimestamp = Long.MIN_VALUE;
					}

					ResourceRequests.prepareSearchVersion()
							.all()
							.filterByResource(result.getUpgradeOf().withoutPath())
							.filterByResourceBranchPath(upgradeOfCodeSystemBranchPath)
							.build()
							.execute(context())
							.stream()
							.filter(csv -> !csv.getVersionResourceURI().isHead())
							.forEach(csv -> {
								RevisionBranch versionBranch = branching.getBranch(csv.getBranchPath());
								if (versionBranch.getBaseTimestamp() > startTimestamp) {
									BranchState versionBranchState = branching.getBranchState(result.getBranchPath(), versionBranch.getPath());
									if (versionBranchState == BranchState.BEHIND || versionBranchState == BranchState.DIVERGED) {
										availableVersions.add(csv.getVersionResourceURI());
									}
									versionBranchInfo.add(new BranchInfo(branch.getPath(), versionBranchState, versionBranch.getBaseTimestamp(), versionBranch.getHeadTimestamp()));
								}
							});
				}
				
				result.setUpgradeInfo(new UpgradeInfo(mainInfo, versionBranchInfo, availableVersions));
			}
		}
	}

	private boolean hasUpgrade(CodeSystem result, List<CodeSystem> results) {
		// first check the results
		return results
			.stream()
			.filter(cs -> result.getResourceURI().equals(cs.getUpgradeOf()))
			.findFirst()
			.or(() -> {
				// then the index
				return CodeSystemRequests.prepareSearchCodeSystem()
					.one()
					.filterByUpgradeOf(result.getResourceURI())
					.build()
					.execute(context())
					.first();
			})
			.or(() -> {
				// then the code system versions
				final List<ResourceURI> codeSystemVersions = ResourceRequests.prepareSearchVersion()
						.all()
						.filterByResource(result.getResourceURI())
						.build()
						.execute(context())
						.stream()
						.map(cs -> cs.getVersionResourceURI())
						.collect(Collectors.toList());
				
				return CodeSystemRequests.prepareSearchCodeSystem()
						.filterByUpgradeOf(codeSystemVersions)
						.build()
						.execute(context())
						.first();
			})
			.isPresent();
	}

}
