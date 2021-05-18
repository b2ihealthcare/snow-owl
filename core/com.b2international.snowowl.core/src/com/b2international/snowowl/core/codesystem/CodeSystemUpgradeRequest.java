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
import com.b2international.index.revision.RevisionBranch.BranchNameValidator;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.authorization.RepositoryAccessControl;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.merge.Merge;
import com.b2international.snowowl.core.merge.MergeConflict;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.core.uri.DefaultResourceURIPathResolver;
import com.b2international.snowowl.core.uri.ResourceURIPathResolver;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 7.14.0 
 */
final class CodeSystemUpgradeRequest implements Request<RepositoryContext, String>, RepositoryAccessControl {

	private static final long serialVersionUID = 1L;

	@JsonProperty
	@NotNull
	private final ResourceURI resource;
	
	@JsonProperty
	@NotNull
	private final ResourceURI extensionOf;

	@JsonProperty
	private String resourceId;
	
	public CodeSystemUpgradeRequest(ResourceURI resource, ResourceURI extensionOf) {
		this.resource = resource;
		this.extensionOf = extensionOf;
	}
	
	void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	
	@Override
	public String execute(RepositoryContext context) {

		// get available upgrades 
		final CodeSystem currentCodeSystem = CodeSystemRequests.prepareGetCodeSystem(resource.getResourceId())
				.setExpand(CodeSystem.Expand.AVAILABLE_UPGRADES + "()")
				.build()
				.execute(context);
		
		if (currentCodeSystem.getUpgradeOf() != null) {
			throw new BadRequestException("Upgrade can not be started on an existing upgrade resource");
		}
		
		final List<ResourceURI> availableUpgrades = currentCodeSystem.getAvailableUpgrades();
		// report bad request if there are no upgrades available
		if (availableUpgrades.isEmpty()) {
			throw new BadRequestException("There are no upgrades available for resource '%s'.", resource.getResourceId());
		}
		
		// or the selected extensionOf version is not present as a valid available upgrade
		if (!availableUpgrades.contains(extensionOf)) {
			throw new BadRequestException("Upgrades can only be performed to the next available version dependency.")
				.withDeveloperMessage("Use '%s/<VERSION_ID>', where <VERSION_ID> is one of: '%s'", extensionOf.getResourceId(), availableUpgrades);
		}
		
		// auto-generate the resourceId if not provided
		// auto-generated upgrade IDs consist of the original Resource's ID and the new extensionOf dependency's path, which is version at this point
		final String upgradeResourceId;
		if (resourceId == null) {
			upgradeResourceId = String.format("%s-%s-UPGRADE", resource.getResourceId(), extensionOf.getPath() /*versionId*/); 
		} else if (resourceId.isBlank()) {
			throw new BadRequestException("'resourceId' property should not be empty, if provided");
		} else {
			BranchNameValidator.DEFAULT.checkName(resourceId);
			upgradeResourceId = resourceId;
		}
		
		String mergeContentFromBranchPath = currentCodeSystem.getBranchPath();
		
		// only allow HEAD or valid code system versions
		if (!resource.isHead()) {
			mergeContentFromBranchPath = ((DefaultResourceURIPathResolver) context.service(ResourceURIPathResolver.class)).resolveBranches(false).resolve(context, List.of(resource)).stream().findFirst().get();
		}
		
		// create the same branch name under the new extensionOf path
		String parentBranch = context.service(ResourceURIPathResolver.class).resolve(context, List.of(extensionOf)).stream().findFirst().get();
		
		// merge content in the tooling repository from the current resource's to the upgrade resource's branch
		final String upgradeBranch = RepositoryRequests.branching().prepareCreate()
				.setParent(parentBranch)
				.setName(resource.getResourceId())
				.build(currentCodeSystem.getToolingId())
				.getRequest()
				.execute(context);
		
		try {
			// merge branch content from the current code system to the new upgradeBranch
			Merge merge = RepositoryRequests.merging().prepareCreate()
				.setSource(mergeContentFromBranchPath)
				.setTarget(upgradeBranch)
				.setSquash(false)
				.build(currentCodeSystem.getToolingId())
				.getRequest()
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
				.setId(upgradeResourceId)
				.setBranchPath(upgradeBranch)
				.setTitle(String.format("Upgrade of '%s' to '%s'", currentCodeSystem.getTitle(), extensionOf))
				// copy shared properties from the original CodeSystem
				.setUrl(currentCodeSystem.getUrl())
				.setLanguage(currentCodeSystem.getLanguage())
				.setDescription(currentCodeSystem.getDescription())
				.setStatus("draft")
				.setCopyright(currentCodeSystem.getCopyright())
				.setOwner(currentCodeSystem.getOwner())
				.setContact(currentCodeSystem.getContact())
				.setUsage(currentCodeSystem.getUsage())
				.setPurpose(currentCodeSystem.getPurpose())
				.setToolingId(currentCodeSystem.getToolingId())
				.setExtensionOf(extensionOf)
				.setUpgradeOf(resource)
				.setSettings(currentCodeSystem.getSettings())
				.commit()
				.setCommitComment(String.format("Start upgrade of '%s' to '%s'", resource, extensionOf))
				.build()
				.execute(context.openBranch(context, Branch.MAIN_PATH))
				.getResultAs(String.class);
		} catch (Throwable e) {
			// delete upgrade branch if any exception have been thrown during the upgrade
			RepositoryRequests.branching().prepareDelete(upgradeBranch)
				.build(currentCodeSystem.getToolingId())
				.getRequest()
				.execute(context);
			throw e;
		}
	}

	@Override
	public String getOperation() {
		return Permission.OPERATION_EDIT;
	}

}
