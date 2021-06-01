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
package com.b2international.snowowl.core.request.resource;

import com.b2international.snowowl.core.context.ResourceRepositoryTransactionRequestBuilder;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.repository.RevisionDocument;
import com.b2international.snowowl.core.request.DeleteRequest;

/**
 * @since 8.0
 */
public final class ResourceDeleteRequestBuilder 
		extends BaseRequestBuilder<ResourceDeleteRequestBuilder, TransactionContext, Boolean> 
		implements ResourceRepositoryTransactionRequestBuilder<Boolean> {

	protected final String componentId;
	protected final Class<? extends RevisionDocument> type;
	protected Boolean force = Boolean.FALSE;
	
	public ResourceDeleteRequestBuilder(String componentId, Class<? extends RevisionDocument> type) {
		super();
		this.componentId = componentId;
		this.type = type;
	}
	
	/**
	 * Forces the deletion of the component if the value is <code>true</code>.
	 * 
	 * @param force
	 *            - whether to force or not the deletion
	 * @return
	 */
	public ResourceDeleteRequestBuilder force(boolean force) {
		this.force = force;
		return getSelf();
	}
	
	@Override
	protected Request<TransactionContext, Boolean> doBuild() {
		return new DeleteRequest(componentId, type, force);
	}

}
