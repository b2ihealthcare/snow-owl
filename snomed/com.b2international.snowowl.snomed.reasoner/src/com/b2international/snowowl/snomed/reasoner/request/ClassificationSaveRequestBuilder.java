/*
 * Copyright 2017-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.request.RepositoryRequestBuilder;

/**
 * @since 5.7
 */
public final class ClassificationSaveRequestBuilder 
		extends BaseRequestBuilder<ClassificationSaveRequestBuilder, RepositoryContext, String> 
		implements RepositoryRequestBuilder<String> {

	private final String classificationId;
	private final String userId;

	ClassificationSaveRequestBuilder(final String classificationId, final String userId) {
		this.classificationId = classificationId;
		this.userId = userId;
	}

	@Override
	protected Request<RepositoryContext, String> doBuild() {
		return new ClassificationSaveRequest(classificationId, userId);
	}
}
