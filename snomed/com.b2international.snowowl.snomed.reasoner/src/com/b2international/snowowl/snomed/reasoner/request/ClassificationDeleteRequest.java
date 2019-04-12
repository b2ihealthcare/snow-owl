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

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.snomed.reasoner.classification.ClassificationTracker;

/**
 * Marks a classification and related items as deleted in the index. "Garbage
 * collection" is performed separately.
 * 
 * @since 7.0
 */
final class ClassificationDeleteRequest implements Request<RepositoryContext, Boolean> {

	@NotEmpty
	private final String classificationId;

	ClassificationDeleteRequest(final String classificationId) {
		this.classificationId = classificationId;
	}

	@Override
	public Boolean execute(final RepositoryContext context) {
		final ClassificationTracker tracker = context.service(ClassificationTracker.class);
		tracker.classificationDeleted(classificationId);
		return Boolean.TRUE;
	}
}
