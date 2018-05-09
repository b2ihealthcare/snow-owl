/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.AsyncRequest;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.RequestBuilder;
import com.b2international.snowowl.core.events.metrics.Metrics;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions;

/**
 * Repository Commit Request builder. Repository commit requests should always be executed in async mode.
 * 
 * @since 4.5
 */
public class RepositoryCommitRequestBuilder extends BaseRequestBuilder<RepositoryCommitRequestBuilder, BranchContext, CommitResult> implements AllowedHealthStates {

	private String userId;
	private String commitComment = "";
	private Request<TransactionContext, ?> body;
	private long preparationTime = Metrics.SKIP;
	private String parentContextDescription = DatastoreLockContextDescriptions.ROOT;

	public final RepositoryCommitRequestBuilder setUserId(String userId) {
		this.userId = userId;
		return getSelf();
	}

	public final RepositoryCommitRequestBuilder setBody(RequestBuilder<TransactionContext, ?> req) {
		return setBody(req.build());
	}

	public final RepositoryCommitRequestBuilder setBody(Request<TransactionContext, ?> req) {
		this.body = req;
		return getSelf();
	}

	public final RepositoryCommitRequestBuilder setCommitComment(String commitComment) {
		this.commitComment = commitComment;
		return getSelf();
	}
	
	/**
	 * Subclasses may override to provide additional request where the wrapper request requires {@link TransactionContext} for its functionality.
	 * @return
	 */
	protected Request<TransactionContext, ?> getBody() {
		return body;
	}

	/**
	 * Set additional preparation time for this commit. The caller is responsible for measuring the time properly before setting it in this builder
	 * and sending the request.
	 * 
	 * @param preparationTime
	 * @return
	 */
	public final RepositoryCommitRequestBuilder setPreparationTime(long preparationTime) {
		this.preparationTime = preparationTime;
		return getSelf();
	}
	
	public final RepositoryCommitRequestBuilder setParentContextDescription(String parentContextDescription) {
		this.parentContextDescription = parentContextDescription;
		return getSelf();
	}

	@Override
	protected final Request<BranchContext, CommitResult> doBuild() {
		return new TransactionalRequest(userId, commitComment, getBody(), preparationTime, parentContextDescription);
	}
	
	public AsyncRequest<CommitResult> build(String repositoryId, String branch) {
		return new AsyncRequest<>(
			new RepositoryRequest<>(repositoryId,
				new HealthCheckingRequest<>(
					new BranchRequest<>(branch,
						new RevisionIndexReadRequest<CommitResult>(
							build()
						)
					),
					allowedHealthstates()
				)
			)
		);
	}
	
}
