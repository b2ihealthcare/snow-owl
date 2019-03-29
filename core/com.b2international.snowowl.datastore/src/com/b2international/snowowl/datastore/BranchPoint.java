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
package com.b2international.snowowl.datastore;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IBranchPoint;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

/**
 * Represents a {@link #getTimestamp() point in time} on a particular {@link IBranchPath branch}.
 */
/*default*/ final class BranchPoint implements IBranchPoint {
	
	private static final long serialVersionUID = -6460742850150481830L;

	private final long timestamp;
	private final IBranchPath branchPath;
	private final String uuid;

	/**Default constructor.
	 * @param connection */
	/*default*/ BranchPoint(final ICDOConnection connection, final IBranchPath branchPath, final long timestamp) {
		Preconditions.checkNotNull(connection, "CDO connection cannot be null.");
		this.uuid = Preconditions.checkNotNull(connection.getUuid(), "UUID for the CDO connection was null.");
		this.branchPath = Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		this.timestamp = timestamp;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.IBranchPoint#getBranchPath()
	 */
	@Override
	public IBranchPath getBranchPath() {
		return branchPath;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.IBranchPoint#getTimestamp()
	 */
	@Override
	public long getTimestamp() {
		return timestamp;
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.IBranchPoint#getUuid()
	 */
	@Override
	public String getUuid() {
		return uuid;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((branchPath == null) ? 0 : branchPath.hashCode());
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
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
		if (!(obj instanceof BranchPoint))
			return false;
		final BranchPoint other = (BranchPoint) obj;
		if (branchPath == null) {
			if (other.branchPath != null)
				return false;
		} else if (!branchPath.equals(other.branchPath))
			return false;
		if (timestamp != other.timestamp)
			return false;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("Branch", branchPath.getPath())
				.add("Timestamp", timestamp)
				.add("UUID", uuid)
				.toString();
	}
	
	
}