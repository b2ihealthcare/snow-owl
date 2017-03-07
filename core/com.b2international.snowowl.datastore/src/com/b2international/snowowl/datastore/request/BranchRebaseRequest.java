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

import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.merge.Merge;
import com.b2international.snowowl.core.merge.MergeService;
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
public final class BranchRebaseRequest extends AbstractBranchChangeRequest<Merge> {

	private static String commitMessageOrDefault(final String sourcePath, final String targetPath, final String commitMessage) {
		return !Strings.isNullOrEmpty(commitMessage) 
				? commitMessage 
				: String.format("Rebase branch '%s' on '%s'", targetPath, sourcePath);
	}

	BranchRebaseRequest(final String sourcePath, final String targetPath, final String commitMessage, String reviewId) {
		super(sourcePath, targetPath, commitMessageOrDefault(sourcePath, targetPath, commitMessage), reviewId);
	}
	
	@Override
	protected Merge execute(RepositoryContext context, Branch source, Branch target) {
		return context.service(MergeService.class).enqueue(sourcePath, targetPath, commitMessage, reviewId);
	}
}
