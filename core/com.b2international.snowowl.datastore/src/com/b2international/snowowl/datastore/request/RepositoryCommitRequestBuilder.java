/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.request;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;

/**
 * @since 4.5
 */
public final class RepositoryCommitRequestBuilder<B> {
	
	private String userId;
	private String repositoryId;
	private String branch;
	private String commitComment = "";
	private Request<TransactionContext, B> body;

	RepositoryCommitRequestBuilder(String userId, String repositoryId, String branch) {
		this.userId = userId;
		this.repositoryId = repositoryId;
		this.branch = branch;
	}
	
	public RepositoryCommitRequestBuilder<B> setBody(Request<TransactionContext, B> body) {
		this.body = body;
		return this;
	}
	
	public RepositoryCommitRequestBuilder<B> setCommitComment(String commitComment) {
		this.commitComment = commitComment;
		return this;
	}
	
	public Request<ServiceProvider, B> build() {
		return new RepositoryRequest<>(repositoryId, branch, new TransactionalRequest<>(userId, commitComment, body));
	}

}
