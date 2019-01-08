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
package com.b2international.snowowl.snomed.reasoner.classification.entry;

import com.b2international.snowowl.snomed.snomedrefset.DataType;
import java.util.Objects;

/**
 * Represents a change entry of a concrete domain reference set member.
 */
public final class ConcreteDomainChangeEntry extends ChangeEntry {

	private static final long serialVersionUID = 2L;

	private final DataType dataType;
	private final String serializedValue;

	/**
	 * Creates a new concrete domain change entry instance with the specified arguments.
	 * 
	 * @param nature the change nature
	 * @param sourceId the relationship source component
	 * @param typeId the relationship type component
	 * @param destination the relationship destination component
	 * @param concreteDomainElement the contained concrete domain element
	 */
	public ConcreteDomainChangeEntry(final Nature nature, 
			final String sourceId, 
			final String typeId,
			final int group, 
			final DataType dataType,
			final String serializedValue) {

		super(nature, sourceId, typeId, group);

		this.dataType = dataType;
		this.serializedValue = serializedValue;
	}

	public DataType getDataType() {
		return dataType;
	}

	public String getSerializedValue() {
		return serializedValue;
	}

	@Override 
	public int hashCode() {
		return 31 * super.hashCode() + Objects.hash(serializedValue, dataType);
	}

	@Override 
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (!super.equals(obj)) { return false; }
		if (getClass() != obj.getClass()) { return false; }

		final ConcreteDomainChangeEntry other = (ConcreteDomainChangeEntry) obj;

		if (dataType != other.dataType) { return false; }
		if (!Objects.equals(serializedValue, other.serializedValue)) { return false; }

		return true;
	}
}
