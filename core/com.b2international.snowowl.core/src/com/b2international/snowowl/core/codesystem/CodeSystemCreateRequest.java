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

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.b2international.commons.StringUtils;
import com.b2international.commons.exceptions.AlreadyExistsException;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.authorization.RepositoryAccessControl;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.branch.Branches;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.core.uri.CodeSystemURI;

/**
 * @since 4.7
 */
final class CodeSystemCreateRequest implements Request<TransactionContext, String>, RepositoryAccessControl {

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
	
	private String parentPath;
	private boolean createBranch = true;
	
	CodeSystemCreateRequest() {}

	void setBranchPath(final String branchPath) {
		this.branchPath = branchPath;
		// Branch should not be created if a path was specified from the outside
		createBranch = StringUtils.isEmpty(branchPath);
	}

	@Override
	public String execute(final TransactionContext context) {
		final Optional<CodeSystemVersion> extensionOfVersion = checkCodeSystem(context);
		
		// Set the parent path if a branch needs to be created
		if (createBranch) {
			parentPath = extensionOfVersion
				.map(CodeSystemVersion::getPath)
				.orElse(Branch.MAIN_PATH);
		}

		checkBranchPath(context);
		checkSettings();
		
		// Set branchPath to the path of the created branch 
		if (createBranch) {
			branchPath = RepositoryRequests.branching()
				.prepareCreate()
				.setParent(parentPath)
				.setName(shortName)
				.build()
				.execute(context);
		}
		
		return context.add(createCodeSystemEntry(context));
	}

	private void checkBranchPath(final TransactionContext context) {
		// If no branch is created, the branch should already exist
		if (!createBranch && !branchExists(branchPath, context)) {
			throw new BadRequestException("Branch path '%s' should point to an existing branch if given.", branchPath);
		}
		
		// If the branch should be created, it branch should not exist, however 
		if (createBranch) {
			final String newBranchPath = Branch.get(parentPath, title);
			if (branchExists(newBranchPath, context)) {
				throw new AlreadyExistsException("Code system branch", newBranchPath);
			}
		}
	}

	private Optional<CodeSystemVersion> checkCodeSystem(final TransactionContext context) {
		// OID must be unique if defined
		if (!StringUtils.isEmpty(oid) && codeSystemExists(oid, context)) {
			throw new AlreadyExistsException("Code system", oid);
		}

		// Short name is always checked against existing code systems
		if (codeSystemExists(shortName, context)) {
			throw new AlreadyExistsException("Code system", shortName);
		}
		
		if (extensionOf != null) {
			
			if (extensionOf.isHead() || extensionOf.isLatest()) {
				throw new BadRequestException("Base code system version was not expicitly given (can not be empty, "
						+ "LATEST or HEAD) in extensionOf URI %s.", extensionOf);
			}
			
			final String extensionOfShortName = extensionOf.getCodeSystem(); 
			final String versionId = extensionOf.getPath();
			
			final Optional<CodeSystemVersion> extensionOfVersion = CodeSystemRequests.prepareSearchCodeSystemVersion()
					.one()
					.filterByCodeSystemShortName(extensionOfShortName)
					.filterByVersionId(versionId)
					.build()
					.execute(context)
					.first();
			
			if (!extensionOfVersion.isPresent()) {
				throw new BadRequestException("Couldn't find base code system version for extensionOf URI %s.", extensionOf);
			}
			
			// The working branch prefix is determined by the extensionOf code system version's path
			final String newCodeSystemPath = extensionOfVersion.get().getPath() + IBranchPath.SEPARATOR + shortName;
			
			// CodeSystem Upgrade branches are managed by CodeSystemUpgradeRequest and they can have different paths than the usual extension branch paths, skip check
			if (upgradeOf == null && !createBranch && !branchPath.equals(newCodeSystemPath)) {
				throw new BadRequestException("Branch path is inconsistent with extensionOf URI ('%s' given, should be '%s').",
						branchPath, newCodeSystemPath);
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
	
	private boolean codeSystemExists(final String uniqeId, final TransactionContext context) {
		return CodeSystemRequests.prepareSearchCodeSystem()
				.setLimit(0)
				.filterById(uniqeId)
				.build()
				.execute(context)
				.getTotal() > 0;
	}
	
	private boolean branchExists(final String path, final TransactionContext context) {
		Branches branches = RepositoryRequests.branching()
				.prepareSearch()
				.setLimit(1)
				.filterById(path)
				.build()
				.execute(context);
		
		if (branches.isEmpty()) {
			return false;
		}
		
		return branches.stream().filter(b -> !b.isDeleted()).findFirst().isPresent();
		
	}

	private ResourceDocument createCodeSystemEntry(final TransactionContext context) {
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
	
	@Override
	public String getOperation() {
		return Permission.OPERATION_EDIT;
	}
}
