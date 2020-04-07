/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.commons.exceptions.AlreadyExistsException;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.authorization.RepositoryAccessControl;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.identity.Permission;
import com.google.common.base.Strings;

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
		return context.add(createCodeSystem(context));
	}

	private void checkCodeSystem(final TransactionContext context) {
		if (getCodeSystem(oid, context) != null) {
			throw new AlreadyExistsException("Code system", oid);
		}
		
		if (getCodeSystem(shortName, context) != null) {
			throw new AlreadyExistsException("Code system", shortName);
		}
		
		if (!Strings.isNullOrEmpty(extensionOf) && getCodeSystem(extensionOf, context) == null) {
			throw new BadRequestException("Couldn't find base Code System with unique ID %s.", extensionOf);
		}
	}
	
	private CodeSystemEntry getCodeSystem(final String uniqeId, final TransactionContext context) {
		try {
			return CodeSystemRequests.prepareGetCodeSystem(uniqeId).build().execute(context);
		} catch (NotFoundException e) {
			 return null;
		}
	}

	private CodeSystemEntry createCodeSystem(final TransactionContext context) {
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
				.repositoryUuid(repositoryUuid)
				.extensionOf(extensionOf)
				.build();
	}
	
	@Override
	public String getOperation() {
		return Permission.EDIT;
	}
	
}
