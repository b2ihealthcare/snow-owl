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

import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.request.job.JobRequests;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.reasoner.domain.ClassificationStatus;
import com.b2international.snowowl.snomed.reasoner.index.ClassificationRepository;
import com.b2international.snowowl.snomed.reasoner.index.ClassificationTaskDocument;

/**
 * @since 7.0
 */
final class ClassificationCreateRequest implements Request<BranchContext, String> {

	@NotEmpty
	private String classificationId;
	
	@NotEmpty
	private String reasonerId;
	
	@NotEmpty
	private String userId;
	
	@NotNull
	private List<SnomedConcept> additionalConcepts;
	
	@NotEmpty
	private String parentLockContextDescription;

	ClassificationCreateRequest() {
	}

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

	void setParentLockContextDescription(final String parentLockContextDescription) {
		this.parentLockContextDescription = parentLockContextDescription;
	}

	@Override
	public String execute(final BranchContext context) {
		final Branch branch = context.branch();
		final ClassificationRepository repository = context.service(ClassificationRepository.class);
		
		final ClassificationTaskDocument classificationRun = ClassificationTaskDocument.builder()
				.id(classificationId)
				.reasonerId(reasonerId)
				.userId(userId)
				.branch(branch.path())
				.timestamp(branch.headTimestamp())
				.creationDate(Dates.todayGmt())
				.status(ClassificationStatus.SCHEDULED)
				.build();

		repository.save(classificationId, classificationRun);

		final ClassificationRunRequest jobRequest = new ClassificationRunRequest();
		jobRequest.setBranch(branch.path());
		jobRequest.setReasonerId(reasonerId);
		jobRequest.setAdditionalConcepts(additionalConcepts);
		jobRequest.setParentLockContextDescription(parentLockContextDescription);

		// TODO: scheduling rule to limit concurrent classifications
		return JobRequests.prepareSchedule()
				.setId(classificationId)
				.setUser(userId)
				.setRequest(jobRequest)
				.setDescription(String.format("Classifying the ontology on %s", branch))
				.build()
				.execute(context);
	}
}
