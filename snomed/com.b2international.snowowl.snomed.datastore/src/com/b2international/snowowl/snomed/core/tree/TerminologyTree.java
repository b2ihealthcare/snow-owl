/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.tree;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Multimap;

/**
 * @since 4.6
 */
public final class TerminologyTree {

	private final Map<String, SnomedConceptDocument> items;
	private final Multimap<String, String> subTypes;
	private final Multimap<String, String> superTypes;

	public TerminologyTree(Map<String, SnomedConceptDocument> items, Multimap<String, String> subTypes, Multimap<String, String> superTypes) {
		this.items = items;
		this.subTypes = subTypes;
		this.superTypes = superTypes;
	}

	/**
	 * @return
	 * @deprecated - will be removed in 4.7
	 */
	public Map<String, SnomedConceptDocument> getItems() {
		return items;
	}

	/**
	 * @return
	 * @deprecated - will be removed in 4.7
	 */
	public Multimap<String, String> getSubTypes() {
		return subTypes;
	}

	/**
	 * @return
	 * @deprecated - will be removed in 4.7
	 */
	public Multimap<String, String> getSuperTypes() {
		return superTypes;
	}

	/**
	 * Returns the node by its ID.
	 * 
	 * @param nodeId
	 * @return the node found with the given ID, never <code>null</code>.
	 * @throws IllegalArgumentException
	 *             if the given nodeId does not exist in the tree
	 */
	public SnomedConceptDocument getNode(String nodeId) {
		checkArgument(items.containsKey(nodeId), "Unknown node: '%s'", nodeId);
		return items.get(nodeId);
	}

	/**
	 * Returns node identified by the given
	 * 
	 * @param nodeIds
	 * @return
	 */
	public Collection<SnomedConceptDocument> getNodes(Set<String> nodeIds) {
		return FluentIterable.from(nodeIds).transform(new Function<String, SnomedConceptDocument>() {
			@Override
			public SnomedConceptDocument apply(String input) {
				return getNode(input);
			}
		}).toList();
	}

	/**
	 * Returns the ancestors (known by this tree) of the given nodeId up to the root of the tree, or an empty collection if the node does not have
	 * ancestors, or does not exist in the tree.
	 * 
	 * @param nodeId
	 * @return an immutable collection, never <code>null</code>
	 */
	public Collection<SnomedConceptDocument> getAncestors(String nodeId) {
		return getNodes(getAncestorIds(nodeId));
	}

	/**
	 * Returns the direct ancestors aka parents (known by this tree) of the given nodeId, or an empty {@link Collection} if the node does not have
	 * parents, or does not exist in the tree.
	 * 
	 * @param nodeId
	 * @return an immutable collection, never <code>null</code>
	 */
	public Collection<SnomedConceptDocument> getParents(String nodeId) {
		final Builder<SnomedConceptDocument> parents = ImmutableSet.builder();
		for (final String superType : superTypes.get(nodeId)) {
			parents.add(getNode(superType));
		}
		return parents.build();
	}

	/**
	 * Returns the ancestor identifiers of the node identified by the given nodeId.
	 * 
	 * @param nodeId
	 * @return
	 */
	public Set<String> getAncestorIds(String nodeId) {
		final Builder<String> ancestors = ImmutableSet.builder();
		for (String parent : superTypes.get(nodeId)) {
			ancestors.add(parent);
			ancestors.addAll(getAncestorIds(parent));
		}
		return ancestors.build();
	}

	/**
	 * Returns the parent identifiers of the node identified by the given nodeId.
	 * 
	 * @param nodeId
	 * @return an immutable collection, never <code>null</code>
	 */
	public Set<String> getParentIds(String nodeId) {
		return ImmutableSet.copyOf(superTypes.get(nodeId));
	}

	/**
	 * Returns the child identifiers of the node identified by the given nodeId.
	 * 
	 * @param nodeId
	 * @return an immutable collection, never <code>null</code>
	 */
	public Set<String> getChildIds(String nodeId) {
		return ImmutableSet.copyOf(subTypes.get(nodeId));
	}

	/**
	 * Returns the descendant identifiers of the node identified by the given nodeId.
	 * 
	 * @param nodeId
	 * @return an immutable collection, never <code>null</code>
	 */
	public Set<String> getDescendantIds(String nodeId) {
		final Builder<String> descendants = ImmutableSet.builder();
		for (String child : subTypes.get(nodeId)) {
			descendants.add(child);
			descendants.addAll(getDescendantIds(child));
		}
		return descendants.build();
	}

	/**
	 * Returns the proximal primitive parents of the node identified by the given ID. If the node does not exist in the tree, it will return an empty
	 * collection. TODO include shortcut when given node is primitive, requires removal or rethinking of how create new sibling concept works on the
	 * UI
	 * 
	 * @param nodeId
	 * @return an immutable collection, never <code>null</code>
	 */
	public Collection<SnomedConceptDocument> getProximalPrimitiveParents(String nodeId) {
		return getNodes(getProximalPrimitiveParentIds(nodeId));
	}

	/**
	 * 
	 * @param nodeId
	 * @return
	 */
	public Set<String> getProximalPrimitiveParentIds(String nodeId) {
		return getProximalPrimitiveParentIds(getAncestors(nodeId));
	}

	/**
	 * <p><i>Non-api, tests only</i></p>
	 * <p>
	 * Returns the identifiers of the proximal primitive parents based on the given ancestor {@link Iterable}. This is the internal implementation of
	 * the proximal primitive algorithm. This method should not be part of the API of this or any other class. Use
	 * {@link #getProximalPrimitiveParents(String)} or {@link #getProximalPrimitiveParentIds(String)} instead. 
	 * </p>
	 * 
	 * @param ancestors
	 * @return
	 */
	/* package */ Set<String> getProximalPrimitiveParentIds(final Iterable<SnomedConceptDocument> ancestors) {
		final Set<String> proximalPrimitiveParentIds = newHashSet();
		for (SnomedConceptDocument ancestor : ancestors) {
			if (ancestor.isPrimitive()) {
				final String primitiveAncestorId = ancestor.getId();
				if (proximalPrimitiveParentIds.isEmpty()) {
					proximalPrimitiveParentIds.add(primitiveAncestorId);
				} else {
					boolean doAdd = true;
					for (String id : newHashSet(proximalPrimitiveParentIds)) {
						// if the current candidate is a subtype of any already visited nodes, then replace those nodes
						if (isSubTypeOf(primitiveAncestorId, id)) {
							proximalPrimitiveParentIds.remove(id);
							proximalPrimitiveParentIds.add(primitiveAncestorId);
							doAdd = false;
						} else if (doAdd && isSuperTypeOf(primitiveAncestorId, id)) {
							// do NOT add the node if it is a super type of any currently selected primitives
							doAdd = false;
						}
					}
					if (doAdd) {
						proximalPrimitiveParentIds.add(primitiveAncestorId);
					}
				}
			}
		}
		return proximalPrimitiveParentIds;
	}

	/**
	 * Returns <code>true</code> if the given superType is a superType of the given subType according to this tree, otherwise returns
	 * <code>false</code>.
	 * 
	 * @param superType
	 * @param subType
	 * @return
	 */
	public boolean isSuperTypeOf(String superType, String subType) {
		return getDescendantIds(superType).contains(subType);
	}

	/**
	 * Returns <code>true</code> if the given subType is a subType of the given superType according to this tree, otherwise returns <code>false</code>
	 * .
	 * 
	 * @param subType
	 * @param superType
	 * @return
	 */
	public boolean isSubTypeOf(String subType, String superType) {
		return getAncestorIds(subType).contains(superType);
	}

}
