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

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.b2international.commons.graph.DfsReachabilityService;
import com.b2international.commons.graph.DirectedEdge;
import com.b2international.commons.graph.DirectedGraph;
import com.google.common.collect.ImmutableSet;

/**
 * Unit test for {@link DfsReachabilityService}.
 * 
 */
public class DfsReachabilityServiceTest {
	
	DfsReachabilityService<Integer> service;
	
	@Before
	public void init() {
		service = new DfsReachabilityService<Integer>();
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
		directedGraph.addDirectedEdge(new DirectedEdge<Integer>(1, 2));
		directedGraph.addDirectedEdge(new DirectedEdge<Integer>(1, 3));
		directedGraph.addDirectedEdge(new DirectedEdge<Integer>(2, 3));
		directedGraph.addDirectedEdge(new DirectedEdge<Integer>(4, 5));
		
		// tested method calls
		Set<Integer> reachableNodesFrom1 = service.getReachableNodes(directedGraph, 1);
		Set<Integer> reachableNodesFrom2 = service.getReachableNodes(directedGraph, 2);
		Set<Integer> reachableNodesFrom3 = service.getReachableNodes(directedGraph, 3);
		Set<Integer> reachableNodesFrom4 = service.getReachableNodes(directedGraph, 4);
		Set<Integer> reachableNodesFrom5 = service.getReachableNodes(directedGraph, 5);
		
		// assertions
		assertEquals(ImmutableSet.of(2, 3), reachableNodesFrom1);
		assertEquals(ImmutableSet.of(3), reachableNodesFrom2);
		assertEquals(ImmutableSet.of(), reachableNodesFrom3);
		assertEquals(ImmutableSet.of(5), reachableNodesFrom4);
		assertEquals(ImmutableSet.of(), reachableNodesFrom5);
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
		Set<Integer> reachableNodesFrom1 = service.getReachableNodes(directedGraph, 1);
		Set<Integer> reachableNodesFrom2 = service.getReachableNodes(directedGraph, 2);
		Set<Integer> reachableNodesFrom3 = service.getReachableNodes(directedGraph, 3);
		Set<Integer> reachableNodesFrom4 = service.getReachableNodes(directedGraph, 4);
		Set<Integer> reachableNodesFrom5 = service.getReachableNodes(directedGraph, 5);
		
		// assertions
		assertEquals(ImmutableSet.of(2, 3, 4, 5), reachableNodesFrom1);
		assertEquals(ImmutableSet.of(), reachableNodesFrom2);
		assertEquals(ImmutableSet.of(3, 4, 5), reachableNodesFrom3);
		assertEquals(ImmutableSet.of(3, 4, 5), reachableNodesFrom4);
		assertEquals(ImmutableSet.of(3, 4, 5), reachableNodesFrom5);
	}
}