/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;
import java.util.Map;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

/**
 * @since 6.14
 */
public class Graphs {

	/**
	 * @param nodesWithOutgoingEdges - the multimap that specifies the graph (should be a DAG)
	 * @return
	 */
	public static <N> List<N> getLongestPath(Multimap<N, N> nodesWithOutgoingEdges) {
		DirectedAcyclicGraph<N, DefaultEdge> graph = new DirectedAcyclicGraph<>(DefaultEdge.class);
		nodesWithOutgoingEdges.asMap().forEach((node, nodeNeighbors) -> {
			graph.addVertex(node);
			nodeNeighbors.forEach(neighbor -> {
				graph.addVertex(neighbor);
				graph.addEdge(node, neighbor);
			});
		});
		return getLongestPath(graph);
	}
	
	private static <N> List<N> getLongestPath(DirectedAcyclicGraph<N, DefaultEdge> graph) {
		if (graph.vertexSet().isEmpty()) {
			return null;
		}
		TopologicalOrderIterator<N, DefaultEdge> topSort = new TopologicalOrderIterator<>(graph);
		Map<N, List<N>> paths = Maps.newHashMapWithExpectedSize(graph.vertexSet().size());
		List<N> max = null;
		while (topSort.hasNext()) {
			N vertex = topSort.next();
			List<N> vertexPath = getOrCreatePath(paths, vertex);
			if (max == null) {
				max = vertexPath;
			}
			for (DefaultEdge outgoingEdge : graph.outgoingEdgesOf(vertex)) {
				N ancestor = graph.getEdgeTarget(outgoingEdge);
				List<N> ancestorPath = getOrCreatePath(paths, ancestor);
				if (ancestorPath.size() < vertexPath.size() + 1 /* weight */) {
					List<N> newPath = newArrayList();
					newPath.addAll(vertexPath);
					newPath.add(ancestor);
					paths.put(ancestor, newPath);
					if (max.size() < newPath.size()) {
						max = newPath;
					}
				}
			}
		}
		return max;
	}
	
	private static <N> List<N> getOrCreatePath(Map<N, List<N>> map, N vertex) {
        List<N> path = map.get(vertex);
        if (path == null) {
        	path = newArrayList();
        	path.add(vertex);
            map.put(vertex, path);
        }
        return path;
    }

	public static void main(String[] args) {
		List<String> l1 = getLongestPath(ImmutableMultimap.<String, String>builder()
				.putAll("A", "B", "D")
				.putAll("B", "C", "D")
				.putAll("C", "D", "E")
				.putAll("D", "E")
				.build());
		
		System.err.println(l1);
//		"A" -> "B" -> "C" -> "D" -> "E"
	}
	
}
