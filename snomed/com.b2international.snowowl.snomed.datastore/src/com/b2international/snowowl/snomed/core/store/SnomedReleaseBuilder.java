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
package com.b2international.snowowl.snomed.core.store;

import java.util.Map;

import com.b2international.snowowl.snomed.SnomedFactory;
import com.b2international.snowowl.snomed.SnomedRelease;
import com.b2international.snowowl.snomed.SnomedReleaseType;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;

/**
 * @since 4.7
 */
public class SnomedReleaseBuilder extends SnomedSimpleComponentBuilder<SnomedReleaseBuilder, SnomedRelease> {

	private static final String KEY_NAME = "name";
	private static final String KEY_SHORT_NAME = "shortName";
	private static final String KEY_LANGUAGE = "language";
	private static final String KEY_CODE_SYSTEM_OID = "codeSystemOID";
	private static final String KEY_BASE_CODE_SYSTEM_OID = "baseCodeSystemOID";
	private static final String KEY_MAINTAINING_ORGANIZATION_LINK = "maintainingOrganizationLink";
	private static final String KEY_CITATION = "citation";
	private static final String KEY_RELEASE_TYPE = "releaseType";
	private static final String KEY_ICON_PATH = "iconPath";
	private static final String KEY_TERMINOLOGY_COMPONENT_ID = "terminologyComponentId";
	private static final String KEY_REPOSITORY_UUID = "repositoryUuid";
	
	private String name;
	private String shortName;
	private String language;
	private String codeSystemOid;
	private String baseCodeSystemOid;
	private String maintainingOrganizationLink;
	private String citation;
	private SnomedReleaseType type;
	private String branchPath;

	private String iconPath = SnomedTerminologyComponentConstants.SNOMED_INT_ICON_PATH;
	private String terminologyComponentId = SnomedTerminologyComponentConstants.TERMINOLOGY_ID;
	private String repositoryUUID = SnomedDatastoreActivator.REPOSITORY_UUID;
	
	public SnomedReleaseBuilder() {}
	
	public SnomedReleaseBuilder(Map<String, String> valueMap) {
		this.name = valueMap.get(KEY_NAME);
		this.shortName = valueMap.get(KEY_SHORT_NAME);
		this.language = valueMap.get(KEY_LANGUAGE);
		this.codeSystemOid = valueMap.get(KEY_CODE_SYSTEM_OID);
		this.baseCodeSystemOid = valueMap.get(KEY_BASE_CODE_SYSTEM_OID);
		this.maintainingOrganizationLink = valueMap.get(KEY_MAINTAINING_ORGANIZATION_LINK);
		this.citation = valueMap.get(KEY_CITATION);
		
		if (valueMap.containsKey(KEY_RELEASE_TYPE)) {
			SnomedReleaseType typeFromMap = SnomedReleaseType.getByName(valueMap.get(KEY_RELEASE_TYPE));
			if (typeFromMap != null) {
				this.type = typeFromMap;
			}
		}
		if (valueMap.containsKey(KEY_ICON_PATH)) {
			this.iconPath = valueMap.get(KEY_ICON_PATH);
		}
		if (valueMap.containsKey(KEY_TERMINOLOGY_COMPONENT_ID)) {
			this.terminologyComponentId = valueMap.get(KEY_TERMINOLOGY_COMPONENT_ID);
		}
		if (valueMap.containsKey(KEY_REPOSITORY_UUID)) {
			this.repositoryUUID = valueMap.get(KEY_REPOSITORY_UUID);
		}
	}
	
	public final SnomedReleaseBuilder withName(final String name) {
		this.name = name;
		return getSelf();
	}
	
	public final SnomedReleaseBuilder withShortName(final String shortName) {
		this.shortName = shortName;
		return getSelf();
	}
	
	public final SnomedReleaseBuilder withLanguage(final String language) {
		this.language = language;
		return getSelf();
	}
	
	public final SnomedReleaseBuilder withMaintainingOrganizationLink(final String maintainingOrganizationLink) {
		this.maintainingOrganizationLink = maintainingOrganizationLink;
		return getSelf();
	}
	
	public final SnomedReleaseBuilder withIconPath(final String iconPath) {
		this.iconPath = iconPath;
		return getSelf();
	}
	
	public final SnomedReleaseBuilder withCodeSystemOid(final String codeSystemOid) {
		this.codeSystemOid = codeSystemOid;
		return getSelf();
	}
	
	public final SnomedReleaseBuilder withBaseCodeSystemOid(final String baseCodeSystemOid) {
		this.baseCodeSystemOid = baseCodeSystemOid;
		return getSelf();
	}
	
	public final SnomedReleaseBuilder withCitation(final String citation) {
		this.citation = citation;
		return getSelf();
	}
	
	public final SnomedReleaseBuilder withTerminologyComponentId(final String terminologyComponentId) {
		this.terminologyComponentId = terminologyComponentId;
		return getSelf();
	}
	
	public final SnomedReleaseBuilder withRepositoryUUID(final String repositoryUUID) {
		this.repositoryUUID = repositoryUUID;
		return getSelf();
	}
	
	public final SnomedReleaseBuilder withType(final SnomedReleaseType type) {
		this.type = type;
		return getSelf();
	}
	
	public final SnomedReleaseBuilder withBranchPath(final String branchPath) {
		this.branchPath = branchPath;
		return getSelf();
	}
	
	@Override
	protected void init(final SnomedRelease snomedRelease) {
		snomedRelease.setName(name);
		snomedRelease.setShortName(shortName);
		snomedRelease.setLanguage(language);
		snomedRelease.setMaintainingOrganizationLink(maintainingOrganizationLink);
		snomedRelease.setIconPath(iconPath);
		snomedRelease.setCodeSystemOID(codeSystemOid);
		snomedRelease.setBaseCodeSystemOID(baseCodeSystemOid);
		snomedRelease.setCitation(citation);
		snomedRelease.setReleaseType(type);
		snomedRelease.setTerminologyComponentId(terminologyComponentId);
		snomedRelease.setRepositoryUuid(repositoryUUID);
		snomedRelease.setBranchPath(branchPath);
	}

	@Override
	protected SnomedRelease create() {
		return SnomedFactory.eINSTANCE.createSnomedRelease();
	}
	
}
