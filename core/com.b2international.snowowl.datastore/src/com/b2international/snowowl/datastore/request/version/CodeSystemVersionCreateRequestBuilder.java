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
package com.b2international.snowowl.datastore.request.version;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;

/**
 * @since 5.7
 */
public final class CodeSystemVersionCreateRequestBuilder extends BaseRequestBuilder<CodeSystemVersionCreateRequestBuilder, ServiceProvider, Boolean> {

	private String codeSystemShortName;
	private String parentBranchPath;
	private String versionId;
	private String description;
	private Date effectiveTime;
	private String primaryToolingId;
	private Collection<String> toolingIds = Collections.emptySet();

	// TODO make it package visible
	public CodeSystemVersionCreateRequestBuilder() {
	}

	public CodeSystemVersionCreateRequestBuilder setCodeSystemShortName(String codeSystemShortName) {
		this.codeSystemShortName = codeSystemShortName;
		return getSelf();
	}
	
	public CodeSystemVersionCreateRequestBuilder setDescription(String description) {
		this.description = description;
		return getSelf();
	}
	
	public CodeSystemVersionCreateRequestBuilder setEffectiveTime(Date effectiveTime) {
		this.effectiveTime = effectiveTime;
		return getSelf();
	}
	
	public CodeSystemVersionCreateRequestBuilder setParentBranchPath(String parentBranchPath) {
		this.parentBranchPath = parentBranchPath;
		return getSelf();
	}
	
	public CodeSystemVersionCreateRequestBuilder setPrimaryToolingId(String primaryToolingId) {
		this.primaryToolingId = primaryToolingId;
		return getSelf();
	}
	
	public CodeSystemVersionCreateRequestBuilder setToolingIds(Collection<String> toolingIds) {
		this.toolingIds = toolingIds;
		return getSelf();
	}
	
	public CodeSystemVersionCreateRequestBuilder setVersionId(String versionId) {
		this.versionId = versionId;
		return getSelf();
	}
	
	@Override
	protected Request<ServiceProvider, Boolean> doBuild() {
		final CodeSystemVersionCreateRequest req = new CodeSystemVersionCreateRequest();
		req.setCodeSystemShortName(codeSystemShortName);
		req.setParentBranchPath(parentBranchPath);
		req.setVersionId(versionId);
		req.setDescription(description);
		req.setEffectiveTime(effectiveTime);
		req.setPrimaryToolingId(primaryToolingId);
		req.setToolingIds(toolingIds);
		return req;
	}

}
