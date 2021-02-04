/*
 * Copyright 2020-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import com.b2international.commons.exceptions.ApiError;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.exceptions.ConflictException;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.identity.User;
import com.b2international.snowowl.core.merge.Merge;
import com.b2international.snowowl.core.merge.MergeConflict;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.core.uri.CodeSystemURI;
import com.b2international.snowowl.core.uri.ResourceURIPathResolver;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 7.14.0 
 */
final class CodeSystemUpgradeRequest implements Request<RepositoryContext, String> {

	private static final long serialVersionUID = 1L;

	@JsonProperty
	@NotNull
	private final CodeSystemURI codeSystem;
	
	@JsonProperty
	@NotNull
	private final CodeSystemURI extensionOf;

	@JsonProperty
	private String codeSystemId;
	
	public CodeSystemUpgradeRequest(CodeSystemURI codeSystem, CodeSystemURI extensionOf) {
		this.codeSystem = codeSystem;
		this.extensionOf = extensionOf;
	}
	
	void setCodeSystemId(String codeSystemId) {
		this.codeSystemId = codeSystemId;
	}
	
	@Override
	public String execute(RepositoryContext context) {
		if (!codeSystem.isHead()) {
			throw new BadRequestException("Upgrades can not be started from CodeSystem versions.")
				.withDeveloperMessage("Use '%s' only instead of '%s'", codeSystem.getCodeSystem(), codeSystem);
		}

		// get available upgrades 
		final CodeSystem currentCodeSystem = CodeSystemRequests.prepareGetCodeSystem(codeSystem.getCodeSystem())
				.setExpand(CodeSystem.Expand.AVAILABLE_UPGRADES + "()")
				.build()
				.execute(context);
		
		if (currentCodeSystem.getUpgradeOf() != null) {
			throw new BadRequestException("Upgrade can not be started on an existing Code System Upgrade");
		}
		
		final List<CodeSystemURI> availableUpgrades = currentCodeSystem.getAvailableUpgrades();
		// report bad request if there are no upgrades available
		if (availableUpgrades.isEmpty()) {
			throw new BadRequestException("There are no upgrades available for the CodeSystem '%s'.", codeSystem.getCodeSystem());
		}
		
		// or the selected extensionOf version is not present as a valid available upgrade
		if (!availableUpgrades.contains(extensionOf)) {
			throw new BadRequestException("Upgrades can only be performed to the next available version dependency.")
				.withDeveloperMessage("Use '%s/<VERSION_ID>', where <VERSION_ID> is one of: '%s'", extensionOf.getCodeSystem(), availableUpgrades);
		}
		
		// auto-generate the codeSystemId if not provided
		// auto-generated upgrade IDs consist of the original CodeSystem's name and the new extensionOf dependency's URI
		final String upgradeCodeSystemId;
		if (codeSystemId == null) {
			upgradeCodeSystemId = String.format("%s-%s-UPGRADE", codeSystem.getCodeSystem(), extensionOf.getPath() /*versionId*/); 
		} else if (codeSystemId.isBlank()) {
			throw new BadRequestException("'codeSystemId' property should not be empty, if provided");
		} else {
			upgradeCodeSystemId = codeSystemId;
		}
		
		// create the same branch name under the new extensionOf path
		String parentBranch = context.service(ResourceURIPathResolver.class).resolve(context, List.of(extensionOf)).stream().findFirst().get();
		
		String upgradeBranch = RepositoryRequests.branching().prepareCreate()
			.setParent(parentBranch)
			.setName(codeSystem.getCodeSystem())
			.build()
			.execute(context);
		
		try {
			// merge branch content from the current code system to the new upgradeBranch
			Merge merge = RepositoryRequests.merging().prepareCreate()
				.setSource(currentCodeSystem.getBranchPath())
				.setTarget(upgradeBranch)
				.setSquash(false)
				.build()
				.execute(context);
			if (merge.getStatus() != Merge.Status.COMPLETED) {
				// report conflicts
				ApiError apiError = merge.getApiError();
				Collection<MergeConflict> conflicts = merge.getConflicts();
				context.log().error("Failed to sync source CodeSystem content to upgrade CodeSystem. Error: {}. Conflicts: {}", apiError.getMessage(), conflicts);
				throw new ConflictException("Upgrade can not be performed due to content synchronization errors.")
					.withAdditionalInfo(Map.of(
						"conflicts", conflicts,
						"mergeError", apiError.getMessage()
					));
			}
			
			// and lastly create the actual CodeSystem so users will be able to browse, access and complete the upgrade
			return CodeSystemRequests.prepareNewCodeSystem()
						.setShortName(upgradeCodeSystemId)
						.setBranchPath(upgradeBranch)
						// copy shared properties from the original CodeSystem
						.setAdditionalProperties(currentCodeSystem.getAdditionalProperties())
						.setCitation(currentCodeSystem.getCitation())
						.setExtensionOf(extensionOf)
						.setUpgradeOf(codeSystem)
						.setIconPath(currentCodeSystem.getIconPath())
						.setLocales(currentCodeSystem.getLocales())
						.setLink(currentCodeSystem.getOrganizationLink())
						.setName(String.format("Upgrade of '%s' to '%s'", currentCodeSystem.getName(), extensionOf))
						.setRepositoryId(context.id())
						.setTerminologyId(currentCodeSystem.getTerminologyId())
						.build(context.id(), Branch.MAIN_PATH, context.service(User.class).getUsername(), String.format("Start upgrade of '%s' to '%s'", codeSystem, extensionOf))
						.getRequest()
						.execute(context)
						.getResultAs(String.class);			
		} catch (Throwable e) {
			// delete upgrade branch if any exception have been thrown during the upgrade
			RepositoryRequests.branching().prepareDelete(upgradeBranch).build().execute(context);
			throw e;
		}
	}
	

}
