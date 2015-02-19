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
package com.b2international.commons.graph;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * Represents a directed graph.
 * 
 *
 * @param <N> the type of the graph nodes
 * @param <E> the type of the graph edges
 */
public class DirectedGraph<N, E extends DirectedEdge<N>> {

	private final Set<N> nodes = Sets.newHashSet();
	private final Set<E> edges = Sets.newHashSet();
	private final Multimap<N, E> nodeToOutboundEdgeMap = ArrayListMultimap.create();
	
	/**
	 * Static utility method to create a new {@link DirectedGraph} instance.
	 * 
	 * @return a new, empty directed graph
	 */
	public static <N, E extends DirectedEdge<N>> DirectedGraph<N, E> create() {
		return new DirectedGraph<N, E>();
	}
	
	/**
	 * Adds a node to the graph.
	 * 
	 * @param node the node to add
	 */
	public void addNode(N node) {
		checkNotNull(node, "Node must not be null.");
		nodes.add(node);
	}
	
	/**
	 * Adds a directed edge to the graph.
	 * 
	 * @param edge the edge to add
	 */
	public void addDirectedEdge(E edge) {
		checkNotNull(edge, "Edge must not be null.");
		checkArgument(nodes.contains(edge.getSource()), "Unknown graph node: " + edge.getSource());
		checkArgument(nodes.contains(edge.getDestination()), "Unknown graph node: " + edge.getDestination());
		edges.add(edge);
		nodeToOutboundEdgeMap.put(edge.getSource(), edge);
	}
	
	/**
	 * Returns an immutable copy of the set of nodes.
	 * 
	 * @return the set of nodes
	 */
	public Set<N> getNodes() {
		return ImmutableSet.copyOf(nodes);
	}
	
	/**
	 * Returns an immutable copy of the set of edges.
	 * 
	 * @return the set of edges
	 */
	public Set<E> getEdges() {
		return ImmutableSet.copyOf(edges);
	}
	
	/**
	 * Returns the outbound edges of the specified node, in the order they were added to the graph.
	 * 
	 * @param node
	 * @return
	 */
	public List<E> getOutboundEdges(N node) {
		checkNotNull(node, "Node must not be null.");
		return ImmutableList.copyOf(nodeToOutboundEdgeMap.get(node));
	}
	
	/**
	 * Returns true iff this directed graph has a directed edge between the specified source and
	 * destination nodes.
	 * 
	 * @param sourceNode the source of the edge
	 * @param destinationNode the destination of the edge
	 * @return true if this directed graph has a directed edge between the specified source and
	 * destination nodes, false otherwise 
	 */
	public boolean hasEdge(N sourceNode, N destinationNode) {
		for (E edge : edges) {
			if (sourceNode.equals(edge.getSource()) && destinationNode.equals(edge.getDestination()))
				return true;
		}
		return false;
	}
	
	/**
	 * Returns the reverse of this directed graph, which is another directed graph on the same set 
	 * of nodes with all of the edges reversed.
	 * 
	 * @return the reverse of this directed graph
	 */
	public DirectedGraph<N, DirectedEdge<N>> reverse() {
		DirectedGraph<N, DirectedEdge<N>> transposedGraph = new DirectedGraph<N, DirectedEdge<N>>();
		for (N node : nodes) {
			transposedGraph.addNode(node);
		}
		for (DirectedEdge<N> edge : edges) {
			transposedGraph.addDirectedEdge(edge.reverse());
		}
		return transposedGraph;
	}
	
	@Override
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer();
		try {
			stringBuffer.append("Nodes:\n------\n");
			Joiner.on('\n').appendTo(stringBuffer, nodes);
			stringBuffer.append("\nEdges:\n------\n");
			for (E edge : edges) {
				stringBuffer.append(edge.toString());
				stringBuffer.append('\n');
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return stringBuffer.toString();
	}
}