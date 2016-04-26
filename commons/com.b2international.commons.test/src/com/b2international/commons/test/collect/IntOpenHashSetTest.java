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
package com.b2international.commons.test.collect;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.NoSuchElementException;

import org.junit.Test;

import com.b2international.collections.PrimitiveLists;
import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.ints.IntCollection;
import com.b2international.collections.ints.IntIterator;
import com.b2international.collections.ints.IntSet;

/**
 * @since 4.7
 */
public class IntOpenHashSetTest {

	@Test
	public void create() {
		IntSet intSet = PrimitiveSets.newIntOpenHashSet();
		assertTrue("Integer set should be empty.", intSet.isEmpty());
	}

	@Test
	public void create_zero_size() {
		IntSet intSet = PrimitiveSets.newIntOpenHashSetWithExpectedSize(0);
		assertTrue("Integer set should be empty.", intSet.isEmpty());
	}

	@Test(expected=IllegalArgumentException.class)
	public void create_negative_size() {
		PrimitiveSets.newIntOpenHashSetWithExpectedSize(-1);
	}

	@Test
	public void create_collection() {
		IntSet intSet = PrimitiveSets.newIntOpenHashSet(PrimitiveLists.newIntArrayList(0, 5, 10, 5));
		assertEquals("Integer set size should be 3.", 3, intSet.size());
		assertTrue("Integer set should contain element 0.", intSet.contains(0));
		assertTrue("Integer set should contain element 5.", intSet.contains(5));
		assertTrue("Integer set should contain element 10.", intSet.contains(10));
	}

	@Test
	public void create_empty_collection() {
		IntSet intSet = PrimitiveSets.newIntOpenHashSet(PrimitiveLists.newIntArrayList());
		assertTrue("Integer set should be empty.", intSet.isEmpty());
	}

	@Test
	public void create_null_collection() {
		IntSet intSet = PrimitiveSets.newIntOpenHashSet((IntCollection) null);
		assertTrue("Integer set should be empty.", intSet.isEmpty());
	}

	@Test
	public void add() {
		IntSet intSet = PrimitiveSets.newIntOpenHashSet();
		intSet.add(0);
		intSet.add(5);
		intSet.add(10);
		assertEquals("Integer set size should be 3.", 3, intSet.size());
		assertTrue("Integer set should contain element 0.", intSet.contains(0));
		assertTrue("Integer set should contain element 5.", intSet.contains(5));
		assertTrue("Integer set should contain element 10.", intSet.contains(10));
	}

	@Test
	public void add_twice() {
		IntSet intSet = PrimitiveSets.newIntOpenHashSet();
		assertTrue("First attempt at adding 0 to the set should modify the set.", intSet.add(0));
		assertTrue("First attempt at adding 5 to the set should modify the set.", intSet.add(5));
		assertFalse("Second attempt at adding 0 to the set should not modify the set.", intSet.add(0));
		assertFalse("Second attempt at adding 5 to the set should not modify the set.", intSet.add(5));
		assertEquals("Integer set size should be 2.", 2, intSet.size());
		assertTrue("Integer set should contain element 0.", intSet.contains(0));
		assertTrue("Integer set should contain element 5.", intSet.contains(5));
	}

	@Test
	public void addAll() {
		IntSet intSet = PrimitiveSets.newIntOpenHashSet();
		IntSet otherSet = PrimitiveSets.newIntOpenHashSet();

		otherSet.add(0);
		otherSet.add(5);
		otherSet.add(10);

		intSet.addAll(otherSet);
		assertEquals("Integer set size should be 3.", 3, intSet.size());
		assertTrue("Integer set should contain element 0.", intSet.contains(0));
		assertTrue("Integer set should contain element 5.", intSet.contains(5));
		assertTrue("Integer set should contain element 10.", intSet.contains(10));
	}

	@Test(expected=NullPointerException.class)
	public void addAll_null() {
		IntSet intSet = PrimitiveSets.newIntOpenHashSet();
		intSet.addAll(null);
	}

	@Test
	public void addAll_empty() {
		IntSet intSet = PrimitiveSets.newIntOpenHashSet();
		intSet.addAll(PrimitiveSets.newIntOpenHashSet());
		assertTrue("Integer set should be empty.", intSet.isEmpty());
	}

	@Test
	public void equals_true() {
		IntSet intSet = PrimitiveSets.newIntOpenHashSet();
		IntSet otherSet = PrimitiveSets.newIntOpenHashSet();

		intSet.add(0);
		intSet.add(5);
		intSet.add(10);

		otherSet.add(0);
		otherSet.add(5);
		otherSet.add(10);

		assertTrue("First set should be equal to second set.", intSet.equals(otherSet));
		assertTrue("Second set should be equal to first set.", otherSet.equals(intSet));
	}

	@Test
	public void equals_false() {
		IntSet intSet = PrimitiveSets.newIntOpenHashSet();
		IntSet otherSet = PrimitiveSets.newIntOpenHashSet();

		intSet.add(0);
		intSet.add(5);
		intSet.add(10);

		otherSet.add(0);
		otherSet.add(1);
		otherSet.add(2);

		assertFalse("First set should not be equal to second set.", intSet.equals(otherSet));
		assertFalse("Second set should not be equal to first set.", otherSet.equals(intSet));
	}

	@Test
	public void containsAll() {
		IntSet intSet = PrimitiveSets.newIntOpenHashSet();
		IntSet otherSet = PrimitiveSets.newIntOpenHashSet();

		intSet.add(0);
		intSet.add(5);
		intSet.add(10);

		otherSet.add(0);
		otherSet.add(5);
		otherSet.add(10);

		assertTrue("First set should contain all elements from second set.", intSet.containsAll(otherSet));
		assertTrue("Second set should contain all elements from first set.", otherSet.containsAll(intSet));
	}

	@Test
	public void containsAll_subset() {
		IntSet intSet = PrimitiveSets.newIntOpenHashSet();
		IntSet otherSet = PrimitiveSets.newIntOpenHashSet();

		intSet.add(0);
		intSet.add(5);
		intSet.add(10);

		otherSet.add(0);
		otherSet.add(5);

		assertTrue("First set should contain all elements from second set.", intSet.containsAll(otherSet));
		assertFalse("Second set should not contain all elements from first set.", otherSet.containsAll(intSet));
	}

	@Test
	public void containsAll_disjoint_set() {
		IntSet intSet = PrimitiveSets.newIntOpenHashSet();
		IntSet otherSet = PrimitiveSets.newIntOpenHashSet();

		intSet.add(0);
		intSet.add(5);
		intSet.add(10);

		otherSet.add(-10);
		otherSet.add(0);
		otherSet.add(5);

		assertFalse("First set should not contain all elements from second set.", intSet.containsAll(otherSet));
		assertFalse("Second set should not contain all elements from first set.", otherSet.containsAll(intSet));
	}

	@Test(expected=NullPointerException.class)
	public void containsAll_null() {
		IntSet intSet = PrimitiveSets.newIntOpenHashSet();
		intSet.containsAll(null);
	}

	@Test
	public void containsAll_empty() {
		IntSet intSet = PrimitiveSets.newIntOpenHashSet();
		intSet.add(0);
		assertTrue("Set should contain all elements from an empty set.", intSet.containsAll(PrimitiveSets.newIntOpenHashSet()));
	}

	@Test
	public void contains_false_after_remove() {
		IntSet intSet = PrimitiveSets.newIntOpenHashSet();

		intSet.add(0);
		intSet.add(5);
		intSet.add(10);

		intSet.remove(0);
		intSet.remove(5);
		intSet.remove(10);

		assertEquals("Integer set size should be 0.", 0, intSet.size());
		assertTrue("Integer set should be empty.", intSet.isEmpty());
		assertFalse("Integer set should not contain element 0.", intSet.contains(0));
		assertFalse("Integer set should not contain element 5.", intSet.contains(5));
		assertFalse("Integer set should not contain element 10.", intSet.contains(10));		
	}

	@Test
	public void trimToSize() {
		IntSet intSet = PrimitiveSets.newIntOpenHashSet();
		intSet.trimToSize();
	}

	@Test
	public void iterator() {
		IntSet intSet = PrimitiveSets.newIntOpenHashSet();

		intSet.add(0);
		intSet.add(5);
		intSet.add(10);

		IntIterator itr = intSet.iterator();

		int[] values = new int[3];

		for (int i = 0; i < 3; i++) {
			assertTrue("Iterator should indicate that the next value is available.", itr.hasNext());
			values[i] = itr.next();
		}

		assertFalse("Iterator should indicate that there are no more elements.", itr.hasNext());

		Arrays.sort(values);

		assertEquals("Iterator should return first element.", 0, values[0]);
		assertEquals("Iterator should return second element.", 5, values[1]);
		assertEquals("Iterator should return third element.", 10, values[2]);
	}

	@Test
	public void iterator_empty() {
		IntSet intSet = PrimitiveSets.newIntOpenHashSet();
		IntIterator itr = intSet.iterator();
		assertFalse("Iterator should indicate that there are no elements.", itr.hasNext());
	}

	@Test(expected=NoSuchElementException.class)
	public void iterator_overrun() {
		IntSet intSet = PrimitiveSets.newIntOpenHashSet();
		IntIterator itr = intSet.iterator();
		itr.next();
	}

	@Test
	public void retainAll() {
		IntSet intSet = PrimitiveSets.newIntOpenHashSet();
		IntSet otherSet = PrimitiveSets.newIntOpenHashSet();

		intSet.add(0);
		intSet.add(5);
		intSet.add(10);

		otherSet.add(0);
		otherSet.add(10);

		intSet.retainAll(otherSet);
		assertEquals("Two elements should remain after retaining.", 2, intSet.size());
		assertTrue("Integer set should contain element 0.", intSet.contains(0));
		assertTrue("Integer set should contain element 10.", intSet.contains(10));
	}

	@Test
	public void retainAll_same() {
		IntSet intSet = PrimitiveSets.newIntOpenHashSet();
		IntSet otherSet = PrimitiveSets.newIntOpenHashSet();

		intSet.add(0);
		intSet.add(5);
		intSet.add(10);

		otherSet.add(0);
		otherSet.add(5);
		otherSet.add(10);

		intSet.retainAll(otherSet);
		assertEquals("Three elements should remain after retaining.", 3, intSet.size());
		assertTrue("Integer set should contain element 0.", intSet.contains(0));
		assertTrue("Integer set should contain element 5.", intSet.contains(5));
		assertTrue("Integer set should contain element 10.", intSet.contains(10));
	}

	@Test
	public void retainAll_superset() {
		IntSet intSet = PrimitiveSets.newIntOpenHashSet();
		IntSet otherSet = PrimitiveSets.newIntOpenHashSet();

		intSet.add(0);
		intSet.add(5);
		intSet.add(10);

		otherSet.add(0);
		otherSet.add(5);
		otherSet.add(10);
		otherSet.add(30);

		intSet.retainAll(otherSet);
		assertEquals("Three elements should remain after retaining.", 3, intSet.size());
		assertTrue("Integer set should contain element 0.", intSet.contains(0));
		assertTrue("Integer set should contain element 5.", intSet.contains(5));
		assertTrue("Integer set should contain element 10.", intSet.contains(10));
	}

	@Test
	public void retainAll_disjoint_set() {
		IntSet intSet = PrimitiveSets.newIntOpenHashSet();
		IntSet otherSet = PrimitiveSets.newIntOpenHashSet();

		intSet.add(0);
		intSet.add(5);
		intSet.add(10);

		otherSet.add(2);
		otherSet.add(3);
		otherSet.add(4);

		intSet.retainAll(otherSet);
		assertTrue("Integer set should be empty after retaining a disjoint set of elements.", intSet.isEmpty());
	}

	@Test(expected=NullPointerException.class)
	public void retainAll_null() {
		IntSet intSet = PrimitiveSets.newIntOpenHashSet();

		intSet.add(0);
		intSet.add(5);
		intSet.add(10);

		intSet.retainAll(null);
	}	

	@Test
	public void retainAll_empty_set() {
		IntSet intSet = PrimitiveSets.newIntOpenHashSet();
		IntSet otherSet = PrimitiveSets.newIntOpenHashSet();

		intSet.add(0);
		intSet.add(5);
		intSet.add(10);

		intSet.retainAll(otherSet);
		assertTrue("Integer set should be empty after retaining an empty set.", intSet.isEmpty());
	}

	@Test
	public void removeAll() {
		IntSet intSet = PrimitiveSets.newIntOpenHashSet();
		IntSet otherSet = PrimitiveSets.newIntOpenHashSet();

		intSet.add(0);
		intSet.add(5);
		intSet.add(10);

		otherSet.add(0);
		otherSet.add(10);

		intSet.removeAll(otherSet);
		assertEquals("Only one element should remain after removing the others.", 1, intSet.size());
		assertTrue("Integer set should contain element 5.", intSet.contains(5));
	}

	@Test
	public void removeAll_same() {
		IntSet intSet = PrimitiveSets.newIntOpenHashSet();
		IntSet otherSet = PrimitiveSets.newIntOpenHashSet();

		intSet.add(0);
		intSet.add(5);
		intSet.add(10);

		otherSet.add(0);
		otherSet.add(5);
		otherSet.add(10);

		intSet.removeAll(otherSet);
		assertTrue("Integer set should be empty after removing all elements.", intSet.isEmpty());
	}

	@Test
	public void removeAll_superset() {
		IntSet intSet = PrimitiveSets.newIntOpenHashSet();
		IntSet otherSet = PrimitiveSets.newIntOpenHashSet();

		intSet.add(0);
		intSet.add(5);
		intSet.add(10);

		otherSet.add(0);
		otherSet.add(5);
		otherSet.add(10);
		otherSet.add(30);

		intSet.removeAll(otherSet);
		assertTrue("Integer set should be empty after removing all elements.", intSet.isEmpty());
	}

	@Test
	public void removeAll_disjoint_set() {
		IntSet intSet = PrimitiveSets.newIntOpenHashSet();
		IntSet otherSet = PrimitiveSets.newIntOpenHashSet();

		intSet.add(0);
		intSet.add(5);
		intSet.add(10);

		otherSet.add(2);
		otherSet.add(3);
		otherSet.add(4);

		intSet.removeAll(otherSet);
		assertEquals("Integer set size should be 3.", 3, intSet.size());
		assertTrue("Integer set should contain element 0.", intSet.contains(0));
		assertTrue("Integer set should contain element 5.", intSet.contains(5));
		assertTrue("Integer set should contain element 10.", intSet.contains(10));
	}

	@Test(expected=NullPointerException.class)
	public void removeAll_null() {
		IntSet intSet = PrimitiveSets.newIntOpenHashSet();

		intSet.add(0);
		intSet.add(5);
		intSet.add(10);

		intSet.removeAll(null);
	}	

	@Test
	public void removeAll_empty_set() {
		IntSet intSet = PrimitiveSets.newIntOpenHashSet();
		IntSet otherSet = PrimitiveSets.newIntOpenHashSet();

		intSet.add(0);
		intSet.add(5);
		intSet.add(10);

		intSet.removeAll(otherSet);
		assertEquals("Integer set size should be 3.", 3, intSet.size());
		assertTrue("Integer set should contain element 0.", intSet.contains(0));
		assertTrue("Integer set should contain element 5.", intSet.contains(5));
		assertTrue("Integer set should contain element 10.", intSet.contains(10));
	}

	@Test
	public void toArray() {
		IntSet intSet = PrimitiveSets.newIntOpenHashSet();

		intSet.add(0);
		intSet.add(5);
		intSet.add(10);

		int[] array = intSet.toArray();
		Arrays.sort(array);

		assertArrayEquals("Array should contain all stored elements.", new int[] { 0, 5, 10 }, array);
	}

	@Test
	public void toArray_empty() {
		IntSet intSet = PrimitiveSets.newIntOpenHashSet();
		int[] array = intSet.toArray();
		assertEquals("Array should be empty for empty sets.", 0, array.length);
	}

	@Test
	public void toString_regular() {
		IntSet intSet = PrimitiveSets.newIntOpenHashSet();

		intSet.add(11);
		intSet.add(22);
		intSet.add(33);

		String toString = intSet.toString();
		assertTrue("ToString output should start with an opening square bracket.", toString.startsWith("["));
		assertTrue("ToString output should end with a closing square bracket.", toString.endsWith("]"));
		assertTrue("ToString output should contain the number 11.", toString.contains("11"));
		assertTrue("ToString output should contain the number 22.", toString.contains("22"));
		assertTrue("ToString output should contain the number 33.", toString.contains("33"));
	}

	@Test
	public void toString_empty() {
		IntSet intSet = PrimitiveSets.newIntOpenHashSet();
		assertEquals("ToString output should be [] for the empty set.", "[]", intSet.toString());
	}
}
