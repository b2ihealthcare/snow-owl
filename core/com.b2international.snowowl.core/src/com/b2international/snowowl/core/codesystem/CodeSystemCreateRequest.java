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

import java.util.Map;
import java.util.Optional;

import com.b2international.commons.exceptions.AlreadyExistsException;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.authorization.RepositoryAccessControl;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.identity.Permission;
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
	private String repositoryUuid;
	private String shortName;
	private String terminologyId;
	private CodeSystemURI extensionOf;
	private Map<String, Object> additionalProperties;

	CodeSystemCreateRequest() {}

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

	void setExtensionOf(final CodeSystemURI extensionOf) {
		this.extensionOf = extensionOf;
	}
	
	void setAdditionalProperties(final Map<String, Object> additionalProperties) {
		this.additionalProperties = additionalProperties;
	}

	@Override
	public String execute(final TransactionContext context) {
		checkCodeSystem(context);
		checkAdditionalProperties();
		return context.add(createCodeSystem(context));
	}

	private void checkCodeSystem(final TransactionContext context) {
		if (getCodeSystem(oid, context) != null) {
			throw new AlreadyExistsException("Code system", oid);
		}
		
		if (getCodeSystem(shortName, context) != null) {
			throw new AlreadyExistsException("Code system", shortName);
		}
		
		if (extensionOf != null) {
			final String extensionOfShortName = extensionOf.getCodeSystem(); 
			if (getCodeSystem(extensionOfShortName, context) == null) {
				throw new BadRequestException("Couldn't find base code system for extensionOf URI %s.", extensionOf);
			}
			
			if (extensionOf.isHead() || extensionOf.isLatest()) {
				throw new BadRequestException("Base code system version was not expicitly given (can not be empty, "
						+ "LATEST or HEAD) in extensionOf URI %s.", extensionOf);
			}
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
				.additionalProperties(additionalProperties)
				.build();
	}
	
	@Override
	public String getOperation() {
		return Permission.EDIT;
	}
}
