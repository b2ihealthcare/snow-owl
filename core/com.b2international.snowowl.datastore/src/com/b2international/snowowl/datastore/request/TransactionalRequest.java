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
import com.b2international.snowowl.core.exceptions.ApiException;
import com.google.common.base.Strings;

/**
 * @since 4.5
 */
public final class TransactionalRequest extends BaseRequest<BranchContext, CommitInfo> {

	private final String commitComment;
	private final String userId;
	private final Request<TransactionContext, ?> next;

	TransactionalRequest(String userId, String commitComment, Request<TransactionContext, ?> next) {
		this.next = checkNotNull(next, "next");
		this.userId = userId;
		checkArgument(!Strings.isNullOrEmpty(commitComment), "Commit comment may not be null or empty.");
		this.commitComment = commitComment;
	}
	
	@Override
	public CommitInfo execute(BranchContext context) {
		try (final TransactionContext transaction = context.service(TransactionContextProvider.class).get(context)) {
			final Object body = next.execute(transaction);
			// TODO consider moving preCommit into commit(userId, commitComment)
			transaction.preCommit();
			
			/*
			 * FIXME: at this point, the component identifier might have changed even though the input 
			 * required an exact ID to be assigned. What to do?
			 */
			final long commitTimestamp = transaction.commit(userId, commitComment);
			return new CommitInfo(commitTimestamp, body);
		} catch (ApiException e) {
			throw e;
		} catch (Exception e) {
			throw new SnowowlRuntimeException(e);
		}
	}
	
	@Override
	protected Class<CommitInfo> getReturnType() {
		return CommitInfo.class;
	}
	
	@Override
	public String toString() {
		return String.format("{userId:%s, commitComment: %s, next: %s}", userId, commitComment, next);
	}
	
}
