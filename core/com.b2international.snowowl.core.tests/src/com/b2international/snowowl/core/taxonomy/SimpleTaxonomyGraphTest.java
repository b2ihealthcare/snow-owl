/*
 * Copyright 2022-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.taxonomy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Test;

import com.b2international.commons.exceptions.NotFoundException;

/**
 * @since 8.0.1
 */
public class SimpleTaxonomyGraphTest {

	private SimpleTaxonomyGraph graph = new SimpleTaxonomyGraph(10, 10);
	
	@Test
	public void isBuilt() {
		assertFalse("Graph should start out in the not-built state.", graph.isBuilt());
	}
	
	@Test
	public void buildSetsFlag() {
		assertFalse("Graph build should report no errors.", graph.build());
		assertTrue("Graph should be built.", graph.isBuilt());
	}
	
	@Test
	public void getDescendantIdsWithoutBuild() {
		assertThrows(IllegalStateException.class, () -> graph.getDescendantIds("A"));
	}
	
	@Test
	public void getParentIdsWithoutBuild() {
		assertThrows(IllegalStateException.class, () -> graph.getParentIds("A"));
	}
	
	@Test
	public void getIndirectAncestorIdsWithoutBuild() {
		assertThrows(IllegalStateException.class, () -> graph.getIndirectAncestorIds("A"));
	}
	
	@Test
	public void updateEdge() {
		graph.addNode("A");
		graph.addNode("B");
		graph.addNode("C");
		
		/*
		 * addEdge(String, String) uses the source ID for identifying the edge, so calling
		 * it twice in succession will overwrite the first information given.
		 */
		graph.addEdge("A", "B");
		graph.addEdge("A", "C");
		assertFalse("Graph build should report no errors.", graph.build());
		
		assertThat(graph.getParentIds("A")).containsOnly("C");
	}
	
	@Test
	public void getDescendantIdsNull() {
		assertThrows(NullPointerException.class, () -> graph.getDescendantIds(null));
	}
	
	@Test
	public void getParentIdsNull() {
		assertThrows(NullPointerException.class, () -> graph.getParentIds(null));
	}
	
	@Test
	public void getIndirectAncestorIdsNull() {
		assertThrows(NullPointerException.class, () -> graph.getIndirectAncestorIds(null));
	}
	
	@Test
	public void getDescendantIdsUnknownId() {
		assertFalse("Graph build should report no errors.", graph.build());
		assertThrows(NotFoundException.class, () -> graph.getDescendantIds("A"));
	}
	
	@Test
	public void getParentIdsUnknownId() {
		assertFalse("Graph build should report no errors.", graph.build());
		assertThrows(NotFoundException.class, () -> graph.getParentIds("A"));
	}
	
	@Test
	public void getIndirectAncestorIdsUnknownId() {
		assertFalse("Graph build should report no errors.", graph.build());
		assertThrows(NotFoundException.class, () -> graph.getIndirectAncestorIds("A"));
	}
	
	@Test
	public void singleNode() {
		graph.addNode("A");
		assertFalse("Graph build should report no errors.", graph.build());
		
		assertThat(graph.getDescendantIds("A")).isEmpty();
		assertThat(graph.getParentIds("A")).isEmpty();
		assertThat(graph.getIndirectAncestorIds("A")).isEmpty();
	}
	
	@Test
	public void twoIsolatedNodes() {
		graph.addNode("A");
		graph.addNode("B");
		assertFalse("Graph build should report no errors.", graph.build());
		
		assertThat(graph.getDescendantIds("A")).isEmpty();
		assertThat(graph.getParentIds("A")).isEmpty();
		assertThat(graph.getIndirectAncestorIds("A")).isEmpty();
		assertThat(graph.getDescendantIds("B")).isEmpty();
		assertThat(graph.getParentIds("B")).isEmpty();
		assertThat(graph.getIndirectAncestorIds("B")).isEmpty();
	}
	
	@Test
	public void parentChild() {
		graph.addNode("A");
		graph.addNode("B");
		graph.addEdge("A", "B");
		assertFalse("Graph build should report no errors.", graph.build());
		
		assertThat(graph.getDescendantIds("A")).isEmpty();
		assertThat(graph.getParentIds("A")).containsOnly("B");
		assertThat(graph.getIndirectAncestorIds("A")).isEmpty();
		assertThat(graph.getDescendantIds("B")).containsOnly("A");
		assertThat(graph.getParentIds("B")).isEmpty();
		assertThat(graph.getIndirectAncestorIds("B")).isEmpty();
	}
	
	@Test
	public void ancestor() {
		graph.addNode("A");
		graph.addNode("B");
		graph.addNode("C");
		graph.addEdge("A", "B");
		graph.addEdge("B", "C");
		assertFalse("Graph build should report no errors.", graph.build());
		
		assertThat(graph.getDescendantIds("A")).isEmpty();
		assertThat(graph.getParentIds("A")).containsOnly("B");
		assertThat(graph.getIndirectAncestorIds("A")).containsOnly("C");
		
		assertThat(graph.getDescendantIds("B")).containsOnly("A");
		assertThat(graph.getParentIds("B")).containsOnly("C");
		assertThat(graph.getIndirectAncestorIds("B")).isEmpty();
		
		assertThat(graph.getDescendantIds("C")).containsOnly("A", "B");
		assertThat(graph.getParentIds("C")).isEmpty();
		assertThat(graph.getIndirectAncestorIds("C")).isEmpty();
	}
	
	@Test
	public void multipleAncestors() {
		graph.addNode("A1");
		graph.addNode("A2");
		graph.addNode("A3");
		graph.addNode("B");
		graph.addNode("C1");
		graph.addNode("C2");
		
		graph.addEdge("A1", "B");
		graph.addEdge("A2", "B");
		graph.addEdge("A3", "B");
		graph.addEdge("B", Set.of("C1", "C2"));
		assertFalse("Graph build should report no errors.", graph.build());
		
		assertThat(graph.getDescendantIds("A1")).isEmpty();
		assertThat(graph.getParentIds("A1")).containsOnly("B");
		assertThat(graph.getIndirectAncestorIds("A1")).containsOnly("C1", "C2");
		
		assertThat(graph.getDescendantIds("A2")).isEmpty();
		assertThat(graph.getParentIds("A2")).containsOnly("B");
		assertThat(graph.getIndirectAncestorIds("A2")).containsOnly("C1", "C2");
		
		assertThat(graph.getDescendantIds("A3")).isEmpty();
		assertThat(graph.getParentIds("A3")).containsOnly("B");
		assertThat(graph.getIndirectAncestorIds("A3")).containsOnly("C1", "C2");
		
		assertThat(graph.getDescendantIds("B")).containsOnly("A1", "A2", "A3");
		assertThat(graph.getParentIds("B")).containsOnly("C1", "C2");
		assertThat(graph.getIndirectAncestorIds("B")).isEmpty();
		
		assertThat(graph.getDescendantIds("C1")).containsOnly("A1", "A2", "A3", "B");
		assertThat(graph.getParentIds("C1")).isEmpty();
		assertThat(graph.getIndirectAncestorIds("C1")).isEmpty();
		
		assertThat(graph.getDescendantIds("C2")).containsOnly("A1", "A2", "A3", "B");
		assertThat(graph.getParentIds("C2")).isEmpty();
		assertThat(graph.getIndirectAncestorIds("C2")).isEmpty();
	}
	
	@Test
	public void deepTreeWithShortcut() {
		graph.addNode("A1");
		graph.addNode("A2");
		graph.addNode("A3");
		graph.addNode("B");
		graph.addNode("C");
		graph.addNode("D1");
		graph.addNode("D2");
		
		graph.addEdge("A1", Set.of("B", "D1")); // direct edge to root
		graph.addEdge("A2", "B");
		graph.addEdge("A3", "B");
		graph.addEdge("B", "C");
		graph.addEdge("C", Set.of("D1", "D2"));
		assertFalse("Graph build should report no errors.", graph.build());
		
		assertThat(graph.getDescendantIds("A1")).isEmpty();
		assertThat(graph.getParentIds("A1")).containsOnly("B", "D1");
		assertThat(graph.getIndirectAncestorIds("A1")).containsOnly("C", "D1", "D2");
		
		assertThat(graph.getDescendantIds("A2")).isEmpty();
		assertThat(graph.getParentIds("A2")).containsOnly("B");
		assertThat(graph.getIndirectAncestorIds("A2")).containsOnly("C", "D1", "D2");
		
		assertThat(graph.getDescendantIds("A3")).isEmpty();
		assertThat(graph.getParentIds("A3")).containsOnly("B");
		assertThat(graph.getIndirectAncestorIds("A3")).containsOnly("C", "D1", "D2");
		
		assertThat(graph.getDescendantIds("B")).containsOnly("A1", "A2", "A3");
		assertThat(graph.getParentIds("B")).containsOnly("C");
		assertThat(graph.getIndirectAncestorIds("B")).containsOnly("D1", "D2");
		
		assertThat(graph.getDescendantIds("C")).containsOnly("A1", "A2", "A3", "B");
		assertThat(graph.getParentIds("C")).containsOnly("D1", "D2");
		assertThat(graph.getIndirectAncestorIds("C")).isEmpty();
		
		assertThat(graph.getDescendantIds("D1")).containsOnly("A1", "A2", "A3", "B", "C");
		assertThat(graph.getParentIds("D1")).isEmpty();
		assertThat(graph.getIndirectAncestorIds("D1")).isEmpty();
		
		assertThat(graph.getDescendantIds("D2")).containsOnly("A1", "A2", "A3", "B", "C");
		assertThat(graph.getParentIds("D2")).isEmpty();
		assertThat(graph.getIndirectAncestorIds("D2")).isEmpty();
	}
	
	@Test
	public void descendantProcessing() {
		graph.addNode("A");
		graph.addNode("B");
		graph.addNode("C1");
		graph.addNode("C2");
		graph.addNode("D");
		graph.addNode("E");
		
		graph.addEdge("A", "B");
		graph.addEdge("B", "C1");
		graph.addEdge("C1", "D");
		graph.addEdge("C2", "D");
		graph.addEdge("D", "E");
		assertFalse("Graph build should report no errors.", graph.build());
		
		assertThat(graph.getDescendantIds("D")).containsOnly("A", "B", "C1", "C2");
		assertThat(graph.getDescendantIds("E")).containsOnly("A", "B", "C1", "C2", "D");
	}
	
	@Test
	public void buildUnknownSourceId() {
		graph.addNode("B");
		graph.addEdge("A", "B");
		assertTrue("Graph build should report an error.", graph.build());
	}
	
	@Test
	public void buildUnknownDestinationId() {
		graph.addNode("A");
		graph.addNode("B");
		graph.addEdge("A", Set.of("B", "C", "D"));
		assertTrue("Graph build should report an error.", graph.build());
	}
	
	@Test
	public void buildSelfCycle() {
		graph.addNode("A");
		graph.addNode("B");
		graph.addNode("C");
		graph.addEdge("A", Set.of("A", "B"));
		graph.addEdge("B", "C");
		assertTrue("Graph build should report an error.", graph.build());
	}
	
	@Test
	public void buildLongCycle() {
		graph.addNode("A");
		graph.addNode("B");
		graph.addNode("C");
		graph.addNode("D");
		graph.addNode("E");
		graph.addEdge("A", "B");
		graph.addEdge("B", "C");
		graph.addEdge("C", "D");
		graph.addEdge("D", Set.of("E", "B"));
		assertTrue("Graph build should report an error.", graph.build());
	}
	
	@Test
	public void removeEdgeDoesNotRemoveInboundEdgesAutomatically() throws Exception {
		graph.addNode("A");
		graph.addNode("B");
		graph.addNode("C");
		graph.addEdge("A", Set.of("B", "C"));
		graph.addEdge("B", "C");
		graph.removeNode("B");
		graph.removeEdge("B");
		assertTrue("Graph build should not report an error.", graph.build());
	}
}
