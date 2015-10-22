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

import java.util.ConcurrentModificationException;

import org.eclipse.emf.cdo.util.CommitException;

import com.b2international.commons.exceptions.Exceptions;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.BaseEvent;
import com.b2international.snowowl.core.events.DelegatingRequest;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.exceptions.ApiValidation;
import com.b2international.snowowl.core.exceptions.ConflictException;
import com.b2international.snowowl.core.exceptions.CycleDetectedException;
import com.b2international.snowowl.core.exceptions.LockedException;
import com.b2international.snowowl.datastore.CDOEditingContext;
import com.b2international.snowowl.datastore.exception.RepositoryLockException;
import com.b2international.snowowl.datastore.server.CDOServerUtils;
import com.google.common.base.Strings;
import com.google.inject.Provider;

/**
 * @since 4.5
 */
public final class TransactionalRequest<B> extends DelegatingRequest<RepositoryContext, TransactionContext, B> {

	private final String commitComment;
	private final String userId;

	public TransactionalRequest(String userId, String commitComment, Request<TransactionContext, B> next) {
		super(next);
		this.userId = userId;
		checkArgument(!Strings.isNullOrEmpty(commitComment), "Commit comment may not be null or empty.");
		this.commitComment = commitComment;
	}
	
	@Override
	public B execute(RepositoryContext context) {
		try (TransactionContext transaction = context.provider(TransactionContext.class).get()) {
			final B component = next(transaction);
			transaction.preCommit();

			/*
			 * FIXME: at this point, the component identifier might have changed even though the input 
			 * required an exact ID to be assigned. What to do?
			 */
			doCommit(transaction);
			return component;
		}
	}
	
	private void doCommit(final TransactionContext transaction) {
		try {
			transaction.commit(userId, commitComment);
//			CDOServerUtils.commit(context.getTransaction(), userId, commitComment, null);
		} catch (final CommitException e) {
			final RepositoryLockException cause = Exceptions.extractCause(e, getClass().getClassLoader(), RepositoryLockException.class);
			if (cause != null) {
				throw new LockedException(cause.getMessage());
			}
			
			final ConcurrentModificationException cause2 = Exceptions.extractCause(e, getClass().getClassLoader(), ConcurrentModificationException.class);
			if (cause2 != null) {
				throw new ConflictException("Concurrent modifications prevented the concept from being persisted. Please try again.");
			}
			
			final CycleDetectedException cause3 = Exceptions.extractCause(e.getCause(), getClass().getClassLoader(), CycleDetectedException.class);
			if (cause3 != null) {
				throw cause3;
			}
			throw new SnowowlRuntimeException(e.getMessage(), e);
		}
	}

}
