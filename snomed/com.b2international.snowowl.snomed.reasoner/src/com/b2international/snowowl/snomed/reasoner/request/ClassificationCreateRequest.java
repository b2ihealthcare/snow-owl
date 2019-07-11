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

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.AsyncRequest;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.request.job.JobRequests;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.snomed.reasoner.classification.ClassificationSchedulingRule;
import com.b2international.snowowl.snomed.reasoner.classification.ClassificationTracker;

/**
 * Signals the classification tracker that a classification run is about to
 * start, then schedules a remote job for the actual work.
 * 
 * @since 7.0
 */
final class ClassificationCreateRequest implements Request<BranchContext, String> {

	private static final long SCHEDULE_TIMEOUT_MILLIS = TimeUnit.MINUTES.toMillis(1L);

	@NotEmpty
	private String classificationId;

	@NotEmpty
	private String reasonerId;

	@NotEmpty
	private String userId;

	@NotNull
	private List<SnomedConcept> additionalConcepts;

	@NotNull
	private String parentLockContext;

	private boolean equivalenceCheckOnly;

	ClassificationCreateRequest() {}

	void setClassificationId(final String classificationId) {
		this.classificationId = classificationId;
	}

	void setReasonerId(final String reasonerId) {
		this.reasonerId = reasonerId;
	}

	void setUserId(final String userId) {
		this.userId = userId;
	}

	void setAdditionalConcepts(final List<SnomedConcept> additionalConcepts) {
		this.additionalConcepts = additionalConcepts;
	}

	void setParentLockContext(final String parentLockContext) {
		this.parentLockContext = parentLockContext;
	}

	void setEquivalenceCheckOnly(final boolean equivalenceCheckOnly) {
		this.equivalenceCheckOnly = equivalenceCheckOnly;
	}

	@Override
	public String execute(final BranchContext context) {
		final String repositoryId = context.id();
		final Branch branch = context.branch();
		final ClassificationTracker tracker = context.service(ClassificationTracker.class);
		final SnomedCoreConfiguration config = context.service(SnomedCoreConfiguration.class);

		tracker.classificationScheduled(classificationId, reasonerId, userId, branch.path());

		final AsyncRequest<Boolean> jobRequest = new ClassificationJobRequestBuilder()
				.setReasonerId(reasonerId)
				.setParentLockContext(parentLockContext)
				.setEquivalenceCheckOnly(equivalenceCheckOnly)
				.addAllConcepts(additionalConcepts)
				.build(repositoryId, branch.path());
		
		final ClassificationSchedulingRule rule = ClassificationSchedulingRule.create(
				config.getMaxReasonerCount(), 
				repositoryId, 
				branch.path());

		return JobRequests.prepareSchedule()
				.setId(classificationId)
				.setUser(userId)
				.setRequest(jobRequest)
				.setDescription(String.format("Classifying the ontology on %s", branch.path()))
				.setSchedulingRule(rule)
				.buildAsync()
				.get(SCHEDULE_TIMEOUT_MILLIS);
	}
}
