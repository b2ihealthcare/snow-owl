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
package com.b2international.snowowl.datastore.request;

import org.eclipse.emf.ecore.EObject;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;

/**
 * <i>Builder</i> class to build a generic requests responsible for deleting a SNOMED CT component.
 * This class should be instantiated from the corresponding static method on the central SnomedRequests class.
 * 
 * @since 4.5
 */
public final class DeleteRequestBuilder extends BaseTransactionalRequestBuilder<DeleteRequestBuilder, Void> {

	private String componentId;
	private Class<? extends EObject> type;
	
	DeleteRequestBuilder(String repositoryId) {
		super(repositoryId);
	}
	
	public DeleteRequestBuilder setComponentId(String componentId) {
		this.componentId = componentId;
		return getSelf();
	}
	
	public DeleteRequestBuilder setType(Class<? extends EObject> type) {
		this.type = type;
		return getSelf();
	}
	
	@Override
	protected Request<TransactionContext, Void> doBuild() {
		return new DeleteRequest(componentId, type);
	}

}
