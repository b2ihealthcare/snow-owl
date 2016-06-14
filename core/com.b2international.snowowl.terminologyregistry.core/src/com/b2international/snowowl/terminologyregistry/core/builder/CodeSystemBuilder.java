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
package com.b2international.snowowl.terminologyregistry.core.builder;

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
	private CodeSystem extensionOf;

	public CodeSystemBuilder withCitation(final String citation) {
		this.citation = citation;
		return getSelf();
	}

	public CodeSystemBuilder withCodeSystemOid(final String codeSystemOid) {
		this.codeSystemOid = codeSystemOid;
		return getSelf();
	}

	public CodeSystemBuilder withIconPath(final String iconPath) {
		this.iconPath = iconPath;
		return getSelf();
	}

	public CodeSystemBuilder withLanguage(final String language) {
		this.language = language;
		return getSelf();
	}

	public CodeSystemBuilder withMaintainingOrganizationLink(final String maintainingOrganiationLink) {
		this.maintainingOrganizationLink = maintainingOrganiationLink;
		return getSelf();
	}

	public CodeSystemBuilder withName(final String name) {
		this.name = name;
		return getSelf();
	}

	public CodeSystemBuilder withShortName(final String shortName) {
		this.shortName = shortName;
		return getSelf();
	}

	public CodeSystemBuilder withTerminologyComponentId(final String terminologyComponentId) {
		this.terminologyComponentId = terminologyComponentId;
		return getSelf();
	}

	public CodeSystemBuilder withRepositoryUuid(final String repositoryUuid) {
		this.repositoryUuid = repositoryUuid;
		return getSelf();
	}

	public CodeSystemBuilder withBranchPath(final String branchPath) {
		this.branchPath = branchPath;
		return getSelf();
	}
	
	public CodeSystemBuilder withExtensionOf(final CodeSystem extensionOf) {
		this.extensionOf = extensionOf;
		return getSelf();
	}

	protected final CodeSystemBuilder getSelf() {
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
		codeSystem.setExtensionOf(extensionOf);

		return codeSystem;
	}
	
}
