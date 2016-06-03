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

import java.util.Map;

import com.b2international.snowowl.datastore.CodeSystemEntry;
import com.b2international.snowowl.terminologymetadata.CodeSystem;

/**
 * @since 4.7
 */
public abstract class CodeSystemBuilder<B extends CodeSystemBuilder<B>> {
	
	static final String EXTENSION_ID = "com.b2international.snowowl.terminologyregistry.core.codeSystemBuilder";

	private static final String KEY_NAME = "name";
	private static final String KEY_SHORT_NAME = "shortName";
	private static final String KEY_LANGUAGE = "language";
	private static final String KEY_CODE_SYSTEM_OID = "codeSystemOID";
	private static final String KEY_MAINTAINING_ORGANIZATION_LINK = "maintainingOrganizationLink";
	private static final String KEY_CITATION = "citation";
	protected static final String KEY_ICON_PATH = "iconPath";
	protected static final String KEY_TERMINOLOGY_COMPONENT_ID = "terminologyComponentId";
	protected static final String KEY_REPOSITORY_UUID = "repositoryUuid";

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

	public B init(final Map<String, String> valueMap) {
		this.name = valueMap.get(KEY_NAME);
		this.shortName = valueMap.get(KEY_SHORT_NAME);
		this.language = valueMap.get(KEY_LANGUAGE);
		this.codeSystemOid = valueMap.get(KEY_CODE_SYSTEM_OID);
		this.maintainingOrganizationLink = valueMap.get(KEY_MAINTAINING_ORGANIZATION_LINK);
		this.citation = valueMap.get(KEY_CITATION);
		this.iconPath = valueMap.get(KEY_ICON_PATH);
		this.terminologyComponentId = valueMap.get(KEY_TERMINOLOGY_COMPONENT_ID);
		this.repositoryUuid = valueMap.get(KEY_REPOSITORY_UUID);
		return getSelf();
	}
	
	public B init(final CodeSystemEntry entry) {
		this.name = entry.getName();
		this.shortName = entry.getShortName();
		this.language = entry.getLanguage();
		this.codeSystemOid = entry.getOid();
		this.maintainingOrganizationLink = entry.getOrgLink();
		this.citation = entry.getCitation();
		this.iconPath = entry.getIconPath();
		this.terminologyComponentId = entry.getSnowOwlId();
		this.repositoryUuid = entry.getRepositoryUuid();
		return getSelf();
	}

	public B withCitation(final String citation) {
		this.citation = citation;
		return getSelf();
	}

	public B withCodeSystemOid(final String codeSystemOid) {
		this.codeSystemOid = codeSystemOid;
		return getSelf();
	}

	public B withIconPath(final String iconPath) {
		this.iconPath = iconPath;
		return getSelf();
	}

	public B withLanguage(final String language) {
		this.language = language;
		return getSelf();
	}

	public B withMaintainingOrganizationLink(final String maintainingOrganiationLink) {
		this.maintainingOrganizationLink = maintainingOrganiationLink;
		return getSelf();
	}

	public B withName(final String name) {
		this.name = name;
		return getSelf();
	}

	public B withShortName(final String shortName) {
		this.shortName = shortName;
		return getSelf();
	}

	public B withTerminologyComponentId(final String terminologyComponentId) {
		this.terminologyComponentId = terminologyComponentId;
		return getSelf();
	}

	public B withRepositoryUuid(final String repositoryUuid) {
		this.repositoryUuid = repositoryUuid;
		return getSelf();
	}

	public B withBranchPath(final String branchPath) {
		this.branchPath = branchPath;
		return getSelf();
	}
	
	public B withAdditionalProperties(final Map<String, String> additionalProperties) {
		return getSelf();
	}

	protected final B getSelf() {
		return (B) this;
	}

	public CodeSystem build() {
		final CodeSystem codeSystem = create();
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
	
	protected abstract CodeSystem create();
	
	protected abstract String getRepositoryUuid();

}
