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
package com.b2international.commons.test;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.b2international.commons.graph.DirectedEdge;
import com.b2international.commons.graph.DirectedGraph;
import com.b2international.commons.graph.TopologicalSort;
import com.google.common.collect.ImmutableList;

/**
 * Unit test for {@link TopologicalSort}.
 * 
 */
public class TopologicalSortTest {

	private DirectedGraph<Integer, DirectedEdge<Integer>> directedGraph;

	@Before
	public void initGraph() {
		directedGraph = new DirectedGraph<Integer, DirectedEdge<Integer>>();
		directedGraph.addNode(1);
		directedGraph.addNode(7);
		directedGraph.addNode(5);
		directedGraph.addNode(3);
		directedGraph.addNode(11);
		directedGraph.addNode(8);
		directedGraph.addNode(2);
		directedGraph.addNode(9);
		directedGraph.addNode(10);
		directedGraph.addNode(12);
		directedGraph.addNode(13);
		directedGraph.addDirectedEdge(new DirectedEdge<Integer>(3, 8));
		directedGraph.addDirectedEdge(new DirectedEdge<Integer>(3, 10));
		directedGraph.addDirectedEdge(new DirectedEdge<Integer>(5, 11));
		directedGraph.addDirectedEdge(new DirectedEdge<Integer>(7, 8));
		directedGraph.addDirectedEdge(new DirectedEdge<Integer>(7, 11));
		directedGraph.addDirectedEdge(new DirectedEdge<Integer>(8, 9));
		directedGraph.addDirectedEdge(new DirectedEdge<Integer>(11, 2));
		directedGraph.addDirectedEdge(new DirectedEdge<Integer>(11, 9));
		directedGraph.addDirectedEdge(new DirectedEdge<Integer>(11, 10));
		directedGraph.addDirectedEdge(new DirectedEdge<Integer>(12, 13));
	}
	
	@Test
	public void testSort() {
		System.out.println(directedGraph);
		TopologicalSort<Integer, DirectedEdge<Integer>> topologicalSort = new TopologicalSort<Integer, DirectedEdge<Integer>>(directedGraph);
		List<Integer> sortedList = topologicalSort.getSortedNodes();
		System.out.println("Sorted nodes: " + sortedList);
		List<Integer> expectedSortedList = ImmutableList.of(1, 7, 5, 11, 2, 3, 8, 9, 10, 12, 13);
		Assert.assertEquals(expectedSortedList, sortedList);
	}
}