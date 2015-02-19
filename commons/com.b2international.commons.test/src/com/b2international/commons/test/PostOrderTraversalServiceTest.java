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

import org.junit.Before;
import org.junit.Test;

import com.b2international.commons.graph.DirectedEdge;
import com.b2international.commons.graph.DirectedGraph;
import com.b2international.commons.graph.INodeVisitor;
import com.b2international.commons.graph.PostOrderTraversalService;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

/**
 * Unit test for {@link PostOrderTraversalService}.
 * 
 */
public class PostOrderTraversalServiceTest {
	
	private PostOrderTraversalService<Integer> service;
	private INodeVisitor<Integer> visitor;
	private final Builder<Integer> visitedNodeListBuilder = ImmutableList.builder();
	
	@Before
	public void init() {
		visitor = new INodeVisitor<Integer>() {
			@Override
			public void visit(Integer node) {
				System.out.println("Visited node: " + node);
				visitedNodeListBuilder.add(node);
			}
		};
		service = new PostOrderTraversalService<Integer>(visitor);
	}
	
	@Test
	public void test1() {
		// build directed acyclic graph
		DirectedGraph<Integer, DirectedEdge<Integer>> directedGraph = new DirectedGraph<Integer, DirectedEdge<Integer>>();
		directedGraph.addNode(1);
		directedGraph.addNode(2);
		directedGraph.addNode(3);
		directedGraph.addNode(4);
		directedGraph.addNode(5);
		directedGraph.addDirectedEdge(new DirectedEdge<Integer>(1, 2));
		directedGraph.addDirectedEdge(new DirectedEdge<Integer>(2, 3));
		directedGraph.addDirectedEdge(new DirectedEdge<Integer>(2, 4));
		directedGraph.addDirectedEdge(new DirectedEdge<Integer>(1, 5));
		
		
		// tested method calls
		service.traverse(directedGraph, 1);
		
		// assertions
		assertEquals(ImmutableList.of(3, 4, 2, 5, 1), visitedNodeListBuilder.build());
	}
	
	@Test
	public void test2() {
		// build directed acyclic graph
		DirectedGraph<Integer, DirectedEdge<Integer>> directedGraph = new DirectedGraph<Integer, DirectedEdge<Integer>>();
		directedGraph.addNode(1);
		directedGraph.addNode(2);
		directedGraph.addNode(3);
		directedGraph.addNode(4);
		directedGraph.addNode(5);
		directedGraph.addDirectedEdge(new DirectedEdge<Integer>(1, 2));
		directedGraph.addDirectedEdge(new DirectedEdge<Integer>(1, 3));
		directedGraph.addDirectedEdge(new DirectedEdge<Integer>(1, 4));
		directedGraph.addDirectedEdge(new DirectedEdge<Integer>(1, 5));
		
		
		// tested method calls
		service.traverse(directedGraph, 1);
		
		// assertions
		assertEquals(ImmutableList.of(2, 3, 4, 5, 1), visitedNodeListBuilder.build());
	}
	
	@Test(expected=StackOverflowError.class)
	public void test3() {
		// build cyclic graph
		DirectedGraph<Integer, DirectedEdge<Integer>> directedGraph = new DirectedGraph<Integer, DirectedEdge<Integer>>();
		directedGraph.addNode(1);
		directedGraph.addNode(2);
		directedGraph.addNode(3);
		directedGraph.addNode(4);
		directedGraph.addDirectedEdge(new DirectedEdge<Integer>(1, 2));
		directedGraph.addDirectedEdge(new DirectedEdge<Integer>(2, 3));
		directedGraph.addDirectedEdge(new DirectedEdge<Integer>(3, 4));
		directedGraph.addDirectedEdge(new DirectedEdge<Integer>(4, 1));
		
		// tested method calls
		service.traverse(directedGraph, 1);
	}
}