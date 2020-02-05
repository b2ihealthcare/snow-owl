/*
 * Copyright 2018-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.reasoner.request;

import com.b2international.snowowl.core.authorization.RepositoryAccessControl;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.request.GetResourceRequest;
import com.b2international.snowowl.identity.domain.Permission;
import com.b2international.snowowl.snomed.reasoner.domain.ClassificationTask;

/**
 * Retrieves information about a single classification task using its
 * identifier.
 * 
 * @since 7.0
 */
final class ClassificationGetRequest 
		extends GetResourceRequest<ClassificationSearchRequestBuilder, RepositoryContext, ClassificationTask>
		implements RepositoryAccessControl {

	ClassificationGetRequest(final String classificationId) {
		super(classificationId);
	}

	@Override
	protected ClassificationSearchRequestBuilder createSearchRequestBuilder() {
		return new ClassificationSearchRequestBuilder();
	}

	@Override
	public String getOperation() {
		return Permission.CLASSIFY;
	}
	
}
