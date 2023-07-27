/*
 * Copyright 2011-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.codesystem;

import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.request.resource.BaseTerminologyResourceCreateRequestBuilder;

/**
 * @since 4.7
 */
public final class CodeSystemCreateRequestBuilder extends BaseTerminologyResourceCreateRequestBuilder<CodeSystemCreateRequestBuilder, CodeSystemCreateRequest> {

	// specialized resource fields
	private String toolingId;
	private ResourceURI extensionOf;
	private ResourceURI upgradeOf;

	/* package */ CodeSystemCreateRequestBuilder() {
	}

	public CodeSystemCreateRequestBuilder setToolingId(String toolingId) {
		this.toolingId = toolingId;
		return getSelf();
	}
	
	/**
	 * @deprecated - replaced by {@link #setDependencies(java.util.List)}, will be removed in 9.0
	 * @param extensionOf
	 * @return
	 */
	public CodeSystemCreateRequestBuilder setExtensionOf(ResourceURI extensionOf) {
		this.extensionOf = extensionOf;
		return getSelf();
	}
	
	/**
	 * @deprecated - replaced by {@link #setDependencies(java.util.List)}, will be removed in 9.0
	 * @param upgradeOf
	 * @return
	 */
	public CodeSystemCreateRequestBuilder setUpgradeOf(ResourceURI upgradeOf) {
		this.upgradeOf = upgradeOf;
		return getSelf();
	}

	@Override
	public CodeSystemCreateRequest createResourceRequest() {
		return new CodeSystemCreateRequest();
	}
	
	@Override
	protected void init(CodeSystemCreateRequest req) {
		super.init(req);
		req.setToolingId(toolingId);
		req.setExtensionOf(extensionOf);
		req.setUpgradeOf(upgradeOf);
	}
}
