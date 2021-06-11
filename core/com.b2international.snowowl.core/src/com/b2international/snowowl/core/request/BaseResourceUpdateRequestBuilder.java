/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.core.context.ResourceRepositoryTransactionRequestBuilder;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;

/**
 * @since 8.0
 */
public abstract class BaseResourceUpdateRequestBuilder<RB extends BaseResourceUpdateRequestBuilder<RB, R>, R extends BaseResourceUpdateRequest>
		extends BaseRequestBuilder<RB, TransactionContext, Boolean>
		implements ResourceRepositoryTransactionRequestBuilder<Boolean> {

	protected final String resourceId;

	protected String url;
	protected String title;
	protected String language;
	protected String description;
	protected String status;
	protected String copyright;
	protected String owner;
	protected String contact;
	protected String usage;
	protected String purpose;
	protected String bundleId;

	protected BaseResourceUpdateRequestBuilder(final String uniqueId) {
		super();
		this.resourceId = uniqueId;
	}

	public RB setUrl(String url) {
		this.url = url;
		return getSelf();
	}

	public RB setTitle(String title) {
		this.title = title;
		return getSelf();
	}

	public RB setLanguage(String language) {
		this.language = language;
		return getSelf();
	}

	public RB setDescription(String description) {
		this.description = description;
		return getSelf();
	}

	public RB setStatus(String status) {
		this.status = status;
		return getSelf();
	}

	public RB setCopyright(String copyright) {
		this.copyright = copyright;
		return getSelf();
	}

	public RB setOwner(String owner) {
		this.owner = owner;
		return getSelf();
	}

	public RB setContact(String contact) {
		this.contact = contact;
		return getSelf();
	}

	public RB setUsage(String usage) {
		this.usage = usage;
		return getSelf();
	}

	public RB setPurpose(String purpose) {
		this.purpose = purpose;
		return getSelf();
	}

	public RB setBundleId(String bundleId) {
		this.bundleId = bundleId;
		return getSelf();
	}

	public abstract R createResourceRequest();

	@Override
	protected final Request<TransactionContext, Boolean> doBuild() {
		final R req = createResourceRequest();

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
		req.bundleId = bundleId;

		return req;
	}
}