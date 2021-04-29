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
import com.b2international.snowowl.core.id.IDs;
import com.b2international.snowowl.core.request.TransactionalRequestBuilder;

/**
 * @since 4.7
 */
public final class CodeSystemCreateRequestBuilder extends BaseRequestBuilder<CodeSystemCreateRequestBuilder, TransactionContext, String> implements TransactionalRequestBuilder<String> {

	private String id = IDs.randomBase64UUID();
	
	private String url;
	private String title;
	private String language;
	private String description;
	private String status;
	private String copyright;
	private String owner;
	private String contact;
	private String usage;
	private String purpose;
	
	// specialized resource fields
	private String oid;
	private String branchPath;
	private String toolingId;
	private ResourceURI extensionOf;
	private ResourceURI upgradeOf;
	private Map<String, Object> settings;

	CodeSystemCreateRequestBuilder() {}

	public CodeSystemCreateRequestBuilder setId(String id) {
		this.id = id;
		return getSelf();
	}
	
	public CodeSystemCreateRequestBuilder setUrl(String url) {
		this.url = url;
		return getSelf();
	}
	
	public CodeSystemCreateRequestBuilder setTitle(String title) {
		this.title = title;
		return getSelf();
	}
	
	public CodeSystemCreateRequestBuilder setLanguage(String language) {
		this.language = language;
		return getSelf();
	}
	
	public CodeSystemCreateRequestBuilder setDescription(String description) {
		this.description = description;
		return getSelf();
	}
	
	public CodeSystemCreateRequestBuilder setStatus(String status) {
		this.status = status;
		return getSelf();
	}
	
	public CodeSystemCreateRequestBuilder setCopyright(String copyright) {
		this.copyright = copyright;
		return getSelf();
	}
	
	public CodeSystemCreateRequestBuilder setOwner(String owner) {
		this.owner = owner;
		return getSelf();
	}
	
	public CodeSystemCreateRequestBuilder setContact(String contact) {
		this.contact = contact;
		return getSelf();
	}
	
	public CodeSystemCreateRequestBuilder setUsage(String usage) {
		this.usage = usage;
		return getSelf();
	}
	
	public CodeSystemCreateRequestBuilder setPurpose(String purpose) {
		this.purpose = purpose;
		return getSelf();
	}
	
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
	
	@Override
	protected Request<TransactionContext, String> doBuild() {
		final CodeSystemCreateRequest req = new CodeSystemCreateRequest();
		req.id = id;
		req.url = url;
		req.title = title;
		req.language = language;
		req.description = description;
		req.status = status;
		req.copyright = copyright;
		req.owner = owner;
		req.contact = contact;
		req.usage = usage;
		req.purpose = purpose;
		req.oid = oid;
		req.branchPath = branchPath;
		req.toolingId = toolingId;
		req.extensionOf = extensionOf;
		req.upgradeOf = upgradeOf;
		req.settings = settings;
		return req;
	}
}
