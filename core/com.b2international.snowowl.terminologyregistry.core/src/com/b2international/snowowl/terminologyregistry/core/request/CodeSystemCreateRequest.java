/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.terminologyregistry.core.request;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.domain.exceptions.CodeSystemNotFoundException;
import com.b2international.snowowl.core.events.BaseRequest;
import com.b2international.snowowl.core.exceptions.AlreadyExistsException;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.terminologymetadata.CodeSystem;
import com.b2international.snowowl.terminologyregistry.core.builder.CodeSystemBuilder;
import com.b2international.snowowl.terminologyregistry.core.index.CodeSystemEntry;

/**
 * @since 4.7
 */
final class CodeSystemCreateRequest extends BaseRequest<TransactionContext, String> {

	private static final long serialVersionUID = 1L;

	private String branchPath;
	private String citation;
	private String oid;
	private String iconPath;
	private String language;
	private String link;
	private String name;
	private String repositoryUuid;
	private String shortName;
	private String terminologyId;
	private String extensionOf;

	CodeSystemCreateRequest() {
	}

	void setBranchPath(final String branchPath) {
		this.branchPath = branchPath;
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

	void setRepositoryUuid(final String repositoryUuid) {
		this.repositoryUuid = repositoryUuid;
	}

	void setShortName(final String shortName) {
		this.shortName = shortName;
	}

	void setTerminologyId(final String terminologyId) {
		this.terminologyId = terminologyId;
	}

	void setExtensionOf(final String extensionOf) {
		this.extensionOf = extensionOf;
	}
	
	@Override
	public String execute(final TransactionContext context) {
		checkCodeSystem(context);
		
		final CodeSystem codeSystem = createCodeSystem(context);
		context.add(codeSystem);

		return codeSystem.getShortName();
	}

	private void checkCodeSystem(final TransactionContext context) {
		if (getCodeSystem(oid, context) != null) {
			throw new AlreadyExistsException("Code system", oid);
		}
		
		if (getCodeSystem(shortName, context) != null) {
			throw new AlreadyExistsException("Code system", shortName);
		}
		
		if (extensionOf != null && getCodeSystem(extensionOf, context) == null) {
			throw new BadRequestException("Couldn't find base Code System with unique ID %s.", extensionOf);
		}
		
		final Branch branch = RepositoryRequests
				.branching(repositoryUuid)
				.prepareGet(branchPath)
				.execute(context);
		
		if (branch.isDeleted()) {
			throw new BadRequestException("Branch with identifier %s is deleted.", branchPath);
		}
	}
	
	private CodeSystemEntry getCodeSystem(final String uniqeId, final TransactionContext context) {
		try {
			return new CodeSystemRequests(repositoryUuid)
					.prepareGetCodeSystem()
					.setUniqueId(uniqeId)
					.build(IBranchPath.MAIN_BRANCH)
					.execute(context);
		} catch (CodeSystemNotFoundException e) {
			 return null;
		}
	}

	private CodeSystem createCodeSystem(final TransactionContext context) {
		return new CodeSystemBuilder()
				.withBranchPath(branchPath)
				.withCitation(citation)
				.withCodeSystemOid(oid)
				.withIconPath(iconPath)
				.withLanguage(language)
				.withMaintainingOrganizationLink(link)
				.withName(name)
				.withRepositoryUuid(repositoryUuid)
				.withShortName(shortName)
				.withTerminologyComponentId(terminologyId)
				.withExtensionOf(extensionOf == null ? null : context.lookup(extensionOf, CodeSystem.class))
				.build();
	}

	@Override
	protected Class<String> getReturnType() {
		return String.class;
	}

}
