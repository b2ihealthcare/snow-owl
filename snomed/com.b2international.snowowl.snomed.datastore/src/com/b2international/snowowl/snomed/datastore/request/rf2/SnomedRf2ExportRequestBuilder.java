/*
 * Copyright 2017-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request.rf2;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.request.RepositoryRequestBuilder;
import com.b2international.snowowl.snomed.core.domain.Rf2MaintainerType;
import com.b2international.snowowl.snomed.core.domain.Rf2RefSetExportLayout;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;

/**
 * @since 5.7
 */
public final class SnomedRf2ExportRequestBuilder 
		extends BaseRequestBuilder<SnomedRf2ExportRequestBuilder, RepositoryContext, UUID> 
		implements RepositoryRequestBuilder<UUID> {

	private String userId;
	private String codeSystem;
	private String referenceBranch;
	private Rf2ReleaseType releaseType;
	private Rf2MaintainerType maintainerType;
	private Rf2RefSetExportLayout refSetExportLayout;
	private String nrcCountryCode;
	private String namespaceId;
	private Date startEffectiveTime;
	private Date endEffectiveTime;
	private boolean includePreReleaseContent;
	private Collection<String> componentTypes = Collections.emptySet();
	private Collection<String> modules = Collections.emptySet();
	private Collection<String> refSets = Collections.emptySet();
	private String transientEffectiveTime;
	private boolean extensionOnly;
	
	SnomedRf2ExportRequestBuilder() {}
	
	public SnomedRf2ExportRequestBuilder setUserId(String userId) {
		this.userId = userId;
		return getSelf();
	}
	
	public SnomedRf2ExportRequestBuilder setCodeSystem(String codeSystem) {
		this.codeSystem = codeSystem;
		return getSelf();
	}
	
	public SnomedRf2ExportRequestBuilder setReferenceBranch(String referenceBranch) {
		this.referenceBranch = referenceBranch;
		return getSelf();
	}
	
	public SnomedRf2ExportRequestBuilder setReleaseType(Rf2ReleaseType releaseType) {
		this.releaseType = releaseType;
		return getSelf();
	}

	public SnomedRf2ExportRequestBuilder setMaintainerType(Rf2MaintainerType maintainerType) {
		this.maintainerType = maintainerType;
		return getSelf();
	}
	
	public SnomedRf2ExportRequestBuilder setRefSetExportLayout(Rf2RefSetExportLayout refSetExportLayout) {
		this.refSetExportLayout = refSetExportLayout;
		return getSelf();
	}
	
	

	public SnomedRf2ExportRequestBuilder setNrcCountryCode(String nrcCountryCode) {
		this.nrcCountryCode = nrcCountryCode;
		return getSelf();
	}

	public SnomedRf2ExportRequestBuilder setNamespaceId(String namespace) {
		this.namespaceId = namespace;
		return getSelf();
	}

	public SnomedRf2ExportRequestBuilder setStartEffectiveTime(Date startEffectiveTime) {
		this.startEffectiveTime = startEffectiveTime;
		return getSelf();
	}
	
	public SnomedRf2ExportRequestBuilder setEndEffectiveTime(Date endEffectiveTime) {
		this.endEffectiveTime = endEffectiveTime;
		return getSelf();
	}
	
	public SnomedRf2ExportRequestBuilder setIncludePreReleaseContent(boolean includePreReleaseContent) {
		this.includePreReleaseContent = includePreReleaseContent;
		return getSelf();
	}

	public SnomedRf2ExportRequestBuilder setComponentTypes(Collection<String> componentTypes) {
		this.componentTypes = componentTypes;
		return getSelf();
	}

	public SnomedRf2ExportRequestBuilder setModules(Collection<String> modules) {
		this.modules = modules;
		return getSelf();
	}
	
	public SnomedRf2ExportRequestBuilder setRefSets(Collection<String> refSets) {
		this.refSets = refSets;
		return getSelf();
	}

	public SnomedRf2ExportRequestBuilder setTransientEffectiveTime(String transientEffectiveTime) {
		this.transientEffectiveTime = transientEffectiveTime;
		return getSelf();
	}

	public SnomedRf2ExportRequestBuilder setExtensionOnly(boolean extensionOnly) {
		this.extensionOnly = extensionOnly;
		return getSelf();
	}
	
	@Override
	protected Request<RepositoryContext, UUID> doBuild() {
		SnomedRf2ExportRequest req = new SnomedRf2ExportRequest();
		req.setUserId(userId);
		req.setCodeSystem(codeSystem);
		req.setReferenceBranch(referenceBranch);
		req.setReleaseType(releaseType);
		req.setMaintainerType(maintainerType);
		req.setRefSetExportLayout(refSetExportLayout);
		req.setNrcCountryCode(nrcCountryCode);
		req.setNamespaceId(namespaceId);
		req.setStartEffectiveTime(startEffectiveTime);
		req.setEndEffectiveTime(endEffectiveTime);
		req.setIncludePreReleaseContent(includePreReleaseContent);
		req.setComponentTypes(componentTypes);
		req.setModules(modules);
		req.setRefSets(refSets);
		req.setTransientEffectiveTime(transientEffectiveTime);
		req.setExtensionOnly(extensionOnly);
		return req;
	}
}
