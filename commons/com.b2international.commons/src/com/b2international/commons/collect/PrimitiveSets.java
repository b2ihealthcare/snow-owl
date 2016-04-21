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
package com.b2international.commons.collect;

import java.util.BitSet;
import java.util.Set;

import com.b2international.collections.FastUtilPrimitiveCollections;
import com.b2international.collections.PrimitiveSetFactory;
import com.b2international.collections.bytes.ByteCollection;
import com.b2international.collections.bytes.ByteSet;
import com.b2international.collections.ints.IntCollection;
import com.b2international.collections.ints.IntIterator;
import com.b2international.collections.ints.IntSet;
import com.b2international.collections.longs.LongCollection;
import com.b2international.collections.longs.LongSet;
import com.google.common.hash.HashFunction;

/**
 * @since 4.7
 */
public class PrimitiveSets {

	private static final PrimitiveSetFactory FACTORY = FastUtilPrimitiveCollections.sets();
	
	private PrimitiveSets() {}

	public static ByteSet newByteOpenHashSet(ByteCollection source) {
		return FACTORY.newByteOpenHashSet(source);
	}

	public static BitSet newBitSet() {
		return new BitSet();
	}

	public static BitSet newBitSet(int expectedSize) {
		return new BitSet(expectedSize);
	}

	public static BitSet newBitSet(int[] source) {
		if (source == null) {
			return newBitSet();
		} else {
			final BitSet bitSet = newBitSet(source.length);
			for (int value : source) {
				bitSet.set(value);
			}
			return bitSet;
		}
	}

	public static BitSet newBitSet(IntCollection source) {
		if (source == null) {
			return newBitSet();
		} else {
			final BitSet bitSet = newBitSet(source.size());
			final IntIterator iter = source.iterator();
			while (iter.hasNext()) {
				bitSet.set(iter.next());
			}
			return bitSet;
		}
	}

	public static IntSet newIntOpenHashSet() {
		return FACTORY.newIntOpenHashSet();
	}

	public static IntSet newIntOpenHashSet(int expectedSize) {
		return FACTORY.newIntOpenHashSet(expectedSize);
	}

	public static LongSet newLongOpenHashSet() {
		return FACTORY.newLongOpenHashSet();
	}

	public static LongSet newLongOpenHashSet(HashFunction hashFunction) {
		return FACTORY.newLongOpenHashSet(hashFunction);
	}

	public static LongSet newLongOpenHashSet(int expectedSize) {
		return FACTORY.newLongOpenHashSet(expectedSize);
	}

	public static LongSet newLongOpenHashSet(int expectedSize, double fillFactor) {
		return FACTORY.newLongOpenHashSet(expectedSize, fillFactor);
	}

	public static LongSet newLongOpenHashSet(long[] source) {
		if (source == null) {
			return newLongOpenHashSet();
		} else {
			return FACTORY.newLongOpenHashSet(source);
		}
	}

	public static LongSet newLongOpenHashSet(LongCollection source) {
		return FACTORY.newLongOpenHashSet(source);
	}

	public static LongSet newUnmodifiableLongSet(LongSet source) {
		return FACTORY.newUnmodifiableLongSet(source);
	}

	public static Set<Long> newLongSetToSetAdapter(LongSet source) {
		return FACTORY.newLongSetToSetAdapter(source);
	}
	
}
