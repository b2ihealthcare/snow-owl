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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.domain.TransactionContextProvider;
import com.b2international.snowowl.core.events.BaseRequest;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.metrics.Metrics;
import com.b2international.snowowl.core.events.metrics.MetricsThreadLocal;
import com.b2international.snowowl.core.events.metrics.Timer;
import com.b2international.snowowl.core.exceptions.ApiException;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;

/**
 * @since 4.5
 */
public final class TransactionalRequest extends BaseRequest<BranchContext, CommitInfo> {

	@JsonProperty
	private final String commitComment;
	
	@JsonProperty
	private final String userId;
	
	private final Request<TransactionContext, ?> next;

	private final long preRequestPreparationTime;

	TransactionalRequest(String userId, String commitComment, Request<TransactionContext, ?> next, long preRequestPreparationTime) {
		this.next = checkNotNull(next, "next");
		this.userId = userId;
		checkArgument(!Strings.isNullOrEmpty(commitComment), "Commit comment may not be null or empty.");
		this.commitComment = commitComment;
		this.preRequestPreparationTime = preRequestPreparationTime;
	}
	
	@Override
	public CommitInfo execute(BranchContext context) {
		final Metrics metrics = context.service(Metrics.class);
		metrics.setExternalValue("preRequest", preRequestPreparationTime);
		try (final TransactionContext transaction = context.service(TransactionContextProvider.class).get(context)) {
			final Object body = executeNext(transaction);
			return commit(transaction, body);
		} catch (ApiException e) {
			throw e;
		} catch (Exception e) {
			throw new SnowowlRuntimeException(e);
		}
	}

	private CommitInfo commit(final TransactionContext context, final Object body) {
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
			final long commitTimestamp = context.commit(userId, commitComment);
			return new CommitInfo(commitTimestamp, body);
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

	@Override
	protected Class<CommitInfo> getReturnType() {
		return CommitInfo.class;
	}
	
}
