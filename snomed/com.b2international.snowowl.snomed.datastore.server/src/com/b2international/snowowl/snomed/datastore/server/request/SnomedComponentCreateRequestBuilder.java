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
import com.b2international.snowowl.core.events.RequestBuilder;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.core.domain.ISnomedComponent;
import com.b2international.snowowl.snomed.core.domain.IdGenerationStrategy;
import com.b2international.snowowl.snomed.core.domain.NamespaceIdGenerationStrategy;
import com.b2international.snowowl.snomed.core.domain.UserIdGenerationStrategy;

/**
 * @since 4.5
 * @param <B> - a subtype of {@link ISnomedComponent} 
 */
public abstract class SnomedComponentCreateRequestBuilder<B extends SnomedComponentCreateRequestBuilder<B, R>, R extends ISnomedComponent> implements RequestBuilder<TransactionContext, R> {
	
	private String moduleId;
	private IdGenerationStrategy idGenerationStrategy;
	
	public final B setId(String id) {
		this.idGenerationStrategy = new UserIdGenerationStrategy(id);
		return getSelf();
	}
	
	public final B setIdFromNamespace(String namespace) {
		this.idGenerationStrategy = new NamespaceIdGenerationStrategy(ComponentCategory.CONCEPT, namespace);
		return getSelf();
	}
	
	public final B setId(IdGenerationStrategy idGenerationStrategy) {
		this.idGenerationStrategy = idGenerationStrategy;
		return getSelf();
	}
	
	public final B setModuleId(String moduleId) {
		this.moduleId = moduleId;
		return getSelf();
	}

	protected final B getSelf() {
		return (B) this;
	}
	
	@Override
	public final Request<TransactionContext, R> build() {
		final BaseSnomedComponentCreateRequest<R> req = createRequest();
		req.setIdGenerationStrategy(idGenerationStrategy);
		req.setModuleId(moduleId);
		init(req);
		return req;
	}

	@OverridingMethodsMustInvokeSuper
	protected abstract void init(BaseSnomedComponentCreateRequest<R> req);

	protected abstract BaseSnomedComponentCreateRequest<R> createRequest();
	
}
