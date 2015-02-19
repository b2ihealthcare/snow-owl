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
package com.b2international.snowowl.datastore.editor.operation.status;

import com.b2international.snowowl.core.api.IBranchPath;
import com.google.common.collect.Multimap;

/**
 * @since 2.9
 */
public class ComponentDeletionStatus extends OperationExecutionStatus {

	private static final long serialVersionUID = -7207399324585524981L;
	
	/* mapped component type names to component ids */
	private final Multimap<String, String> deletedComponentMap;
	private final String userId;
	private final IBranchPath branchPath;
	
	public ComponentDeletionStatus(String operationTypName, String message, Multimap<String, String> deletedComponentMap, String userId, IBranchPath branchPath) {
		super(operationTypName, "<unspecified plugin>", OK, message, null);
		this.deletedComponentMap = deletedComponentMap;
		this.userId = userId;
		this.branchPath = branchPath;
	}
	
	public boolean containsDeletedComponent(Class<?> type, String componentId){
		String typeName = type.getName();
		return deletedComponentMap.containsEntry(typeName, componentId);
	}
	
	public Multimap<String, String> getDeletedComponentMap() {
		return deletedComponentMap;
	}
	
	public String getUserId() {
		return userId;
	}

	public IBranchPath getBranchPath() {
		return branchPath;
	}
	
}