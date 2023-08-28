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

import com.b2international.commons.CompareUtils;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.Resource;
import com.b2international.snowowl.core.bundle.Bundle;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.request.resource.BaseTerminologyResourceCreateRequest;

/**
 * @since 9.0
 */
final class TerminologyResourceCollectionCreateRequest extends BaseTerminologyResourceCreateRequest {

	private static final long serialVersionUID = 1L;

	@Override
	protected String getResourceType() {
		return TerminologyResourceCollection.RESOURCE_TYPE;
	}
	
	@Override
	protected void preExecute(TransactionContext context) {
		// TODO support fetching toolingId from the first dependency if not defined? or just report error that is not defined?
		var toolingId = getToolingId();
		
		var terminologyToolingSupports = context.service(TerminologyResourceCollectionToolingSupport.Registry.class).getAllByToolingId(toolingId);

		if (CompareUtils.isEmpty(terminologyToolingSupports)) {
			throw new BadRequestException("ToolingId '%s' is not supported for resource collections.", toolingId);
		}

		// FIXME call preExecute after validating child resource type as the method has a side effect on creating a new branch, which is not rolled back in case of an error 
		super.preExecute(context);
	}
	
	@Override
	protected void checkParentCollection(TransactionContext context, Resource parentCollection) {
		if (parentCollection instanceof TerminologyResourceCollection) {
			throw new BadRequestException("Nesting terminology collection resources is not supported. Use regular bundles to organize content.")
				.withDeveloperMessage("'bundleId' points to the terminology resource collection, '%s'.", parentCollection.getId());
		} else if (parentCollection instanceof Bundle) {
			// allow bundle parents without any validation
		} else {
			super.checkParentCollection(context, parentCollection);
		}
	}
	
}
