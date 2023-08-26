/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.collection;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.request.resource.BaseTerminologyResourceCreateRequest;

/**
 * @since 9.0
 */
final class TerminologyResourceCollectionCreateRequest extends BaseTerminologyResourceCreateRequest {

	private static final long serialVersionUID = 1L;

	@NotEmpty
	private String childResourceType;

	void setChildResourceType(String childResourceType) {
		this.childResourceType = childResourceType;
	}
	
	@Override
	protected String getResourceType() {
		return TerminologyResourceCollection.RESOURCE_TYPE;
	}
	
	@Override
	protected void preExecute(TransactionContext context) {
		// TODO support fetching toolingId from the first dependency if not defined? or just report error that is not defined?
		var toolingId = getToolingId();
		
		var terminologyToolingSupport = context.service(TerminologyResourceCollectionToolingSupport.Registry.class).getToolingSupport(toolingId, childResourceType);
		
		// validate dependency array for tooling specific requirements
		terminologyToolingSupport.validateRequiredDependencies(getDependencies());

		// FIXME call preExecute after validating child resource type as the method has a side effect on creating a new branch, which is not rolled back in case of an error 
		super.preExecute(context);
	}
	
}
