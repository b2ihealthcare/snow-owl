/*
 * Copyright 2021-2023 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.request;

import java.util.Map;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.b2international.snowowl.core.context.ResourceRepositoryTransactionRequestBuilder;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.id.IDs;

/**
 * @since 8.0
 */
public abstract class BaseResourceCreateRequestBuilder <RB extends BaseResourceCreateRequestBuilder<RB, R>, R extends BaseResourceCreateRequest>
		extends BaseRequestBuilder<RB, TransactionContext, String> 
		implements ResourceRepositoryTransactionRequestBuilder<String> {
	
	private String id;
	private String bundleId;
	
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
	private Map<String, Object> settings;
	
	public final RB setId(String id) {
		this.id = id;
		return getSelf();
	}
	
	public final RB setUrl(String url) {
		this.url = url;
		return getSelf();
	}
	
	public final RB setTitle(String title) {
		this.title = title;
		return getSelf();
	}
	
	public final RB setLanguage(String language) {
		this.language = language;
		return getSelf();
	}
	
	public final RB setDescription(String description) {
		this.description = description;
		return getSelf();
	}
	
	public final RB setStatus(String status) {
		this.status = status;
		return getSelf();
	}
	
	public final RB setCopyright(String copyright) {
		this.copyright = copyright;
		return getSelf();
	}
	
	public final RB setOwner(String owner) {
		this.owner = owner;
		return getSelf();
	}
	
	public final RB setContact(String contact) {
		this.contact = contact;
		return getSelf();
	}
	
	public final RB setUsage(String usage) {
		this.usage = usage;
		return getSelf();
	}
	
	public final RB setPurpose(String purpose) {
		this.purpose = purpose;
		return getSelf();
	}
	
	public final RB setBundleId(String bundleId) {
		this.bundleId = bundleId;
		return getSelf();
	}
	
	public final RB setSettings(Map<String, Object> settings) {
		this.settings = settings;
		return getSelf();
	}
	
	public abstract R createResourceRequest();
	
	@Override
	protected final Request<TransactionContext, String> doBuild() {
		final R req = createResourceRequest();
		init(req);
		return req;
	}

	@OverridingMethodsMustInvokeSuper
	protected void init(R req) {
		req.setId(id == null ? IDs.base62UUID() : id);
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
		req.setBundleId(bundleId == null ? IComponent.ROOT_ID : bundleId);
		req.setSettings(settings);
	}
}
