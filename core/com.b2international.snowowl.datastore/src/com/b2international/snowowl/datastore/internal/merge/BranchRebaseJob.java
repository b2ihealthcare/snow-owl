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
package com.b2international.snowowl.datastore.internal.merge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.exceptions.ConflictException;
import com.b2international.index.revision.BaseRevisionBranching;
import com.b2international.index.revision.BranchMergeException;
import com.b2international.snowowl.core.Repository;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.AsyncRequest;
import com.b2international.snowowl.core.merge.ComponentRevisionConflictProcessor;
import com.b2international.snowowl.core.merge.Merge;
import com.b2international.snowowl.datastore.oplock.OperationLockException;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions;
import com.b2international.snowowl.datastore.request.AbstractBranchChangeRequest;
import com.b2international.snowowl.datastore.request.IndexReadRequest;
import com.b2international.snowowl.datastore.request.Locks;
import com.b2international.snowowl.datastore.request.RepositoryRequest;
import com.google.common.base.Strings;

/**
 * @since 4.6
 */
public class BranchRebaseJob extends AbstractBranchChangeRemoteJob {

	private static class SyncRebaseRequest extends AbstractBranchChangeRequest<Boolean> {

		SyncRebaseRequest(final Merge merge, final String userId, final String commitMessage, String reviewId, String parentLockContext) {
			super(merge.getSource(), merge.getTarget(), userId, commitMessage, reviewId, parentLockContext);
		}

		@Override
		protected Boolean execute(final RepositoryContext context, final Branch source, final Branch target) {

			if (!target.parentPath().equals(source.path())) {
				throw new BadRequestException("Cannot rebase target '%s' on source '%s'; source is not the direct parent of target.", target.path(), source.path());
			}
			
			try (Locks locks = new Locks(context, userId, DatastoreLockContextDescriptions.SYNCHRONIZE, parentLockContext, source, target)) {
				context.service(BaseRevisionBranching.class).merge(source.path(), target.path(), commitMessage, context.service(ComponentRevisionConflictProcessor.class));
				return true;
			} catch (BranchMergeException e) {
				throw new ConflictException(Strings.isNullOrEmpty(e.getMessage()) ? "Cannot rebase target '%s' on source '%s'." : e.getMessage(), target.path(), source.path(), e);
			} catch (OperationLockException e) {
				throw new ConflictException("Lock exception caught while rebasing target '%s' on source '%s'. %s", target.path(), source.path(), e.getMessage());
			} catch (InterruptedException e) {
				throw new ConflictException("Lock obtaining process was interrupted while rebasing target '%s' on source '%s'.", target.path(), source.path());
			}
		}
	}
	
	public BranchRebaseJob(Repository repository, String source, String target, String userId, String commitMessage, String reviewId, String parentLockContext) {
		super(repository, source, target, userId, commitMessage, reviewId, parentLockContext);
	}

	@Override
	protected void applyChanges() {
		new AsyncRequest<>(
			new RepositoryRequest<>(repository.id(),
				new IndexReadRequest<>(
					new SyncRebaseRequest(getMerge(), userId, commitComment, reviewId, parentLockContext)
				)
			)
		).execute(repository.events()).getSync();
	}
}
