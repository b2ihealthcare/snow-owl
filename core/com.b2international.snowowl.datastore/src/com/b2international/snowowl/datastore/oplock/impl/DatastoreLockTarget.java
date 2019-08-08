/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.Objects;

import javax.annotation.Nullable;

/**
 * A lock target referring to a single repository branch by the unique identifier of the repository and the branch path.
 */
public class DatastoreLockTarget {
	
	private static final String _ALL = "all";

	public static DatastoreLockTarget ALL = new DatastoreLockTarget(_ALL, _ALL);

	@Nullable
	private final String branchPath;
	
	private final String repositoryUuid;

	/**
	 * Creates a new instance based on the specified repository identifier and branch path.
	 * 
	 * @param repositoryUuid the target repository's unique identifier (may not be {@code null})
	 * @param branchPath the target repository's branch path 
	 */
	public DatastoreLockTarget(final String repositoryUuid, final String branchPath) {
		this.repositoryUuid = repositoryUuid;
		this.branchPath = branchPath;
	}

	public String getBranchPath() {
		return branchPath;
	}
	
	public String getRepositoryUuid() {
		return repositoryUuid;
	}
	
	public boolean conflicts(final DatastoreLockTarget other) {
		return equals(other);
	}

	@Override
	public int hashCode() {
		return 31 * super.hashCode() + branchPath.hashCode() + repositoryUuid.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {

		if (this == obj) {
			return true;
		}

		if (!super.equals(obj)) {
			return false;
		}

		if (!(obj instanceof DatastoreLockTarget)) {
			return false;
		}

		final DatastoreLockTarget other = (DatastoreLockTarget) obj;
		return Objects.equals(branchPath, other.branchPath) && Objects.equals(repositoryUuid, other.repositoryUuid);
	}

	@Override
	public String toString() {
		return MessageFormat.format("branch ''{0}'' of repository ''{1}''", branchPath, repositoryUuid);
	}
}