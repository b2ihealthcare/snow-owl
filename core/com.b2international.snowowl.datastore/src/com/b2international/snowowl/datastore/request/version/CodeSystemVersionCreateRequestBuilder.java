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
package com.b2international.snowowl.datastore.request.version;

import java.util.Date;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;

/**
 * @since 5.7
 */
public final class CodeSystemVersionCreateRequestBuilder extends BaseRequestBuilder<CodeSystemVersionCreateRequestBuilder, ServiceProvider, Boolean> {

	private String codeSystemShortName;
	private String versionId;
	private String description;
	private Date effectiveTime;

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
	
	public CodeSystemVersionCreateRequestBuilder setVersionId(String versionId) {
		this.versionId = versionId;
		return getSelf();
	}
	
	@Override
	protected Request<ServiceProvider, Boolean> doBuild() {
		final CodeSystemVersionCreateRequest req = new CodeSystemVersionCreateRequest();
		req.codeSystemShortName = codeSystemShortName;
		req.versionId = versionId;
		req.description= description;
		req.effectiveTime = effectiveTime;
		return req;
	}

}
