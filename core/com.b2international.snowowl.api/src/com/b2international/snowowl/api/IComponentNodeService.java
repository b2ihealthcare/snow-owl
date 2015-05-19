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

import com.b2international.snowowl.api.codesystem.exception.CodeSystemNotFoundException;
import com.b2international.snowowl.api.codesystem.exception.CodeSystemVersionNotFoundException;
import com.b2international.snowowl.api.domain.IComponentList;
import com.b2international.snowowl.api.domain.IComponentNode;
import com.b2international.snowowl.api.domain.IComponentRef;
import com.b2international.snowowl.api.domain.IStorageRef;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;

/**
 * Component node service implementations allow browsing components of a code system as a directed, acyclic graph (DAG)
 * or tree.
 * <p>
 * Individual edges connecting graph nodes can be queried by {@link IComponentEdgeService}.
 * {@code IComponentNodeService} is only concerned with one particular edge type, usually corresponding to the
 * {@code IS A} relationship between two concepts of the code system, denoting inheritance.
 * <p>
 * Component representation as a graph node can differ from the result returned by the {@link IComponentService} for the
 * same component type.
 * 
 * @param <N> the concrete component node type (must implement {@link IComponentNode})
 */
public interface IComponentNodeService<N extends IComponentNode> {

	/**
	 * Returns the root nodes of the graph, serving as a starting point for further exploration.
	 * Root nodes have no ancestors.
	 * 
	 * @param ref the {@code IStorageRef} pointing to a version (and optionally, task) to read (may not be {@code null})
	 * 
	 * @return the list of root nodes for the given location
	 * 
	 * @throws CodeSystemNotFoundException        if a code system with the given short name is not registered
	 * @throws CodeSystemVersionNotFoundException if a code system version for the code system with the given identifier
	 *                                            is not registered
	 */
	List<N> getRootNodes(IStorageRef ref);

	/**
	 * Retrieves a single node for the specified {@link IComponentRef component reference}, if it exists.
	 * 
	 * @param ref the {@code IComponentRef} pointing to the component graph node to read (may not be {@code null})
	 * 
	 * @return the graph node standing in for the referenced component
	 * 
	 * @throws CodeSystemNotFoundException        if a code system with the given short name is not registered
	 * @throws CodeSystemVersionNotFoundException if a code system version for the code system with the given identifier
	 *                                            is not registered
	 * @throws ComponentNotFoundException         if the component identifier does not match any component on the given
	 * 											  version, task
	 */
	N getNode(IComponentRef ref);

	/**
	 * Returns the descendant nodes of the node with the specified component reference.
	 * <p>
	 * Descendants will be collected by following inbound active {@code IS A} edges in the reverse direction.
	 * <p>
	 * This method supports paging: the returned {@link IComponentList} will include the total number of ancestors, 
	 * but can be restricted to only hold a subset of all ancestor nodes with the {@code offset} and {@code limit} parameters.
	 *  
	 * @param ref    the {@code IComponentRef} of the component to inspect for descendants (may not be {@code null})
	 * @param direct {@code true} if only direct descendants should be returned, {@code false} if inbound edges should
	 *               be followed recursively
	 * @param offset the starting offset in the list (may not be negative)
	 * @param limit  the maximum number of results to return (may not be negative)
	 * 
	 * @return a list of descendants for the component node
	 * 
	 * @throws CodeSystemNotFoundException        if a code system with the given short name is not registered
	 * @throws CodeSystemVersionNotFoundException if a code system version for the code system with the given identifier
	 *                                            is not registered
	 * @throws ComponentNotFoundException         if the component identifier does not match any component on the given
	 * 											  version, task
	 */
	IComponentList<N> getDescendants(IComponentRef ref, boolean direct, int offset, int limit);

	/**
	 * Returns the ancestor nodes of the node with the specified component reference.
	 * <p>
	 * Ancestors will be collected by following outbound active {@code IS A} edges in the forward direction.
	 * <p>
	 * This method supports paging: the returned {@link IComponentList} will include the total number of ancestors, 
	 * but can be restricted to only hold a subset of all ancestor nodes with the {@code offset} and {@code limit} parameters.
	 * 
	 * @param ref    the {@code IComponentRef} of the component to inspect for descendants (may not be {@code null})
	 * @param direct {@code true} if only direct ancestors should be returned, {@code false} if outbound edges should
	 *               be followed recursively
	 * @param offset the starting offset in the list (may not be negative)
	 * @param limit  the maximum number of results to return (may not be negative)
	 * 
	 * @return a list of ancestors for the component node
	 * 
	 * @throws CodeSystemNotFoundException        if a code system with the given short name is not registered
	 * @throws CodeSystemVersionNotFoundException if a code system version for the code system with the given identifier
	 *                                            is not registered
	 * @throws ComponentNotFoundException         if the component identifier does not match any component on the given
	 * 											  version, task
	 */
	IComponentList<N> getAncestors(IComponentRef ref, boolean direct, int offset, int limit);
}
