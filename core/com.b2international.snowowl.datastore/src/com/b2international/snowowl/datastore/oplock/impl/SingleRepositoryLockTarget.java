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

/**
 * A lock target referring to a single repository by its unique identifier. Conflicts with other
 * {@link SingleRepositoryLockTarget}s which refer to the same repository.
 * 
 */
public class SingleRepositoryLockTarget extends AbstractDatastoreLockTarget {

	private static final long serialVersionUID = 1L;

	private final String repositoryUuid;

	/**
	 * Creates a new instance based on the specified unique identifier.
	 * 
	 * @param repositoryUuid the target repository's unique identifier (may not be {@code null})
	 */
	public SingleRepositoryLockTarget(final String repositoryUuid) {
		this.repositoryUuid = repositoryUuid;
	}

	public String getRepositoryUuid() {
		return repositoryUuid;
	}
	
	@Override
	public int hashCode() {
		return 31 + repositoryUuid.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {

		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (!(obj instanceof SingleRepositoryLockTarget)) {
			return false;
		}

		final SingleRepositoryLockTarget other = (SingleRepositoryLockTarget) obj;
		return repositoryUuid.equals(other.repositoryUuid);
	}

	@Override
	public String toString() {
		return MessageFormat.format("repository ''{0}''", getRepositoryUuid());
	}
}