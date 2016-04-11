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
package com.b2international.snowowl.datastore.server.version;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.b2international.commons.Change;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.index.diff.CompareResult;
import com.b2international.snowowl.datastore.index.diff.NodeDiff;
import com.b2international.snowowl.datastore.index.diff.VersionCompareConfiguration;
import com.google.common.collect.Multimap;

/**
 * Representation of a helper service responsible for building a hierarchy among changed components after a successful version compare process.
 *
 */
public interface VersionCompareHierarchyBuilder {

	/**
	 * Creates a placeholder unchanged item representing a component with its unique component ID on the given branch.
	 * 
	 * @param branchPath
	 *            the branch path.
	 * @param componentId
	 *            the unique component ID.
	 * @return a node representing an unchanged component.
	 */
	NodeDiff createUnchangedNode(final IBranchPath branchPath, final String componentId);

	/**
	 * Creates a new {@link NodeDiff node} representing a new, changed or detached component.
	 * 
	 * @param branchPath
	 *            the branch path where the component last existed.
	 * @param storageKey
	 *            the unique storage key of the component.
	 * @param change
	 *            the change.
	 * @return a node representing a changed component.
	 */
	NodeDiff createNode(final IBranchPath branchPath, final long storageKey, final Change change);

	/**
	 * Resolves labels for the given component ID set on the given {@link IBranchPath}.
	 * 
	 * @param componentIdsByBranch
	 * @return
	 */
	Map<String, String> resolveLabels(Multimap<IBranchPath, String> componentIdsByBranch);

	/**
	 * Returns with a set of direct ancestor component IDs of a component on a branch.
	 * 
	 * @param branchPath
	 *            the branch path.
	 * @param componentId
	 *            the component ID.
	 * @return a set of IDs representing the direct ancestors of the given component.
	 */
	Set<String> getSuperTypeIds(final IBranchPath branchPath, final String componentId);

	/**
	 * Returns {@code true} if the node argument representing a terminology component is a root node in the associated terminology. Otherwise
	 * {@code false}.
	 * 
	 * @param node
	 *            the node to test.
	 * @return {@code true} if the node is a root item. Otherwise {@code false}.
	 */
	boolean isRoot(final NodeDiff node);

	/**
	 * Creates the {@link CompareResult result} from the changed nodes.
	 * 
	 * @param configuration
	 *            configuration used for the version compare.
	 * @param changedNodes
	 *            the changed nodes.
	 * @return the result of the version compare operation.
	 */
	CompareResult createCompareResult(final VersionCompareConfiguration configuration, final Collection<NodeDiff> changedNodes);

	/**
	 * Collapses the hierarchy among the components represented as {@link NodeDiff}s
	 * 
	 * @param nodeDiffs
	 *            an iterable of node differences representing a hierarchy to collapse.
	 */
	void collapseHierarchy(Iterable<NodeDiff> nodeDiffs);

}