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
 * @see IsAStatement
 *
 */
public class SnomedIsAStatementWithKey extends SnomedIsAStatement implements IsAStatementWithKey, Serializable {

	private static final long serialVersionUID = 5190490405208090193L;

	private final long storageKey;

	/**
	 * Creates a new triple of IDs representing the bare minimum of a SNOMED&nbsp;CT IS_A relationships.
	 * @param sourceId the value concept ID.
	 * @param destinationId the object concept ID.
	 * @param storageKey the storage key of the relationship.
	 */
	public SnomedIsAStatementWithKey(final long sourceId, final long destinationId, final long storageKey) {
		super(sourceId, destinationId);
		this.storageKey = storageKey;
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.IsAStatementWithKey#getStorageKey()
	 */
	@Override
	public long getStorageKey() {
		return storageKey;
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
				.add("storageKey", storageKey)
				.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (int) (storageKey ^ (storageKey >>> 32));
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final SnomedIsAStatementWithKey other = (SnomedIsAStatementWithKey) obj;
		if (storageKey != other.storageKey)
			return false;
		return true;
	}
}