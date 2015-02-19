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
package com.b2international.snowowl.datastore.server;

import java.util.Iterator;

/**
 * Simple cycle detector to detect cycles in a graph by using depth-first search (DFS) algorithm.
 * 
 * @since 3.4
 */
public class CycleDetector {
	
	/**
	 * Checks if the given graph contains cycle or not.
	 * 
	 * @param graph
	 *            the {@link Graph} to check.
	 * @return <code>true</code> if the graph contains cycle, <code>false</code> otherwise.
	 */
	public boolean isCycleDirected(final Graph graph) {

		for (final Node node : graph.getNodes()) {

			if (isCyclicDirected(node)) {
				return true;
			}

		}
		
		return false;

	}

	/**
	 * Checks if the given node is cyclic directed or not.
	 * 
	 * @param node
	 *            the {@link Node} to check.
	 * @return <code>true</code> if the node is cyclic directed, false otherwise.
	 */
	public boolean isCyclicDirected(final Node node) {

		if (node.isVisited()) {

			return true;

		} else {

			node.setVisited(true);
			final Iterator<Node> iterator = node.getAdjacents().iterator();

			while (iterator.hasNext()) {

				final Node adjacentNode = iterator.next();

				if (adjacentNode.isVisited()) {

					return true;

				}

				if (isCyclicDirected(adjacentNode)) {

					return true;

				}

			}

		}

		node.setVisited(false);

		return false;

	}

}