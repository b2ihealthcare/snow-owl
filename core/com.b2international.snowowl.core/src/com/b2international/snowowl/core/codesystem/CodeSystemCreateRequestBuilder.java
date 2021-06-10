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
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.request.BaseResourceCreateRequestBuilder;

/**
 * @since 4.7
 */
public final class CodeSystemCreateRequestBuilder extends BaseResourceCreateRequestBuilder<CodeSystemCreateRequestBuilder> {

	// specialized resource fields
	private String oid;
	private String branchPath;
	private String toolingId;
	private ResourceURI extensionOf;
	private ResourceURI upgradeOf;
	private Map<String, Object> settings;

	CodeSystemCreateRequestBuilder() {}

	public CodeSystemCreateRequestBuilder setOid(String oid) {
		this.oid = oid;
		return getSelf();
	}
	
	public CodeSystemCreateRequestBuilder setBranchPath(String branchPath) {
		this.branchPath = branchPath;
		return getSelf();
	}
	
	public CodeSystemCreateRequestBuilder setToolingId(String toolingId) {
		this.toolingId = toolingId;
		return getSelf();
	}
	
	public CodeSystemCreateRequestBuilder setExtensionOf(ResourceURI extensionOf) {
		this.extensionOf = extensionOf;
		return getSelf();
	}
	
	public CodeSystemCreateRequestBuilder setUpgradeOf(ResourceURI upgradeOf) {
		this.upgradeOf = upgradeOf;
		return getSelf();
	}
	
	public CodeSystemCreateRequestBuilder setSettings(Map<String, Object> settings) {
		this.settings = settings;
		return getSelf();
	}
	
	@Override
	protected Request<TransactionContext, String> doBuild() {
		final CodeSystemCreateRequest req = new CodeSystemCreateRequest();
		req.setId(id);
		req.setUrl(url);
		req.setTitle(title);
		req.setLanguage(language);
		req.setDescription(description);
		req.setStatus(status);
		req.setCopyright(copyright);
		req.setOwner(owner);
		req.setContact(contact);
		req.setUsage(usage);
		req.setPurpose(purpose);
		req.setBundleId(bundleId);

		req.oid = oid;
		req.branchPath = branchPath;
		req.toolingId = toolingId;
		req.extensionOf = extensionOf;
		req.upgradeOf = upgradeOf;
		req.settings = settings;
		return req;
	}
}
