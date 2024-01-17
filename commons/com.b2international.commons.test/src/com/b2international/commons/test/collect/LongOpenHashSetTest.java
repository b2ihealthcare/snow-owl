/*
 * Copyright 2011-2016 B2i Healthcare, https://b2ihealthcare.com
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
import com.b2international.collections.longs.LongCollection;
import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongSet;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

/**
 * @since 4.7
 */
public class LongOpenHashSetTest {

	@Test
	public void create() {
		LongSet longSet = PrimitiveSets.newLongOpenHashSet();
		assertTrue("Long set should be empty.", longSet.isEmpty());
	}

	@Test
	public void create_zero_size() {
		LongSet longSet = PrimitiveSets.newLongOpenHashSetWithExpectedSize(0);
		assertTrue("Long set should be empty.", longSet.isEmpty());
	}

	@Test(expected=IllegalArgumentException.class)
	public void create_negative_size() {
		PrimitiveSets.newLongOpenHashSetWithExpectedSize(-1);
	}

	@Test
	public void create_fill_factor() {
		LongSet longSet = PrimitiveSets.newLongOpenHashSetWithExpectedSize(10, 0.5d);
		assertTrue("Long set should be empty.", longSet.isEmpty());
	}
	
	@Test
	public void create_zero_size_fill_factor() {
		LongSet longSet = PrimitiveSets.newLongOpenHashSetWithExpectedSize(0, 0.5d);
		assertTrue("Long set should be empty.", longSet.isEmpty());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void create_negative_size_fill_factor() {
		PrimitiveSets.newLongOpenHashSetWithExpectedSize(-1, 0.5d);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void create_negative_fill_factor() {
		PrimitiveSets.newLongOpenHashSetWithExpectedSize(10, -0.5d);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void create_zero_fill_factor() {
		PrimitiveSets.newLongOpenHashSetWithExpectedSize(10, 0d);
	}
	
	@Test
	public void create_one_fill_factor() {
		LongSet longSet = PrimitiveSets.newLongOpenHashSetWithExpectedSize(10, 1.0d);
		assertTrue("Long set should be empty.", longSet.isEmpty());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void create_greater_than_one_fill_factor() {
		PrimitiveSets.newLongOpenHashSetWithExpectedSize(10, 1.0001d);
	}
	
	@Test
	public void create_array() {
		LongSet longSet = PrimitiveSets.newLongOpenHashSet(0L, 5L, 10L);
		assertEquals("Long set size should be 3.", 3, longSet.size());
		assertTrue("Long set should contain element 0.", longSet.contains(0L));
		assertTrue("Long set should contain element 5.", longSet.contains(5L));
		assertTrue("Long set should contain element 10.", longSet.contains(10L));
	}

	@Test
	public void create_empty_array() {
		LongSet longSet = PrimitiveSets.newLongOpenHashSet(new long[0]);
		assertTrue("Long set should be empty.", longSet.isEmpty());
	}

	@Test
	public void create_null_array() {
		LongSet longSet = PrimitiveSets.newLongOpenHashSet((long[]) null);
		assertTrue("Long set should be empty.", longSet.isEmpty());
	}

	@Test
	public void create_collection() {
		LongSet longSet = PrimitiveSets.newLongOpenHashSet(PrimitiveLists.newLongArrayList(0L, 5L, 10L, 5L));
		assertEquals("Long set size should be 3.", 3, longSet.size());
		assertTrue("Long set should contain element 0.", longSet.contains(0L));
		assertTrue("Long set should contain element 5.", longSet.contains(5L));
		assertTrue("Long set should contain element 10.", longSet.contains(10L));
	}

	@Test
	public void create_empty_collection() {
		LongSet longSet = PrimitiveSets.newLongOpenHashSet(PrimitiveLists.newLongArrayList());
		assertTrue("Long set should be empty.", longSet.isEmpty());
	}

	@Test
	public void create_null_collection() {
		LongSet longSet = PrimitiveSets.newLongOpenHashSet((LongCollection) null);
		assertTrue("Long set should be empty.", longSet.isEmpty());
	}
	
	@Test
	public void create_hash_function() {
		LongSet longSet = PrimitiveSets.newLongOpenHashSet(Hashing.murmur3_32());
		assertTrue("Long set should be empty.", longSet.isEmpty());
	}
	
	@Test(expected=NullPointerException.class)
	public void create_null_hash_function() {
		PrimitiveSets.newLongOpenHashSet((HashFunction) null);
	}

	@Test
	public void add() {
		LongSet longSet = PrimitiveSets.newLongOpenHashSet();
		longSet.add(0L);
		longSet.add(5L);
		longSet.add(10L);
		assertEquals("Long set size should be 3.", 3, longSet.size());
		assertTrue("Long set should contain element 0.", longSet.contains(0L));
		assertTrue("Long set should contain element 5.", longSet.contains(5L));
		assertTrue("Long set should contain element 10.", longSet.contains(10L));
	}

	@Test
	public void add_twice() {
		LongSet longSet = PrimitiveSets.newLongOpenHashSet();
		assertTrue("First attempt at adding 0 to the set should modify the set.", longSet.add(0L));
		assertTrue("First attempt at adding 5 to the set should modify the set.", longSet.add(5L));
		assertFalse("Second attempt at adding 0 to the set should not modify the set.", longSet.add(0L));
		assertFalse("Second attempt at adding 5 to the set should not modify the set.", longSet.add(5L));
		assertEquals("Long set size should be 2.", 2, longSet.size());
		assertTrue("Long set should contain element 0.", longSet.contains(0L));
		assertTrue("Long set should contain element 5.", longSet.contains(5L));
	}

	@Test
	public void addAll() {
		LongSet longSet = PrimitiveSets.newLongOpenHashSet();
		LongSet otherSet = PrimitiveSets.newLongOpenHashSet();

		otherSet.add(0L);
		otherSet.add(5L);
		otherSet.add(10L);

		longSet.addAll(otherSet);
		assertEquals("Long set size should be 3.", 3, longSet.size());
		assertTrue("Long set should contain element 0.", longSet.contains(0L));
		assertTrue("Long set should contain element 5.", longSet.contains(5L));
		assertTrue("Long set should contain element 10.", longSet.contains(10L));
	}

	@Test(expected=NullPointerException.class)
	public void addAll_null() {
		LongSet longSet = PrimitiveSets.newLongOpenHashSet();
		longSet.addAll(null);
	}

	@Test
	public void addAll_empty() {
		LongSet longSet = PrimitiveSets.newLongOpenHashSet();
		longSet.addAll(PrimitiveSets.newLongOpenHashSet());
		assertTrue("Long set should be empty.", longSet.isEmpty());
	}

	@Test
	public void equals_true() {
		LongSet longSet = PrimitiveSets.newLongOpenHashSet();
		LongSet otherSet = PrimitiveSets.newLongOpenHashSet();

		longSet.add(0L);
		longSet.add(5L);
		longSet.add(10L);

		otherSet.add(0L);
		otherSet.add(5L);
		otherSet.add(10L);

		assertTrue("First set should be equal to second set.", longSet.equals(otherSet));
		assertTrue("Second set should be equal to first set.", otherSet.equals(longSet));
	}

	@Test
	public void equals_false() {
		LongSet longSet = PrimitiveSets.newLongOpenHashSet();
		LongSet otherSet = PrimitiveSets.newLongOpenHashSet();

		longSet.add(0L);
		longSet.add(5L);
		longSet.add(10L);

		otherSet.add(0L);
		otherSet.add(1L);
		otherSet.add(2L);

		assertFalse("First set should not be equal to second set.", longSet.equals(otherSet));
		assertFalse("Second set should not be equal to first set.", otherSet.equals(longSet));
	}

	@Test
	public void containsAll() {
		LongSet longSet = PrimitiveSets.newLongOpenHashSet();
		LongSet otherSet = PrimitiveSets.newLongOpenHashSet();

		longSet.add(0L);
		longSet.add(5L);
		longSet.add(10L);

		otherSet.add(0L);
		otherSet.add(5L);
		otherSet.add(10L);

		assertTrue("First set should contain all elements from second set.", longSet.containsAll(otherSet));
		assertTrue("Second set should contain all elements from first set.", otherSet.containsAll(longSet));
	}

	@Test
	public void containsAll_subset() {
		LongSet longSet = PrimitiveSets.newLongOpenHashSet();
		LongSet otherSet = PrimitiveSets.newLongOpenHashSet();

		longSet.add(0L);
		longSet.add(5L);
		longSet.add(10L);

		otherSet.add(0L);
		otherSet.add(5L);

		assertTrue("First set should contain all elements from second set.", longSet.containsAll(otherSet));
		assertFalse("Second set should not contain all elements from first set.", otherSet.containsAll(longSet));
	}

	@Test
	public void containsAll_disjoint_set() {
		LongSet longSet = PrimitiveSets.newLongOpenHashSet();
		LongSet otherSet = PrimitiveSets.newLongOpenHashSet();

		longSet.add(0L);
		longSet.add(5L);
		longSet.add(10L);

		otherSet.add(-10L);
		otherSet.add(0L);
		otherSet.add(5L);

		assertFalse("First set should not contain all elements from second set.", longSet.containsAll(otherSet));
		assertFalse("Second set should not contain all elements from first set.", otherSet.containsAll(longSet));
	}

	@Test(expected=NullPointerException.class)
	public void containsAll_null() {
		LongSet longSet = PrimitiveSets.newLongOpenHashSet();
		longSet.containsAll(null);
	}

	@Test
	public void containsAll_empty() {
		LongSet longSet = PrimitiveSets.newLongOpenHashSet();
		longSet.add(0L);
		assertTrue("Set should contain all elements from an empty set.", longSet.containsAll(PrimitiveSets.newLongOpenHashSet()));
	}

	@Test
	public void contains_false_after_remove() {
		LongSet longSet = PrimitiveSets.newLongOpenHashSet();

		longSet.add(0L);
		longSet.add(5L);
		longSet.add(10L);

		longSet.remove(0L);
		longSet.remove(5L);
		longSet.remove(10L);

		assertEquals("Long set size should be 0.", 0, longSet.size());
		assertTrue("Long set should be empty.", longSet.isEmpty());
		assertFalse("Long set should not contain element 0.", longSet.contains(0L));
		assertFalse("Long set should not contain element 5.", longSet.contains(5L));
		assertFalse("Long set should not contain element 10.", longSet.contains(10L));		
	}

	@Test
	public void trimToSize() {
		LongSet longSet = PrimitiveSets.newLongOpenHashSet();
		longSet.trimToSize();
	}

	@Test
	public void iterator() {
		LongSet longSet = PrimitiveSets.newLongOpenHashSet();

		longSet.add(0L);
		longSet.add(5L);
		longSet.add(10L);

		LongIterator itr = longSet.iterator();

		long[] values = new long[3];

		for (int i = 0; i < 3; i++) {
			assertTrue("Iterator should indicate that the next value is available.", itr.hasNext());
			values[i] = itr.next();
		}

		assertFalse("Iterator should indicate that there are no more elements.", itr.hasNext());

		Arrays.sort(values);

		assertEquals("Iterator should return first element.", 0L, values[0]);
		assertEquals("Iterator should return second element.", 5L, values[1]);
		assertEquals("Iterator should return third element.", 10L, values[2]);
	}

	@Test
	public void iterator_empty() {
		LongSet longSet = PrimitiveSets.newLongOpenHashSet();
		LongIterator itr = longSet.iterator();
		assertFalse("Iterator should indicate that there are no elements.", itr.hasNext());
	}

	@Test(expected=NoSuchElementException.class)
	public void iterator_overrun() {
		LongSet longSet = PrimitiveSets.newLongOpenHashSet();
		LongIterator itr = longSet.iterator();
		itr.next();
	}

	@Test
	public void retainAll() {
		LongSet longSet = PrimitiveSets.newLongOpenHashSet();
		LongSet otherSet = PrimitiveSets.newLongOpenHashSet();

		longSet.add(0L);
		longSet.add(5L);
		longSet.add(10L);

		otherSet.add(0L);
		otherSet.add(10L);

		longSet.retainAll(otherSet);
		assertEquals("Two elements should remain after retaining.", 2, longSet.size());
		assertTrue("Long set should contain element 0.", longSet.contains(0L));
		assertTrue("Long set should contain element 10.", longSet.contains(10L));
	}

	@Test
	public void retainAll_same() {
		LongSet longSet = PrimitiveSets.newLongOpenHashSet();
		LongSet otherSet = PrimitiveSets.newLongOpenHashSet();

		longSet.add(0L);
		longSet.add(5L);
		longSet.add(10L);

		otherSet.add(0L);
		otherSet.add(5L);
		otherSet.add(10L);

		longSet.retainAll(otherSet);
		assertEquals("Three elements should remain after retaining.", 3, longSet.size());
		assertTrue("Long set should contain element 0.", longSet.contains(0L));
		assertTrue("Long set should contain element 5.", longSet.contains(5L));
		assertTrue("Long set should contain element 10.", longSet.contains(10L));
	}

	@Test
	public void retainAll_superset() {
		LongSet longSet = PrimitiveSets.newLongOpenHashSet();
		LongSet otherSet = PrimitiveSets.newLongOpenHashSet();

		longSet.add(0L);
		longSet.add(5L);
		longSet.add(10L);

		otherSet.add(0L);
		otherSet.add(5L);
		otherSet.add(10L);
		otherSet.add(30L);

		longSet.retainAll(otherSet);
		assertEquals("Three elements should remain after retaining.", 3, longSet.size());
		assertTrue("Long set should contain element 0.", longSet.contains(0L));
		assertTrue("Long set should contain element 5.", longSet.contains(5L));
		assertTrue("Long set should contain element 10.", longSet.contains(10L));
	}

	@Test
	public void retainAll_disjoint_set() {
		LongSet longSet = PrimitiveSets.newLongOpenHashSet();
		LongSet otherSet = PrimitiveSets.newLongOpenHashSet();

		longSet.add(0L);
		longSet.add(5L);
		longSet.add(10L);

		otherSet.add(2L);
		otherSet.add(3L);
		otherSet.add(4L);

		longSet.retainAll(otherSet);
		assertTrue("Long set should be empty after retaining a disjoint set of elements.", longSet.isEmpty());
	}

	@Test(expected=NullPointerException.class)
	public void retainAll_null() {
		LongSet longSet = PrimitiveSets.newLongOpenHashSet();

		longSet.add(0L);
		longSet.add(5L);
		longSet.add(10L);

		longSet.retainAll(null);
	}	

	@Test
	public void retainAll_empty_set() {
		LongSet longSet = PrimitiveSets.newLongOpenHashSet();
		LongSet otherSet = PrimitiveSets.newLongOpenHashSet();

		longSet.add(0L);
		longSet.add(5L);
		longSet.add(10L);

		longSet.retainAll(otherSet);
		assertTrue("Long set should be empty after retaining an empty set.", longSet.isEmpty());
	}

	@Test
	public void removeAll() {
		LongSet longSet = PrimitiveSets.newLongOpenHashSet();
		LongSet otherSet = PrimitiveSets.newLongOpenHashSet();

		longSet.add(0L);
		longSet.add(5L);
		longSet.add(10L);

		otherSet.add(0L);
		otherSet.add(10L);

		longSet.removeAll(otherSet);
		assertEquals("Only one element should remain after removing the others.", 1, longSet.size());
		assertTrue("Long set should contain element 5.", longSet.contains(5L));
	}

	@Test
	public void removeAll_same() {
		LongSet longSet = PrimitiveSets.newLongOpenHashSet();
		LongSet otherSet = PrimitiveSets.newLongOpenHashSet();

		longSet.add(0L);
		longSet.add(5L);
		longSet.add(10L);

		otherSet.add(0L);
		otherSet.add(5L);
		otherSet.add(10L);

		longSet.removeAll(otherSet);
		assertTrue("Long set should be empty after removing all elements.", longSet.isEmpty());
	}

	@Test
	public void removeAll_superset() {
		LongSet longSet = PrimitiveSets.newLongOpenHashSet();
		LongSet otherSet = PrimitiveSets.newLongOpenHashSet();

		longSet.add(0L);
		longSet.add(5L);
		longSet.add(10L);

		otherSet.add(0L);
		otherSet.add(5L);
		otherSet.add(10L);
		otherSet.add(30L);

		longSet.removeAll(otherSet);
		assertTrue("Long set should be empty after removing all elements.", longSet.isEmpty());
	}

	@Test
	public void removeAll_disjoint_set() {
		LongSet longSet = PrimitiveSets.newLongOpenHashSet();
		LongSet otherSet = PrimitiveSets.newLongOpenHashSet();

		longSet.add(0L);
		longSet.add(5L);
		longSet.add(10L);

		otherSet.add(2L);
		otherSet.add(3L);
		otherSet.add(4L);

		longSet.removeAll(otherSet);
		assertEquals("Long set size should be 3.", 3, longSet.size());
		assertTrue("Long set should contain element 0.", longSet.contains(0L));
		assertTrue("Long set should contain element 5.", longSet.contains(5L));
		assertTrue("Long set should contain element 10.", longSet.contains(10L));
	}

	@Test(expected=NullPointerException.class)
	public void removeAll_null() {
		LongSet longSet = PrimitiveSets.newLongOpenHashSet();

		longSet.add(0L);
		longSet.add(5L);
		longSet.add(10L);

		longSet.removeAll(null);
	}	

	@Test
	public void removeAll_empty_set() {
		LongSet longSet = PrimitiveSets.newLongOpenHashSet();
		LongSet otherSet = PrimitiveSets.newLongOpenHashSet();

		longSet.add(0L);
		longSet.add(5L);
		longSet.add(10L);

		longSet.removeAll(otherSet);
		assertEquals("Long set size should be 3.", 3, longSet.size());
		assertTrue("Long set should contain element 0.", longSet.contains(0L));
		assertTrue("Long set should contain element 5.", longSet.contains(5L));
		assertTrue("Long set should contain element 10.", longSet.contains(10L));
	}

	@Test
	public void toArray() {
		LongSet longSet = PrimitiveSets.newLongOpenHashSet();

		longSet.add(0L);
		longSet.add(5L);
		longSet.add(10L);

		long[] array = longSet.toArray();
		Arrays.sort(array);

		assertArrayEquals("Array should contain all stored elements.", new long[] { 0L, 5L, 10L }, array);
	}

	@Test
	public void toArray_empty() {
		LongSet longSet = PrimitiveSets.newLongOpenHashSet();
		long[] array = longSet.toArray();
		assertEquals("Array should be empty for empty sets.", 0, array.length);
	}

	@Test
	public void toString_regular() {
		LongSet longSet = PrimitiveSets.newLongOpenHashSet();

		longSet.add(11L);
		longSet.add(22L);
		longSet.add(33L);

		String toString = longSet.toString();
		assertTrue("ToString output should start with an opening square bracket.", toString.startsWith("["));
		assertTrue("ToString output should end with a closing square bracket.", toString.endsWith("]"));
		assertTrue("ToString output should contain the number 11.", toString.contains("11"));
		assertTrue("ToString output should contain the number 22.", toString.contains("22"));
		assertTrue("ToString output should contain the number 33.", toString.contains("33"));
	}

	@Test
	public void toString_empty() {
		LongSet longSet = PrimitiveSets.newLongOpenHashSet();
		assertEquals("ToString output should be [] for the empty set.", "[]", longSet.toString());
	}
}
