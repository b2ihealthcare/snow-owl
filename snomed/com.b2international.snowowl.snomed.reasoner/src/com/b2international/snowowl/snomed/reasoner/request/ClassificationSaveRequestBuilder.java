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

import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions;
import com.b2international.snowowl.datastore.request.RepositoryRequestBuilder;

/**
 * @since 5.7
 */
public final class ClassificationSaveRequestBuilder 
		extends BaseRequestBuilder<ClassificationSaveRequestBuilder, RepositoryContext, String> 
		implements RepositoryRequestBuilder<String> {

	private String classificationId;
	private String userId;
	private String parentLockContext = DatastoreLockContextDescriptions.ROOT;
	private String commitComment = "Classified ontology.";
	private String moduleId = null;
	private String namespace = null;
	private boolean fixEquivalences = true;
	private boolean handleConcreteDomains = true;
	
	ClassificationSaveRequestBuilder() { }
	
	public ClassificationSaveRequestBuilder setClassificationId(String classificationId) {
		this.classificationId = classificationId;
		return getSelf();
	}

	public ClassificationSaveRequestBuilder setUserId(String userId) {
		this.userId = userId;
		return getSelf();
	}
	
	public ClassificationSaveRequestBuilder setParentLockContext(String parentLockContext) {
		this.parentLockContext = parentLockContext;
		return getSelf();
	}
	
	public ClassificationSaveRequestBuilder setCommitComment(String commitComment) {
		this.commitComment = commitComment;
		return getSelf();
	}
	
	public ClassificationSaveRequestBuilder setModuleId(String moduleId) {
		this.moduleId = moduleId;
		return getSelf();
	}
	
	public ClassificationSaveRequestBuilder setNamespace(String namespace) {
		this.namespace = namespace;
		return getSelf();
	}
	
	public ClassificationSaveRequestBuilder setFixEquivalences(boolean fixEquivalences) {
		this.fixEquivalences = fixEquivalences;
		return getSelf();
	}
	
	public ClassificationSaveRequestBuilder setHandleConcreteDomains(boolean handleConcreteDomains) {
		this.handleConcreteDomains = handleConcreteDomains;
		return getSelf();
	}

	@Override
	protected Request<RepositoryContext, String> doBuild() {
		ClassificationSaveRequest request = new ClassificationSaveRequest();
		request.setClassificationId(classificationId);
		request.setUserId(userId);
		request.setParentLockContext(parentLockContext);
		request.setCommitComment(commitComment);
		request.setModuleId(moduleId);
		request.setNamespace(namespace);
		request.setFixEquivalences(fixEquivalences);
		request.setHandleConcreteDomains(handleConcreteDomains);
		return request;
	}
}
