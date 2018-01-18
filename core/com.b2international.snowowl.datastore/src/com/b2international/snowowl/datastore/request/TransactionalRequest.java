/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.base.Preconditions.checkNotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.domain.TransactionContextProvider;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.metrics.Metrics;
import com.b2international.snowowl.core.events.metrics.MetricsThreadLocal;
import com.b2international.snowowl.core.events.metrics.Timer;
import com.b2international.snowowl.core.exceptions.ApiException;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 4.5
 */
public final class TransactionalRequest implements Request<BranchContext, CommitResult> {

	@JsonProperty
	@NotEmpty
	private final String commitComment;
	
	@JsonProperty
	private final String userId;
	
	private final Request<TransactionContext, ?> next;

	private final long preRequestPreparationTime;
	
	private final String parentContextDescription;

	TransactionalRequest(String userId, String commitComment, Request<TransactionContext, ?> next, long preRequestPreparationTime, String parentContextDescription) {
		this.next = checkNotNull(next, "next");
		this.userId = userId;
		this.commitComment = commitComment;
		this.preRequestPreparationTime = preRequestPreparationTime;
		this.parentContextDescription = parentContextDescription;
	}
	
	@Override
	public CommitResult execute(BranchContext context) {
		final Metrics metrics = context.service(Metrics.class);
		metrics.setExternalValue("preRequest", preRequestPreparationTime);
		try (final TransactionContext transaction = context.service(TransactionContextProvider.class).get(context, userId, commitComment, parentContextDescription)) {
			final Object body = executeNext(transaction);
			return commit(transaction, body);
		} catch (ApiException e) {
			throw e;
		} catch (Exception e) {
			throw new SnowowlRuntimeException(e);
		}
	}

	private CommitResult commit(final TransactionContext context, final Object body) {
		final Metrics metrics = context.service(Metrics.class);
		final Timer commitTimer = metrics.timer("commit");
		MetricsThreadLocal.set(metrics);
		try {
			commitTimer.start();
			// TODO consider moving preCommit into commit(userId, commitComment)
			context.preCommit();
			
			/*
			 * FIXME: at this point, the component identifier might have changed even though the input 
			 * required an exact ID to be assigned. What to do?
			 */
			final long commitTimestamp = context.commit(userId, commitComment, parentContextDescription);
			return new CommitResult(commitTimestamp, body);
		} finally {
			commitTimer.stop();
			MetricsThreadLocal.release();
		}
	}
	
	private Object executeNext(TransactionContext context) {
		final Timer preCommit = context.service(Metrics.class).timer("preCommit");
		try {
			preCommit.start();
			return next.execute(context);
		} finally {
			preCommit.stop();
		}
	}

	/**
	 * @return the next request in the chain, which will be executed
	 */
	public Request<TransactionContext, ?> getNext() {
		return next;
	}
}
