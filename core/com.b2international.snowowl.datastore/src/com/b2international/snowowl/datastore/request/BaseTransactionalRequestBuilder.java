/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.AsyncRequest;
import com.b2international.snowowl.core.events.BaseRequestBuilder;

/**
 * @since 4.5
 */
public abstract class BaseTransactionalRequestBuilder<B extends BaseTransactionalRequestBuilder<B, R>, R>
		extends BaseRequestBuilder<B, TransactionContext, R> {

	private final RepositoryCommitRequestBuilder commitRequestBuilder;

	public BaseTransactionalRequestBuilder(RepositoryCommitRequestBuilder commitRequestBuilder) {
		this.commitRequestBuilder = commitRequestBuilder;
	}
	
	public final AsyncRequest<CommitResult> build(String repositoryId, String branch, String userId, String commitComment) {
		return commitRequestBuilder
				.setUserId(userId)
				.setCommitComment(commitComment)
				.setBody(build())
				.build(repositoryId, branch);
	}
	
}
