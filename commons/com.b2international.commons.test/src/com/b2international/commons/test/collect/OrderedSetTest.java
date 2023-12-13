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

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Test;

import com.b2international.commons.collections.OrderedSet;
import com.b2international.commons.collections.OrderedSetImpl;

/**
 * @since 8.0.1
 */
public class OrderedSetTest {

	@Test
	public void create() {
		OrderedSet<String> orderedSet = new OrderedSetImpl<>();
		assertTrue("Ordered set should be empty.", orderedSet.isEmpty());
	}

	@Test
	public void create_zero_size() {
		OrderedSet<String> orderedSet = new OrderedSetImpl<>(0);
		assertTrue("Ordered set should be empty.", orderedSet.isEmpty());
	}

	@Test(expected=IllegalArgumentException.class)
	public void create_negative_size() {
		new OrderedSetImpl<>(-1);
	}

	@Test
	public void add() {
		OrderedSet<String> orderedSet = new OrderedSetImpl<>();
		orderedSet.add("0");
		orderedSet.add("5");
		orderedSet.add("10");
		assertEquals("Ordered set size should be 3.", 3, orderedSet.size());
		assertTrue("Ordered set should contain element 0.", orderedSet.contains("0"));
		assertTrue("Ordered set should contain element 5.", orderedSet.contains("5"));
		assertTrue("Ordered set should contain element 10.", orderedSet.contains("10"));
	}

	@Test
	public void add_twice() {
		OrderedSet<String> orderedSet = new OrderedSetImpl<>();
		assertTrue("First attempt at adding 0 to the set should modify the set.", orderedSet.add("0"));
		assertTrue("First attempt at adding 5 to the set should modify the set.", orderedSet.add("5"));
		assertFalse("Second attempt at adding 0 to the set should not modify the set.", orderedSet.add("0"));
		assertFalse("Second attempt at adding 5 to the set should not modify the set.", orderedSet.add("5"));
		assertEquals("Ordered set size should be 2.", 2, orderedSet.size());
		assertTrue("Ordered set should contain element 0.", orderedSet.contains("0"));
		assertTrue("Ordered set should contain element 5.", orderedSet.contains("5"));
	}

	@Test
	public void addAll() {
		OrderedSet<String> orderedSet = new OrderedSetImpl<>();
		OrderedSet<String> otherSet = new OrderedSetImpl<>();

		otherSet.add("0");
		otherSet.add("5");
		otherSet.add("10");

		orderedSet.addAll(otherSet);
		assertEquals("Ordered set size should be 3.", 3, orderedSet.size());
		assertTrue("Ordered set should contain element 0.", orderedSet.contains("0"));
		assertTrue("Ordered set should contain element 5.", orderedSet.contains("5"));
		assertTrue("Ordered set should contain element 10.", orderedSet.contains("10"));
	}

	@Test(expected=NullPointerException.class)
	public void addAll_null() {
		OrderedSet<String> orderedSet = new OrderedSetImpl<>();
		orderedSet.addAll(null);
	}

	@Test
	public void addAll_empty() {
		OrderedSet<String> orderedSet = new OrderedSetImpl<>();
		orderedSet.addAll(new OrderedSetImpl<>());
		assertTrue("Ordered set should be empty.", orderedSet.isEmpty());
	}

	@Test
	public void equals_true() {
		OrderedSet<String> orderedSet = new OrderedSetImpl<>();
		OrderedSet<String> otherSet = new OrderedSetImpl<>();

		orderedSet.add("0");
		orderedSet.add("5");
		orderedSet.add("10");

		otherSet.add("0");
		otherSet.add("5");
		otherSet.add("10");

		assertTrue("First set should be equal to second set.", orderedSet.equals(otherSet));
		assertTrue("Second set should be equal to first set.", otherSet.equals(orderedSet));
	}

	@Test
	public void equals_false() {
		OrderedSet<String> orderedSet = new OrderedSetImpl<>();
		OrderedSet<String> otherSet = new OrderedSetImpl<>();

		orderedSet.add("0");
		orderedSet.add("5");
		orderedSet.add("10");

		otherSet.add("0");
		otherSet.add("1");
		otherSet.add("2");

		assertFalse("First set should not be equal to second set.", orderedSet.equals(otherSet));
		assertFalse("Second set should not be equal to first set.", otherSet.equals(orderedSet));
	}

	@Test
	public void containsAll() {
		OrderedSet<String> orderedSet = new OrderedSetImpl<>();
		OrderedSet<String> otherSet = new OrderedSetImpl<>();

		orderedSet.add("0");
		orderedSet.add("5");
		orderedSet.add("10");

		otherSet.add("0");
		otherSet.add("5");
		otherSet.add("10");

		assertTrue("First set should contain all elements from second set.", orderedSet.containsAll(otherSet));
		assertTrue("Second set should contain all elements from first set.", otherSet.containsAll(orderedSet));
	}

	@Test
	public void containsAll_subset() {
		OrderedSet<String> orderedSet = new OrderedSetImpl<>();
		OrderedSet<String> otherSet = new OrderedSetImpl<>();

		orderedSet.add("0");
		orderedSet.add("5");
		orderedSet.add("10");

		otherSet.add("0");
		otherSet.add("5");

		assertTrue("First set should contain all elements from second set.", orderedSet.containsAll(otherSet));
		assertFalse("Second set should not contain all elements from first set.", otherSet.containsAll(orderedSet));
	}

	@Test
	public void containsAll_disjoint_set() {
		OrderedSet<String> orderedSet = new OrderedSetImpl<>();
		OrderedSet<String> otherSet = new OrderedSetImpl<>();

		orderedSet.add("0");
		orderedSet.add("5");
		orderedSet.add("10");

		otherSet.add("-10");
		otherSet.add("0");
		otherSet.add("5");

		assertFalse("First set should not contain all elements from second set.", orderedSet.containsAll(otherSet));
		assertFalse("Second set should not contain all elements from first set.", otherSet.containsAll(orderedSet));
	}

	@Test(expected=NullPointerException.class)
	public void containsAll_null() {
		OrderedSet<String> orderedSet = new OrderedSetImpl<>();
		orderedSet.containsAll(null);
	}

	@Test
	public void containsAll_empty() {
		OrderedSet<String> orderedSet = new OrderedSetImpl<>();
		orderedSet.add("0");
		assertTrue("Set should contain all elements from an empty set.", orderedSet.containsAll(new OrderedSetImpl<>()));
	}

	@Test
	public void contains_false_after_remove() {
		OrderedSet<String> orderedSet = new OrderedSetImpl<>();

		orderedSet.add("0");
		orderedSet.add("5");
		orderedSet.add("10");

		orderedSet.remove("0");
		orderedSet.remove("5");
		orderedSet.remove("10");

		assertEquals("Ordered set size should be 0.", 0, orderedSet.size());
		assertTrue("Ordered set should be empty.", orderedSet.isEmpty());
		assertFalse("Ordered set should not contain element 0.", orderedSet.contains("0"));
		assertFalse("Ordered set should not contain element 5.", orderedSet.contains("5"));
		assertFalse("Ordered set should not contain element 10.", orderedSet.contains("10"));		
	}

	@Test
	public void trimToSize() {
		OrderedSet<String> orderedSet = new OrderedSetImpl<>();
		orderedSet.trimToSize();
	}

	@Test
	public void iterator() {
		OrderedSet<String> orderedSet = new OrderedSetImpl<>();

		orderedSet.add("0");
		orderedSet.add("10");
		orderedSet.add("5");
		
		// This should have no effect on the set or the insertion order
		orderedSet.add("10");
		assertEquals("Ordered set size should be 3.", 3, orderedSet.size());
		
		Iterator<String> itr = orderedSet.iterator();
		String[] values = new String[3];

		for (int i = 0; i < 3; i++) {
			assertTrue("Iterator should indicate that the next value is available.", itr.hasNext());
			values[i] = itr.next();
		}

		assertFalse("Iterator should indicate that there are no more elements.", itr.hasNext());

		// Iterator follows insertion order
		assertEquals("Iterator should return first element.", "0", values[0]);
		assertEquals("Iterator should return second element.", "10", values[1]);
		assertEquals("Iterator should return third element.", "5", values[2]);
	}

	@Test
	public void iterator_empty() {
		OrderedSet<String> orderedSet = new OrderedSetImpl<>();
		Iterator<String> itr = orderedSet.iterator();
		assertFalse("Iterator should indicate that there are no elements.", itr.hasNext());
	}

	@Test(expected=NoSuchElementException.class)
	public void iterator_overrun() {
		OrderedSet<String> orderedSet = new OrderedSetImpl<>();
		Iterator<String> itr = orderedSet.iterator();
		itr.next();
	}

	@Test
	public void toArray() {
		OrderedSet<String> orderedSet = new OrderedSetImpl<>();

		orderedSet.add("0");
		orderedSet.add("10");
		orderedSet.add("5");

		// This should have no effect on the set or the insertion order
		orderedSet.add("10");
		assertEquals("Ordered set size should be 3.", 3, orderedSet.size());
		
		Object[] array = orderedSet.toArray();
		assertArrayEquals("Array should contain all stored elements.", new Object[] { "0", "10", "5" }, array);
	}

	@Test
	public void toArray_empty() {
		OrderedSet<String> orderedSet = new OrderedSetImpl<>();
		Object[] array = orderedSet.toArray();
		assertEquals("Array should be empty for empty sets.", 0, array.length);
	}

	@Test
	public void toString_regular() {
		OrderedSet<String> orderedSet = new OrderedSetImpl<>();

		orderedSet.add("11");
		orderedSet.add("22");
		orderedSet.add("33");

		String toString = orderedSet.toString();
		assertTrue("ToString output should start with an opening square bracket.", toString.startsWith("["));
		assertTrue("ToString output should end with a closing square bracket.", toString.endsWith("]"));
		assertTrue("ToString output should contain the number 11.", toString.contains("11"));
		assertTrue("ToString output should contain the number 22.", toString.contains("22"));
		assertTrue("ToString output should contain the number 33.", toString.contains("33"));
	}

	@Test
	public void toString_empty() {
		OrderedSet<String> orderedSet = new OrderedSetImpl<>();
		assertEquals("ToString output should be [] for the empty set.", "[]", orderedSet.toString());
	}
	
	@Test
	public void indexedAdd() {
		OrderedSet<String> orderedSet = new OrderedSetImpl<>();
		
		assertEquals("First element should have an index of 0.", 0, orderedSet.indexedAdd("0"));
		assertEquals("Second element should have an index of 1.", 1, orderedSet.indexedAdd("10"));
		assertEquals("Third element should have an index of 2.", 2, orderedSet.indexedAdd("5"));
		assertEquals("Adding the second element again should return index 1.", 1, orderedSet.indexedAdd("10"));
	}
	
	@Test
	public void indexOf() {
		OrderedSet<String> orderedSet = new OrderedSetImpl<>();
		
		orderedSet.add("0");
		orderedSet.add("20");
		orderedSet.add("15");
		orderedSet.add("42");
		
		assertEquals("First element should have an index of 0.", 0, orderedSet.indexOf("0"));
		assertEquals("Second element should have an index of 1.", 1, orderedSet.indexOf("20"));
		assertEquals("An unknown element should return index -1.", -1, orderedSet.indexOf("not-a-member-of-this-set"));
	}
	
	@Test
	public void get() {
		OrderedSet<String> orderedSet = new OrderedSetImpl<>();
		
		orderedSet.add("0");
		orderedSet.add("20");
		orderedSet.add("15");
		orderedSet.add("42");
		
		assertEquals("Element at index 0 should return '0'.", "0", orderedSet.get(0));
		assertEquals("Element at index 2 should return '15'.", "15", orderedSet.get(2));
	}
	
	@Test
	public void clear_restarts_insertion_order() {
		OrderedSet<String> orderedSet = new OrderedSetImpl<>();
		
		orderedSet.add("0");
		orderedSet.add("20");
		orderedSet.add("15");
		orderedSet.add("42");
	
		assertEquals("Third element should have an index of 2.", 2, orderedSet.indexOf("15"));
		
		orderedSet.clear();
		assertEquals("The set should have no elements after clearing.", 0, orderedSet.size());
		
		orderedSet.add("15");
		
		assertEquals("The same element should have index 0 after clearing and re-inserting.", 0, orderedSet.indexOf("15"));
		assertEquals("Elements that are not added back again should not be present.", -1, orderedSet.indexOf("42"));
	}
	
	@Test
	public void remove_preserves_insertion_order() {
		OrderedSet<String> orderedSet = new OrderedSetImpl<>();
		
		orderedSet.add("0");
		orderedSet.add("20");
		orderedSet.add("15");
		orderedSet.add("42");
		
		orderedSet.remove("20");
		orderedSet.remove("15");
	
		assertEquals("Remaining element '0' should keep its original insertion order index.", 0, orderedSet.indexOf("0"));	
		assertEquals("Removed element '20' should not have an insertion order index.", -1, orderedSet.indexOf("20"));	
		assertEquals("Removed element '15' should not have an insertion order index.", -1, orderedSet.indexOf("15"));	
		assertEquals("Remaining element '42' should keep its original insertion order index.", 3, orderedSet.indexOf("42"));	
	}
	
	@Test
	public void trimToSize_compacts_insertion_order() {
		OrderedSet<String> orderedSet = new OrderedSetImpl<>();
		
		orderedSet.add("0");
		orderedSet.add("20");
		orderedSet.add("15");
		orderedSet.add("42");
		
		orderedSet.remove("20");
		orderedSet.remove("15");
	
		orderedSet.trimToSize();
		
		assertEquals("Remaining element '0' should keep its original insertion order index.", 0, orderedSet.indexOf("0"));	
		assertEquals("Removed element '20' should not have an insertion order index.", -1, orderedSet.indexOf("20"));	
		assertEquals("Removed element '15' should not have an insertion order index.", -1, orderedSet.indexOf("15"));	
		assertEquals("Remaining element '42' should have a new insertion order index.", 1, orderedSet.indexOf("42"));
	}
}
