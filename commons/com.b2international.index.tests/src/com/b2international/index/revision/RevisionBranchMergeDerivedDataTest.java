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
package com.b2international.index.revision;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import com.b2international.commons.collections.Collections3;
import com.b2international.index.Doc;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.graph.MutableNetwork;
import com.google.common.graph.Network;
import com.google.common.graph.NetworkBuilder;

/**
 * @since 7.2
 */
public class RevisionBranchMergeDerivedDataTest extends BaseRevisionIndexTest {

	@Doc(revisionHash = {"id"})
	public static class Node extends Revision {

		private final Set<String> ancestors;

		public Node() {
			this(UUID.randomUUID().toString());
		}
		
		public Node(String id) {
			this(id, Collections.emptySet());
		}
		
		@JsonCreator
		public Node(
				@JsonProperty("id") String id, 
				@JsonProperty("ancestors") Set<String> ancestors) {
			super(id);
			this.ancestors = Collections3.toImmutableSet(ancestors);
		}
		
		public Set<String> getAncestors() {
			return ancestors;
		}
		
	}
	
	@Doc(revisionHash = {"id", "source", "target"})
	public static class Edge extends Revision {

		private final String source;
		private final String target;

		public Edge(Node source, Node target) {
			this(UUID.randomUUID().toString(), source.getId(), target.getId());
		}
		
		@JsonCreator
		public Edge(
				@JsonProperty("id") String id, 
				@JsonProperty("source") String source, 
				@JsonProperty("target") String target) {
			super(id);
			this.source = source;
			this.target = target;
		}
		
		public String getSource() {
			return source;
		}
		
		public String getTarget() {
			return target;
		}
		
		@Override
		protected ObjectId getContainerId() {
			return ObjectId.of(Node.class, source);
		}
		
	}
	
	@Override
	protected Collection<Class<?>> getTypes() {
		return ImmutableList.of(Node.class, Edge.class);
	}

	@Before
	public void before() throws Exception {
		index().hooks().addHook(derivedDataHook());
	}
	
	@Test
	public void mergeBranchWithDerivedData() throws Exception {
		final String a = createBranch(MAIN, "a");
		Node n1 = new Node();
		Node n2 = new Node();
		Node n3 = new Node();
		Edge e1 = new Edge(n2, n1);
		Edge e2 = new Edge(n3, n2);
		indexRevision(
			a,
			n1, n2, n3, // nodes
			e1, e2 		// edges
		);
		
		// merge task back to MAIN
		branching().prepareMerge(a, MAIN).merge();
		
		// verify derived data presence
		Node updatedN1 = getRevision(MAIN, Node.class, n1.getId());
		Node updatedN2 = getRevision(MAIN, Node.class, n2.getId());
		Node updatedN3 = getRevision(MAIN, Node.class, n3.getId());		
		
		Assertions.assertThat(updatedN1.getAncestors()).isEmpty();
		Assertions.assertThat(updatedN2.getAncestors()).contains(n1.getId());
		Assertions.assertThat(updatedN3.getAncestors()).contains(n1.getId(), n2.getId());
	}
	
	@Test
	public void rebaseBranchWithDerivedData() throws Exception {
		final String a = createBranch(MAIN, "a");
		Node n1 = new Node();
		Node n2 = new Node();
		Node n3 = new Node();
		Edge e1 = new Edge(n2, n1);
		Edge e2 = new Edge(n3, n2);
		indexRevision(
			MAIN,
			n1, n2, n3, // nodes
			e1, e2 		// edges
		);
		
		// merge task back to MAIN
		branching().prepareMerge(MAIN, a).merge();
		
		// verify derived data presence
		Node updatedN1 = getRevision(a, Node.class, n1.getId());
		Node updatedN2 = getRevision(a, Node.class, n2.getId());
		Node updatedN3 = getRevision(a, Node.class, n3.getId());		
		
		Assertions.assertThat(updatedN1.getAncestors()).isEmpty();
		Assertions.assertThat(updatedN2.getAncestors()).contains(n1.getId());
		Assertions.assertThat(updatedN3.getAncestors()).contains(n1.getId(), n2.getId());
	}
	
	@Test
	public void mergeDivergedBranchWithDerivedData() throws Exception {
		Node n2 = new Node("n2");
		indexRevision(MAIN,	n2);
		
		final String a = createBranch(MAIN, "a");
		
		// after creating the branch, index new nodes and edges on both branches
		Node n1 = new Node("n1");
		Edge e1 = new Edge(n2, n1);
		indexRevision(MAIN, n1, e1);
		
		Node n3 = new Node("n3");
		Edge e2 = new Edge(n3, n2);
		indexRevision(a, n3, e2);
		
		branching().prepareMerge(a, MAIN).merge();
		
		// verify derived data presence
		Node updatedN1 = getRevision(MAIN, Node.class, n1.getId());
		Node updatedN2 = getRevision(MAIN, Node.class, n2.getId());
		Node updatedN3 = getRevision(MAIN, Node.class, n3.getId());
		
		Assertions.assertThat(updatedN1.getAncestors()).isEmpty();
		Assertions.assertThat(updatedN2.getAncestors()).contains(n1.getId());
		Assertions.assertThat(updatedN3.getAncestors()).contains(n1.getId(), n2.getId());
	}
	
	@Test
	public void rebaseDivergedBranchWithDerivedData() throws Exception {
		Node n2 = new Node("n2");
		indexRevision(MAIN,	n2);
		
		final String a = createBranch(MAIN, "a");
		
		// after creating the branch, index new nodes and edges on both branches
		Node n1 = new Node("n1");
		Edge e1 = new Edge(n2, n1);
		indexRevision(MAIN, n1, e1);
		
		Node n3 = new Node("n3");
		Edge e2 = new Edge(n3, n2);
		indexRevision(a, n3, e2);
		
		branching().prepareMerge(MAIN, a).merge();
		
		// verify derived data presence
		Node updatedN1 = getRevision(a, Node.class, n1.getId());
		Node updatedN2 = getRevision(a, Node.class, n2.getId());
		Node updatedN3 = getRevision(a, Node.class, n3.getId());
		
		Assertions.assertThat(updatedN1.getAncestors()).isEmpty();
		Assertions.assertThat(updatedN2.getAncestors()).contains(n1.getId());
		Assertions.assertThat(updatedN3.getAncestors()).contains(n1.getId(), n2.getId());
	}
	
	private Hooks.PreCommitHook derivedDataHook() {
		return staging -> {
			// add all nodes and edges from the store first
			final Map<String, Node> nodes = newHashMap(Maps.uniqueIndex(staging.read(searcher -> searcher.search(Query.select(Node.class).where(Expressions.matchAll()).build())).getHits(), Node::getId));
			final Map<String, Edge> edges = newHashMap(Maps.uniqueIndex(staging.read(searcher -> searcher.search(Query.select(Edge.class).where(Expressions.matchAll()).build())).getHits(), Edge::getId));
			
			// then apply changes from the current staging area
			staging.getNewObjects().forEach(newObject -> {
				if (newObject instanceof Node) {
					Node node = (Node) newObject;
					nodes.put(node.getId(), node);
				} else if (newObject instanceof Edge) {
					Edge edge = (Edge) newObject;
					edges.put(edge.getId(), edge);
				}
			});
			staging.getChangedObjects().forEach(changedObject -> {
				if (changedObject instanceof Node) {
					Node node = (Node) changedObject;
					nodes.put(node.getId(), node);
				} else if (changedObject instanceof Edge) {
					Edge edge = (Edge) changedObject;
					edges.put(edge.getId(), edge);
				}
			});
			staging.getRemovedObjects().forEach(removedObject -> {
				if (removedObject instanceof Node) {
					Node node = (Node) removedObject;
					nodes.remove(node.getId());
				} else if (removedObject instanceof Edge) {
					Edge edge = (Edge) removedObject;
					edges.remove(edge.getId());
				}
			});
			
			MutableNetwork<Node, Edge> graph = NetworkBuilder.directed()
				    .allowsParallelEdges(true)
				    .expectedNodeCount(100)
				    .expectedEdgeCount(100)
				    .build();
			
			// build graph
			nodes.forEach((id, node) -> graph.addNode(node));
			edges.forEach((id, edge) -> graph.addEdge(nodes.get(edge.getSource()), nodes.get(edge.getTarget()), edge));
			
			// update ancestors derived data based on the built graph
			nodes.forEach((id, node) -> {
				Set<String> ancestors = getAncestors(node, graph);
				staging.stageChange(node, new Node(id, ancestors));
			});
		};
	}
	
	private Set<String> getAncestors(Node node, Network<Node, Edge> graph) {
		final Set<String> ancestors = newHashSet();
		
		graph.successors(node).forEach(parent -> {
			ancestors.add(parent.getId());
			ancestors.addAll(getAncestors(parent, graph));
		});
		
		return ancestors;
	}
	
}
