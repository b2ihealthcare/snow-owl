/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.authorization.RepositoryAccessControl;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.branch.Branches;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.core.uri.CodeSystemURI;

/**
 * @since 4.7
 */
final class CodeSystemCreateRequest implements Request<TransactionContext, String>, RepositoryAccessControl {

	private static final long serialVersionUID = 1L;

	private String branchPath;
	private String citation;
	private String oid;
	private String iconPath;
	private String language;
	private String link;
	private String name;
	private String repositoryId;
	private String shortName;
	private String terminologyId;
	private CodeSystemURI extensionOf;
	private CodeSystemURI upgradeOf;
	private List<ExtendedLocale> locales;
	private Map<String, Object> additionalProperties;

	private String parentPath;
	private boolean createBranch = true;
	
	CodeSystemCreateRequest() {}

	void setBranchPath(final String branchPath) {
		this.branchPath = branchPath;
		// Branch should not be created if a path was specified from the outside
		createBranch = StringUtils.isEmpty(branchPath);
	}

	void setCitation(final String citation) {
		this.citation = citation;
	}

	void setOid(final String oid) {
		this.oid = oid;
	}

	void setIconPath(final String iconPath) {
		this.iconPath = iconPath;
	}

	void setLanguage(final String language) {
		this.language = language;
	}

	void setLink(final String link) {
		this.link = link;
	}

	void setName(final String name) {
		this.name = name;
	}

	void setRepositoryId(final String repositoryId) {
		this.repositoryId = repositoryId;
	}

	void setShortName(final String shortName) {
		this.shortName = shortName;
	}

	void setTerminologyId(final String terminologyId) {
		this.terminologyId = terminologyId;
	}

	void setExtensionOf(final CodeSystemURI extensionOf) {
		this.extensionOf = extensionOf;
	}
	
	void setUpgradeOf(CodeSystemURI upgradeOf) {
		this.upgradeOf = upgradeOf;
	}
	
	void setLocales(final List<ExtendedLocale> locales) {
		this.locales = locales;
	}
	
	void setAdditionalProperties(final Map<String, Object> additionalProperties) {
		this.additionalProperties = additionalProperties;
	}

	@Override
	public String execute(final TransactionContext context) {
		final Optional<CodeSystemVersionEntry> extensionOfVersion = checkCodeSystem(context);
		
		// Set the parent path if a branch needs to be created
		if (createBranch) {
			parentPath = extensionOfVersion
				.map(CodeSystemVersionEntry::getPath)
				.orElse(Branch.MAIN_PATH);
		}

		checkBranchPath(context);
		checkLocales();
		checkAdditionalProperties();
		
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
			final String newBranchPath = Branch.get(parentPath, name);
			if (branchExists(newBranchPath, context)) {
				throw new AlreadyExistsException("Code system branch", newBranchPath);
			}
		}
	}

	private Optional<CodeSystemVersionEntry> checkCodeSystem(final TransactionContext context) {
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
			
			final Optional<CodeSystemVersionEntry> extensionOfVersion = CodeSystemRequests.prepareSearchCodeSystemVersion()
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
			
			if (!createBranch && !branchPath.equals(newCodeSystemPath)) {
				throw new BadRequestException("Branch path is inconsistent with extensionOf URI ('%s' given, should be '%s').",
						branchPath, newCodeSystemPath);
			}

			return extensionOfVersion;
		}
		
		return Optional.empty();
	}

	private void checkLocales() {
		if (locales != null && locales.contains(null)) {
			throw new BadRequestException("Locale list can not contain null.");
		}
	}

	private void checkAdditionalProperties() {
		if (additionalProperties != null) {
			final Optional<String> nullValueProperty = additionalProperties.entrySet()
				.stream()
				.filter(e -> e.getValue() == null)
				.map(e -> e.getKey())
				.findFirst();
			
			nullValueProperty.ifPresent(key -> {
				throw new BadRequestException("Additional property value for key %s is null.", key);	
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

	private CodeSystemEntry createCodeSystemEntry(final TransactionContext context) {
		return CodeSystemEntry.builder()
				.oid(oid)
				.branchPath(branchPath)
				.name(name)
				.shortName(shortName)
				.orgLink(link)
				.language(language)
				.citation(citation)
				.iconPath(iconPath)
				.terminologyComponentId(terminologyId)
				.repositoryId(repositoryId)
				.extensionOf(extensionOf)
				.upgradeOf(upgradeOf)
				.locales(locales)
				.additionalProperties(additionalProperties)
				.build();
	}
	
	@Override
	public String getOperation() {
		return Permission.OPERATION_EDIT;
	}
}
