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

import static com.b2international.commons.CompareUtils.isEmpty;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.util.Collections.emptyList;
import gr.forth.ics.graph.InspectableGraph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.PrimaryGraph;
import gr.forth.ics.graph.algo.Dags;
import gr.forth.ics.graph.path.Path;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;

/**
 * Internal utility class for DAGs. Used for hiding direct third party dependency.
 * 
 *
 */
/*default*/ class GraphUtilsInternal {

	/**
	 * Returns with the longest path from the DAG represented as the multimap argument.
	 * <p>Keys are nodes and there are a dedicated edged to each value associated with a key.  
	 * @param multimap the multimap representing a DAG.
	 * @return the longest path.
	 */
	/*default*/ static <N> List<N> getLongestPath(final Multimap<N, N> multimap) {
		checkNotNull(multimap, "multimap");
		return getLongestPath(multimap.asMap());
	}
	
	/**
	 * Returns with the longest path from the DAG represented as the map argument.
	 * <p>Keys are nodes and there are a dedicated edged to each value associated with a key. 
	 * @param map the graph as a map.
	 * @return the longest path.
	 */
	/*default*/ static <N> List<N> getLongestPath(final Map<? extends N, Collection<N>> map) {

		checkNotNull(map, "map");
		
		final Map<N, Collection<N>> copy = ImmutableMap.copyOf(map);
		
		final Map<N, Node> nodes = newHashMap();
		final PrimaryGraph graph = new PrimaryGraph();
		
		//initialize nodes in the graph
		for (final Entry<N, Collection<N>> entry : copy.entrySet()) {
			final N key = entry.getKey();
			
			if (!nodes.containsKey(key)) {
				nodes.put(key, graph.newNode(key));
			}
			
			final Iterable<? extends N> values = entry.getValue();
			if (!isEmpty(values)) {
				for (final N node : values) {
					if (!nodes.containsKey(node)) {
						nodes.put(node, graph.newNode(node));
					}
				}
			}
		}
		
		for (final N node : nodes.keySet()) {
			final Node graphNode = nodes.get(node);
			
			final Iterable<? extends N> targetNodes = copy.get(node);
			if (!isEmpty(targetNodes)) {
				for (final N targetNode : targetNodes) {
					graph.newEdge(graphNode, nodes.get(targetNode));
				}
			}
		}
		
		return getValueArray(getLongestPath(graph));
		
	}
	
	private static Path getLongestPath(final InspectableGraph graph) {
		return Dags.longestPath(checkNotNull(graph, "graph"));
	}
	
	@SuppressWarnings("unchecked")
	private static <N> List<N> getValueArray(final Path path) {
		if (null == path) {
			return emptyList();
		}
		final List<N> longestPath = newArrayList();
		longestPath.add((N) path.headNode().getValue());
        for (final Path s : path.steps()) {
            longestPath.add((N) s.tailNode().getValue());
        }
        return longestPath;
	}
	
	private GraphUtilsInternal() {
		//suppress instantiation
	}
	
	
}