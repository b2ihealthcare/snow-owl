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

import javax.validation.constraints.NotNull;

import org.eclipse.emf.ecore.EObject;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.BaseRequest;

/**
 * @since 4.5
 */
final class DeleteRequest extends BaseRequest<TransactionContext, Void> {

	@NotNull
	private String componentId;
	
	@NotNull
	private Class<? extends EObject> type;

	public DeleteRequest(String componentId, Class<? extends EObject> type) {
		this.componentId = componentId;
		this.type = type;
	}
	
	@Override
	public Void execute(TransactionContext context) {
		context.delete(context.lookup(componentId, type));
		return null;
	}
	
	@Override
	protected Class<Void> getReturnType() {
		return Void.class;
	}

}
