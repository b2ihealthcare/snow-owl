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
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

/**
 * Finds a directed path in a {@link DirectedGraph directed graph} using DFS.
 * 
 *
 * @param <N> the type of the graph nodes
 */
public class DfsDirectedPathService<N, E extends DirectedEdge<N>> {

    /**
     * Returns the list of edges between the specified source and target nodes, if it exists.
     * If not, then returns an empty list.
     * 
     * @param graph
     * @param sourceNode the source node
     * @param targetNode the target node
     * @return the list of edges from the source node to the target node
     */
    public List<E> getPath(DirectedGraph<N, E> graph, N sourceNode, N targetNode) {
    	Stack<E> path = new Stack<E>();
    	HashSet<N> reachableNodes = Sets.<N>newHashSet();
    	dfs(graph, sourceNode, targetNode, reachableNodes, path);
    	return ImmutableList.copyOf(path);
    }
    
    private boolean dfs(DirectedGraph<N, E> g, N node, N targetNode, Set<N> visitedNodes, Stack<E> path) { 
    	boolean foundTargetNode = false;
        for (E e : g.getOutboundEdges(node)) {
            if (!visitedNodes.contains(e.getDestination())) {
            	path.push(e);
            	visitedNodes.add(e.getDestination());
            	if (targetNode.equals(e.getDestination())) {
            		return true;
            	}
				foundTargetNode = dfs(g, e.getDestination(), targetNode, visitedNodes, path);
            	if (!foundTargetNode)
            		path.pop();
            	else
            		return true;
            }
        }
        return foundTargetNode;
    }
}