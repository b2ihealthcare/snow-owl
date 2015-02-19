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

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Determines the set of reachable nodes in a {@link DirectedGraph directed graph} using DFS.
 * 
 *
 * @param <N> the type of the graph nodes
 */
public class DfsReachabilityService<N> {

    /**
     * Returns the set of nodes reachable from the specified source node.
     * 
     * @param graph
     * @param sourceNode
     * @return the set of reachable nodes
     */
    public Set<N> getReachableNodes(DirectedGraph<N, DirectedEdge<N>> graph, N sourceNode) {
    	HashSet<DirectedEdge<N>> reachableEdges = Sets.<DirectedEdge<N>>newHashSet();
    	HashSet<N> reachableNodes = Sets.<N>newHashSet();
    	dfs(graph, sourceNode, reachableNodes, reachableEdges);
    	return ImmutableSet.copyOf(reachableNodes);
    }
    
    /**
     * Returns the set of edges reachable from the specified source node.
     * 
     * @param graph
     * @param sourceNode
     * @return the set of reachable edges
     */
    public Set<DirectedEdge<N>> getReachableEdges(DirectedGraph<N, DirectedEdge<N>> graph, N sourceNode) {
    	HashSet<DirectedEdge<N>> reachableEdges = Sets.<DirectedEdge<N>>newHashSet();
		HashSet<N> reachableNodes = Sets.<N>newHashSet();
		dfs(graph, sourceNode, reachableNodes, reachableEdges);
    	return ImmutableSet.copyOf(reachableEdges);
    }
    
    private void dfs(DirectedGraph<N, DirectedEdge<N>> g, N node, Set<N> reachableNodes, Set<DirectedEdge<N>> reachableEdges) { 
        for (DirectedEdge<N> e : g.getOutboundEdges(node)) {
            if (!reachableNodes.contains(e.getDestination())) {
            	reachableEdges.add(e);
            	reachableNodes.add(e.getDestination());
            	dfs(g, e.getDestination(), reachableNodes, reachableEdges);
            }
        }
    }
}