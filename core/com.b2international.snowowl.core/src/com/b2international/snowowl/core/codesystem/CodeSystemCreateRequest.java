/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Map;
import java.util.Optional;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.commons.StringUtils;
import com.b2international.commons.exceptions.AlreadyExistsException;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.branch.Branches;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.internal.ResourceDocument.Builder;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.core.request.BaseResourceCreateRequest;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.core.version.Version;
import com.google.common.base.Strings;

/**
 * @since 4.7
 */
final class CodeSystemCreateRequest extends BaseResourceCreateRequest {

	private static final long serialVersionUID = 2L;

	// specialized resource fields
	// optional OID, but if defined it must be unique
	String oid;
	
	String branchPath;
	
	@NotEmpty
	String toolingId;
	
	ResourceURI extensionOf;
	ResourceURI upgradeOf;
	Map<String, Object> settings;
	
	private transient String parentPath;
	
	CodeSystemCreateRequest() {}

	@Override
	protected Builder setSpecializedFields(Builder builder) {
		return builder.resourceType(CodeSystem.CODESYSTEM_RESOURCE_TYPE)
				.oid(oid)
				.branchPath(branchPath)
				.toolingId(toolingId)
				.extensionOf(extensionOf)
				.upgradeOf(upgradeOf)
				.settings(settings);
	}
	
	@Override
	protected void executeAdditionalLogic(final TransactionContext context) {
		// Create branch if null or empty path was specified in the request
		final boolean createBranch = StringUtils.isEmpty(branchPath);
		
		final Optional<Version> extensionOfVersion = checkCodeSystem(context, createBranch);
		
		// Set the parent path if a branch needs to be created
		if (createBranch) {
			parentPath = extensionOfVersion
				.map(Version::getBranchPath)
				.orElse(Branch.MAIN_PATH); // TODO totally separate branching system?? MAIN could be removed
		}

		checkBranchPath(context, createBranch);
		checkSettings();
		
		// Set branchPath to the path of the created branch 
		if (createBranch) {
			branchPath = RepositoryRequests.branching()
				.prepareCreate()
				.setParent(parentPath)
				.setName(getId())
				.build(toolingId)
				.getRequest()
				.execute(context);
		}
	}

	private void checkBranchPath(final RepositoryContext context, final boolean create) {
		// If no branch is created, the branch should already exist
		if (!create && !branchExists(branchPath, context)) {
			throw new BadRequestException("Branch path '%s' should point to an existing branch if given.", branchPath);
		}
		
		// If the branch should be created, it branch should not exist, however 
		if (create) {
			final String newBranchPath = Branch.get(parentPath, getTitle());
			if (branchExists(newBranchPath, context)) {
				throw new AlreadyExistsException("Code system branch", newBranchPath);
			}
		}
	}

	private Optional<Version> checkCodeSystem(final RepositoryContext context, final boolean create) {
		// codeSystemId checked against all resources
		final boolean existingId = ResourceRequests.prepareSearch()
			.setLimit(0)
			.filterById(getId())
			.build()
			.execute(context)
			.getTotal() > 0;
			
		if (existingId) {
			throw new AlreadyExistsException("Resource", getId());
		}
		
		// title should be unique across all resources
		final boolean existingTitle = ResourceRequests.prepareSearch()
			.setLimit(0)
			.filterByTitleExact(getTitle())
			.build()
			.execute(context)
			.getTotal() > 0;
			
		if (existingTitle) {
			throw new AlreadyExistsException("Resource", "title", getTitle());
		}

		// OID must be unique if defined
		if (!Strings.isNullOrEmpty(oid)) {
			final boolean existingOid = CodeSystemRequests.prepareSearchCodeSystem()
					.setLimit(0)
					.filterByOid(oid)
					.build()
					.execute(context)
					.getTotal() > 0;
			if (existingOid) {
				throw new AlreadyExistsException("Resource", "oid", oid);
			}
		}
		
		// toolingId must be supported
		context.service(RepositoryManager.class)
			.repositories()
			.stream()
			.filter(repository -> repository.id().equals(toolingId))
			.findFirst()
			.orElseThrow(() -> new BadRequestException("ToolingId '%s' is not supported by this server.", toolingId));
		
		if (extensionOf != null) {
			
			if (extensionOf.isHead() || extensionOf.isLatest()) {
				throw new BadRequestException("Base code system version was not expicitly given (can not be empty, "
						+ "LATEST or HEAD) in extensionOf URI %s.", extensionOf);
			}
			
			final String versionId = extensionOf.getPath();
			
			final Optional<Version> extensionOfVersion = ResourceRequests.prepareSearchVersion()
					.one()
					.filterByResource(extensionOf.withoutPath())
					.filterByVersionId(versionId)
					.build()
					.execute(context)
					.first();
			
			if (!extensionOfVersion.isPresent()) {
				throw new BadRequestException("Couldn't find base code system version for extensionOf URI %s.", extensionOf);
			}
			
			// The working branch prefix is determined by the extensionOf code system version's path
			final String newResourceBranchPath = Branch.get(extensionOfVersion.get().getBranchPath(), getId());
			
			// CodeSystem Upgrade branches are managed by CodeSystemUpgradeRequest and they can have different paths than the usual extension branch paths, skip check
			if (upgradeOf == null && !create && !branchPath.equals(newResourceBranchPath)) {
				throw new BadRequestException("Branch path is inconsistent with extensionOf URI ('%s' given, should be '%s').",
						branchPath, newResourceBranchPath);
			}

			return extensionOfVersion;
		}
		
		return Optional.empty();
	}

	private void checkSettings() {
		if (settings != null) {
			final Optional<String> nullValueProperty = settings.entrySet()
				.stream()
				.filter(e -> e.getValue() == null)
				.map(e -> e.getKey())
				.findFirst();
			
			nullValueProperty.ifPresent(key -> {
				throw new BadRequestException("Setting value for key '%s' is null.", key);	
			});
		}
	}
	
	private boolean branchExists(final String path, final ServiceProvider context) {
		Branches branches = RepositoryRequests.branching()
				.prepareSearch()
				.setLimit(1)
				.filterById(path)
				.build(toolingId)
				.getRequest()
				.execute(context);
		
		if (branches.isEmpty()) {
			return false;
		}
		
		return branches.stream().filter(b -> !b.isDeleted()).findFirst().isPresent();
		
	}
	
}
