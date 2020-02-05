/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.commons.graph;

import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Implementation of the depth-first topological sorting algorithm.
 * 
 * @see <a href="http://en.wikipedia.org/wiki/Topological_sorting#Algorithms">Topological sorting algorithms</a>
 * 
 * @param <N> the type of the graph nodes
 * @param <E> the type of the graph edges
 */
public class TopologicalSort<N, E extends DirectedEdge<N>> {

	private final Set<N> visitedNodes = Sets.newHashSet();
	private final DirectedGraph<N, E> directedGraph;
	
	public TopologicalSort(DirectedGraph<N, E> directedGraph) {
		this.directedGraph = directedGraph;
	}

	/**
	 * Returns the graph nodes in an order such that for every edge <em>u => v</em>, 
	 * <em>u</em> comes before <em>v</em> in the ordering.
	 * 
	 * @return the sorted list of nodes
	 */
	public List<N> getSortedNodes() {
		Builder<N> sortedListBuilder = ImmutableList.builder();
		Set<N> nodesWithoutOutgoingEdges = getNodesWithoutOutgoingEdges();
		for (N node : nodesWithoutOutgoingEdges) {
			visit(node, sortedListBuilder);
		}
		return sortedListBuilder.build();
	}
	
	private void visit(N node, Builder<N> sortedListBuilder) {
		if (visitedNodes.contains(node))
			return;
		visitedNodes.add(node);
		Set<N> incomingEdgeStartNodes = getIncomingEdgeSourceNodes(node);
		for (N incomingNode : incomingEdgeStartNodes) {
			visit(incomingNode, sortedListBuilder);
		}
		sortedListBuilder.add(node);
	}

	/**
	 * @return the set of nodes, which don't have outgoing edges
	 */
	private Set<N> getNodesWithoutOutgoingEdges() {
		ImmutableSet.Builder<N> builder = ImmutableSet.builder();
		Set<N> nodes = directedGraph.getNodes();
		Set<E> edges = directedGraph.getEdges();
		for (N node : nodes) {
			boolean foundOutgoingEdge = false;
			for (E edge : edges) {
				if (edge.getSource().equals(node)) {
					foundOutgoingEdge = true;
					break;
				}
			}
			if (!foundOutgoingEdge)
				builder.add(node);
		}
		return builder.build();
	}

	/**
	 * @return the set of nodes, which have an edge to the specified node
	 */
	private Set<N> getIncomingEdgeSourceNodes(N node) {
		ImmutableSet.Builder<N> builder = ImmutableSet.builder();
		Set<E> edges = directedGraph.getEdges();
		for (E edge : edges) {
			if (edge.getDestination().equals(node)) {
				builder.add(edge.getSource());
			}
		}
		return builder.build();
	}
}