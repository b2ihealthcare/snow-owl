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
package com.b2international.snowowl.core.api;

/**
 * Represents a branch identifier and a timestamp pair.
 */
public class DataVersion {

	private int branchId;
	private long timeStamp;

	/**
	 * Empty constructor.
	 */
	public DataVersion() {
	}
	
	/**
	 * Creates a new instance based on the passed in branch ID and timestamp.
	 * @param branchId the branch identifier as an integer.
	 * @param timeStamp the timestamp as a long number.
	 */
	public DataVersion(final int branchId, final long timeStamp) {
		this.branchId = branchId;
		this.timeStamp = timeStamp;
	}
	
	/**
	 * Returns with the branch id.
	 * @return branch identifier.
	 */
	public int getBranchId() {
		return branchId;
	}
	
	/**
	 * Sets the branch identifier. 
	 * @param branchId the new identifier.
	 */
	public void setBranchId(final int branchId) {
		this.branchId = branchId;
	}
	
	/**
	 * Returns with the timestamp of the current instance.
	 * @return the timestamp.
	 */
	public long getTimeStamp() {
		return timeStamp;
	}
	
	/**
	 * Sets the timestamp.
	 * @param timeStamp the new timestamp.
	 */
	public void setTimeStamp(final long timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("Data version: branchId = %d, timeStamp = %d", branchId, timeStamp);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + branchId;
		result = prime * result + (int) (timeStamp ^ (timeStamp >>> 32));
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final DataVersion other = (DataVersion) obj;
		if (branchId != other.branchId)
			return false;
		if (timeStamp != other.timeStamp)
			return false;
		return true;
	}
	
	
	
}