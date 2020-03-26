/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.request;

import static com.google.common.base.Preconditions.checkNotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.commons.exceptions.ApiException;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 4.5
 */
public final class TransactionalRequest implements Request<BranchContext, CommitResult> {

	@JsonProperty
	@NotEmpty
	private final String commitComment;
	
	@JsonProperty
	private final String author;
	
	private final Request<TransactionContext, ?> next;

	private final long preRequestPreparationTime;
	
	private final String parentLockContext;

	public TransactionalRequest(String author, String commitComment, Request<TransactionContext, ?> next, long preRequestPreparationTime, String parentLockContext) {
		this.next = checkNotNull(next, "next");
		this.author = author;
		this.commitComment = commitComment;
		this.preRequestPreparationTime = preRequestPreparationTime;
		this.parentLockContext = parentLockContext;
	}
	
	@Override
	public CommitResult execute(BranchContext context) {
//		final Metrics metrics = context.service(Metrics.class);
//		metrics.setExternalValue("preRequest", preRequestPreparationTime);
		try (final TransactionContext transaction = context.openTransaction(context, author, commitComment, parentLockContext)) {
			final Object body = executeNext(transaction);
			return commit(transaction, body);
		} catch (ApiException e) {
			throw e;
		} catch (Exception e) {
			throw new SnowowlRuntimeException(e);
		}
	}

	private CommitResult commit(final TransactionContext context, final Object body) {
		/*
		 * FIXME: at this point, the component identifier might have changed even though the input 
		 * required an exact ID to be assigned. What to do?
		 */
		final long commitTimestamp = context.commit(context.author(), commitComment, parentLockContext);
		return new CommitResult(commitTimestamp, body);
	}
	
	private Object executeNext(TransactionContext context) {
		return next.execute(context);
	}

	/**
	 * @return the next request in the chain, which will be executed
	 */
	public Request<TransactionContext, ?> getNext() {
		return next;
	}
	
	@Override
	public ClassLoader getClassLoader() {
		return next.getClassLoader();
	}
	
}
