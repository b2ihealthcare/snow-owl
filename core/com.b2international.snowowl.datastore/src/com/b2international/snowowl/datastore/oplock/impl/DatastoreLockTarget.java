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

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Objects;

import javax.annotation.Nullable;

import com.google.common.base.Strings;

/**
 * A lock target referring to a single repository branch by the unique identifier of the repository and the branch path.
 */
public class DatastoreLockTarget implements Serializable {
	
	private static final String _ALL = "all";

	public static DatastoreLockTarget ALL = new DatastoreLockTarget(_ALL, _ALL);

	@Nullable
	private final String branchPath;
	
	private final String repositoryId;

	/**
	 * Creates a new instance based on the specified repository identifier and branch path.
	 * 
	 * @param repositoryId the target repository's unique identifier (may not be {@code null})
	 * @param branchPath the target repository's branch path 
	 */
	public DatastoreLockTarget(final String repositoryId, final String branchPath) {
		this.repositoryId = repositoryId;
		this.branchPath = branchPath;
	}

	public String getBranchPath() {
		return branchPath;
	}
	
	public String getRepositoryId() {
		return repositoryId;
	}
	
	public boolean conflicts(final DatastoreLockTarget other) {
		if (this.equals(ALL) || other.equals(ALL)) {
			return true;
		}
		
		if (Strings.isNullOrEmpty(branchPath) || Strings.isNullOrEmpty(other.getBranchPath())) {
			return repositoryId.equals(other.getRepositoryId());
		}
		
		return equals(other);
	}

	@Override
	public int hashCode() {
		return Objects.hash(repositoryId, branchPath);
	}

	@Override
	public boolean equals(final Object obj) {

		if (this == obj) {
			return true;
		}
		
		if (!(obj instanceof DatastoreLockTarget)) {
			return false;
		}

		final DatastoreLockTarget other = (DatastoreLockTarget) obj;
		return Objects.equals(branchPath, other.branchPath) && Objects.equals(repositoryId, other.repositoryId);
	}

	@Override
	public String toString() {
		return MessageFormat.format("branch ''{0}'' of repository ''{1}''", branchPath, repositoryId);
	}
}