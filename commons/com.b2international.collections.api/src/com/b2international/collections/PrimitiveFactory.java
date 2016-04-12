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
package com.b2international.collections;

import java.util.Collection;
import java.util.Set;

import com.b2international.collections.list.ByteList;
import com.b2international.collections.list.IntDeque;
import com.b2international.collections.list.IntList;
import com.b2international.collections.list.LongDeque;
import com.b2international.collections.list.LongList;
import com.b2international.collections.map.ByteKeyLongMap;
import com.b2international.collections.map.ByteKeyMap;
import com.b2international.collections.map.ByteValueMap;
import com.b2international.collections.map.IntKeyMap;
import com.b2international.collections.map.LongKeyFloatMap;
import com.b2international.collections.map.LongKeyIntMap;
import com.b2international.collections.map.LongKeyLongMap;
import com.b2international.collections.map.LongKeyMap;
import com.b2international.collections.map.LongValueMap;
import com.b2international.collections.set.ByteSet;
import com.b2international.collections.set.IntSet;
import com.b2international.collections.set.LongSet;
import com.google.common.hash.HashFunction;

/**
 * @since 4.6
 */
public interface PrimitiveFactory {

	ByteList newByteArrayList(byte[] source);

	ByteList newByteArrayList(ByteCollection source);

	ByteList newByteArrayList(int expectedSize);

	ByteKeyLongMap newByteKeyLongOpenHashMap();

	<V> ByteKeyMap<V> newByteKeyOpenHashMap(int expectedSize);

	ByteSet newByteOpenHashSet(ByteCollection source);

	IntDeque newIntArrayDeque(int[] source);

	IntList newIntArrayList();

	IntList newIntArrayList(int[] source);

	IntSet newIntBitSet();

	IntSet newIntBitSet(int expectedSize);

	IntSet newIntBitSet(int[] source);

	IntSet newIntBitSet(IntCollection source);

	<V> IntKeyMap<V> newIntKeyOpenHashMap();

	<V> IntKeyMap<V> newIntKeyOpenHashMap(int expectedSize);

	IntSet newIntOpenHashSet();

	IntSet newIntOpenHashSet(int expectedSize);

	LongDeque newLongArrayDeque();

	LongList newLongArrayList();

	LongList newLongArrayList(int expectedSize);

	LongList newLongArrayList(long[] source);

	LongList newLongArrayList(LongCollection source);

	LongSet newLongChainedHashSet(long[] source);

	Collection<Long> newLongCollectionToCollectionAdapter(LongCollection source);

	LongKeyFloatMap newLongKeyFloatOpenHashMap();

	LongKeyFloatMap newLongKeyFloatOpenHashMap(int expectedSize);

	LongKeyIntMap newLongKeyIntOpenHashMap();

	LongKeyIntMap newLongKeyIntOpenHashMap(int expectedSize);

	LongKeyLongMap newLongKeyLongOpenHashMap();

	LongKeyLongMap newLongKeyLongOpenHashMap(int expectedSize);

	<V> LongKeyMap<V> newLongKeyOpenHashMap();

	<V> LongKeyMap<V> newLongKeyOpenHashMap(HashFunction hashFunction);

	<V> LongKeyMap<V> newLongKeyOpenHashMap(int expectedSize);

	LongSet newLongOpenHashSet();

	LongSet newLongOpenHashSet(HashFunction hashFunction);

	LongSet newLongOpenHashSet(int expectedSize);

	LongSet newLongOpenHashSet(int expectedSize, double fillFactor);

	LongSet newLongOpenHashSet(long[] source);

	LongSet newLongOpenHashSet(LongCollection source);

	Set<Long> newLongSetToSetAdapter(LongSet source);

	<K> ByteValueMap<K> newObjectKeyByteOpenHashMap(int expectedSize);

	<K> LongValueMap<K> newObjectKeyLongOpenHashMap(int expectedSize);

	IntList newUnmodifiableIntList(IntList source);

	IntSet newUnmodifiableIntSet(IntSet source);

	LongSet newUnmodifiableLongSet(LongSet source);
}
