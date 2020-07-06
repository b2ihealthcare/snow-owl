/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import javax.validation.constraints.NotNull;

import com.b2international.snowowl.core.authorization.BranchAccessControl;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.repository.RevisionDocument;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 4.5
 */
public final class DeleteRequest implements Request<TransactionContext, Boolean>, BranchAccessControl {

	@JsonProperty
	@NotNull
	private String componentId;
	
	@NotNull
	private Class<? extends RevisionDocument> type;
	
	@NotNull
	private Boolean force;

	DeleteRequest(String componentId, Class<? extends RevisionDocument> type, Boolean force) {
		this.componentId = componentId;
		this.type = type;
		this.force = force;
	}
	
	@Override
	public Boolean execute(TransactionContext context) {
		context.delete(context.lookup(componentId, type), force);
		return Boolean.TRUE;
	}

	public String getComponentId() {
		return componentId;
	}
	
	@Override
	public String getOperation() {
		return Permission.OPERATION_EDIT;
	}
	
}
