/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.exceptions.ConflictException;
import com.b2international.index.revision.BaseRevisionBranching;
import com.b2international.index.revision.BranchMergeException;
import com.b2international.index.revision.Commit;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.internal.locks.DatastoreLockContextDescriptions;
import com.b2international.snowowl.core.locks.Locks;
import com.b2international.snowowl.core.merge.ComponentRevisionConflictProcessor;
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

	private static final long serialVersionUID = 1L;

	private static String commitMessageOrDefault(final String sourcePath, final String targetPath, final String commitMessage) {
		return !Strings.isNullOrEmpty(commitMessage) 
				? commitMessage 
				: String.format("Rebase branch '%s' on '%s'", targetPath, sourcePath);
	}

	BranchRebaseRequest(final String sourcePath, final String targetPath, final String userId, final String commitMessage, String parentLockContext) {
		super(sourcePath, targetPath, userId, commitMessageOrDefault(sourcePath, targetPath, commitMessage), parentLockContext);
	}
	
	@Override
	protected Commit applyChanges(RepositoryContext context, Branch source, Branch target) {
		if (!target.parentPath().equals(source.path())) {
			throw new BadRequestException("Cannot rebase target '%s' on source '%s'; source is not the direct parent of target.", target.path(), source.path());
		}
		
		final String author = userId(context);
		try (Locks locks = Locks
				.on(context)
				.branches(source.path(), target.path())
				.user(author)
				.lock(DatastoreLockContextDescriptions.SYNCHRONIZE, parentLockContext)) {
			return context.service(BaseRevisionBranching.class)
				.prepareMerge(source.path(), target.path())
				.author(author)
				.commitMessage(commitMessage)
				.conflictProcessor(context.service(ComponentRevisionConflictProcessor.class))
				.context(context)
				.merge();
		} catch (BranchMergeException e) {
			throw new ConflictException(Strings.isNullOrEmpty(e.getMessage()) ? "Cannot rebase target '%s' on source '%s'." : e.getMessage(), target.path(), source.path(), e);
		}
	}
	
}
