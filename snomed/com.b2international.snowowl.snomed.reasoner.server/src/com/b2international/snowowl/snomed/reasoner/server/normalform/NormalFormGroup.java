/*
 * Copyright 2009 International Health Terminology Standards Development Organisation
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
 *
 * Copyright CSIRO Australian e-Health Research Centre (http://aehrc.com).
 * All rights reserved. Use is subject to license terms and conditions.
 */
package com.b2international.snowowl.snomed.reasoner.server.normalform;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.ints.IntSet;
import com.google.common.collect.ImmutableList;

/**
 * Represents a relationship group, consisting of a(n optionally preserved)
 * group number and a list of union groups. The object (source concept) is not
 * stored with the group; it is assumed to be known in context.
 *
 * @author law223
 */
final class NormalFormGroup implements NormalFormProperty {

	private final List<NormalFormUnionGroup> unionGroups;
	private int groupNumber;

	/**
	 * Creates a new group instance from a single union group.
	 * <p>
	 * The group number is automatically set to 0.
	 *
	 * @param unionGroup the single union group to associate with this group (may
	 *                   not be <code>null</code>)
	 */
	public NormalFormGroup(final NormalFormUnionGroup unionGroup) {
		checkNotNull(unionGroup, "unionGroup");
		this.unionGroups = ImmutableList.of(unionGroup);
		this.groupNumber = ZERO_GROUP;
	}
	
	/**
	 * Creates a new group instance.
	 *
	 * @param unionGroups the union groups to associate with this group (may not be
	 *                    <code>null</code>)
	 */
	public NormalFormGroup(final Iterable<NormalFormUnionGroup> unionGroups) {
		checkNotNull(unionGroups, "unionGroups");
		this.unionGroups = ImmutableList.copyOf(unionGroups);
		this.groupNumber = UNKOWN_GROUP;
	}

	public List<NormalFormUnionGroup> getUnionGroups() {
		return unionGroups;
	}

	public int getGroupNumber() {
		return groupNumber;
	}

	public void setGroupNumber(final int groupNumber) {
		checkState(this.groupNumber == UNKOWN_GROUP, "Group number is already set.");
		checkArgument(groupNumber > 0, "Illegal group number '%s'.", groupNumber);
		this.groupNumber = groupNumber;
	}

	@Override
	public boolean isSameOrStrongerThan(final NormalFormProperty property) {
		if (this == property) { return true; }
		if (!(property instanceof NormalFormGroup)) { return false; }
		
		final NormalFormGroup other = (NormalFormGroup) property;
		
		/*
		 * Things same or stronger than A AND B AND C:
		 *
		 * - A' AND B AND C, where A' is a subclass of A
		 * - A AND B AND C AND D
		 *
		 * So for each and every union group in "other", we'll have to find
		 * a more expressive union group in this group. Points are awarded
		 * if we have extra union groups not used in the comparison.
		 */
		return other.unionGroups
			.stream()
			.allMatch(otherUnionGroup -> this.unionGroups
				.stream()
				.anyMatch(ourUnionGroup -> ourUnionGroup.isSameOrStrongerThan(otherUnionGroup)));
	}

	@Override
	public int hashCode() {
		return Objects.hash(unionGroups);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (!(obj instanceof NormalFormGroup)) { return false; }

		final NormalFormGroup other = (NormalFormGroup) obj;

		if (unionGroups.size() != other.unionGroups.size()) { return false; }
		return unionGroups.containsAll(other.unionGroups);
	}

	public void adjustOrder(final NormalFormGroup other) {
		if (unionGroups.isEmpty()) { 
			return; 
		}
		
		/* 
		 * Assumptions: 
		 * - all union groups in this group are either zero or unnumbered
		 * - all union groups in "other" are zero or have a positive number
		 * - there are no union group number collisions in "other"
		 */
		other.unionGroups
			.stream()
			.filter(otherUnionGroup -> otherUnionGroup.getUnionGroupNumber() > 0)
			.sorted(Comparator.comparingInt(otherUnionGroup -> otherUnionGroup.getUnionGroupNumber()))
			.forEachOrdered(otherUnionGroup -> this.unionGroups
				.stream()
				.filter(unionGroup -> unionGroup.getUnionGroupNumber() == NormalFormUnionGroup.UNKOWN_GROUP && unionGroup.equals(otherUnionGroup))
				.findFirst()
				.ifPresent(unionGroup -> {
					unionGroup.setUnionGroupNumber(otherUnionGroup.getUnionGroupNumber());
				}));
	}

	public void fillNumbers() {
		final IntSet numbersUsed = PrimitiveSets.newIntOpenHashSetWithExpectedSize(unionGroups.size());
		
		this.unionGroups
			.stream()
			.filter(unionGroup -> unionGroup.getUnionGroupNumber() > 0)
			.forEachOrdered(unionGroup -> numbersUsed.add(unionGroup.getUnionGroupNumber()));
		
		int unionGroupNumber = 1;

		for (final NormalFormUnionGroup unionGroup : unionGroups) {
			if (unionGroup.getUnionGroupNumber() == NormalFormUnionGroup.UNKOWN_GROUP) {
				while (numbersUsed.contains(unionGroupNumber)) {
					unionGroupNumber++;
				}
				
				unionGroup.setUnionGroupNumber(unionGroupNumber);
				unionGroupNumber++;
			}
		}
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Group [unionGroups=");
		builder.append(unionGroups);
		builder.append("]");
		return builder.toString();
	}
}
