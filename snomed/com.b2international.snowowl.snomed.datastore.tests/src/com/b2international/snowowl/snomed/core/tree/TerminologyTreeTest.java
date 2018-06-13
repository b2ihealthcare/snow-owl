/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.tree;

import static com.b2international.snowowl.snomed.datastore.id.RandomSnomedIdentiferGenerator.generateConceptId;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * @since 4.6
 */
public class TerminologyTreeTest {

	private static final String N1 = generateConceptId();
	private static final String N2 = generateConceptId();
	private static final String N3 = generateConceptId();
	private static final String N4 = generateConceptId();

	@Test(expected = IllegalArgumentException.class)
	public void getNodeThrowsExceptionIfDoesNotExist() throws Exception {
		final TerminologyTree tree = new TestTree().build();
		tree.getNode(N1);
	}
	
	@Test
	public void getNodeReturnsNodeIfExists() throws Exception {
		final TerminologyTree tree = new TestTree()
				.addNode(N1)
				.build();
		assertNotNull(tree.getNode(N1));
	}
	
	@Test
	public void getProximalPrimitiveParentsOfNonPrimitiveConceptWithoutAncestors() throws Exception {
		final TerminologyTree tree = new TestTree()
				.addNode(N1)
				.build();
		assertThat(tree.getProximalPrimitiveParents(N1)).isEmpty();
	}

	@Ignore("Fix create sibling concept creation to enable this feature in TerminologyTree")
	@Test
	public void getProximalPrimitiveParentsOfPrimitiveConcept() throws Exception {
		final TerminologyTree tree = new TestTree()
				.addNode(N1, true)
				.build();
		final SnomedConceptDocument n1 = tree.getNode(N1);
		assertThat(tree.getProximalPrimitiveParents(N1)).containsOnly(n1);
	}
	
	@Test
	public void getProximalPrimitiveParentsOfDefiningConceptWithOnePrimitiveParent() throws Exception {
		final TerminologyTree tree = new TestTree()
				.addNode(N1, true, null, newHashSet(N2))
					.addNode(N2, false, newHashSet(N1))
				.build();
		final SnomedConceptDocument n1 = tree.getNode(N1);
		assertThat(tree.getProximalPrimitiveParents(N2)).containsOnly(n1);
	}
	
	@Test
	public void getProximalPrimitiveParentsOfDefiningConceptWithTwoDistinctPrimitiveParents() throws Exception {
		final TerminologyTree tree = new TestTree()
				.addNode(N1, true, null, newHashSet(N3))
				.addNode(N2, true, null, newHashSet(N3))
					.addNode(N3, false, newHashSet(N1, N2))
				.build();
		final SnomedConceptDocument n1 = tree.getNode(N1);
		final SnomedConceptDocument n2 = tree.getNode(N2);
		assertThat(tree.getProximalPrimitiveParents(N3)).containsOnly(n1, n2);
	}
	
	@Test
	public void getProximalPrimitiveParentsOfDefiningConceptWithPrimitiveAncestor() throws Exception {
		final TerminologyTree tree = new TestTree()
				.addNode(N1, true, null, newHashSet(N2))
					.addNode(N2, false, newHashSet(N1), newHashSet(N3))
						.addNode(N3, false, newHashSet(N2))
				.build();
		final SnomedConceptDocument n1 = tree.getNode(N1);
		assertThat(tree.getProximalPrimitiveParents(N3)).containsOnly(n1);
	}
	
	@Test
	public void getProximalPrimitiveParentsOfDefiningConceptWithMultiplePrimitiveAncestors() throws Exception {
		final TerminologyTree tree = new TestTree()
				.addNode(N1, true, null, newHashSet(N2))
					.addNode(N2, true, newHashSet(N1), newHashSet(N3))
						.addNode(N3, false, newHashSet(N2))
				.build();
		final SnomedConceptDocument n2 = tree.getNode(N2);
		assertThat(tree.getProximalPrimitiveParents(N3)).containsOnly(n2);
	}
	
	@Test
	public void getProximalPrimitiveParentsOfDefiningConceptWithMultiplePrimitiveAncestorsViaLongRoutes() throws Exception {
		final TerminologyTree tree = new TestTree()
				.addNode(N1, true, null, newHashSet(N2, N4))
					.addNode(N2, true, newHashSet(N1), newHashSet(N3))
						.addNode(N3, false, newHashSet(N2), newHashSet(N4))
					.addNode(N4, false, newHashSet(N3, N1))
				.build();
		final SnomedConceptDocument n2 = tree.getNode(N2);
		assertThat(tree.getProximalPrimitiveParents(N4)).containsOnly(n2);
	}
	
	@Test
	public void getProximalPrimitiveWithFixedAncestorSet() throws Exception {
		final TerminologyTree tree = new TestTree()
				.addNode(N1, true, null, newHashSet(N2, N4))
					.addNode(N2, true, newHashSet(N1), newHashSet(N3))
						.addNode(N3, true, newHashSet(N2), newHashSet(N4))
					.addNode(N4, false, newHashSet(N3, N1))
				.build();
		final SnomedConceptDocument n3 = tree.getNode(N3);
		assertThat(tree.getProximalPrimitiveParents(N4)).containsOnly(n3);
		
		final SnomedConceptDocument n1 = tree.getNode(N1);
		final SnomedConceptDocument n2 = tree.getNode(N2);
		assertThat(tree.getProximalPrimitiveParentIds(newArrayList(n3, n1, n2))).containsOnly(N3);
	}
	
	@Test
	public void getProximalPrimitiveWithFixedAncestorSet_ReplacementTest() throws Exception {
		final TerminologyTree tree = new TestTree()
				.addNode(N1, true, null, newHashSet(N2))
					.addNode(N2, true, newHashSet(N1), newHashSet(N4))
				.addNode(N3, true, null, newHashSet(N4))
					.addNode(N4, false, newHashSet(N3, N2))
				.build();
		
		final SnomedConceptDocument n2 = tree.getNode(N2);
		final SnomedConceptDocument n3 = tree.getNode(N3);
		assertThat(tree.getProximalPrimitiveParents(N4)).containsOnly(n3, n2);
		
		final SnomedConceptDocument n1 = tree.getNode(N1);
		assertThat(tree.getProximalPrimitiveParentIds(newArrayList(n1, n3, n2))).containsOnly(N3, N2);
	}
	
	private static class TestTree {
		private final Map<String, SnomedConceptDocument> items = newHashMap();
		private final Multimap<String, String> subTypes = HashMultimap.create();
		private final Multimap<String, String> superTypes = HashMultimap.create();
		
		public TerminologyTree build() {
			return new TerminologyTree(items, subTypes, superTypes);
		}

		public TestTree addNode(String nodeId) {
			return addNode(nodeId, false);
		}
		
		public TestTree addNode(String nodeId, boolean primitive) {
			return addNode(nodeId, primitive, Collections.<String>emptySet());
		}
		
		public TestTree addNode(String nodeId, boolean primitive, Set<String> parents) {
			return addNode(nodeId, primitive, parents, Collections.<String>emptySet());
		}
		
		public TestTree addNode(String nodeId, boolean primitive, Set<String> parents, Set<String> children) {
			final SnomedConceptDocument entry = SnomedConceptDocument.builder()
					.id(nodeId)
					.iconId(Concepts.ROOT_CONCEPT)
					.active(true)
					.moduleId(Concepts.MODULE_ROOT)
					.primitive(primitive)
					.build();
					
			items.put(nodeId, entry);
			if (parents != null) {
				superTypes.putAll(nodeId, parents);
			}
			if (children != null) {
				subTypes.putAll(nodeId, children);
			}
			return this;
		}
		
	}
	
}
