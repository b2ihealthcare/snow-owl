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
package com.b2international.snowowl.datastore.server.internal.merge;

import com.b2international.snowowl.core.Repository;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.branch.BranchMergeException;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.exceptions.ConflictException;
import com.b2international.snowowl.core.merge.Merge;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreOperationLockException;
import com.b2international.snowowl.datastore.request.AbstractBranchChangeRequest;
import com.b2international.snowowl.datastore.request.Locks;
import com.b2international.snowowl.datastore.request.RepositoryRequests;

/**
 * @since 4.6
 */
public class BranchMergeJob extends AbstractBranchChangeRemoteJob {

	private static class SyncMergeRequest extends AbstractBranchChangeRequest<Branch> {

		SyncMergeRequest(final Merge merge, final String commitMessage, String reviewId) {
			super(Branch.class, merge.getSource(), merge.getTarget(), commitMessage, reviewId);
		}

		@Override
		protected Branch execute(RepositoryContext context, Branch source, Branch target) {
			try (Locks locks = new Locks(context, source, target)) {
				return target.merge(source, commitMessage);
			} catch (BranchMergeException e) {
				throw new ConflictException("Cannot merge source '%s' into target '%s'.", source.path(), target.path(), e);
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
		RepositoryRequests
			.wrap(repository.id(), new SyncMergeRequest(merge, commitComment, reviewId))
			.executeSync(repository.events());
	}
}
