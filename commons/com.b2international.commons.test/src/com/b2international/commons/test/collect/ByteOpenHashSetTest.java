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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.NoSuchElementException;

import org.junit.Test;

import com.b2international.collections.PrimitiveLists;
import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.bytes.ByteCollection;
import com.b2international.collections.bytes.ByteIterator;
import com.b2international.collections.bytes.ByteSet;

/**
 * @since 4.7
 */
public class ByteOpenHashSetTest {

	private static final byte _M10 = -10;
	private static final byte _0 = 0;
	private static final byte _1 = 1;
	private static final byte _2 = 2;
	private static final byte _3 = 3;
	private static final byte _4 = 4;
	private static final byte _5 = 5;
	private static final byte _10 = 10;
	private static final byte _11 = 11;
	private static final byte _22 = 22;
	private static final byte _30 = 30;
	private static final byte _33 = 33;

	@Test
	public void create() {
		ByteSet byteSet = PrimitiveSets.newByteOpenHashSet();
		assertTrue("Byte set should be empty.", byteSet.isEmpty());
	}

	@Test
	public void create_collection() {
		ByteSet byteSet = PrimitiveSets.newByteOpenHashSet(PrimitiveLists.newByteArrayList(_0, _5, _10, _5));
		assertEquals("Byte set size should be 3.", 3, byteSet.size());
		assertTrue("Byte set should contain element 0.", byteSet.contains(_0));
		assertTrue("Byte set should contain element 5.", byteSet.contains(_5));
		assertTrue("Byte set should contain element 10.", byteSet.contains(_10));
	}

	@Test
	public void create_empty_collection() {
		ByteSet byteSet = PrimitiveSets.newByteOpenHashSet(PrimitiveLists.newByteArrayList());
		assertTrue("Byte set should be empty.", byteSet.isEmpty());
	}

	@Test
	public void create_null_collection() {
		ByteSet byteSet = PrimitiveSets.newByteOpenHashSet((ByteCollection) null);
		assertTrue("Byte set should be empty.", byteSet.isEmpty());
	}

	@Test
	public void add() {
		ByteSet byteSet = PrimitiveSets.newByteOpenHashSet();
		byteSet.add(_0);
		byteSet.add(_5);
		byteSet.add(_10);
		assertEquals("Byte set size should be 3.", 3, byteSet.size());
		assertTrue("Byte set should contain element 0.", byteSet.contains(_0));
		assertTrue("Byte set should contain element 5.", byteSet.contains(_5));
		assertTrue("Byte set should contain element 10.", byteSet.contains(_10));
	}

	@Test
	public void add_twice() {
		ByteSet byteSet = PrimitiveSets.newByteOpenHashSet();
		assertTrue("First attempt at adding 0 to the set should modify the set.", byteSet.add(_0));
		assertTrue("First attempt at adding 5 to the set should modify the set.", byteSet.add(_5));
		assertFalse("Second attempt at adding 0 to the set should not modify the set.", byteSet.add(_0));
		assertFalse("Second attempt at adding 5 to the set should not modify the set.", byteSet.add(_5));
		assertEquals("Byte set size should be 2.", 2, byteSet.size());
		assertTrue("Byte set should contain element 0.", byteSet.contains(_0));
		assertTrue("Byte set should contain element 5.", byteSet.contains(_5));
	}

	@Test
	public void addAll() {
		ByteSet byteSet = PrimitiveSets.newByteOpenHashSet();
		ByteSet otherSet = PrimitiveSets.newByteOpenHashSet();

		otherSet.add(_0);
		otherSet.add(_5);
		otherSet.add(_10);

		byteSet.addAll(otherSet);
		assertEquals("Byte set size should be 3.", 3, byteSet.size());
		assertTrue("Byte set should contain element 0.", byteSet.contains(_0));
		assertTrue("Byte set should contain element 5.", byteSet.contains(_5));
		assertTrue("Byte set should contain element 10.", byteSet.contains(_10));
	}

	@Test(expected=NullPointerException.class)
	public void addAll_null() {
		ByteSet byteSet = PrimitiveSets.newByteOpenHashSet();
		byteSet.addAll(null);
	}

	@Test
	public void addAll_empty() {
		ByteSet byteSet = PrimitiveSets.newByteOpenHashSet();
		byteSet.addAll(PrimitiveSets.newByteOpenHashSet());
		assertTrue("Byte set should be empty.", byteSet.isEmpty());
	}

	@Test
	public void equals_true() {
		ByteSet byteSet = PrimitiveSets.newByteOpenHashSet();
		ByteSet otherSet = PrimitiveSets.newByteOpenHashSet();

		byteSet.add(_0);
		byteSet.add(_5);
		byteSet.add(_10);

		otherSet.add(_0);
		otherSet.add(_5);
		otherSet.add(_10);

		assertTrue("First set should be equal to second set.", byteSet.equals(otherSet));
		assertTrue("Second set should be equal to first set.", otherSet.equals(byteSet));
	}

	@Test
	public void equals_false() {
		ByteSet byteSet = PrimitiveSets.newByteOpenHashSet();
		ByteSet otherSet = PrimitiveSets.newByteOpenHashSet();

		byteSet.add(_0);
		byteSet.add(_5);
		byteSet.add(_10);

		otherSet.add(_0);
		otherSet.add(_1);
		otherSet.add(_2);

		assertFalse("First set should not be equal to second set.", byteSet.equals(otherSet));
		assertFalse("Second set should not be equal to first set.", otherSet.equals(byteSet));
	}

	@Test
	public void containsAll() {
		ByteSet byteSet = PrimitiveSets.newByteOpenHashSet();
		ByteSet otherSet = PrimitiveSets.newByteOpenHashSet();

		byteSet.add(_0);
		byteSet.add(_5);
		byteSet.add(_10);

		otherSet.add(_0);
		otherSet.add(_5);
		otherSet.add(_10);

		assertTrue("First set should contain all elements from second set.", byteSet.containsAll(otherSet));
		assertTrue("Second set should contain all elements from first set.", otherSet.containsAll(byteSet));
	}

	@Test
	public void containsAll_subset() {
		ByteSet byteSet = PrimitiveSets.newByteOpenHashSet();
		ByteSet otherSet = PrimitiveSets.newByteOpenHashSet();

		byteSet.add(_0);
		byteSet.add(_5);
		byteSet.add(_10);

		otherSet.add(_0);
		otherSet.add(_5);

		assertTrue("First set should contain all elements from second set.", byteSet.containsAll(otherSet));
		assertFalse("Second set should not contain all elements from first set.", otherSet.containsAll(byteSet));
	}

	@Test
	public void containsAll_disjoint_set() {
		ByteSet byteSet = PrimitiveSets.newByteOpenHashSet();
		ByteSet otherSet = PrimitiveSets.newByteOpenHashSet();

		byteSet.add(_0);
		byteSet.add(_5);
		byteSet.add(_10);

		otherSet.add(_M10);
		otherSet.add(_0);
		otherSet.add(_5);

		assertFalse("First set should not contain all elements from second set.", byteSet.containsAll(otherSet));
		assertFalse("Second set should not contain all elements from first set.", otherSet.containsAll(byteSet));
	}

	@Test(expected=NullPointerException.class)
	public void containsAll_null() {
		ByteSet byteSet = PrimitiveSets.newByteOpenHashSet();
		byteSet.containsAll(null);
	}

	@Test
	public void containsAll_empty() {
		ByteSet byteSet = PrimitiveSets.newByteOpenHashSet();
		byteSet.add(_0);
		assertTrue("Set should contain all elements from an empty set.", byteSet.containsAll(PrimitiveSets.newByteOpenHashSet()));
	}

	@Test
	public void contains_false_after_remove() {
		ByteSet byteSet = PrimitiveSets.newByteOpenHashSet();

		byteSet.add(_0);
		byteSet.add(_5);
		byteSet.add(_10);

		byteSet.remove(_0);
		byteSet.remove(_5);
		byteSet.remove(_10);

		assertEquals("Byte set size should be 0.", 0, byteSet.size());
		assertTrue("Byte set should be empty.", byteSet.isEmpty());
		assertFalse("Byte set should not contain element 0.", byteSet.contains(_0));
		assertFalse("Byte set should not contain element 5.", byteSet.contains(_5));
		assertFalse("Byte set should not contain element 10.", byteSet.contains(_10));		
	}

	@Test
	public void trimToSize() {
		ByteSet byteSet = PrimitiveSets.newByteOpenHashSet();
		byteSet.trimToSize();
	}

	@Test
	public void iterator() {
		ByteSet byteSet = PrimitiveSets.newByteOpenHashSet();

		byteSet.add(_0);
		byteSet.add(_5);
		byteSet.add(_10);

		ByteIterator itr = byteSet.iterator();

		byte[] values = new byte[3];

		for (int i = 0; i < 3; i++) {
			assertTrue("Iterator should indicate that the next value is available.", itr.hasNext());
			values[i] = itr.next();
		}

		assertFalse("Iterator should indicate that there are no more elements.", itr.hasNext());

		Arrays.sort(values);

		assertEquals("Iterator should return first element.", _0, values[0]);
		assertEquals("Iterator should return second element.", _5, values[1]);
		assertEquals("Iterator should return third element.", _10, values[2]);
	}

	@Test
	public void iterator_empty() {
		ByteSet byteSet = PrimitiveSets.newByteOpenHashSet();
		ByteIterator itr = byteSet.iterator();
		assertFalse("Iterator should indicate that there are no elements.", itr.hasNext());
	}

	@Test(expected=NoSuchElementException.class)
	public void iterator_overrun() {
		ByteSet byteSet = PrimitiveSets.newByteOpenHashSet();
		ByteIterator itr = byteSet.iterator();
		itr.next();
	}

	@Test
	public void retainAll() {
		ByteSet byteSet = PrimitiveSets.newByteOpenHashSet();
		ByteSet otherSet = PrimitiveSets.newByteOpenHashSet();

		byteSet.add(_0);
		byteSet.add(_5);
		byteSet.add(_10);

		otherSet.add(_0);
		otherSet.add(_10);

		byteSet.retainAll(otherSet);
		assertEquals("Two elements should remain after retaining.", 2, byteSet.size());
		assertTrue("Byte set should contain element 0.", byteSet.contains(_0));
		assertTrue("Byte set should contain element 10.", byteSet.contains(_10));
	}

	@Test
	public void retainAll_same() {
		ByteSet byteSet = PrimitiveSets.newByteOpenHashSet();
		ByteSet otherSet = PrimitiveSets.newByteOpenHashSet();

		byteSet.add(_0);
		byteSet.add(_5);
		byteSet.add(_10);

		otherSet.add(_0);
		otherSet.add(_5);
		otherSet.add(_10);

		byteSet.retainAll(otherSet);
		assertEquals("Three elements should remain after retaining.", 3, byteSet.size());
		assertTrue("Byte set should contain element 0.", byteSet.contains(_0));
		assertTrue("Byte set should contain element 5.", byteSet.contains(_5));
		assertTrue("Byte set should contain element 10.", byteSet.contains(_10));
	}

	@Test
	public void retainAll_superset() {
		ByteSet byteSet = PrimitiveSets.newByteOpenHashSet();
		ByteSet otherSet = PrimitiveSets.newByteOpenHashSet();

		byteSet.add(_0);
		byteSet.add(_5);
		byteSet.add(_10);

		otherSet.add(_0);
		otherSet.add(_5);
		otherSet.add(_10);
		otherSet.add(_30);

		byteSet.retainAll(otherSet);
		assertEquals("Three elements should remain after retaining.", 3, byteSet.size());
		assertTrue("Byte set should contain element 0.", byteSet.contains(_0));
		assertTrue("Byte set should contain element 5.", byteSet.contains(_5));
		assertTrue("Byte set should contain element 10.", byteSet.contains(_10));
	}

	@Test
	public void retainAll_disjoint_set() {
		ByteSet byteSet = PrimitiveSets.newByteOpenHashSet();
		ByteSet otherSet = PrimitiveSets.newByteOpenHashSet();

		byteSet.add(_0);
		byteSet.add(_5);
		byteSet.add(_10);

		otherSet.add(_2);
		otherSet.add(_3);
		otherSet.add(_4);

		byteSet.retainAll(otherSet);
		assertTrue("Byte set should be empty after retaining a disjoint set of elements.", byteSet.isEmpty());
	}

	@Test(expected=NullPointerException.class)
	public void retainAll_null() {
		ByteSet byteSet = PrimitiveSets.newByteOpenHashSet();

		byteSet.add(_0);
		byteSet.add(_5);
		byteSet.add(_10);

		byteSet.retainAll(null);
	}	

	@Test
	public void retainAll_empty_set() {
		ByteSet byteSet = PrimitiveSets.newByteOpenHashSet();
		ByteSet otherSet = PrimitiveSets.newByteOpenHashSet();

		byteSet.add(_0);
		byteSet.add(_5);
		byteSet.add(_10);

		byteSet.retainAll(otherSet);
		assertTrue("Byte set should be empty after retaining an empty set.", byteSet.isEmpty());
	}

	@Test
	public void removeAll() {
		ByteSet byteSet = PrimitiveSets.newByteOpenHashSet();
		ByteSet otherSet = PrimitiveSets.newByteOpenHashSet();

		byteSet.add(_0);
		byteSet.add(_5);
		byteSet.add(_10);

		otherSet.add(_0);
		otherSet.add(_10);

		byteSet.removeAll(otherSet);
		assertEquals("Only one element should remain after removing the others.", 1, byteSet.size());
		assertTrue("Byte set should contain element 5.", byteSet.contains(_5));
	}

	@Test
	public void removeAll_same() {
		ByteSet byteSet = PrimitiveSets.newByteOpenHashSet();
		ByteSet otherSet = PrimitiveSets.newByteOpenHashSet();

		byteSet.add(_0);
		byteSet.add(_5);
		byteSet.add(_10);

		otherSet.add(_0);
		otherSet.add(_5);
		otherSet.add(_10);

		byteSet.removeAll(otherSet);
		assertTrue("Byte set should be empty after removing all elements.", byteSet.isEmpty());
	}

	@Test
	public void removeAll_superset() {
		ByteSet byteSet = PrimitiveSets.newByteOpenHashSet();
		ByteSet otherSet = PrimitiveSets.newByteOpenHashSet();

		byteSet.add(_0);
		byteSet.add(_5);
		byteSet.add(_10);

		otherSet.add(_0);
		otherSet.add(_5);
		otherSet.add(_10);
		otherSet.add(_30);

		byteSet.removeAll(otherSet);
		assertTrue("Byte set should be empty after removing all elements.", byteSet.isEmpty());
	}

	@Test
	public void removeAll_disjoint_set() {
		ByteSet byteSet = PrimitiveSets.newByteOpenHashSet();
		ByteSet otherSet = PrimitiveSets.newByteOpenHashSet();

		byteSet.add(_0);
		byteSet.add(_5);
		byteSet.add(_10);

		otherSet.add(_2);
		otherSet.add(_3);
		otherSet.add(_4);

		byteSet.removeAll(otherSet);
		assertEquals("Byte set size should be 3.", 3, byteSet.size());
		assertTrue("Byte set should contain element 0.", byteSet.contains(_0));
		assertTrue("Byte set should contain element 5.", byteSet.contains(_5));
		assertTrue("Byte set should contain element 10.", byteSet.contains(_10));
	}

	@Test(expected=NullPointerException.class)
	public void removeAll_null() {
		ByteSet byteSet = PrimitiveSets.newByteOpenHashSet();

		byteSet.add(_0);
		byteSet.add(_5);
		byteSet.add(_10);

		byteSet.removeAll(null);
	}	

	@Test
	public void removeAll_empty_set() {
		ByteSet byteSet = PrimitiveSets.newByteOpenHashSet();
		ByteSet otherSet = PrimitiveSets.newByteOpenHashSet();

		byteSet.add(_0);
		byteSet.add(_5);
		byteSet.add(_10);

		byteSet.removeAll(otherSet);
		assertEquals("Byte set size should be 3.", 3, byteSet.size());
		assertTrue("Byte set should contain element 0.", byteSet.contains(_0));
		assertTrue("Byte set should contain element 5.", byteSet.contains(_5));
		assertTrue("Byte set should contain element 10.", byteSet.contains(_10));
	}

	@Test
	public void toArray() {
		ByteSet byteSet = PrimitiveSets.newByteOpenHashSet();

		byteSet.add(_0);
		byteSet.add(_5);
		byteSet.add(_10);

		byte[] array = byteSet.toArray();
		Arrays.sort(array);

		assertArrayEquals("Array should contain all stored elements.", new byte[] { _0, _5, _10 }, array);
	}

	@Test
	public void toArray_empty() {
		ByteSet byteSet = PrimitiveSets.newByteOpenHashSet();
		byte[] array = byteSet.toArray();
		assertEquals("Array should be empty for empty sets.", 0, array.length);
	}

	@Test
	public void toString_regular() {
		ByteSet byteSet = PrimitiveSets.newByteOpenHashSet();

		byteSet.add(_11);
		byteSet.add(_22);
		byteSet.add(_33);

		String toString = byteSet.toString();
		assertTrue("ToString output should start with an opening square bracket.", toString.startsWith("["));
		assertTrue("ToString output should end with a closing square bracket.", toString.endsWith("]"));
		assertTrue("ToString output should contain the number 11.", toString.contains("11"));
		assertTrue("ToString output should contain the number 22.", toString.contains("22"));
		assertTrue("ToString output should contain the number 33.", toString.contains("33"));
	}

	@Test
	public void toString_empty() {
		ByteSet byteSet = PrimitiveSets.newByteOpenHashSet();
		assertEquals("ToString output should be [] for the empty set.", "[]", byteSet.toString());
	}
}
