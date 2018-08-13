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
package com.b2international.snowowl.datastore.oplock.impl;

import java.text.MessageFormat;

import com.b2international.snowowl.core.api.IBranchPath;

/**
 * A lock target referring to a single repository branch by the unique identifier of the repository and the branch path.
 * Conflicts with other {@link SingleRepositoryLockTarget}s which refer to the same repository, or
 * {@link SingleRepositoryAndBranchLockTarget}s which refer to the same repository-and-branch pair.
 * 
 */
public class SingleRepositoryAndBranchLockTarget extends SingleRepositoryLockTarget {

	private static final long serialVersionUID = 1L;

	private final IBranchPath branchPath;

	/**
	 * Creates a new instance based on the specified repository identifier and branch path.
	 * 
	 * @param repositoryUuid the target repository's unique identifier (may not be {@code null})
	 * @param branchPath the target repository's branch path (may not be {@code null})
	 */
	public SingleRepositoryAndBranchLockTarget(final String repositoryUuid, final IBranchPath branchPath) {
		super(repositoryUuid);
		this.branchPath = branchPath;
	}

	public IBranchPath getBranchPath() {
		return branchPath;
	}

	protected boolean _conflicts(final SingleRepositoryLockTarget other) {
		return other.equals(this);
	}

	@Override
	public int hashCode() {
		return 31 * super.hashCode() + branchPath.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {

		if (this == obj) {
			return true;
		}

		if (!super.equals(obj)) {
			return false;
		}

		if (!(obj instanceof SingleRepositoryAndBranchLockTarget)) {
			return false;
		}

		final SingleRepositoryAndBranchLockTarget other = (SingleRepositoryAndBranchLockTarget) obj;
		return branchPath.equals(other.branchPath);
	}

	@Override
	public String toString() {
		return MessageFormat.format("branch ''{0}'' of repository ''{1}''", branchPath, getRepositoryUuid());
	}
}