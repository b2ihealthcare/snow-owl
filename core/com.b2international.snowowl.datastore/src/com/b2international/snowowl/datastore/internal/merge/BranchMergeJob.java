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
package com.b2international.snowowl.datastore.internal.merge;

import com.b2international.commons.exceptions.ConflictException;
import com.b2international.index.revision.BaseRevisionBranching;
import com.b2international.index.revision.BranchMergeException;
import com.b2international.index.revision.RevisionConflictProcessor;
import com.b2international.snowowl.core.Repository;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.AsyncRequest;
import com.b2international.snowowl.core.merge.Merge;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreOperationLockException;
import com.b2international.snowowl.datastore.request.AbstractBranchChangeRequest;
import com.b2international.snowowl.datastore.request.IndexReadRequest;
import com.b2international.snowowl.datastore.request.Locks;
import com.b2international.snowowl.datastore.request.RepositoryRequest;
import com.google.common.base.Strings;

/**
 * @since 4.6
 */
public class BranchMergeJob extends AbstractBranchChangeRemoteJob {

	private static class SyncMergeRequest extends AbstractBranchChangeRequest<Boolean> {

		SyncMergeRequest(final Merge merge, final String commitMessage, String reviewId) {
			super(merge.getSource(), merge.getTarget(), commitMessage, reviewId);
		}

		@Override
		protected Boolean execute(RepositoryContext context, Branch source, Branch target) {
			try (Locks locks = new Locks(context, source, target)) {
				context.service(BaseRevisionBranching.class).merge(source.path(), target.path(), commitMessage, RevisionConflictProcessor.DEFAULT, true);
				return true;
			} catch (BranchMergeException e) {
				throw new ConflictException(Strings.isNullOrEmpty(e.getMessage()) ? "Cannot merge source '%s' into target '%s'." : e.getMessage(), source.path(), target.path(), e);
			} catch (DatastoreOperationLockException e) {
				throw new ConflictException("Lock exception caught while merging source '%s' into target '%s'. %s", source.path(), target.path(), e.getMessage());
			} catch (InterruptedException e) {
				throw new ConflictException("Lock obtaining process was interrupted while merging source '%s' into target '%s'.", source.path(), target.path());
			}
		}
	}
	
	public BranchMergeJob(Repository repository, String source, String target, String commitMessage, String reviewId) {
		super(repository, source, target, commitMessage, reviewId);
	}

	@Override
	protected void applyChanges() {
		new AsyncRequest<>(
			new RepositoryRequest<>(repository.id(),
				new IndexReadRequest<>(
					new SyncMergeRequest(getMerge(), commitComment, reviewId)
				)
			)
		).execute(repository.events()).getSync();
	}
}
