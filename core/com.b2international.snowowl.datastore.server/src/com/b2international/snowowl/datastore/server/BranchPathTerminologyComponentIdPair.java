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
package com.b2international.snowowl.datastore.server;

import java.io.Serializable;

import com.b2international.snowowl.core.api.IBranchPath;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * Pair wrapping a unique {@link #getTerminologyComponentId() terminology component ID} and a {@link #getPath() branch path}.
 */
public class BranchPathTerminologyComponentIdPair implements Serializable {

	private static final long serialVersionUID = 1619613716902336922L;
	
	private final String terminologyComponentId;
	private final IBranchPath path;
	
	public BranchPathTerminologyComponentIdPair(final IBranchPath path, final String terminologyComponentId) {
		this.terminologyComponentId = Preconditions.checkNotNull(terminologyComponentId, "Terminology component ID argument cannot be null");
		this.path = Preconditions.checkNotNull(path, "Branch path argument cannot be null");
	}
	
	/**
	 * Returns with the unique terminology component ID.
	 * @return the unique terminology component ID. 
	 */
	public String getTerminologyComponentId() {
		return terminologyComponentId;
	}
	
	/**
	 * The branch path.
	 * @return the branch path.
	 */
	public IBranchPath getPath() {
		return path;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("Path", path).add("ID", terminologyComponentId).toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + ((terminologyComponentId == null) ? 0 : terminologyComponentId.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof BranchPathTerminologyComponentIdPair))
			return false;
		final BranchPathTerminologyComponentIdPair other = (BranchPathTerminologyComponentIdPair) obj;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (terminologyComponentId == null) {
			if (other.terminologyComponentId != null)
				return false;
		} else if (!terminologyComponentId.equals(other.terminologyComponentId))
			return false;
		return true;
	}

	
	
}