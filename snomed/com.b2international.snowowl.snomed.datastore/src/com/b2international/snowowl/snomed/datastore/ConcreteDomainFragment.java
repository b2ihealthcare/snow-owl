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
package com.b2international.snowowl.snomed.datastore;

import java.io.Serializable;
import java.util.Objects;

/**
 * Bare minimum representation of a concrete domain.
 * 
 * @since
 */
public final class ConcreteDomainFragment implements Serializable {

	private static final long serialVersionUID = 2L;

	private final long refSetId;
	private final int group;
	private final String serializedValue;
	private final long typeId;
	private final boolean released;

	// For tracking the original member
	private final String memberId;

	public ConcreteDomainFragment(final String memberId, 
			final long refSetId, 
			final int group, 
			final String serializedValue, 
			final long typeId, 
			final boolean released) {
		
		this.memberId = memberId;
		this.refSetId = refSetId;
		this.group = group;
		this.serializedValue = serializedValue;
		this.typeId = typeId;
		this.released = released;
	}

	public long getRefSetId() {
		return refSetId;
	}

	public int getGroup() {
		return group;
	}

	/**
	 * @return the originating reference set member's UUID
	 */
	public String getMemberId() {
		return memberId;
	}
	
	public String getSerializedValue() {
		return serializedValue;
	}

	public long getTypeId() {
		return typeId;
	}
	
	public boolean isReleased() {
		return released;
	}

	@Override
	public int hashCode() {
		return Objects.hash(refSetId, group, serializedValue, typeId);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }

		final ConcreteDomainFragment other = (ConcreteDomainFragment) obj;

		if (refSetId != other.refSetId) { return false; }
		if (group != other.group) { return false; }
		if (!Objects.equals(serializedValue, other.serializedValue)) { return false; }
		if (typeId != other.typeId) { return false; }

		return true;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("ConcreteDomainFragment [serializedValue=");
		builder.append(serializedValue);
		builder.append(", typeId=");
		builder.append(typeId);
		builder.append(", group=");
		builder.append(group);
		builder.append(", refSetId=");
		builder.append(refSetId);
		builder.append(", memberId=");
		builder.append(memberId);
		builder.append("]");
		return builder.toString();
	}
}
