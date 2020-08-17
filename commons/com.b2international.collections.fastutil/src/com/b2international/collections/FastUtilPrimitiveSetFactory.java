/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.collections.bytes.ByteCollection;
import com.b2international.collections.bytes.ByteOpenHashSetWrapper;
import com.b2international.collections.bytes.ByteSet;
import com.b2international.collections.ints.IntCollection;
import com.b2international.collections.ints.IntOpenHashSetWrapper;
import com.b2international.collections.ints.IntSet;
import com.b2international.collections.longs.LongCollection;
import com.b2international.collections.longs.LongOpenHashSetWrapper;
import com.b2international.collections.longs.LongSet;
import com.b2international.collections.longs.LongSortedSet;
import com.b2international.collections.longs.LongSortedSetWrapper;
import com.google.common.hash.HashFunction;

/**
 * @since 4.7
 */
public final class FastUtilPrimitiveSetFactory implements PrimitiveSetFactory {
	
	@Override
	public ByteSet newByteOpenHashSet() {
		return ByteOpenHashSetWrapper.create();
	}
	
	@Override
	public ByteSet newByteOpenHashSet(ByteCollection source) {
		return ByteOpenHashSetWrapper.create(source);
	}

	@Override
	public IntSet newIntOpenHashSet() {
		return IntOpenHashSetWrapper.create();
	}

	@Override
	public IntSet newIntOpenHashSetWithExpectedSize(int expectedSize) {
		return IntOpenHashSetWrapper.createWithExpectedSize(expectedSize);
	}

	@Override
	public IntSet newIntOpenHashSet(IntCollection source) {
		return IntOpenHashSetWrapper.create(source);
	}

	@Override
	public LongSet newLongOpenHashSet() {
		return LongOpenHashSetWrapper.create();
	}

	@Override
	public LongSet newLongOpenHashSet(HashFunction hashFunction) {
		return LongOpenHashSetWrapper.create(hashFunction);
	}

	@Override
	public LongSet newLongOpenHashSetWithExpectedSize(int expectedSize) {
		return LongOpenHashSetWrapper.createWithExpectedSize(expectedSize);
	}

	@Override
	public LongSet newLongOpenHashSetWithExpectedSize(int expectedSize, double fillFactor) {
		return LongOpenHashSetWrapper.createWithExpectedSize(expectedSize, fillFactor);
	}

	@Override
	public LongSet newLongOpenHashSet(long... source) {
		return LongOpenHashSetWrapper.create(source);
	}

	@Override
	public LongSet newLongOpenHashSet(LongCollection source) {
		return LongOpenHashSetWrapper.create(source);
	}
	
	@Override
	public LongSortedSet newLongSortedSet(long... source) {
		return LongSortedSetWrapper.create(source);
	}
	
	
	@Override
	public LongSortedSet newLongSortedSet(LongCollection source) {
		return LongSortedSetWrapper.create(source);
	}
}
