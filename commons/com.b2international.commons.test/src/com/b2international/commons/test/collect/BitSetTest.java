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

import java.util.BitSet;

import org.junit.Test;

import com.b2international.collections.PrimitiveLists;
import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.ints.IntCollection;

/**
 * @since 4.7
 */
public class BitSetTest {

	@Test
	public void create() {
		BitSet bitSet = PrimitiveSets.newBitSet();
		assertTrue("Bit set should be empty.", bitSet.isEmpty());
	}

	@Test
	public void create_zero_size() {
		BitSet bitSet = PrimitiveSets.newBitSetWithExpectedSize(0);
		assertTrue("Bit set should be empty.", bitSet.isEmpty());
	}

	@Test(expected=NegativeArraySizeException.class)
	public void create_negative_size() {
		PrimitiveSets.newBitSetWithExpectedSize(-1);
	}

	@Test
	public void create_array() {
		BitSet bitSet = PrimitiveSets.newBitSet(0, 5, 10);
		assertEquals("Three bits should be set.", 3, bitSet.cardinality());
		assertTrue("Bit 0 should be set.", bitSet.get(0));
		assertTrue("Bit 5 should be set.", bitSet.get(5));
		assertTrue("Bit 10 should be set.", bitSet.get(10));
	}

	@Test
	public void create_empty_array() {
		BitSet bitSet = PrimitiveSets.newBitSet(new int[0]);
		assertTrue("Bit set should be empty.", bitSet.isEmpty());
	}

	@Test
	public void create_null_array() {
		BitSet bitSet = PrimitiveSets.newBitSet((int[]) null);
		assertTrue("Bit set should be empty.", bitSet.isEmpty());
	}

	@Test
	public void create_collection() {
		BitSet bitSet = PrimitiveSets.newBitSet(PrimitiveLists.newIntArrayList(0, 5, 10, 5));
		assertEquals("Three bits should be set.", 3, bitSet.cardinality());
		assertTrue("Bit 0 should be set.", bitSet.get(0));
		assertTrue("Bit 5 should be set.", bitSet.get(5));
		assertTrue("Bit 10 should be set.", bitSet.get(10));
	}

	@Test
	public void create_empty_collection() {
		BitSet bitSet = PrimitiveSets.newBitSet(PrimitiveLists.newIntArrayList());
		assertTrue("Bit set should be empty.", bitSet.isEmpty());
	}

	@Test
	public void create_null_collection() {
		BitSet bitSet = PrimitiveSets.newBitSet((IntCollection) null);
		assertTrue("Bit set should be empty.", bitSet.isEmpty());
	}
}
