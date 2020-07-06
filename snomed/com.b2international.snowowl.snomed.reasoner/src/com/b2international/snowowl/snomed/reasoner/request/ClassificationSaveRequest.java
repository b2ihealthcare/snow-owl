/*
 * Copyright 2017-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.authorization.RepositoryAccessControl;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.AsyncRequest;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.identity.User;
import com.b2international.snowowl.core.jobs.JobRequests;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.snomed.reasoner.domain.ClassificationStatus;
import com.b2international.snowowl.snomed.reasoner.domain.ClassificationTask;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

/**
 * Runs pre-save checks on the given classification task, signals the tracker
 * that this task is about to be saved, and schedules a remote job to perform
 * the actual saving.
 * 
 * @since 5.7
 */
final class ClassificationSaveRequest implements Request<RepositoryContext, String>, RepositoryAccessControl {

	private static final long SCHEDULE_TIMEOUT_MILLIS = TimeUnit.MINUTES.toMillis(1L);
	
	// Also used in SaveJobRequest
	static final Set<ClassificationStatus> SAVEABLE_STATUSES = ImmutableSet.of(
			ClassificationStatus.COMPLETED, 
			ClassificationStatus.SAVE_FAILED);

	@JsonProperty
	@NotEmpty
	private String classificationId;

	@JsonProperty
	private String userId;
	
	@NotNull
	private String parentLockContext;

	@NotEmpty
	private String commitComment;

	// @Nullable
	private String moduleId;

	// @Nullable
	private String namespace;

	// @Nullable
	private String assignerType;
	
	private boolean fixEquivalences;

	private boolean handleConcreteDomains;


	ClassificationSaveRequest() {}
	
	void setClassificationId(final String classificationId) {
		this.classificationId = classificationId;
	}
	
	void setUserId(final String userId) {
		this.userId = userId;
	}
	
	void setParentLockContext(final String parentLockContext) {
		this.parentLockContext = parentLockContext;
	}
	
	void setCommitComment(final String commitComment) {
		this.commitComment = commitComment;
	}

	void setModuleId(final String moduleId) {
		this.moduleId = moduleId;
	}

	void setNamespace(final String namespace) {
		this.namespace = namespace;
	}
	
	void setAssignerType(final String assignerType) {
		this.assignerType = assignerType;
	}

	void setFixEquivalences(final boolean fixEquivalences) {
		this.fixEquivalences = fixEquivalences;
	}

	void setHandleConcreteDomains(final boolean handleConcreteDomains) {
		this.handleConcreteDomains = handleConcreteDomains;
	}

	@Override
	public String execute(final RepositoryContext context) {
		final Request<RepositoryContext, ClassificationTask> classificationRequest = ClassificationRequests
				.prepareGetClassification(classificationId)
				.build();
		
		final ClassificationTask classification = classificationRequest.execute(context);
		final String branchPath = classification.getBranch();
		
		final Request<RepositoryContext, Branch> branchRequest = RepositoryRequests.branching()
				.prepareGet(branchPath)
				.build();
		
		final Branch branch = branchRequest.execute(context);

		if (!SAVEABLE_STATUSES.contains(classification.getStatus())) {
			throw new BadRequestException("Classification '%s' is not in the expected state to start saving changes.", classificationId);
		}

		if (classification.getTimestamp() < branch.headTimestamp()) {
			throw new BadRequestException("Classification '%s' on branch '%s' is stale (classification timestamp: %s, head timestamp: %s).", 
					classificationId,
					branchPath,
					classification.getTimestamp(),
					branch.headTimestamp());
		}
		
		final String user = !Strings.isNullOrEmpty(userId) ? userId : context.service(User.class).getUsername();

		final AsyncRequest<?> saveRequest = new SaveJobRequestBuilder()
				.setClassificationId(classificationId)
				.setUserId(user)
				.setParentLockContext(parentLockContext)
				.setCommitComment(commitComment)
				.setModuleId(moduleId)
				.setNamespace(namespace)
				.setAssignerType(assignerType)
				.setFixEquivalences(fixEquivalences)
				.setHandleConcreteDomains(handleConcreteDomains)
				.build(context.id(), branchPath);

		return JobRequests.prepareSchedule()
				.setUser(userId)
				.setRequest(saveRequest)
				.setDescription(String.format("Saving classification changes on %s", branch.path()))
				.buildAsync()
				.get(context, SCHEDULE_TIMEOUT_MILLIS);
	}
	
	@Override
	public String getOperation() {
		return Permission.OPERATION_CLASSIFY;
	}
}
