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
package com.b2international.snowowl.snomed.datastore;

import java.io.Serializable;

import com.google.common.base.MoreObjects;

/**
 * Bare minimum representation of a SNOMED&nbsp;CT IS_A relationship.
 * @see SnomedIsAStatement
 */
public class SnomedIsAStatement implements IsAStatement, Serializable {

	private static final long serialVersionUID = -3758991099967594999L;

	private final long sourceId;
	private final long destinationId;

	public SnomedIsAStatement(final String sourceId, final String destinationId) {
		this(Long.parseLong(sourceId), Long.parseLong(destinationId));
	}
	
	/**
	 * Creates a new pair of IDs representing the bare minimum of a SNOMED&nbsp;CT IS_A relationships.
	 * @param sourceId the source concept ID.
	 * @param destinationId the destination concept ID.
	 */
	public SnomedIsAStatement(final long sourceId, final long destinationId) {
		this.sourceId = sourceId;
		this.destinationId = destinationId;
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.IsAStatement#getSourceId()
	 */
	@Override
	public long getSourceId() {
		return sourceId;
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.IsAStatement#getDestinationId()
	 */
	@Override
	public long getDestinationId() {
		return destinationId;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("sourceId", sourceId)
				.add("destinationId", destinationId)
				.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (destinationId ^ (destinationId >>> 32));
		result = prime * result + (int) (sourceId ^ (sourceId >>> 32));
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final SnomedIsAStatement other = (SnomedIsAStatement) obj;
		if (destinationId != other.destinationId)
			return false;
		if (sourceId != other.sourceId)
			return false;
		return true;
	}
}