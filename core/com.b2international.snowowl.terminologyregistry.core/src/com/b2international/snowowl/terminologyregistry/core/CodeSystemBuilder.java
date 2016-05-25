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
package com.b2international.snowowl.terminologyregistry.core;

import com.b2international.snowowl.terminologymetadata.CodeSystem;
import com.b2international.snowowl.terminologymetadata.TerminologymetadataFactory;

/**
 * @since 4.7
 */
public class CodeSystemBuilder {

	private String citation;
	private String codeSystemOid;
	private String iconPath;
	private String language;
	private String maintainingOrganizationLink;
	private String name;
	private String shortName;
	private String terminologyComponentId;
	private String repositoryUuid;
	private String branchPath;

	public CodeSystemBuilder withCitation(final String citation) {
		this.citation = citation;
		return this;
	}

	public CodeSystemBuilder withCodeSystemOid(final String codeSystemOid) {
		this.codeSystemOid = codeSystemOid;
		return this;
	}

	public CodeSystemBuilder withIconPath(final String iconPath) {
		this.iconPath = iconPath;
		return this;
	}

	public CodeSystemBuilder withLanguage(final String language) {
		this.language = language;
		return this;
	}

	public CodeSystemBuilder withMaintainingOrganizationLink(final String maintainingOrganiationLink) {
		this.maintainingOrganizationLink = maintainingOrganiationLink;
		return this;
	}

	public CodeSystemBuilder withName(final String name) {
		this.name = name;
		return this;
	}

	public CodeSystemBuilder withShortName(final String shortName) {
		this.shortName = shortName;
		return this;
	}

	public CodeSystemBuilder withTerminologyComponentId(final String terminologyComponentId) {
		this.terminologyComponentId = terminologyComponentId;
		return this;
	}

	public CodeSystemBuilder withRepositoryUuid(final String repositoryUuid) {
		this.repositoryUuid = repositoryUuid;
		return this;
	}

	public CodeSystemBuilder withBranchPath(final String branchPath) {
		this.branchPath = branchPath;
		return this;
	}

	public CodeSystem build() {
		final CodeSystem codeSystem = TerminologymetadataFactory.eINSTANCE.createCodeSystem();
		codeSystem.setCitation(citation);
		codeSystem.setCodeSystemOID(codeSystemOid);
		codeSystem.setIconPath(iconPath);
		codeSystem.setLanguage(language);
		codeSystem.setMaintainingOrganizationLink(maintainingOrganizationLink);
		codeSystem.setName(name);
		codeSystem.setShortName(shortName);
		codeSystem.setTerminologyComponentId(terminologyComponentId);
		codeSystem.setRepositoryUuid(repositoryUuid);
		codeSystem.setBranchPath(branchPath);

		return codeSystem;
	}

}
