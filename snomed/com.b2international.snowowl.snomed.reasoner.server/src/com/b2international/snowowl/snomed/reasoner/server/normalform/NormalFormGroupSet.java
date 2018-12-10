/*
 * Copyright 2013-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.reasoner.server.normalform;

import static com.google.common.collect.Lists.newArrayList;

import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.ints.IntSet;

/**
 * Represents a set of groups that do not allow redundant elements.
 */
final class NormalFormGroupSet extends AbstractSet<NormalFormGroup> {

	private final List<NormalFormGroup> groups = newArrayList();

	/**
	 * Adds the specified group to this set if it is not already present.
	 * <p>
	 * More formally, adds the specified group e to this set if no group e2 exists
	 * where e2.isSameOrStrongerThan(e) applies. If this set already contains such
	 * group, the call leaves the set unchanged and returns <code>false</code>.
	 * <p>
	 * If no current group can be matched this way to group e, the call removes all
	 * current e(i) members from the set where e.isSameOrStrongerThan(e(i)) applies,
	 * adds the new element, and returns <code>true</code>.
	 */
	@Override
	public boolean add(final NormalFormGroup e) {
		final List<NormalFormGroup> redundant = newArrayList();

		for (final NormalFormGroup existingGroup : groups) {
			if (existingGroup.isSameOrStrongerThan(e)) {
				return false;
			} else if (e.isSameOrStrongerThan(existingGroup)) {
				redundant.add(existingGroup);
			}
		}

		groups.removeAll(redundant);
		groups.add(e);
		return true;
	}

	/**
	 * Adds a group to the set, bypassing redundancy checks.
	 * 
	 * @see #add(NormalFormGroup)
	 */
	public boolean addUnique(final NormalFormGroup e) {
		return groups.add(e);
	}

	@Override
	public Iterator<NormalFormGroup> iterator() {
		return groups.iterator();
	}

	@Override
	public int size() {
		return groups.size();
	}

	public void adjustOrder(final NormalFormGroupSet other) {
		if (isEmpty()) { 
			return; 
		}
		
		/* 
		 * Assumptions: 
		 * - all groups in this set are either zero or unnumbered
		 * - all groups in "other" are zero or have a positive number
		 * - there are no group number collisions in "other"
		 */
		other.groups
			.stream()
			.filter(otherGroup -> otherGroup.getGroupNumber() > 0)
			.sorted(Comparator.comparingInt(otherGroup -> otherGroup.getGroupNumber()))
			.forEachOrdered(otherGroup -> this.groups
				.stream()
				.filter(group -> group.getGroupNumber() == NormalFormGroup.UNKOWN_GROUP && group.equals(otherGroup))
				.findFirst()
				.ifPresent(group -> {
					group.adjustOrder(otherGroup);
					group.setGroupNumber(otherGroup.getGroupNumber());
				}));
	}

	public void fillNumbers() {
		final IntSet numbersUsed = PrimitiveSets.newIntOpenHashSetWithExpectedSize(groups.size());
		
		this.groups
			.stream()
			.filter(group -> group.getGroupNumber() > 0)
			.forEachOrdered(group -> numbersUsed.add(group.getGroupNumber()));
		
		int groupNumber = 1;

		for (final NormalFormGroup group : groups) {
			if (group.getGroupNumber() != NormalFormGroup.UNKOWN_GROUP) {
				continue;
			}
			
			while (numbersUsed.contains(groupNumber)) {
				groupNumber++;
			}
				
			group.fillNumbers();
			group.setGroupNumber(groupNumber);
			groupNumber++;
		}
	}
}
