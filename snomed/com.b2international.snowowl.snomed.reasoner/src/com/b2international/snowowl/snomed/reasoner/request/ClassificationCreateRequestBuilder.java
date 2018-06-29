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

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;
import java.util.UUID;

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.AsyncRequest;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions;
import com.b2international.snowowl.datastore.request.AllowedHealthStates;
import com.b2international.snowowl.datastore.request.BranchRequest;
import com.b2international.snowowl.datastore.request.HealthCheckingRequest;
import com.b2international.snowowl.datastore.request.RepositoryRequest;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;

/**
 * @since 5.7
 */
public final class ClassificationCreateRequestBuilder 
		extends BaseRequestBuilder<ClassificationCreateRequestBuilder, BranchContext, String>
		implements AllowedHealthStates {

	private String classificationId = UUID.randomUUID().toString();
	private String reasonerId;
	private String userId;
	private final List<SnomedConcept> additionalConcepts = newArrayList();
	private String parentLockContextDescription = DatastoreLockContextDescriptions.CLASSIFY_WITH_REVIEW;

	ClassificationCreateRequestBuilder() {
	}

	public ClassificationCreateRequestBuilder setClassificationId(final String classificationId) {
		this.classificationId = classificationId;
		return this;
	}

	public ClassificationCreateRequestBuilder setReasonerId(final String reasonerId) {
		this.reasonerId = reasonerId;
		return this;
	}

	public ClassificationCreateRequestBuilder setUserId(final String userId) {
		this.userId = userId;
		return this;
	}

	public ClassificationCreateRequestBuilder addConcept(final SnomedConcept additionalConcept) {
		this.additionalConcepts.add(additionalConcept);
		return this;
	}

	public ClassificationCreateRequestBuilder addAllConcepts(final List<SnomedConcept> additionalConcepts) {
		this.additionalConcepts.addAll(additionalConcepts);
		return this;
	}

	public ClassificationCreateRequestBuilder setParentLockContextDescription(final String parentLockContextDescription) {
		this.parentLockContextDescription = parentLockContextDescription;
		return this;
	}

	@Override
	protected Request<BranchContext, String> doBuild() {
		final ClassificationCreateRequest request = new ClassificationCreateRequest();
		request.setClassificationId(classificationId);
		request.setReasonerId(reasonerId);
		request.setUserId(userId);
		request.setAdditionalConcepts(additionalConcepts);
		request.setParentLockContextDescription(parentLockContextDescription);
		return request;
	}

	public AsyncRequest<String> build(final String repositoryId, final String branch) {
		return new AsyncRequest<>(
			new RepositoryRequest<>(repositoryId,
				new HealthCheckingRequest<>(
					new BranchRequest<>(branch, build()), 
					allowedHealthstates()
				)
			)
		);
	}
}
