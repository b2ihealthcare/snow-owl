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

import com.b2international.commons.StringUtils;
import com.b2international.commons.exceptions.AlreadyExistsException;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.index.revision.Commit;
import com.b2international.index.revision.TimestampProvider;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.branch.Branches;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.identity.User;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.core.internal.ResourceRepository;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.core.version.Version;

/**
 * @since 4.7
 */
final class CodeSystemCreateRequest implements Request<TransactionContext, String> {

	private static final long serialVersionUID = 2L;

	// the new codesystem's ID, if not specified, it will be auto-generated
	String id;
	
	// common resource fields TODO move to superclass
	String url;
	String title;
	String language;
	String description;
	String status;
	String copyright;
	String owner;
	String contact;
	String usage;
	String purpose;
	
	// specialized resource fields
	String oid;
	String branchPath;
	String toolingId;
	ResourceURI extensionOf;
	ResourceURI upgradeOf;
	Map<String, Object> settings;
	
	private transient String parentPath;
	
	CodeSystemCreateRequest() {}

	@Override
	public String execute(final TransactionContext context) {
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
				.setName(id)
				.build(toolingId)
				.getRequest()
				.execute(context);
		}

		final long timestamp = context.service(TimestampProvider.class).getTimestamp();
		final String username = context.service(User.class).getUsername();
		
		Commit commit = context.service(ResourceRepository.class).prepareCommit()
			.stageNew(createCodeSystemEntry())
			.commit(timestamp, username, "Create new Code System: " + id);
		
		// TODO notification about the new code system
		
		return id;
	}

	private void checkBranchPath(final RepositoryContext context, final boolean create) {
		// If no branch is created, the branch should already exist
		if (!create && !branchExists(branchPath, context)) {
			throw new BadRequestException("Branch path '%s' should point to an existing branch if given.", branchPath);
		}
		
		// If the branch should be created, it branch should not exist, however 
		if (create) {
			final String newBranchPath = Branch.get(parentPath, title);
			if (branchExists(newBranchPath, context)) {
				throw new AlreadyExistsException("Code system branch", newBranchPath);
			}
		}
	}

	private Optional<Version> checkCodeSystem(final RepositoryContext context, final boolean create) {
		// OID must be unique if defined
		if (!StringUtils.isEmpty(oid) && codeSystemExists(oid, context)) {
			throw new AlreadyExistsException("Resource", oid);
		}

		// Short name is always checked against existing code systems
		if (codeSystemExists(id, context)) {
			throw new AlreadyExistsException("Resource", id);
		}
		
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
			final String newResourceBranchPath = Branch.get(extensionOfVersion.get().getBranchPath(), id);
			
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
	
	private boolean codeSystemExists(final String uniqeId, final RepositoryContext context) {
		return CodeSystemRequests.prepareSearchCodeSystem()
				.setLimit(0)
				.filterById(uniqeId)
				.build()
				.execute(context)
				.getTotal() > 0;
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

	private ResourceDocument createCodeSystemEntry() {
		return ResourceDocument.builder()
				.id(id)
				.url(url)
				.title(title)
				.language(language)
				.description(description)
				.status(status)
				.copyright(copyright)
				.owner(owner)
				.contact(contact)
				.usage(usage)
				.purpose(purpose)
				.oid(oid)
				.branchPath(branchPath)
				.toolingId(toolingId)
				.extensionOf(extensionOf)
				.upgradeOf(upgradeOf)
				.settings(settings)
				.build();
	}
	
}
