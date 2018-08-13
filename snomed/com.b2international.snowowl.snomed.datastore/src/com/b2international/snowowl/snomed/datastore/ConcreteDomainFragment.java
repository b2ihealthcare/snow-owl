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
import java.util.Objects;

import com.b2international.snowowl.snomed.core.domain.refset.DataType;
import com.google.common.base.Preconditions;

/**
 * Bare minimum representation of a concrete domain.
 * 
 * @since
 */
public final class ConcreteDomainFragment implements Serializable {

	private static final long serialVersionUID = -160835407410122373L;

	private final String value;
	private final String label;
	private final byte type;
	private final long uomId;
	private final long refSetId;

	// For tracking the original member
	private final String memberId;

	/**
	 * Creates a new instance.
	 * 
	 * @param value the data type value.
	 * @param label the label of the data type.
	 * @param type the ordinal of the type.
	 * @param uomId UOM concept ID.
	 */
	public ConcreteDomainFragment(final String value, 
			final String label, 
			final byte type, 
			final long uomId, 
			final long refSetId,
			final String memberId) {

		this.value = Preconditions.checkNotNull(value, "Value argument cannot be null.");
		this.label = Preconditions.checkNotNull(label, "Label argument cannot be null.");
		this.uomId = uomId;
		this.type = type;
		this.refSetId = refSetId;
		this.memberId = memberId;
	}

	/**
	 * Returns with the label of the concrete domain as {@code byte[]}.
	 * @return the label
	 * @see BytesRef
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Returns with the serialized value of the concrete domain as a {@code byte[]}.
	 * @return the serialized value.
	 * @see BytesRef
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Returns with the type ordinal of the data type.
	 * @return the type ordinal.
	 * @see DataType
	 */
	public byte getType() {
		return type;
	}

	/**
	 * Returns with the {@link DataType data type}.
	 * @return the data type.
	 */
	public DataType getDataType() {
		return DataType.get(type);
	}

	/**
	 * Returns with the ID of the UOM concept.
	 * @return the ID of the UOM concept.
	 */
	public long getUomId() {
		return uomId;
	}

	/**
	 * Returns with the identifier concept ID of the conrete domain member's reference set.
	 * @return the reference set identifier concept ID.
	 */
	public long getRefSetId() {
		return refSetId;
	}

	/**
	 * @return the originating reference set member's UUID
	 */
	public String getMemberId() {
		return memberId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(label, refSetId, type, uomId, value);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (!(obj instanceof ConcreteDomainFragment)) { return false; }

		final ConcreteDomainFragment other = (ConcreteDomainFragment) obj;

		if (!Objects.equals(label, other.label)) { return false; }
		if (!Objects.equals(value, other.value)) { return false; }
		if (refSetId != other.refSetId) { return false; }
		if (type != other.type) { return false; }
		if (uomId != other.uomId) { return false; }

		return true;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("ConcreteDomainFragment [value=");
		builder.append(value);
		builder.append(", label=");
		builder.append(label);
		builder.append(", type=");
		builder.append(type);
		builder.append(", uomId=");
		builder.append(uomId);
		builder.append(", refSetId=");
		builder.append(refSetId);
		builder.append(", memberId=");
		builder.append(memberId);
		builder.append("]");
		return builder.toString();
	}
}