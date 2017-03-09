/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.UUID;

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.request.RevisionIndexRequestBuilder;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;

/**
 * @since 5.7
 */
public final class SnomedRf2ExportRequestBuilder extends BaseRequestBuilder<SnomedRf2ExportRequestBuilder, BranchContext, UUID> implements RevisionIndexRequestBuilder<UUID> {

	private String codeSystem = SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME;
	private boolean includeUnpublished;
	private String startEffectiveTime;
	private String endEffectiveTime;
	private Rf2ReleaseType releaseType;
	private Collection<String> modules = Collections.emptySet();
	private String transientEffectiveTime;
	private boolean extensionOnly;
	private String namespace;
	private Collection<String> refSets = Collections.emptySet();
	
	SnomedRf2ExportRequestBuilder() {}
	
	public SnomedRf2ExportRequestBuilder setCodeSystem(String codeSystem) {
		this.codeSystem = codeSystem;
		return getSelf();
	}
	
	public SnomedRf2ExportRequestBuilder setIncludeUnpublished(boolean includeUnpublished) {
		this.includeUnpublished = includeUnpublished;
		return getSelf();
	}

	public SnomedRf2ExportRequestBuilder setStartEffectiveTime(String startEffectiveTime) {
		this.startEffectiveTime = startEffectiveTime;
		return getSelf();
	}
	
	public SnomedRf2ExportRequestBuilder setEndEffectiveTime(String endEffectiveTime) {
		this.endEffectiveTime = endEffectiveTime;
		return getSelf();
	}
	
	public SnomedRf2ExportRequestBuilder setReleaseType(Rf2ReleaseType releaseType) {
		this.releaseType = releaseType;
		return getSelf();
	}
	
	public SnomedRf2ExportRequestBuilder setModules(Collection<String> modules) {
		this.modules = modules;
		return getSelf();
	}
	
	public SnomedRf2ExportRequestBuilder setExtensionOnly(boolean extensionOnly) {
		this.extensionOnly = extensionOnly;
		return getSelf();
	}
	
	public SnomedRf2ExportRequestBuilder setTransientEffectiveTime(String transientEffectiveTime) {
		this.transientEffectiveTime = transientEffectiveTime;
		return getSelf();
	}
	
	public SnomedRf2ExportRequestBuilder setNamespace(String namespace) {
		this.namespace = namespace;
		return getSelf();
	}
	
	public SnomedRf2ExportRequestBuilder setRefSets(Collection<String> refSets) {
		this.refSets = refSets;
		return getSelf();
	}

	@Override
	protected Request<BranchContext, UUID> doBuild() {
		SnomedRf2ExportRequest req = new SnomedRf2ExportRequest();
		req.setReleaseType(releaseType);
		req.setCodeSystem(codeSystem);
		req.setIncludeUnpublished(includeUnpublished);
		req.setExtensionOnly(extensionOnly);
		req.setStartEffectiveTime(startEffectiveTime);
		req.setEndEffectiveTime(endEffectiveTime);
		req.setModules(modules);
		req.setTransientEffectiveTime(transientEffectiveTime);
		req.setNamespace(namespace);
		req.setRefSets(refSets);
		return req;
	}

}
