/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.collections;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.BitSet;
import java.util.Iterator;
import java.util.ServiceLoader;

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
public abstract class PrimitiveSets {

	private static final PrimitiveSetFactory FACTORY;
	
	static {
		final ServiceLoader<PrimitiveSetFactory> loader = ServiceLoader.load(PrimitiveSetFactory.class, PrimitiveSets.class.getClassLoader());
		final Iterator<PrimitiveSetFactory> services = loader.iterator();
		checkState(services.hasNext(), "No %s implementation has been found", PrimitiveSetFactory.class.getName());
		FACTORY = services.next();
	}
	
	private static final LongSet EMPTY_SET = PrimitiveSets.newLongOpenHashSetWithExpectedSize(0);
	private PrimitiveSets() {}

	public static ByteSet newByteOpenHashSet() {
		return FACTORY.newByteOpenHashSet();
	}
	
	public static ByteSet newByteOpenHashSet(ByteCollection source) {
		if (source == null) {
			return newByteOpenHashSet();
		} else {
			return FACTORY.newByteOpenHashSet(source);
		}
	}

	public static BitSet newBitSet() {
		return new BitSet();
	}

	public static BitSet newBitSetWithExpectedSize(int expectedSize) {
		return new BitSet(expectedSize);
	}

	public static BitSet newBitSet(int... source) {
		if (source == null) {
			return newBitSet();
		} else {
			final BitSet bitSet = newBitSetWithExpectedSize(source.length);
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
			final BitSet bitSet = newBitSetWithExpectedSize(source.size());
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

	public static IntSet newIntOpenHashSetWithExpectedSize(int expectedSize) {
		return FACTORY.newIntOpenHashSetWithExpectedSize(expectedSize);
	}
	
	public static IntSet newIntOpenHashSet(IntCollection source) {
		if (source == null) {
			return newIntOpenHashSet();
		} else {
			return FACTORY.newIntOpenHashSet(source);
		}
	}

	public static LongSet newLongOpenHashSet() {
		return FACTORY.newLongOpenHashSet();
	}

	public static LongSet newLongOpenHashSet(HashFunction hashFunction) {
		checkNotNull(hashFunction, "hashFunction may not be null.");
		return FACTORY.newLongOpenHashSet(hashFunction);
	}

	public static LongSet newLongOpenHashSetWithExpectedSize(int expectedSize) {
		return FACTORY.newLongOpenHashSetWithExpectedSize(expectedSize);
	}

	public static LongSet newLongOpenHashSetWithExpectedSize(int expectedSize, double fillFactor) {
		return FACTORY.newLongOpenHashSetWithExpectedSize(expectedSize, fillFactor);
	}

	public static LongSet newLongOpenHashSet(long... source) {
		if (source == null) {
			return newLongOpenHashSet();
		} else {
			return FACTORY.newLongOpenHashSet(source);
		}
	}

	public static LongSet newLongOpenHashSet(LongCollection source) {
		if (source == null) {
			return newLongOpenHashSet();
		} else {
			return FACTORY.newLongOpenHashSet(source);
		}
	}

	public static LongSet emptyLongSet() {
		return EMPTY_SET;
	}
}
