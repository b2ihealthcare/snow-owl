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

import com.b2international.snowowl.api.domain.IComponentEdge;
import com.b2international.snowowl.api.domain.IComponentList;
import com.b2international.snowowl.api.domain.IComponentRef;

/**
 * Terminology independent interface of the Component Edge Service.
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link #getInboundEdges(IComponentRef) <em>Retrieve inbound edges</em>}</li>
 *   <li>{@link #getOutboundEdges(IComponentRef) <em>Retrieve outbound edges</em>}</li>
 * </ul>
 * 
 * @param <E> the concrete component edge type (must implement {@link IComponentEdge}
 * 
 */
public interface IComponentEdgeService<E extends IComponentEdge> {

	/**
	 * Retrieves a segment of the list of inbound edges for the specified component reference.
	 * @param nodeRef the component reference to look for (may not be {@code null})
	 * @param offset the starting offset in the list (may not be negative)
	 * @param limit the maximum number of results to return (may not be negative)
	 * @return the list of inbound edges for the component, sorted by source component reference (never {@code null}) 
	 */
	IComponentList<E> getInboundEdges(IComponentRef nodeRef, int offset, int limit);

	/**
	 * Retrieves a segment of the list of outbound edges for the specified component reference.
	 * @param nodeRef the component reference to look for (may not be {@code null})
	 * @param offset the starting offset in the list (may not be negative)
	 * @param limit the maximum number of results to return (may not be negative)
	 * @return the list of outbound edges for the component, sorted by target component reference (never {@code null}) 
	 */
	IComponentList<E> getOutboundEdges(IComponentRef nodeRef, int offset, int limit);
}