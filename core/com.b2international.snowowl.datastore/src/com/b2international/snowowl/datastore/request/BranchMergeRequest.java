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

import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.branch.BranchMergeException;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.exceptions.ConflictException;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreOperationLockException;
import com.google.common.base.Strings;

/**
 * Merges {@code source} into {@code target} (only if {@code target} is the most recent version of {@code source}'s parent).
 * 
 * @since 4.1
 */
public final class BranchMergeRequest extends AbstractBranchChangeRequest {

	private static String commitMessageOrDefault(final String sourcePath, final String targetPath, final String commitMessage) {
		return !Strings.isNullOrEmpty(commitMessage) 
				? commitMessage 
				: String.format("Merge branch '%s' into '%s'", sourcePath, targetPath);
	}

	BranchMergeRequest(final String sourcePath, final String targetPath, final String commitMessage, String reviewId) {
		super(sourcePath, targetPath, commitMessageOrDefault(sourcePath, targetPath, commitMessage), reviewId);
	}

	@Override
	protected Branch executeChange(RepositoryContext context, Branch source, Branch target) {

		if (!source.parent().equals(target)) {
			throw new BadRequestException("Cannot merge source '%s' into target '%s'; target is not the direct parent of source.", source.path(), target.path());
		}

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
