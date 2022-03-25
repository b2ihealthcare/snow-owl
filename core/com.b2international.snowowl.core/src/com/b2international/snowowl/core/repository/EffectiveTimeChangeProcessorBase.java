/*
 * Copyright 2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.repository;

import java.util.List;
import java.util.Optional;

import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.codesystem.CodeSystemVersion;
import com.b2international.snowowl.core.codesystem.CodeSystemVersionEntry;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.core.uri.CodeSystemURI;
import com.b2international.snowowl.core.uri.ResourceURIPathResolver;
import com.google.common.collect.Lists;

/**
 * @since 7.19.0
 */
public abstract class EffectiveTimeChangeProcessorBase extends ChangeSetProcessorBase {

	protected EffectiveTimeChangeProcessorBase(String description) {
		super(description);
	}

	protected List<String> getAvailableVersionPaths(RepositoryContext context, String branchPath) {
		final List<CodeSystemURI> codeSystemsToCheck = Lists.newArrayList();
		
		CodeSystem relativeCodeSystem = context.service(RepositoryCodeSystemProvider.class).get(branchPath);
		
		// based on the relative CodeSystem, we might need to check up to two CodeSystems
		// in case of upgrade, we need to check the original CodeSystem branch 
		// in case of regular extension or no-extension CodeSystem, we need to check the extensionOf
		
		// always check the direct extensionOf (aka parent) CodeSystem
		if (relativeCodeSystem.getExtensionOf() != null) {
			if (relativeCodeSystem.getExtensionOf().isHead()) {
				// in case of regular CodeSystem check the latest available version if available, if not, then skip
				getLatestCodeSystemVersion(context, relativeCodeSystem.getExtensionOf().getCodeSystem()).ifPresent(latestVersion -> {
					codeSystemsToCheck.add(CodeSystemURI.latest(relativeCodeSystem.getExtensionOf().getCodeSystem()));
				});
			} else {
				codeSystemsToCheck.add(relativeCodeSystem.getExtensionOf());
			}
		}
		
		// in case of an upgrade CodeSystem check the original CodeSystem as well
		if (relativeCodeSystem.getUpgradeOf() != null) {
			// TODO, it would be great to know that sync point between the Upgrade and the UpdradeOf and use that timestamp as reference, for now, fall back to the HEAD 
			codeSystemsToCheck.add(relativeCodeSystem.getUpgradeOf());
		} else {
			// in case of regular CodeSystem check the latest available version if available, if not, then skip
			getLatestCodeSystemVersion(context, relativeCodeSystem.getCodeSystemURI().getCodeSystem()).ifPresent(latestVersion -> {
				codeSystemsToCheck.add(latestVersion.getUri());
			});
		}
		
		return context.service(ResourceURIPathResolver.class).resolve(context, codeSystemsToCheck);
	}

	private Optional<CodeSystemVersion> getLatestCodeSystemVersion(RepositoryContext context, String codeSystemId) {
		return CodeSystemRequests.prepareSearchCodeSystemVersion()
				.one()
				.filterByCodeSystemShortName(codeSystemId)
				.sortBy(SearchResourceRequest.SortField.descending(CodeSystemVersionEntry.Fields.EFFECTIVE_DATE))
				.build()
				.execute(context)
				.stream()
				.findFirst();
	}

	
}