/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.b2international.snowowl.core.events.Request;

/**
 * @since 4.5
 */
final class DeleteRequest implements Request<TransactionContext, Void> {

	@NotNull
	private String componentId;
	
	@NotNull
	private Class<? extends EObject> type;
	
	@NotNull
	private Boolean force;

	DeleteRequest(String componentId, Class<? extends EObject> type, Boolean force) {
		this.componentId = componentId;
		this.type = type;
		this.force = force;
	}
	
	@Override
	public Void execute(TransactionContext context) {
		context.delete(context.lookup(componentId, type), force);
		return null;
	}
	
}
