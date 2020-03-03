/*
 * Copyright 2018-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions;
import com.b2international.snowowl.datastore.request.RevisionIndexRequestBuilder;

/**
 * @since 7.0
 */
public final class SaveJobRequestBuilder 
		extends BaseRequestBuilder<SaveJobRequestBuilder, BranchContext, Boolean>
		implements RevisionIndexRequestBuilder<Boolean> {

	private String classificationId;
	private String userId;
	private String parentLockContext = DatastoreLockContextDescriptions.ROOT;
	private String commitComment;
	private String moduleId;
	private String namespace;
	private String assignerType;
	private boolean fixEquivalences;
	private boolean handleConcreteDomains;

	public SaveJobRequestBuilder setClassificationId(final String classificationId) {
		this.classificationId = classificationId;
		return getSelf();
	}

	public SaveJobRequestBuilder setUserId(final String userId) {
		this.userId = userId;
		return getSelf();
	}
	
	public SaveJobRequestBuilder setParentLockContext(String parentLockContext) {
		this.parentLockContext = parentLockContext;
		return getSelf();
	}
	
	public SaveJobRequestBuilder setCommitComment(String commitComment) {
		this.commitComment = commitComment;
		return getSelf();
	}
	
	public SaveJobRequestBuilder setModuleId(String moduleId) {
		this.moduleId = moduleId;
		return getSelf();
	}
	
	public SaveJobRequestBuilder setNamespace(String namespace) {
		this.namespace = namespace;
		return getSelf();
	}
	
	public SaveJobRequestBuilder setAssignerType(String assignerType) {
		this.assignerType = assignerType;
		return getSelf();
	}
	
	public SaveJobRequestBuilder setFixEquivalences(boolean fixEquivalences) {
		this.fixEquivalences = fixEquivalences;
		return getSelf();
	}
	
	public SaveJobRequestBuilder setHandleConcreteDomains(boolean handleConcreteDomains) {
		this.handleConcreteDomains = handleConcreteDomains;
		return getSelf();
	}

	@Override
	protected Request<BranchContext, Boolean> doBuild() {
		SaveJobRequest request = new SaveJobRequest();
		request.setClassificationId(classificationId);
		request.setUserId(userId);
		request.setParentLockContext(parentLockContext);
		request.setCommitComment(commitComment);
		request.setModuleId(moduleId);
		request.setNamespace(namespace);
		request.setAssignerType(assignerType);
		request.setFixEquivalences(fixEquivalences);
		request.setHandleConcreteDomains(handleConcreteDomains);
		return request;
	}
}
