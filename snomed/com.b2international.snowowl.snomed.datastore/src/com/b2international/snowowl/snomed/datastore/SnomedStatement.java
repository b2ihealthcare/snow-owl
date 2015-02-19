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

import com.b2international.snowowl.snomed.datastore.IsAStatement.Statement;
import com.google.common.base.Objects;

/**
 * SNOMED&nbsp;CT statement representation.
 *
 */
public class SnomedStatement extends SnomedIsAStatement implements Statement, Serializable {

	private static final long serialVersionUID = 8404783505233211780L;

	private final long typeId;

	public SnomedStatement(final long sourceId, final long typeId, final long destinationId) {
		super(sourceId, destinationId);
		this.typeId = typeId;
	}

	@Override
	public long getTypeId() {
		return typeId;
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("source ID", getSourceId())
				.add("type ID", typeId)
				.add("destination ID", getDestinationId())
				.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (int) (typeId ^ (typeId >>> 32));
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof SnomedStatement))
			return false;
		final SnomedStatement other = (SnomedStatement) obj;
		if (getDestinationId() != other.getDestinationId())
			return false;
		if (getSourceId() != other.getSourceId())
			return false;
		if (typeId != other.typeId)
			return false;
		return true;
	}

	
	
	
}