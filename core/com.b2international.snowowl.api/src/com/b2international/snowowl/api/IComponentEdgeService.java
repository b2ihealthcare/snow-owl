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

import com.b2international.snowowl.api.codesystem.exception.CodeSystemNotFoundException;
import com.b2international.snowowl.api.codesystem.exception.CodeSystemVersionNotFoundException;
import com.b2international.snowowl.api.domain.IComponentEdge;
import com.b2international.snowowl.api.domain.IComponentList;
import com.b2international.snowowl.api.domain.IComponentRef;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;

/**
 * Component edge service implementations allow exploring the connecting edges of a code system's graph view.
 * <p>
 * Nodes of the graph can be retrieved using an implementation of {@link IComponentNodeService}.
 * 
 * @param <E> the concrete component edge type (must implement {@link IComponentEdge})
 */
public interface IComponentEdgeService<E extends IComponentEdge> {

	/**
	 * Retrieves inbound edges for the node with the specified component reference.
	 * <p>
	 * This method supports paging: the returned {@link IComponentList} will include the total number of edges, 
	 * but can be restricted to only hold a subset of edges with the {@code offset} and {@code limit} parameters.
	 * 
	 * @param ref    the {@code IComponentRef} of the component to inspect for inbound edges (may not be {@code null})
	 * @param offset the starting offset in the list (may not be negative)
	 * @param limit  the maximum number of results to return (may not be negative)
	 * 
	 * @return the list of inbound edges for the component, sorted by source component identifier (never {@code null})
	 * 
	 * @throws CodeSystemNotFoundException        if a code system with the given short name is not registered
	 * @throws CodeSystemVersionNotFoundException if a code system version for the code system with the given identifier
	 *                                            is not registered
	 * @throws ComponentNotFoundException         if the component identifier does not match any component on the given
	 * 											  version, task
	 */
	IComponentList<E> getInboundEdges(IComponentRef ref, int offset, int limit);

	/**
	 * Retrieves outbound edges for the node with the specified component reference.
	 * <p>
	 * This method supports paging: the returned {@link IComponentList} will include the total number of edges, 
	 * but can be restricted to only hold a subset of edges with the {@code offset} and {@code limit} parameters.
	 * 
	 * @param ref    the {@code IComponentRef} of the component to inspect for outbound edges (may not be {@code null})
	 * @param offset the starting offset in the list (may not be negative)
	 * @param limit  the maximum number of results to return (may not be negative)
	 * 
	 * @return the list of outbound edges for the component, sorted by target component identifier (never {@code null})
	 * 
	 * @throws CodeSystemNotFoundException        if a code system with the given short name is not registered
	 * @throws CodeSystemVersionNotFoundException if a code system version for the code system with the given identifier
	 *                                            is not registered
	 * @throws ComponentNotFoundException         if the component identifier does not match any component on the given
	 * 											  version, task
	 */
	IComponentList<E> getOutboundEdges(IComponentRef ref, int offset, int limit);
}
