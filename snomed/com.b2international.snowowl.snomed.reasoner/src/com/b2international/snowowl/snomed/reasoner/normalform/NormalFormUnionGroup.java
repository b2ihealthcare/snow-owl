/*
 * Copyright 2013-2018 B2i Healthcare Pte Ltd, http://b2i.sg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.snowowl.snomed.reasoner.normalform;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.List;
import java.util.Objects;

import com.google.common.collect.ImmutableList;

/**
 * Represents a relationship group, consisting of a(n optionally preserved)
 * group number and a list of union groups. The object (source concept) is not
 * stored with the group; it is assumed to be known in context.
 *
 * @author law223
 */
final class NormalFormUnionGroup implements NormalFormProperty {

	private final List<NormalFormProperty/*NormalFormRelationship|NormalFormMember*/> properties;
	private int unionGroupNumber;

	/**
	 * Creates a new union group instance from a single property.
	 * <p>
	 * The union group number is automatically set to 0.
	 *
	 * @param property the single property to associate with this union group (may
	 *                   not be <code>null</code>)
	 */
	public NormalFormUnionGroup(final NormalFormProperty property) {
		checkNotNull(property, "property");
		this.properties = ImmutableList.of(property);
		this.unionGroupNumber = ZERO_GROUP;
	}
	
	/**
	 * Creates a new union group instance.
	 *
	 * @param properties the properties to associate with this union group (may not be
	 *                    <code>null</code>)
	 */
	public NormalFormUnionGroup(final Iterable<NormalFormProperty> properties) {
		checkNotNull(properties, "properties");
		this.properties = ImmutableList.copyOf(properties);
		this.unionGroupNumber = UNKOWN_GROUP;
	}

	public List<NormalFormProperty> getProperties() {
		return properties;
	}

	public int getUnionGroupNumber() {
		return unionGroupNumber;
	}

	public void setUnionGroupNumber(final int unionGroupNumber) {
		checkState(this.unionGroupNumber == UNKOWN_GROUP, "Union group number is already set.");
		checkArgument(unionGroupNumber > 0, "Illegal union group number '%s'.", unionGroupNumber);
		this.unionGroupNumber = unionGroupNumber;
	}

	@Override
	public boolean isSameOrStrongerThan(final NormalFormProperty property) {
		if (this == property) { return true; }
		if (!(property instanceof NormalFormUnionGroup)) { return false; }
		
		final NormalFormUnionGroup other = (NormalFormUnionGroup) property;
		
		/*
		 * Things same or stronger than A OR B OR C:
		 *
		 * - A' OR B OR C, where A' is a subclass of A
		 * - B
		 *
		 * So we'll have to check for all of our properties to see if a less
		 * expressive fragment exists in the "other" union group. Points are
		 * awarded if we manage to get away with less properties than the
		 * "other" union group.
		 */
		return this.properties
			.stream()
			.allMatch(ourProperty -> other.properties
				.stream()
				.anyMatch(otherProperty -> ourProperty.isSameOrStrongerThan(otherProperty)));
	}

	@Override
	public int hashCode() {
		return Objects.hash(properties);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (!(obj instanceof NormalFormUnionGroup)) { return false; }

		final NormalFormUnionGroup other = (NormalFormUnionGroup) obj;

		if (properties.size() != other.properties.size()) { return false; }
		return properties.containsAll(other.properties);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("UnionGroup [properties=");
		builder.append(properties);
		builder.append("]");
		return builder.toString();
	}
}
