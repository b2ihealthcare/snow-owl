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
package com.b2international.commons.test.collect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.NoSuchElementException;

import org.junit.Test;

import com.b2international.collections.PrimitiveLists;
import com.b2international.collections.longs.LongCollections;
import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongList;

/**
 * @since 4.7
 */
public class EmptyLongListTest {

	@Test
	public void create() {
		LongList emptyList = LongCollections.emptyList();
		assertEquals("Long list size should be 0.", 0, emptyList.size());
		assertTrue("Long list should be empty.", emptyList.isEmpty());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void add() {
		LongList emptyList = LongCollections.emptyList();
		emptyList.add(0L);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void addAll() {
		LongList emptyList = LongCollections.emptyList();
		LongList otherList = PrimitiveLists.newLongArrayList(1L, 2L, 3L);
		emptyList.addAll(otherList);
	}

	@Test(expected = NullPointerException.class)
	public void addAll_null() {
		LongList emptyList = LongCollections.emptyList();
		emptyList.addAll(null);
	}

	public void addAll_empty() {
		LongList emptyList = LongCollections.emptyList();
		emptyList.addAll(LongCollections.emptyList());
	}

	@Test
	public void equals_true() {
		LongList emptyList = LongCollections.emptyList();
		LongList otherList = LongCollections.emptyList();
		assertTrue("First list should be equal to second list.", emptyList.equals(otherList));
		assertTrue("Second list should be equal to first list.", otherList.equals(emptyList));
	}
	
	@Test
	public void equals_true_different_types() {
		LongList emptyList = LongCollections.emptyList();
		LongList otherList = PrimitiveLists.newLongArrayList();
		assertTrue("First list should be equal to second list.", emptyList.equals(otherList));
		assertTrue("Second list should be equal to first list.", otherList.equals(emptyList));
	}

	@Test
	public void equals_false() {
		LongList emptyList = LongCollections.emptyList();
		LongList otherList = PrimitiveLists.newLongArrayList();

		otherList.add(0L);
		otherList.add(1L);
		otherList.add(2L);

		assertFalse("First list should not be equal to second list.", emptyList.equals(otherList));
		assertFalse("Second list should not be equal to first list.", otherList.equals(emptyList));
	}

	@Test
	public void containsAll_same() {
		LongList emptyList = LongCollections.emptyList();
		LongList otherList = LongCollections.emptyList();
		assertTrue("First list should contain all elements from second list.", emptyList.containsAll(otherList));
		assertTrue("Second list should contain all elements from first list.", otherList.containsAll(emptyList));
	}

	@Test
	public void containsAll_non_empty() {
		LongList emptyList = LongCollections.emptyList();
		LongList otherList = PrimitiveLists.newLongArrayList(5L, 10L);

		assertFalse("Empty list should not contain all elements from second list.", emptyList.containsAll(otherList));
		assertTrue("Second list should not contain all elements from empty list.", otherList.containsAll(emptyList));
	}

	@Test(expected=NullPointerException.class)
	public void containsAll_null() {
		LongList emptyList = LongCollections.emptyList();
		emptyList.containsAll(null);
	}

	@Test
	public void trimToSize() {
		LongList emptyList = LongCollections.emptyList();
		emptyList.trimToSize();
	}

	@Test
	public void iterator_empty() {
		LongList emptyList = LongCollections.emptyList();
		LongIterator itr = emptyList.iterator();
		assertFalse("Iterator should indicate that there are no elements.", itr.hasNext());
	}

	@Test(expected=NoSuchElementException.class)
	public void iterator_overrun() {
		LongList emptyList = LongCollections.emptyList();
		LongIterator itr = emptyList.iterator();
		itr.next();
	}

	@Test
	public void retainAll() {
		LongList emptyList = LongCollections.emptyList();
		LongList otherList = PrimitiveLists.newLongArrayList(0L, 5L, 10L);

		emptyList.retainAll(otherList);
		
		assertEquals("Empty list should remain empty.", 0, emptyList.size());
		assertTrue("Non-empty list should not change.", otherList.get(0) == 0L 
				&& otherList.get(1) == 5L
				&& otherList.get(2) == 10L
				&& otherList.size() == 3);
	}

	@Test
	public void retainAll_opposite() {
		LongList emptyList = LongCollections.emptyList();
		LongList otherList = PrimitiveLists.newLongArrayList(0L, 5L, 10L);

		otherList.retainAll(emptyList);
		
		assertEquals("Empty list should remain empty.", 0, emptyList.size());
		assertEquals("Non-empty list should become empty.", 0, emptyList.size());
	}

	@Test(expected=NullPointerException.class)
	public void retainAll_null() {
		LongList emptyList = LongCollections.emptyList();
		emptyList.retainAll(null);
	}	

	@Test
	public void removeAll() {
		LongList emptyList = LongCollections.emptyList();
		LongList otherList = PrimitiveLists.newLongArrayList(0L, 5L, 10L);
		
		emptyList.removeAll(otherList);
		
		assertEquals("Empty list should remain empty.", 0, emptyList.size());
		assertTrue("Non-empty list should not change.", otherList.get(0) == 0L 
				&& otherList.get(1) == 5L
				&& otherList.get(2) == 10L
				&& otherList.size() == 3);
	}

	@Test
	public void removeAll_opposite() {
		LongList emptyList = LongCollections.emptyList();
		LongList otherList = PrimitiveLists.newLongArrayList(0L, 5L, 10L);
		
		otherList.removeAll(emptyList);
		
		assertEquals("Empty list should remain empty.", 0, emptyList.size());
		assertTrue("Non-empty list should not change.", otherList.get(0) == 0L 
				&& otherList.get(1) == 5L
				&& otherList.get(2) == 10L
				&& otherList.size() == 3);
	}

	@Test(expected=NullPointerException.class)
	public void removeAll_null() {
		LongList emptyList = LongCollections.emptyList();
		emptyList.removeAll(null);
	}	

	@Test
	public void removeAll_empty_list() {
		LongList emptyList = LongCollections.emptyList();
		LongList otherList = LongCollections.emptyList();
		emptyList.removeAll(otherList);
		assertEquals("Long list size should be 0.", 0, emptyList.size());
	}

	@Test
	public void toArray_empty() {
		LongList emptyList = LongCollections.emptyList();
		long[] array = emptyList.toArray();
		assertEquals("Array should be empty for empty lists.", 0, array.length);
	}

	@Test
	public void toString_empty() {
		LongList emptyList = LongCollections.emptyList();
		assertEquals("ToString output should be [] for the empty list.", "[]", emptyList.toString());
	}
}
