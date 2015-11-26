/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.server.request;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;

/**
 * @since 4.5
 */
public abstract class BaseSnomedComponentUpdateRequestBuilder<B extends BaseSnomedComponentUpdateRequestBuilder<B, R>, R extends BaseSnomedComponentUpdateRequest> extends BaseSnomedTransactionalRequestBuilder<B, Void> {

	private final String componentId;
	
	private String moduleId;
	private Boolean active;

	protected BaseSnomedComponentUpdateRequestBuilder(String repositoryId, String componentId) {
		super(repositoryId);
		this.componentId = componentId;
	}
	
	public final B setActive(Boolean active) {
		this.active = active;
		return getSelf();
	}
	
	public final B setModuleId(String moduleId) {
		this.moduleId = moduleId;
		return getSelf();
	}
	
	@Override
	protected final Request<TransactionContext, Void> doBuild() {
		final R req = create(componentId);
		init(req);
		return req;
	}

	@OverridingMethodsMustInvokeSuper
	protected void init(R req) {
		req.setActive(active);
		req.setModuleId(moduleId);
	}

	protected abstract R create(String componentId);
	
}
