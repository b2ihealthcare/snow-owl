/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Map;

import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.request.TransactionalRequestBuilder;

/**
 * @since 4.7
 */
public final class CodeSystemUpdateRequestBuilder extends BaseRequestBuilder<CodeSystemUpdateRequestBuilder, TransactionContext, Boolean> implements TransactionalRequestBuilder<Boolean> {

	private final String resourceId;

	private String title;
	private String language;
	private String branchPath;
	private ResourceURI extensionOf;
	private Map<String, Object> settings;

	CodeSystemUpdateRequestBuilder(final String uniqueId) {
		super();
		this.resourceId = uniqueId;
	}

	public CodeSystemUpdateRequestBuilder setTitle(String title) {
		this.title = title;
		return getSelf();
	}

	public CodeSystemUpdateRequestBuilder setLanguage(String language) {
		this.language = language;
		return getSelf();
	}

	public CodeSystemUpdateRequestBuilder setBranchPath(String branchPath) {
		this.branchPath = branchPath;
		return getSelf();
	}

	public CodeSystemUpdateRequestBuilder setExtensionOf(ResourceURI extensionOf) {
		this.extensionOf = extensionOf;
		return getSelf();
	}
	
	public CodeSystemUpdateRequestBuilder setSettings(Map<String, Object> settings) {
		this.settings = settings;
		return getSelf();
	}

	@Override
	protected Request<TransactionContext, Boolean> doBuild() {
		final CodeSystemUpdateRequest req = new CodeSystemUpdateRequest(resourceId);
		req.setTitle(title);
		req.setLanguage(language);
		req.setBranchPath(branchPath);
		req.setExtensionOf(extensionOf);
		req.setSettings(settings);
		return req;
	}
}
