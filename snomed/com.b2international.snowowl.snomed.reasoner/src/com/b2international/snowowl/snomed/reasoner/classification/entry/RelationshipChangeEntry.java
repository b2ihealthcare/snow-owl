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
 * Represents a reasoner change entry capturing the details of inferred or redundant relationships.
 */
public class RelationshipChangeEntry extends RelationshipChangeEntryBase {

	private static final long serialVersionUID = 1L;

	private final byte group;
	private final byte unionGroup;
	private final ChangeConcept modifier;
	private final boolean destinationNegated;

	/**
	 * Creates a new relationship change entry with the specified arguments.
	 * 
	 * @param nature the change nature
	 * @param source the source component
	 * @param type the type component
	 * @param destination the destination component
	 * @param group the relationship's group
	 * @param unionGroup the relationship's union group
	 * @param modifier the modifier component
	 * @param destinationNegated {@code true} if the destination component is to be negated, {@code false} otherwise
	 */
	public RelationshipChangeEntry(final Nature nature, 
			final ChangeConcept source, 
			final ChangeConcept type,
			final ChangeConcept destination, 
			final byte group, 
			final byte unionGroup, 
			final ChangeConcept modifier,
			final boolean destinationNegated) {

		super(nature, source, type, destination);

		this.group = group;
		this.unionGroup = unionGroup;
		this.modifier = modifier;
		this.destinationNegated = destinationNegated;
	}

	/**
	 * @return the relationship group
	 */
	public byte getGroup() {
		return group;
	}

	/**
	 * @return the relationship union group
	 */
	public byte getUnionGroup() {
		return unionGroup;
	}

	/**
	 * @return the modifier component ("existential" or "universal")
	 */
	public ChangeConcept getModifier() {
		return modifier;
	}

	/**
	 * @return {@code true} if the destination component is to be negated, {@code false} otherwise
	 */
	public boolean isDestinationNegated() {
		return destinationNegated;
	}

	@Override 
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (destinationNegated ? 1231 : 1237);
		result = prime * result + group;
		result = prime * result + ((modifier == null) ? 0 : modifier.hashCode());
		result = prime * result + unionGroup;
		return result;
	}

	@Override 
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (!super.equals(obj)) { return false; }
		if (getClass() != obj.getClass()) { return false; }

		final RelationshipChangeEntry other = (RelationshipChangeEntry) obj;

		if (destinationNegated != other.destinationNegated) { return false; }
		if (group != other.group) { return false; }
		if (unionGroup != other.unionGroup) { return false; }
		if (!Objects.equal(modifier, other.modifier)) { return false; }
		return true;
	}
}
