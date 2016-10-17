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
package com.b2international.snowowl.datastore.server.internal.cdo;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Objects;

import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDOListFactory;
import org.eclipse.emf.cdo.common.util.UnorderedListDifferenceAnalyzer;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class UnorderedListDifferenceAnalyzerTest {

	private static interface Change { };
	
	private static final class Add implements Change {
		public int i;
		public Object newObject;
		
		public Add(int i, Object newObject) {
			this.i = i;
			this.newObject = newObject;
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(i, newObject);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			
			Add other = (Add) obj;
			return i == other.i && Objects.equals(newObject, other.newObject);
		}

		@Override
		public String toString() {
			return String.format("Add [i=%s, newObject=%s]", i, newObject);
		}
	}
	
	private static final class Remove implements Change {
		public int i;
		public Object oldObject;
		
		public Remove(int i, Object oldObject) {
			this.i = i;
			this.oldObject = oldObject;
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(i, oldObject);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			
			Remove other = (Remove) obj;
			return i == other.i && Objects.equals(oldObject, other.oldObject);
		}

		@Override
		public String toString() {
			return String.format("Remove [i=%s, oldObject=%s]", i, oldObject);
		}
	}
	
	private final CDOList oldList = CDOListFactory.DEFAULT.createList(10, 0, 0, false);
	private final CDOList newList = CDOListFactory.DEFAULT.createList(10, 0, 0, false);
	private final List<Change> changes = newArrayList();
	
	private final UnorderedListDifferenceAnalyzer analyzer = new UnorderedListDifferenceAnalyzer() {
		@Override
		protected void createAddListChange(CDOList oldList, Object newObject, int index) {
			changes.add(new Add(index, newObject));
			super.createAddListChange(oldList, newObject, index);
		}
		
		@Override
		protected void createRemoveListChange(CDOList oldList, Object oldObject, int index) {
			changes.add(new Remove(index, oldObject));
			super.createRemoveListChange(oldList, oldObject, index);
		}
	};
	
	@Test
	public void testAddition() throws Exception {
		oldList.addAll(ImmutableList.of(1, 2, 3, 4, 5));
		newList.addAll(ImmutableList.of(1, 2, 3, 4, 5, 6));
		
		analyzer.createListChanges(oldList, newList);
		
		assertEquals(1, changes.size());
		assertEquals(new Add(5, 6), changes.get(0));
	}
	
	@Test
	public void testRemove() throws Exception {
		oldList.addAll(ImmutableList.of(1, 2, 3, 4, 5));
		newList.addAll(ImmutableList.of(1, 2, 3, 4));
		
		analyzer.createListChanges(oldList, newList);
		
		assertEquals(1, changes.size());
		assertEquals(new Remove(4, 5), changes.get(0));
	}
	
	@Test
	public void testUnorderedRemove() throws Exception {
		oldList.addAll(ImmutableList.of(1, 2, 3, 4, 5));
		newList.addAll(ImmutableList.of(5, 2, 3, 4));
		
		analyzer.createListChanges(oldList, newList);
		
		assertEquals(1, changes.size());
		assertEquals(new Remove(0, 1), changes.get(0));
	}
	
	@Test
	public void testMove() throws Exception {
		oldList.addAll(ImmutableList.of(1, 2, 3, 4, 5));
		newList.addAll(ImmutableList.of(5, 2, 4, 3, 1));
		
		analyzer.createListChanges(oldList, newList);
		
		assertEquals(0, changes.size());
	}
	
	@Test
	public void testAddAndRemove() throws Exception {
		oldList.addAll(ImmutableList.of(1, 2, 3, 4, 5));
		// Add 6 to index 2, remove 3 from index 3
		newList.addAll(ImmutableList.of(1, 2, 6, 5, 4));
		
		analyzer.createListChanges(oldList, newList);
		
		// Seen as removing 3 from its original index 2, then adding 6 at the end to index 4 
		assertEquals(2, changes.size());
		assertEquals(new Remove(2, 3), changes.get(0));
		assertEquals(new Add(4, 6), changes.get(1));
	}
	
	@Test
	public void testSmallAdd() throws Exception {
		for (int i = 0; i < 1_000_000; i++) {
			oldList.add(i);
			newList.add(i);
		}
		
		newList.add(1_000_000);
		
		analyzer.createListChanges(oldList, newList);
		
		assertEquals(1, changes.size());
		assertEquals(new Add(1_000_000, 1_000_000), changes.get(0));
	}
	
	@Test
	public void testSmallRemove() throws Exception {
		for (int i = 0; i < 1_000_000; i++) {
			oldList.add(i);
			newList.add(i);
		}
		
		newList.remove(500);
		
		analyzer.createListChanges(oldList, newList);
		
		assertEquals(1, changes.size());
		assertEquals(new Remove(500, 500), changes.get(0));
	}
	
	@Test(timeout=3000L)
	public void testNoChangeWorstCase() throws Exception {
		// Two lists in completely opposite element order results in n^2 iterations. 
		for (int i = 0; i < 20_000; i++) {
			oldList.add(i);
			newList.add(19_999 - i);
		}
		
		analyzer.createListChanges(oldList, newList);
		
		assertEquals(0, changes.size());
	}
}
