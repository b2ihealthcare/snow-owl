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
package com.b2international.snowowl.snomed.reasoner.classification;

import com.b2international.collections.longs.LongSet;

/**
 * 
 */
public class ReasonerTaxonomyEntry {

	private final long sourceId;
	
	private final LongSet parentIds;

	/**
	 * 
	 * @param sourceId
	 * @param parentIds
	 */
	public ReasonerTaxonomyEntry(long sourceId, LongSet parentIds) {
		this.sourceId = sourceId;
		this.parentIds = parentIds;
	}

	public long getSourceId() {
		return sourceId;
	}

	public LongSet getParentIds() {
		return parentIds;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((parentIds == null) ? 0 : parentIds.hashCode());
		result = prime * result + (int) (sourceId ^ (sourceId >>> 32));
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ReasonerTaxonomyEntry)) {
			return false;
		}
		ReasonerTaxonomyEntry other = (ReasonerTaxonomyEntry) obj;
		if (parentIds == null) {
			if (other.parentIds != null) {
				return false;
			}
		} else if (!parentIds.equals(other.parentIds)) {
			return false;
		}
		if (sourceId != other.sourceId) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ReasonerTaxonomyEntry [sourceId=");
		builder.append(sourceId);
		builder.append(", parentIds=");
		builder.append(parentIds);
		builder.append("]");
		return builder.toString();
	}
}