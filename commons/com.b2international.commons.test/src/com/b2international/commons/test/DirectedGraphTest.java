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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.b2international.commons.graph.DirectedEdge;
import com.b2international.commons.graph.DirectedGraph;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;


/**
 * Unit test for {@link DirectedGraph}.
 * 
 */
public class DirectedGraphTest {
	
	private DirectedGraph<Integer,DirectedEdge<Integer>> directedGraph;
	private DirectedEdge<Integer> edge12;
	private DirectedEdge<Integer> edge13;
	private DirectedEdge<Integer> edge23;
	private DirectedEdge<Integer> edge45;

	@Before
	public void init() {
		directedGraph = new DirectedGraph<Integer, DirectedEdge<Integer>>();
		directedGraph.addNode(1);
		directedGraph.addNode(2);
		directedGraph.addNode(3);
		directedGraph.addNode(4);
		directedGraph.addNode(5);
		edge12 = new DirectedEdge<Integer>(1, 2);
		directedGraph.addDirectedEdge(edge12);
		edge13 = new DirectedEdge<Integer>(1, 3);
		directedGraph.addDirectedEdge(edge13);
		edge23 = new DirectedEdge<Integer>(2, 3);
		directedGraph.addDirectedEdge(edge23);
		edge45 = new DirectedEdge<Integer>(4, 5);
		directedGraph.addDirectedEdge(edge45);
	}
	
	@Test
	public void testReverse() {
		// tested method call
		DirectedGraph<Integer, DirectedEdge<Integer>> reversedDirectedGraph = directedGraph.reverse();
		
		// assertions
		Set<DirectedEdge<Integer>> reversedGraphEdges = reversedDirectedGraph.getEdges();
		ImmutableSet<DirectedEdge<Integer>> expectedReversedEdges = ImmutableSet.of(
				new DirectedEdge<Integer>(2, 1), new DirectedEdge<Integer>(3, 1), 
				new DirectedEdge<Integer>(3, 2), new DirectedEdge<Integer>(5, 4));
		assertEquals(expectedReversedEdges, reversedGraphEdges);
		ImmutableSet<Integer> expectedNodes = ImmutableSet.of(1, 2, 3, 4, 5);
		Set<Integer> reversedGraphNodes = reversedDirectedGraph.getNodes();
		assertEquals(expectedNodes, reversedGraphNodes);
	}
	
	@Test
	public void testGetOutboundEdges() {
		// tested method calls
		List<DirectedEdge<Integer>> outboundEdges1 = directedGraph.getOutboundEdges(1);
		List<DirectedEdge<Integer>> outboundEdges2 = directedGraph.getOutboundEdges(2);
		List<DirectedEdge<Integer>> outboundEdges3 = directedGraph.getOutboundEdges(3);
		List<DirectedEdge<Integer>> outboundEdges4 = directedGraph.getOutboundEdges(4);
		List<DirectedEdge<Integer>> outboundEdges5 = directedGraph.getOutboundEdges(5);
		
		// assertions
		assertEquals(ImmutableList.of(edge12, edge13), outboundEdges1);
		assertEquals(ImmutableList.of(edge23), outboundEdges2);
		assertEquals(ImmutableList.of(), outboundEdges3);
		assertEquals(ImmutableList.of(edge45), outboundEdges4);
		assertEquals(ImmutableList.of(), outboundEdges5);
	}
	
	@Test
	public void testHasEdge() {
		// tested method calls
		boolean hasEdge12 = directedGraph.hasEdge(1, 2);
		boolean hasEdge21 = directedGraph.hasEdge(2, 1);
		boolean hasEdge24 = directedGraph.hasEdge(2, 4);
		
		// assertions
		assertTrue(hasEdge12);
		assertFalse(hasEdge21);
		assertFalse(hasEdge24);
	}
}