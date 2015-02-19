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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Multimap;

/**
 * Utility class for unweighted DAGs.
 *
 */
public class GraphUtils {
	
	/**
	 * Returns with the longest path from the DAG represented as the multimap argument.
	 * <p>Keys are nodes and there are a dedicated edged to each value associated with a key.  
	 * @param multimap the multimap representing a DAG.
	 * @return the longest path.
	 */
	public static <N> List<N> getLongestPath(final Multimap<N, N> multimap) {
		return GraphUtilsInternal.getLongestPath(checkNotNull(multimap, "multimap"));
	}
	
	/**
	 * Returns with the longest path from the DAG represented as the map argument.
	 * <p>Keys are nodes and there are a dedicated edged to each value associated with a key. 
	 * @param map the graph as a map.
	 * @return the longest path.
	 */
	public static <N> List<N> getLongestPath(final Map<? extends N, Collection<N>> map) {
		return GraphUtilsInternal.getLongestPath(checkNotNull(map, "map"));
	}
	
	private GraphUtils() {
		//suppress instantiation
	}
	
}