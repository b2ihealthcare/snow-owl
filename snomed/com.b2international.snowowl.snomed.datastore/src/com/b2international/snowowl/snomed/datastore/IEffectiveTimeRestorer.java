/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.CodeSystemEntry;
import com.b2international.snowowl.datastore.CodeSystemVersionEntry;
import com.b2international.snowowl.datastore.CodeSystems;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemRequests;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.primitives.Longs;

/**
 * @since 6.14
 */
public interface IEffectiveTimeRestorer<T> {

	void tryRestoreEffectiveTime(String branchPath, T componentToRestore);
	
	default List<String> getAvailableVersionPaths(String branchPath) {

			final IEventBus eventBus = ApplicationContext.getServiceForClass(IEventBus.class);
			final CodeSystems codeSystems = CodeSystemRequests.prepareSearchCodeSystem()
					.all()
					.build(SnomedDatastoreActivator.REPOSITORY_UUID)
					.execute(eventBus)
					.getSync();
			
			final Map<String, CodeSystemEntry> codeSystemsByMainBranch = Maps.uniqueIndex(codeSystems, CodeSystemEntry::getBranchPath);

			final List<CodeSystemEntry> relativeCodeSystems = Lists.newArrayList();

			final Iterator<IBranchPath> bottomToTop = BranchPathUtils.bottomToTopIterator(BranchPathUtils.createPath(branchPath));

			while (bottomToTop.hasNext()) {
				final IBranchPath candidate = bottomToTop.next();
				if (codeSystemsByMainBranch.containsKey(candidate.getPath())) {
					relativeCodeSystems.add(codeSystemsByMainBranch.get(candidate.getPath()));
				}
			}
			if (relativeCodeSystems.isEmpty()) {
				throw new IllegalStateException("No relative code system has been found for branch '" + branchPath + "'");
			}

			// the first code system in the list is the working codesystem
			final CodeSystemEntry workingCodeSystem = relativeCodeSystems.stream().findFirst().get();

			final Optional<CodeSystemVersionEntry> workingCodeSystemVersion = CodeSystemRequests.prepareSearchCodeSystemVersion()
				.one()
				.filterByCodeSystemShortName(workingCodeSystem.getShortName())
				.sortBy(SearchResourceRequest.SortField.descending(CodeSystemVersionEntry.Fields.EFFECTIVE_DATE))
				.build(SnomedDatastoreActivator.REPOSITORY_UUID)
				.execute(eventBus)
				.getSync()
				.first();

			final List<CodeSystemVersionEntry> relativeCodeSystemVersions = Lists.newArrayList();

			if (workingCodeSystemVersion.isPresent() && !Strings.isNullOrEmpty(workingCodeSystemVersion.get().getPath())) {
				relativeCodeSystemVersions.add(workingCodeSystemVersion.get());
			}

			if (relativeCodeSystems.size() > 1) {

				relativeCodeSystems.stream()
					.skip(1)
					.forEach( codeSystem -> {

						final Map<String, CodeSystemVersionEntry> pathToVersionMap = CodeSystemRequests.prepareSearchCodeSystemVersion()
							.all()
							.filterByCodeSystemShortName(codeSystem.getShortName())
							.build(SnomedDatastoreActivator.REPOSITORY_UUID)
							.execute(eventBus)
							.getSync()
							.stream()
							.collect(Collectors.toMap(version -> version.getPath(), v -> v));

						final Iterator<IBranchPath> branchPathIterator = BranchPathUtils.bottomToTopIterator(BranchPathUtils.createPath(branchPath));

						while (branchPathIterator.hasNext()) {
							final IBranchPath candidate = branchPathIterator.next();
							if (pathToVersionMap.containsKey(candidate.getPath())) {
								relativeCodeSystemVersions.add(pathToVersionMap.get(candidate.getPath()));
								break;
							}
						}

					});

			}

			return relativeCodeSystemVersions.stream()
						// sort versions by effective date in reversed order 
						.sorted( (v1, v2) -> Longs.compare(v2.getEffectiveDate(), v1.getEffectiveDate()))
						.map(CodeSystemVersionEntry::getPath)
						.collect(Collectors.toList());
	}
}
	
