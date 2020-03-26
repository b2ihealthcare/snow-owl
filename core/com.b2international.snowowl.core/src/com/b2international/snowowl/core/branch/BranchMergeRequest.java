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
package com.b2international.snowowl.core.branch;

import com.b2international.commons.exceptions.ConflictException;
import com.b2international.index.revision.BaseRevisionBranching;
import com.b2international.index.revision.BranchMergeException;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.internal.locks.DatastoreLockContextDescriptions;
import com.b2international.snowowl.core.internal.locks.DatastoreOperationLockException;
import com.b2international.snowowl.core.locks.Locks;
import com.b2international.snowowl.core.merge.ComponentRevisionConflictProcessor;
import com.google.common.base.Strings;

/**
 * Merges {@code source} into {@code target} (only if {@code target} is the most recent version of {@code source}'s parent).
 * 
 * @since 4.1
 */
public final class BranchMergeRequest extends AbstractBranchChangeRequest {

	private static final long serialVersionUID = 1L;

	private static String commitMessageOrDefault(final String sourcePath, final String targetPath, final String commitMessage) {
		return !Strings.isNullOrEmpty(commitMessage) 
				? commitMessage
				: String.format("Merge branch '%s' into '%s'", sourcePath, targetPath);
	}

	BranchMergeRequest(final String sourcePath, final String targetPath, final String userId, final String commitMessage, String reviewId, String parentLockContext) {
		super(sourcePath, targetPath, userId, commitMessageOrDefault(sourcePath, targetPath, commitMessage), reviewId, parentLockContext);
	}
	
	@Override
	protected void applyChanges(RepositoryContext context, Branch source, Branch target) {
		final String author = userId(context);
		try (Locks locks = Locks
				.on(context)
				.branches(source.path(), target.path())
				.user(author)
				.lock(DatastoreLockContextDescriptions.SYNCHRONIZE, parentLockContext)) {
			context.service(BaseRevisionBranching.class)
				.prepareMerge(source.path(), target.path())
				.author(author)
				.commitMessage(commitMessage)
				.conflictProcessor(context.service(ComponentRevisionConflictProcessor.class))
				.squash(true)
				.context(context)
				.merge();
		} catch (BranchMergeException e) {
			throw new ConflictException(Strings.isNullOrEmpty(e.getMessage()) ? "Cannot merge source '%s' into target '%s'." : e.getMessage(), source.path(), target.path(), e);
		} catch (DatastoreOperationLockException e) {
			throw new ConflictException("Lock exception caught while merging source '%s' into target '%s'. %s", source.path(), target.path(), e.getMessage());
		}
	}
	
}
