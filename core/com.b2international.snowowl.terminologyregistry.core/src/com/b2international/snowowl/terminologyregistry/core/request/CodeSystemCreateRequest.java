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

import java.util.Map;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.BaseRequest;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.TerminologyRegistryService;
import com.b2international.snowowl.datastore.UserBranchPathMap;
import com.b2international.snowowl.terminologymetadata.CodeSystem;
import com.b2international.snowowl.terminologyregistry.core.builder.CodeSystemBuilder;

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
	private Map<String, String> additionalProperties;

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
	
	void setAdditionalProperties(final Map<String, String> additionalProperties) {
		this.additionalProperties = additionalProperties;
	}
	
	@Override
	public String execute(final TransactionContext context) {
		validateCodeSystem(context);

		final CodeSystem codeSystem = createCodeSystem(context);
		context.add(codeSystem);

		return codeSystem.getShortName();
	}

	private void validateCodeSystem(final TransactionContext context) {
		final UserBranchPathMap branchPathMap = new UserBranchPathMap();
		branchPathMap.putBranchPath(repositoryUuid, BranchPathUtils.createMainPath());

		final TerminologyRegistryService registryService = context.service(TerminologyRegistryService.class);

		if (registryService.getCodeSystemByOid(branchPathMap, oid) != null) {
			throw new BadRequestException("Couldn't create code system with OID %s as it is not unique.", oid);
		}

		if (registryService.getCodeSystemByShortName(branchPathMap, shortName) != null) {
			throw new BadRequestException("Couldn't create code system with short name %s as it is not unique.", shortName);
		}
	}

	private CodeSystem createCodeSystem(final TransactionContext context) {
		final CodeSystemBuilder<?, ?, ?> builder = context.service(CodeSystemBuilder.class);
		return builder.withBranchPath(branchPath)
				.withCitation(citation)
				.withCodeSystemOid(oid)
				.withIconPath(iconPath)
				.withLanguage(language)
				.withMaintainingOrganizationLink(link)
				.withName(name)
				.withRepositoryUuid(repositoryUuid)
				.withShortName(shortName)
				.withTerminologyComponentId(terminologyId)
				.withAdditionalProperties(additionalProperties)
				.build();
	}

	@Override
	protected Class<String> getReturnType() {
		return String.class;
	}

}
