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
package com.b2international.snowowl.datastore.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.branch.Branch.BranchState;
import com.b2international.snowowl.core.branch.BranchMergeException;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.exceptions.ConflictException;
import com.b2international.snowowl.datastore.oplock.OperationLockException;
import com.google.common.base.Strings;

/**
 * Rebases {@code target} on {@code source}.
 * <p>
 * The branch represented by {@code target} is not modified; instead, a new branch representing the rebased {@code target} will be created on top of
 * {@code source}, preserving {@code target}'s path. This ensures that any changes that were made to {@code source} in the meantime will be visible on
 * the rebased target branch.
 * <p>
 * Commits available on the previous instance of {@code target}, if present, will also be visible on the resulting {@link Branch} after a successful
 * rebase.
 *
 * @since 4.6
 */
public final class BranchRebaseRequest extends AbstractBranchChangeRequest {

	private static final Logger LOG = LoggerFactory.getLogger(BranchRebaseRequest.class);

	private static String commitMessageOrDefault(final String sourcePath, final String targetPath, final String commitMessage) {
		return !Strings.isNullOrEmpty(commitMessage) 
				? commitMessage 
				: String.format("Rebase branch '%s' on '%s'", targetPath, sourcePath);
	}

	BranchRebaseRequest(final String sourcePath, final String targetPath, final String commitMessage, String reviewId) {
		super(sourcePath, targetPath, commitMessageOrDefault(sourcePath, targetPath, commitMessage), reviewId);
	}

	@Override
	protected Branch executeChange(RepositoryContext context, Branch source, Branch target) {
		
		if (!target.parent().equals(source)) {
			throw new BadRequestException("Cannot rebase target '%s' on source '%s'; source is not the direct parent of target.", target.path(), source.path());
		}

		try (Locks locks = new Locks(context, source, target)) {
			
			final BranchState targetState = target.state(source);

			if (targetState == BranchState.BEHIND || targetState == BranchState.DIVERGED || targetState == BranchState.STALE) {
				
				// Check conflicts by pretending to merge target into source
				source.applyChangeSet(target, true, commitMessage);
				
				// Reopen target branch and release lock on source
				final Branch reopenedTarget = target.reopen();
				
				try {
					locks.unlock(source.path());
				} catch (OperationLockException e) {
					LOG.warn("Failed to unlock source branch in BranchRebaseRequest; continuing.", e);
				}
				
				// Copy changes on old target to reopened target
				final Branch rebasedTarget = reopenedTarget.applyChangeSet(target, false, commitMessage); 
				
				if (rebasedTarget.headTimestamp() > rebasedTarget.baseTimestamp()) {
					return rebasedTarget; // Implicit notification already sent (because of a non-empty commit)
				} else {
					return rebasedTarget.notifyChanged(); // Send explicit notification (reopen but no commit)
				}
				
			} else {
				return target;
			}
			
		} catch (BranchMergeException e) {
			throw new ConflictException("Cannot rebase target '%s' on source '%s'.", target.path(), source.path(), e);
		} catch (OperationLockException e) {
			throw new ConflictException("Lock exception caught while rebasing target '%s' on source '%s'. %s", target.path(), source.path(), e.getMessage());
		} catch (InterruptedException e) {
			throw new ConflictException("Lock obtaining process was interrupted while rebasing target '%s' on source '%s'.", target.path(), source.path());
		}
	}
}
