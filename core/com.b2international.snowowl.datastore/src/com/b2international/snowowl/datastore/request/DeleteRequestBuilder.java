/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.request;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.index.RevisionDocument;

/**
 * <i>Builder</i> class to build a generic requests responsible for deleting a SNOMED CT component. This class should be instantiated from the
 * corresponding static method on the central SnomedRequests class.
 * 
 * @since 4.5
 */
public class DeleteRequestBuilder extends BaseRequestBuilder<DeleteRequestBuilder, TransactionContext, Boolean>
		implements TransactionalRequestBuilder<Boolean> {

	private final String componentId;
	private final Class<? extends RevisionDocument> type;
	private Boolean force = Boolean.FALSE;

	public DeleteRequestBuilder(String componentId, Class<? extends RevisionDocument> type) {
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
	public DeleteRequestBuilder force(boolean force) {
		this.force = force;
		return getSelf();
	}

	@Override
	protected Request<TransactionContext, Boolean> doBuild() {
		return new DeleteRequest(componentId, type, force);
	}

}
