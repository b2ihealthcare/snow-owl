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
package com.b2international.snowowl.snomed.datastore;

import java.io.Serializable;

import com.google.common.base.Objects;

/**
 * Bare minimum representation of a SNOMED&nbsp;CT IS_A relationship.
 *
 * @see IsAStatementWithId
 *
 */
public class SnomedIsAStatementWithId extends SnomedIsAStatement implements IsAStatementWithId, Serializable {

	private static final long serialVersionUID = -2783485293088318719L;

	private final long relationshipId;

	/**
	 * Creates a new triple of IDs representing the bare minimum of a SNOMED&nbsp;CT IS_A relationships.
	 * @param sourceId the source concept ID.
	 * @param destinationId the destination concept ID.
	 * @param relationshipId the SNOMED&nbsp;CT identifier of the relationship.
	 */
	public SnomedIsAStatementWithId(final long sourceId, final long destinationId, final long relationshipId) {
		super(sourceId, destinationId);
		this.relationshipId = relationshipId;
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.IsAStatementWithId#getRelationshipId()
	 */
	@Override
	public long getRelationshipId() {
		return relationshipId;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("sourceId", getSourceId())
				.add("destinationId", getDestinationId())
				.add("relationshipId", relationshipId)
				.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.SnomedIsAStatement#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (int) (relationshipId ^ (relationshipId >>> 32));
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.SnomedIsAStatement#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final SnomedIsAStatementWithId other = (SnomedIsAStatementWithId) obj;
		if (relationshipId != other.relationshipId)
			return false;
		return true;
	}
}