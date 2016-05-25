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

import com.b2international.snowowl.snomed.SnomedFactory;
import com.b2international.snowowl.snomed.SnomedRelease;
import com.b2international.snowowl.snomed.SnomedReleaseType;

/**
 * @since 4.7
 */
public class SnomedReleaseBuilder extends SnomedSimpleComponentBuilder<SnomedReleaseBuilder, SnomedRelease> {

	private String shortName;
	private String name;
	private String language;
	private String maintainingOrganizationLink;
	private String iconPath;
	private String codeSystemOid;
	private String baseCodeSystemOid;
	private String citation;
	private String terminologyComponentId;
	private String repositoryUUID;
	private SnomedReleaseType type;
	
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
	}

	@Override
	protected SnomedRelease create() {
		return SnomedFactory.eINSTANCE.createSnomedRelease();
	}
	
}
