/*
 * Copyright 2017-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;
import java.util.UUID;

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions;
import com.b2international.snowowl.datastore.request.BranchRequestBuilder;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;

/**
 * @since 5.7
 */
public final class ClassificationCreateRequestBuilder 
		extends BaseRequestBuilder<ClassificationCreateRequestBuilder, BranchContext, String>
		implements BranchRequestBuilder<String> {

	private String classificationId = UUID.randomUUID().toString();
	private String reasonerId;
	private String userId;
	private final List<SnomedConcept> additionalConcepts = newArrayList();
	private String parentLockContext = DatastoreLockContextDescriptions.ROOT;
	private boolean equivalenceCheckOnly = false;

	ClassificationCreateRequestBuilder() {}

	public ClassificationCreateRequestBuilder setClassificationId(final String classificationId) {
		this.classificationId = classificationId;
		return getSelf();
	}

	public ClassificationCreateRequestBuilder setReasonerId(final String reasonerId) {
		this.reasonerId = reasonerId;
		return getSelf();
	}

	public ClassificationCreateRequestBuilder setUserId(final String userId) {
		this.userId = userId;
		return getSelf();
	}

	public ClassificationCreateRequestBuilder addConcept(final SnomedConcept additionalConcept) {
		this.additionalConcepts.add(additionalConcept);
		return getSelf();
	}

	public ClassificationCreateRequestBuilder addAllConcepts(final List<SnomedConcept> additionalConcepts) {
		this.additionalConcepts.addAll(additionalConcepts);
		return getSelf();
	}
	
	public ClassificationCreateRequestBuilder setParentLockContext(final String parentLockContext) {
		this.parentLockContext = parentLockContext;
		return getSelf();
	}
	
	public ClassificationCreateRequestBuilder setEquivalenceCheckOnly(final boolean equivalenceCheckOnly) {
		this.equivalenceCheckOnly = equivalenceCheckOnly;
		return getSelf();
	}

	@Override
	protected Request<BranchContext, String> doBuild() {
		final ClassificationCreateRequest request = new ClassificationCreateRequest();
		request.setClassificationId(classificationId);
		request.setReasonerId(reasonerId);
		request.setUserId(userId);
		request.setAdditionalConcepts(additionalConcepts);
		request.setParentLockContext(parentLockContext);
		request.setEquivalenceCheckOnly(equivalenceCheckOnly);
		return request;
	}
}
