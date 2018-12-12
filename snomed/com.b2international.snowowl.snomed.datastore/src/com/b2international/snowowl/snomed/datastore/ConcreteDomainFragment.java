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

import com.b2international.snowowl.snomed.snomedrefset.DataType;

/**
 * Bare minimum representation of a concrete domain.
 */
public class ConcreteDomainFragment implements Serializable {

	private static final long serialVersionUID = 2L;

	private final String id;
	private final String serializedValue;
	private final long typeId;
	private final long storageKey;
	private final long refSetId;
	private final int group;

	public ConcreteDomainFragment(final String id,
			final String serializedValue, 
			final long typeId, 
			final long storageKey, 
			final long refSetId, 
			final int group) {
		
		this.id = id;
		this.serializedValue = serializedValue;
		this.typeId = typeId;
		this.storageKey = storageKey;
		this.refSetId = refSetId;
		this.group = group;
	}

	/**
	 * @return the data type (derived from the reference set SCTID)
	 */
	public DataType getDataType() {
		return SnomedRefSetUtil.getDataType(Long.toString(refSetId));
	}

	public String getId() {
		return id;
	}
	
	public String getSerializedValue() {
		return serializedValue;
	}

	public long getTypeId() {
		return typeId;
	}

	public long getStorageKey() {
		return storageKey;
	}

	public long getRefSetId() {
		return refSetId;
	}

	public int getGroup() {
		return group;
	}

	@Override
	public int hashCode() {
		return Objects.hash(serializedValue, typeId, storageKey, refSetId, group);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }

		final ConcreteDomainFragment other = (ConcreteDomainFragment) obj;

		if (!Objects.equals(serializedValue, other.serializedValue)) { return false; }
		if (typeId != other.typeId) { return false; }
		if (storageKey != other.storageKey) { return false; }
		if (refSetId != other.refSetId) { return false; }
		if (group != other.group) { return false; }

		return true;
	}

	@Override
	public String toString() {
		return "ConcreteDomainFragment [id=" + id + ", serializedValue=" + serializedValue + ", typeId=" + typeId + ", storageKey=" + storageKey
				+ ", refSetId=" + refSetId + ", group=" + group + "]";
	}

	
}
