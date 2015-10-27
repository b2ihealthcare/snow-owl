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
package com.b2international.snowowl.snomed.reasoner.classification.entry;

/**
 * Abstract base class for relationship related reasoner response entries.
 * @see RelationshipChangeEntry
 * @see RelationshipConcreteDomainChangeEntry
 */
public abstract class RelationshipChangeEntryBase extends AbstractChangeEntry {

	private static final long serialVersionUID = 445232862733257314L;

	private final LongComponent type;
	private final LongComponent destination;

	protected RelationshipChangeEntryBase(final Nature nature, final LongComponent source, final LongComponent type,
			final LongComponent destination) {
		super(nature, source);
		this.type = type;
		this.destination = destination;
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.reasoner.classification.entry.IChangeEntry#getType()
	 */
	@Override public LongComponent getType() {
		return type;
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.reasoner.classification.entry.IChangeEntry#getDestination()
	 */
	@Override public LongComponent getDestination() {
		return destination;
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.reasoner.classification.entry.AbstractChangeEntry#hashCode()
	 */
	@Override public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((destination == null) ? 0 : destination.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.reasoner.classification.entry.AbstractChangeEntry#equals(java.lang.Object)
	 */
	@Override public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final RelationshipChangeEntryBase other = (RelationshipChangeEntryBase) obj;
		if (destination == null) {
			if (other.destination != null)
				return false;
		} else if (!destination.equals(other.destination))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
}