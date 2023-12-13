/*
 * Copyright 2022 B2i Healthcare, https://b2ihealthcare.com
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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.NoSuchElementException;

import org.junit.Test;

import com.b2international.collections.longs.LongIterator;
import com.b2international.commons.collect.LongOrderedSet;
import com.b2international.commons.collect.LongOrderedSetImpl;

/**
 * @since 8.0.1
 */
public class LongOrderedSetTest {

	@Test
	public void create() {
		LongOrderedSet orderedSet = new LongOrderedSetImpl();
		assertTrue("Ordered set should be empty.", orderedSet.isEmpty());
	}

	@Test
	public void create_zero_size() {
		LongOrderedSet orderedSet = new LongOrderedSetImpl(0);
		assertTrue("Ordered set should be empty.", orderedSet.isEmpty());
	}

	@Test(expected=IllegalArgumentException.class)
	public void create_negative_size() {
		new LongOrderedSetImpl(-1);
	}

	@Test
	public void add() {
		LongOrderedSet orderedSet = new LongOrderedSetImpl();
		orderedSet.add(0L);
		orderedSet.add(5L);
		orderedSet.add(10L);
		assertEquals("Ordered set size should be 3.", 3, orderedSet.size());
		assertTrue("Ordered set should contain element 0.", orderedSet.contains(0L));
		assertTrue("Ordered set should contain element 5.", orderedSet.contains(5L));
		assertTrue("Ordered set should contain element 10.", orderedSet.contains(10L));
	}

	@Test
	public void add_twice() {
		LongOrderedSet orderedSet = new LongOrderedSetImpl();
		assertTrue("First attempt at adding 0 to the set should modify the set.", orderedSet.add(0L));
		assertTrue("First attempt at adding 5 to the set should modify the set.", orderedSet.add(5L));
		assertFalse("Second attempt at adding 0 to the set should not modify the set.", orderedSet.add(0L));
		assertFalse("Second attempt at adding 5 to the set should not modify the set.", orderedSet.add(5L));
		assertEquals("Ordered set size should be 2.", 2, orderedSet.size());
		assertTrue("Ordered set should contain element 0.", orderedSet.contains(0L));
		assertTrue("Ordered set should contain element 5.", orderedSet.contains(5L));
	}

	@Test
	public void addAll() {
		LongOrderedSet orderedSet = new LongOrderedSetImpl();
		LongOrderedSet otherSet = new LongOrderedSetImpl();

		otherSet.add(0L);
		otherSet.add(5L);
		otherSet.add(10L);

		orderedSet.addAll(otherSet);
		assertEquals("Ordered set size should be 3.", 3, orderedSet.size());
		assertTrue("Ordered set should contain element 0.", orderedSet.contains(0L));
		assertTrue("Ordered set should contain element 5.", orderedSet.contains(5L));
		assertTrue("Ordered set should contain element 10.", orderedSet.contains(10L));
	}

	@Test(expected=NullPointerException.class)
	public void addAll_null() {
		LongOrderedSet orderedSet = new LongOrderedSetImpl();
		orderedSet.addAll(null);
	}

	@Test
	public void addAll_empty() {
		LongOrderedSet orderedSet = new LongOrderedSetImpl();
		orderedSet.addAll(new LongOrderedSetImpl());
		assertTrue("Ordered set should be empty.", orderedSet.isEmpty());
	}

	@Test
	public void equals_true() {
		LongOrderedSet orderedSet = new LongOrderedSetImpl();
		LongOrderedSet otherSet = new LongOrderedSetImpl();

		orderedSet.add(0L);
		orderedSet.add(5L);
		orderedSet.add(10L);

		otherSet.add(0L);
		otherSet.add(5L);
		otherSet.add(10L);

		assertTrue("First set should be equal to second set.", orderedSet.equals(otherSet));
		assertTrue("Second set should be equal to first set.", otherSet.equals(orderedSet));
	}

	@Test
	public void equals_false() {
		LongOrderedSet orderedSet = new LongOrderedSetImpl();
		LongOrderedSet otherSet = new LongOrderedSetImpl();

		orderedSet.add(0L);
		orderedSet.add(5L);
		orderedSet.add(10L);

		otherSet.add(0L);
		otherSet.add(1L);
		otherSet.add(2L);

		assertFalse("First set should not be equal to second set.", orderedSet.equals(otherSet));
		assertFalse("Second set should not be equal to first set.", otherSet.equals(orderedSet));
	}

	@Test
	public void containsAll() {
		LongOrderedSet orderedSet = new LongOrderedSetImpl();
		LongOrderedSet otherSet = new LongOrderedSetImpl();

		orderedSet.add(0L);
		orderedSet.add(5L);
		orderedSet.add(10L);

		otherSet.add(0L);
		otherSet.add(5L);
		otherSet.add(10L);

		assertTrue("First set should contain all elements from second set.", orderedSet.containsAll(otherSet));
		assertTrue("Second set should contain all elements from first set.", otherSet.containsAll(orderedSet));
	}

	@Test
	public void containsAll_subset() {
		LongOrderedSet orderedSet = new LongOrderedSetImpl();
		LongOrderedSet otherSet = new LongOrderedSetImpl();

		orderedSet.add(0L);
		orderedSet.add(5L);
		orderedSet.add(10L);

		otherSet.add(0L);
		otherSet.add(5L);

		assertTrue("First set should contain all elements from second set.", orderedSet.containsAll(otherSet));
		assertFalse("Second set should not contain all elements from first set.", otherSet.containsAll(orderedSet));
	}

	@Test
	public void containsAll_disjoint_set() {
		LongOrderedSet orderedSet = new LongOrderedSetImpl();
		LongOrderedSet otherSet = new LongOrderedSetImpl();

		orderedSet.add(0L);
		orderedSet.add(5L);
		orderedSet.add(10L);

		otherSet.add(-10L);
		otherSet.add(0L);
		otherSet.add(5L);

		assertFalse("First set should not contain all elements from second set.", orderedSet.containsAll(otherSet));
		assertFalse("Second set should not contain all elements from first set.", otherSet.containsAll(orderedSet));
	}

	@Test(expected=NullPointerException.class)
	public void containsAll_null() {
		LongOrderedSet orderedSet = new LongOrderedSetImpl();
		orderedSet.containsAll(null);
	}

	@Test
	public void containsAll_empty() {
		LongOrderedSet orderedSet = new LongOrderedSetImpl();
		orderedSet.add(0L);
		assertTrue("Set should contain all elements from an empty set.", orderedSet.containsAll(new LongOrderedSetImpl()));
	}

	@Test
	public void contains_false_after_remove() {
		LongOrderedSet orderedSet = new LongOrderedSetImpl();

		orderedSet.add(0L);
		orderedSet.add(5L);
		orderedSet.add(10L);

		orderedSet.remove(0L);
		orderedSet.remove(5L);
		orderedSet.remove(10L);

		assertEquals("Ordered set size should be 0.", 0, orderedSet.size());
		assertTrue("Ordered set should be empty.", orderedSet.isEmpty());
		assertFalse("Ordered set should not contain element 0.", orderedSet.contains(0L));
		assertFalse("Ordered set should not contain element 5.", orderedSet.contains(5L));
		assertFalse("Ordered set should not contain element 10.", orderedSet.contains(10L));		
	}

	@Test
	public void trimToSize() {
		LongOrderedSet orderedSet = new LongOrderedSetImpl();
		orderedSet.trimToSize();
	}

	@Test
	public void iterator() {
		LongOrderedSet orderedSet = new LongOrderedSetImpl();

		orderedSet.add(0L);
		orderedSet.add(10L);
		orderedSet.add(5L);
		
		// This should have no effect on the set or the insertion order
		orderedSet.add(10L);
		assertEquals("Ordered set size should be 3.", 3, orderedSet.size());
		
		LongIterator itr = orderedSet.iterator();
		long[] values = new long[3];

		for (int i = 0; i < 3; i++) {
			assertTrue("Iterator should indicate that the next value is available.", itr.hasNext());
			values[i] = itr.next();
		}

		assertFalse("Iterator should indicate that there are no more elements.", itr.hasNext());

		// Iterator follows insertion order
		assertEquals("Iterator should return first element.", 0L, values[0]);
		assertEquals("Iterator should return second element.", 10L, values[1]);
		assertEquals("Iterator should return third element.", 5L, values[2]);
	}

	@Test
	public void iterator_empty() {
		LongOrderedSet orderedSet = new LongOrderedSetImpl();
		LongIterator itr = orderedSet.iterator();
		assertFalse("Iterator should indicate that there are no elements.", itr.hasNext());
	}

	@Test(expected=NoSuchElementException.class)
	public void iterator_overrun() {
		LongOrderedSet orderedSet = new LongOrderedSetImpl();
		LongIterator itr = orderedSet.iterator();
		itr.next();
	}

	@Test
	public void toArray() {
		LongOrderedSet orderedSet = new LongOrderedSetImpl();

		orderedSet.add(0L);
		orderedSet.add(10L);
		orderedSet.add(5L);

		// This should have no effect on the set or the insertion order
		orderedSet.add(10L);
		assertEquals("Ordered set size should be 3.", 3, orderedSet.size());
		
		long[] array = orderedSet.toArray();
		assertArrayEquals("Array should contain all stored elements.", new long[] { 0L, 10L, 5L }, array);
	}

	@Test
	public void toArray_empty() {
		LongOrderedSet orderedSet = new LongOrderedSetImpl();
		long[] array = orderedSet.toArray();
		assertEquals("Array should be empty for empty sets.", 0, array.length);
	}

	@Test
	public void toString_regular() {
		LongOrderedSet orderedSet = new LongOrderedSetImpl();

		orderedSet.add(11L);
		orderedSet.add(22L);
		orderedSet.add(33L);

		String toString = orderedSet.toString();
		assertTrue("ToString output should start with an opening square bracket.", toString.startsWith("["));
		assertTrue("ToString output should end with a closing square bracket.", toString.endsWith("]"));
		assertTrue("ToString output should contain the number 11.", toString.contains("11"));
		assertTrue("ToString output should contain the number 22.", toString.contains("22"));
		assertTrue("ToString output should contain the number 33.", toString.contains("33"));
	}

	@Test
	public void toString_empty() {
		LongOrderedSet orderedSet = new LongOrderedSetImpl();
		assertEquals("ToString output should be [] for the empty set.", "[]", orderedSet.toString());
	}
	
	@Test
	public void indexedAdd() {
		LongOrderedSet orderedSet = new LongOrderedSetImpl();
		
		assertEquals("First element should have an index of 0.", 0, orderedSet.indexedAdd(0L));
		assertEquals("Second element should have an index of 1.", 1, orderedSet.indexedAdd(10L));
		assertEquals("Third element should have an index of 2.", 2, orderedSet.indexedAdd(5L));
		assertEquals("Adding the second element again should return index 1.", 1, orderedSet.indexedAdd(10L));
	}
	
	@Test
	public void indexOf() {
		LongOrderedSet orderedSet = new LongOrderedSetImpl();
		
		orderedSet.add(0L);
		orderedSet.add(20L);
		orderedSet.add(15L);
		orderedSet.add(42L);
		
		assertEquals("First element should have an index of 0.", 0, orderedSet.indexOf(0L));
		assertEquals("Second element should have an index of 1.", 1, orderedSet.indexOf(20L));
		assertEquals("An unknown element should return index -1.", -1, orderedSet.indexOf(999L));
	}
	
	@Test
	public void get() {
		LongOrderedSet orderedSet = new LongOrderedSetImpl();
		
		orderedSet.add(0L);
		orderedSet.add(20L);
		orderedSet.add(15L);
		orderedSet.add(42L);
		
		assertEquals("Element at index 0 should return '0'.", 0L, orderedSet.get(0));
		assertEquals("Element at index 2 should return '15'.", 15L, orderedSet.get(2));
	}
	
	@Test
	public void clear_restarts_insertion_order() {
		LongOrderedSet orderedSet = new LongOrderedSetImpl();
		
		orderedSet.add(0L);
		orderedSet.add(20L);
		orderedSet.add(15L);
		orderedSet.add(42L);
	
		assertEquals("Third element should have an index of 2.", 2, orderedSet.indexOf(15L));
		
		orderedSet.clear();
		assertEquals("The set should have no elements after clearing.", 0, orderedSet.size());
		
		orderedSet.add(15L);
		
		assertEquals("The same element should have index 0 after clearing and re-inserting.", 0, orderedSet.indexOf(15L));
		assertEquals("Elements that are not added back again should not be present.", -1, orderedSet.indexOf(42L));
	}
	
	@Test
	public void remove_preserves_insertion_order() {
		LongOrderedSet orderedSet = new LongOrderedSetImpl();
		
		orderedSet.add(0L);
		orderedSet.add(20L);
		orderedSet.add(15L);
		orderedSet.add(42L);
		
		orderedSet.remove(20L);
		orderedSet.remove(15L);
	
		assertEquals("Remaining element '0' should keep its original insertion order index.", 0, orderedSet.indexOf(0L));	
		assertEquals("Removed element '20' should not have an insertion order index.", -1, orderedSet.indexOf(20L));	
		assertEquals("Removed element '15' should not have an insertion order index.", -1, orderedSet.indexOf(15L));	
		assertEquals("Remaining element '42' should keep its original insertion order index.", 3, orderedSet.indexOf(42L));	
	}
	
	@Test
	public void trimToSize_compacts_insertion_order() {
		LongOrderedSet orderedSet = new LongOrderedSetImpl();
		
		orderedSet.add(0L);
		orderedSet.add(20L);
		orderedSet.add(15L);
		orderedSet.add(42L);
		
		orderedSet.remove(20L);
		orderedSet.remove(15L);
	
		orderedSet.trimToSize();
		
		assertEquals("Remaining element '0' should keep its original insertion order index.", 0, orderedSet.indexOf(0L));	
		assertEquals("Removed element '20' should not have an insertion order index.", -1, orderedSet.indexOf(20L));	
		assertEquals("Removed element '15' should not have an insertion order index.", -1, orderedSet.indexOf(15L));	
		assertEquals("Remaining element '42' should have a new insertion order index.", 1, orderedSet.indexOf(42L));
	}
}
