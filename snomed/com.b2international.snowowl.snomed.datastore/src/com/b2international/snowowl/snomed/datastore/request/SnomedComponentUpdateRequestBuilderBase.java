/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;

/**
 * @since 7.11
 * @param <B>
 */
public abstract class SnomedComponentUpdateRequestBuilderBase<B extends SnomedComponentUpdateRequestBuilderBase<B, R>, R extends SnomedComponentUpdateRequestBase> 
		extends BaseRequestBuilder<B, TransactionContext, Boolean>
		implements SnomedTransactionalRequestBuilder<Boolean> {

	protected final String componentId;
	
	protected Boolean force = Boolean.FALSE;
	
	public SnomedComponentUpdateRequestBuilderBase(String componentId) {
		this.componentId = componentId;
	}
	
	public B force(Boolean force) {
		this.force = force;
		return getSelf();
	}
	
	@Override
	protected final Request<TransactionContext, Boolean> doBuild() {
		final R req = create(componentId);
		init(req);
		return req;
	}
	
	protected abstract R create(String componentId);
	
	@OverridingMethodsMustInvokeSuper
	protected void init(R req) {
		req.setForce(force);
	}
	
}
