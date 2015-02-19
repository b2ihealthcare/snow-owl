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

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.singleton;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

import com.b2international.commons.hierarchy.HierarchicalElement;
import com.b2international.commons.hierarchy.PreorderIterator;
import com.google.common.base.Joiner;
import com.google.common.primitives.Ints;

/**
 * Test for checking the {@link PreorderIterator}.
 *
 */
public class PreorderIteratorTest {

	@Test
	public void testIterator() {
		
		Node node_14 = new Node("14");
		
		Node node_12 = new Node("12");
		Node node_13 = new Node("13");
		
		Node node_3 = new Node("3");
		Node node_4 = new Node("4");
		Node node_8 = new Node("8");
		Node node_10 = new Node("10");
		Node node_11 = new Node("11");
		
		Node node_2 = new Node("2");
		Node node_5 = new Node("5");
		Node node_7 = new Node("7");
		Node node_9 = new Node("9");
		
		Node node_1 = new Node("1");
		Node node_6 = new Node("6");
		
		node_14.parent = node_12;
		
		node_12.parent = node_8;
		node_12.children = singleton(node_14);
		node_13.parent = node_8;
		
		node_3.parent = node_2;
		node_4.parent = node_2;
		node_8.parent = node_7;
		node_8.children = newArrayList(node_12, node_13);
		node_10.parent = node_9;
		node_11.parent = node_9;
		
		node_2.parent = node_1;
		node_2.children = newArrayList(node_3, node_4);
		node_5.parent = node_1;
		node_7.parent = node_6;
		node_7.children = singleton(node_8);
		node_9.parent = node_6;
		node_9.children = newArrayList(node_10, node_11);
		
		node_1.children = newArrayList(node_2, node_5);
		node_6.children = newArrayList(node_7, node_9);
		
		String expected = Joiner.on("|").join(Ints.asList(1, 2, 3, 4, 5, 6, 7, 8, 12, 14, 13, 9, 10, 11));

		StringBuilder sb = new StringBuilder();
		final Iterator<Node> itr = new PreorderIterator<>(newArrayList(node_1, node_6));
		while (itr.hasNext()) {
			if (sb.length() > 0) {
				sb.append("|");
			}
			sb.append(String.valueOf(itr.next()));
		}
		
		Assert.assertEquals(expected, sb.toString());
		
	}
	
	private static final class Node implements HierarchicalElement<Node> {
		private String id;
		private Node parent;
		private Collection<Node> children;
		private Node(String id) {
			this.id = id;
		}
		@Override
		public Collection<Node> getChildren() {
			return children;
		}
		@Override
		public Node getParent() {
			return parent;
		}
		@Override
		public String toString() {
			return id;
		}
	}
	
	
}