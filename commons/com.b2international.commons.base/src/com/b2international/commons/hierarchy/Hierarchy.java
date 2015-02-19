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
package com.b2international.commons.hierarchy;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

/**
 * Simple editable hierarchical structure where:
 * <ul>
 * <li>Each member may be added only once.</li>
 * <li>Each member may have arbitrary number of children or no child at all.</li>
 * <li>Each member may have exactly one parent or no parent at all.</li>
 * </ul>
 * 
 * @param <M> member type
 * 
 */
public class Hierarchy<M> {

	private final Set<M> topLevelMembers;
	private final Map<M, M> childToParentMap;

	public Hierarchy() {
		topLevelMembers = Sets.newHashSet();
		childToParentMap = Maps.newHashMap();
	}

	public void add(M member) {
		Preconditions.checkArgument(member != null, "Member to add must not be null");
		Preconditions.checkArgument(!contains(member), "Duplicate members are not allowed: " + member);

		topLevelMembers.add(member);
	}

	public void addAll(Iterable<M> members) {
		Preconditions.checkArgument(members != null, "Members to add must not be null");
		Set<M> membersToAdd = Sets.newHashSet(members);
		Set<M> actualMembers = doGetAllMembers();
		boolean notContainAny = Sets.intersection(actualMembers, membersToAdd).isEmpty();
		Preconditions.checkArgument(notContainAny, "Members to add most not contain any member that is already present in hierarchy");

		topLevelMembers.addAll(membersToAdd);
	}

	public void removeMemberAndAllSubTypes(M member) {
		Builder<M> membersToRemove = ImmutableSet.<M> builder();
		membersToRemove.add(member);
		membersToRemove.addAll(getAllChildren(member));
		for (M memberToRemove : membersToRemove.build()) {
			topLevelMembers.remove(memberToRemove);
			childToParentMap.remove(memberToRemove);
		}
	}

	public void removeMemberAndPreserveHierarchy(M member) {
		if (contains(member)) {
			M memberParent = getParent(member);
			childToParentMap.remove(member);
			preserveHierarchy(member, memberParent);
			topLevelMembers.remove(member);
		}
	}

	private void preserveHierarchy(M member, M newParent) {
		Collection<M> children = getChildren(member);
		for (M child : children) {
			setParent(newParent, child);
		}
	}

	public void setParent(M parent, M child) {
		Preconditions.checkArgument(child != null, "Child must not be null");
		Preconditions.checkArgument(contains(child), "Child must be present in hierarchy");
		Preconditions.checkArgument(parent == null || contains(parent), "Parent must be null or present in hierarchy otherwise");

		childToParentMap.remove(child);
		if (parent != null) {
			if (topLevelMembers.contains(child)) {
				topLevelMembers.remove(child);
			}
			childToParentMap.put(child, parent);
		} else {
			topLevelMembers.add(child);
		}
	}

	public Set<M> getChildren(M parent) {
		return ImmutableSet.copyOf(doGetChildren(parent));
	}

	private Collection<M> doGetChildren(M parent) {
		Preconditions.checkArgument(parent != null, "Parent must not be null");

		Set<Entry<M, M>> childEntries = Sets.filter(childToParentMap.entrySet(), new EntryValuePredicate<M>(parent));
		return Collections2.transform(childEntries, new EntryToKeyFunction<M>());
	}

	public Set<M> getAllChildren(M parent) {
		Builder<M> allChildren = ImmutableSet.<M> builder();
		for (M child : doGetChildren(parent)) {
			allChildren.add(child);
			allChildren.addAll(getAllChildren(child));
		}
		return allChildren.build();
	}

	public Set<M> getTopLevelMembers() {
		return ImmutableSet.copyOf(topLevelMembers);
	}

	public Set<M> getAllMembers() {
		return ImmutableSet.copyOf(doGetAllMembers());
	}

	private SetView<M> doGetAllMembers() {
		return Sets.union(topLevelMembers, childToParentMap.keySet());
	}

	public boolean contains(M member) {
		return topLevelMembers.contains(member) || childToParentMap.containsKey(member);
	}

	public M getParent(M member) {
		Preconditions.checkArgument(member != null, "Member must not be null");

		return childToParentMap.get(member);
	}

	public Set<M> getAllParents(M member) {
		Builder<M> allParents = ImmutableSet.<M> builder();
		M parent = getParent(member);
		while (parent != null) {
			allParents.add(parent);
			parent = getParent(parent);
		}
		return allParents.build();
	}

	public int size() {
		return topLevelMembers.size() + childToParentMap.keySet().size();
	}

	public boolean isEmpty() {
		return topLevelMembers.isEmpty();
	}

	public boolean isParentOf(M parent, M child) {
		return getAllChildren(parent).contains(child);
	}

	public void clear() {
		topLevelMembers.clear();
		childToParentMap.clear();
	}

}