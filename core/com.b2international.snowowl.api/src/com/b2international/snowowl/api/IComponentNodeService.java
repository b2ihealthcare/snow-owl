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
package com.b2international.snowowl.api;

import java.util.List;

import com.b2international.snowowl.api.domain.IComponentList;
import com.b2international.snowowl.api.domain.IComponentNode;
import com.b2international.snowowl.api.domain.IComponentRef;
import com.b2international.snowowl.api.domain.IStorageRef;

/**
 * Terminology independent interface of the Component Node Service.
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link #getRootNodes() <em>Retrieve root nodes</em>}</li>
 *   <li>{@link #getNode(IComponentRef) <em>Retrieve single node by component reference</em>}</li>
 *   <li>{@link #getDescendants(IComponentRef, boolean) <em>Retrieve descendant nodes</em>}</li>
 *   <li>{@link #getAncestors(IComponentRef, boolean) <em>Retrieve ancestor nodes</em>}</li>
 * </ul>
 * 
 * @param <N> the concrete component node type (must implement {@link IComponentNode})
 * 
 */
public interface IComponentNodeService<N extends IComponentNode> {

	/**
	 * Returns the root nodes of the graph, serving as a starting point for further exploration. Root nodes have no
	 * inbound edges.
	 * @return a list of root nodes
	 */
	List<N> getRootNodes(final IStorageRef ref);

	/**
	 * Retrieves a single node with the specified component reference, if it exists.
	 * @param nodeRef the component reference to look for (may not be {@code null})
	 * @return the node standing in for the component pointed to by the reference
	 */
	N getNode(IComponentRef nodeRef);

	/**
	 * Returns the descendant nodes of the node with the specified component reference. Descendants are reachable by
	 * following inbound edges of a pre-determined type in the reverse direction.
	 * @param nodeRef the component reference to look for (may not be {@code null})
	 * @param direct {@code true} if only direct descendants should be returned, {@code false} if inbound edges should
	 * be followed recursively
	 * @param offset the starting offset in the list (may not be negative)
	 * @param limit the maximum number of results to return (may not be negative)
	 * @return a list of descendants for the component node
	 */
	IComponentList<N> getDescendants(IComponentRef nodeRef, boolean direct, int offset, int limit);

	/**
	 * Returns the ancestor nodes of the node with the specified component reference. Ancestors are reachable by
	 * following outbound edges of a pre-determined type in the standard direction.
	 * @param nodeRef the component reference to look for (may not be {@code null})
	 * @param direct {@code true} if only direct ancestors should be returned, {@code false} if outbound edges should
	 * be followed recursively
	 * @param offset the starting offset in the list (may not be negative)
	 * @param limit the maximum number of results to return (may not be negative)
	 * @return a list of ancestors for the component node
	 */
	IComponentList<N> getAncestors(IComponentRef nodeRef, boolean direct, int offset, int limit);
}