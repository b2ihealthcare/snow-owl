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

import com.google.common.base.Objects;

/**
 * Abstract base class for relationship related reasoner response entries.
 */
public abstract class RelationshipChangeEntryBase extends AbstractChangeEntry {

	private static final long serialVersionUID = 445232862733257314L;

	private final ChangeConcept type;
	private final ChangeConcept destination;

	protected RelationshipChangeEntryBase(final Nature nature, 
			final ChangeConcept source, 
			final ChangeConcept type,
			final ChangeConcept destination) {

		super(nature, source);

		this.type = type;
		this.destination = destination;
	}

	@Override 
	public ChangeConcept getType() {
		return type;
	}

	@Override 
	public ChangeConcept getDestination() {
		return destination;
	}

	@Override 
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((destination == null) ? 0 : destination.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override 
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (!super.equals(obj)) { return false; }
		if (getClass() != obj.getClass()) { return false; }

		final RelationshipChangeEntryBase other = (RelationshipChangeEntryBase) obj;

		if (!Objects.equal(destination, other.destination)) { return false; }
		if (!Objects.equal(type, other.type)) { return false; }
		return true;
	}
}
