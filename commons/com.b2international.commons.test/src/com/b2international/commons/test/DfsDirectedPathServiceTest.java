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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.b2international.commons.graph.DfsDirectedPathService;
import com.b2international.commons.graph.DirectedEdge;
import com.b2international.commons.graph.DirectedGraph;
import com.google.common.collect.ImmutableList;

/**
 * Unit test for {@link DfsDirectedPathService}.
 * 
 */
public class DfsDirectedPathServiceTest {
	
	DfsDirectedPathService<Integer, DirectedEdge<Integer>> service;
	
	@Before
	public void init() {
		service = new DfsDirectedPathService<Integer, DirectedEdge<Integer>>();
	}
	
	@Test
	public void testDAG() {
		// build directed acyclic graph
		DirectedGraph<Integer, DirectedEdge<Integer>> directedGraph = new DirectedGraph<Integer, DirectedEdge<Integer>>();
		directedGraph.addNode(1);
		directedGraph.addNode(2);
		directedGraph.addNode(3);
		directedGraph.addNode(4);
		directedGraph.addNode(5);
		directedGraph.addNode(6);
		directedGraph.addDirectedEdge(new DirectedEdge<Integer>(1, 3));
		directedGraph.addDirectedEdge(new DirectedEdge<Integer>(3, 6));
		directedGraph.addDirectedEdge(new DirectedEdge<Integer>(1, 2));
		directedGraph.addDirectedEdge(new DirectedEdge<Integer>(2, 3));
		directedGraph.addDirectedEdge(new DirectedEdge<Integer>(4, 5));
		
		// tested method calls
		List<DirectedEdge<Integer>> path16 = service.getPath(directedGraph, 1, 6);
		List<DirectedEdge<Integer>> path13 = service.getPath(directedGraph, 1, 3);
		List<DirectedEdge<Integer>> path31 = service.getPath(directedGraph, 3, 1);
		List<DirectedEdge<Integer>> path45 = service.getPath(directedGraph, 4, 5);
		
		// assertions
		assertTrue(path31.isEmpty());
		assertEquals(ImmutableList.of(new DirectedEdge<Integer>(1, 3), new DirectedEdge<Integer>(3, 6)), path16);
		assertEquals(ImmutableList.of(new DirectedEdge<Integer>(1, 3)), path13);
		assertEquals(ImmutableList.of(new DirectedEdge<Integer>(4, 5)), path45);
	}
	
	@Test
	public void testDirectedCyclicGraph() {
		// build directed graph
		DirectedGraph<Integer, DirectedEdge<Integer>> directedGraph = new DirectedGraph<Integer, DirectedEdge<Integer>>();
		directedGraph.addNode(1);
		directedGraph.addNode(2);
		directedGraph.addNode(3);
		directedGraph.addNode(4);
		directedGraph.addNode(5);
		directedGraph.addDirectedEdge(new DirectedEdge<Integer>(1, 2));
		directedGraph.addDirectedEdge(new DirectedEdge<Integer>(1, 3));
		directedGraph.addDirectedEdge(new DirectedEdge<Integer>(3, 4));
		directedGraph.addDirectedEdge(new DirectedEdge<Integer>(4, 5));
		directedGraph.addDirectedEdge(new DirectedEdge<Integer>(5, 3)); // cycle
		
		// tested method calls
		List<DirectedEdge<Integer>> path13 = service.getPath(directedGraph, 1, 3);
		List<DirectedEdge<Integer>> path15 = service.getPath(directedGraph, 1, 5);
		
		// assertions
		assertEquals(ImmutableList.of(
				new DirectedEdge<Integer>(1, 3),
				new DirectedEdge<Integer>(3, 4),
				new DirectedEdge<Integer>(4, 5)	
				), path15);
		assertEquals(ImmutableList.of(
				new DirectedEdge<Integer>(1, 3)
				), path13);
	}
}