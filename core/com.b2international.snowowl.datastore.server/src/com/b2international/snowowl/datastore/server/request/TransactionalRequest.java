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
package com.b2international.snowowl.datastore.server.request;

import static com.google.common.base.Preconditions.checkArgument;

import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.DelegatingRequest;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.exceptions.ApiException;
import com.google.common.base.Strings;

/**
 * @since 4.5
 */
public final class TransactionalRequest<B> extends DelegatingRequest<BranchContext, TransactionContext, B> {

	private final String commitComment;
	private final String userId;

	public TransactionalRequest(String userId, String commitComment, Request<TransactionContext, B> next) {
		super(next);
		this.userId = userId;
		checkArgument(!Strings.isNullOrEmpty(commitComment), "Commit comment may not be null or empty.");
		this.commitComment = commitComment;
	}
	
	@Override
	public B execute(BranchContext context) {
		try (final TransactionContext transaction = context.provider(TransactionContext.class).get()) {
			final B component = next(transaction);
			// TODO consider moving preCommit into commit(userId, commitComment)
			transaction.preCommit();
			
			/*
			 * FIXME: at this point, the component identifier might have changed even though the input 
			 * required an exact ID to be assigned. What to do?
			 */
			transaction.commit(userId, commitComment);
			return component;
		} catch (ApiException e) {
			throw e;
		} catch (Exception e) {
			throw new SnowowlRuntimeException(e);
		}
	}
	
}
