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
import com.b2international.snowowl.core.events.RequestBuilder;
import com.b2international.snowowl.core.exceptions.ApiValidation;

/**
 * @since 4.5
 */
public final class RepositoryCommitRequestBuilder implements RequestBuilder<ServiceProvider, CommitInfo> {
	
	private String userId;
	private String repositoryId;
	private String branch;
	private String commitComment = "";
	private Request<TransactionContext, ?> body;

	RepositoryCommitRequestBuilder(String userId, String repositoryId, String branch) {
		this.userId = userId;
		this.repositoryId = repositoryId;
		this.branch = branch;
	}
	
	public RepositoryCommitRequestBuilder setBody(RequestBuilder<TransactionContext, ?> req) {
		this.body = req.build();
		return this;
	}
	
	public RepositoryCommitRequestBuilder setBody(Request<TransactionContext, ?> body) {
		this.body = body;
		return this;
	}
	
	public RepositoryCommitRequestBuilder setCommitComment(String commitComment) {
		this.commitComment = commitComment;
		return this;
	}

	@Override
	public Request<ServiceProvider, CommitInfo> build() {
		ApiValidation.checkInput(body);
		return RepositoryRequests.wrap(repositoryId, branch, new IndexReadRequest<>(new TransactionalRequest(userId, commitComment, body)));
	}

}
