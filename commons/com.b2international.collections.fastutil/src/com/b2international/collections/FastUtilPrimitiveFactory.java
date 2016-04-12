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

import com.b2international.collections.list.ByteArrayListWrapper;
import com.b2international.collections.list.ByteList;
import com.b2international.collections.list.IntArrayDequeWrapper;
import com.b2international.collections.list.IntArrayListWrapper;
import com.b2international.collections.list.IntDeque;
import com.b2international.collections.list.IntList;
import com.b2international.collections.list.LongArrayDequeWrapper;
import com.b2international.collections.list.LongArrayListWrapper;
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
import com.b2international.collections.set.ByteOpenHashSetWrapper;
import com.b2international.collections.set.ByteSet;
import com.b2international.collections.set.IntOpenHashSetWrapper;
import com.b2international.collections.set.IntSet;
import com.b2international.collections.set.LongOpenHashSetWrapper;
import com.b2international.collections.set.LongSet;
import com.google.common.hash.HashFunction;

/**
 * @since 4.6
 */
public class FastUtilPrimitiveFactory implements PrimitiveFactory {

	@Override
	public ByteList newByteArrayList(byte[] source) {
		return ByteArrayListWrapper.create(source);
	}

	@Override
	public ByteList newByteArrayList(ByteCollection source) {
		return ByteArrayListWrapper.create(source);
	}

	@Override
	public ByteList newByteArrayList(int expectedSize) {
		return ByteArrayListWrapper.create(expectedSize);
	}

	@Override
	public ByteKeyLongMap newByteKeyLongOpenHashMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <V> ByteKeyMap<V> newByteKeyOpenHashMap(int expectedSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ByteSet newByteOpenHashSet(ByteCollection source) {
		return ByteOpenHashSetWrapper.create(source);
	}

	@Override
	public IntDeque newIntArrayDeque(int[] source) {
		return IntArrayDequeWrapper.create(source);
	}

	@Override
	public IntList newIntArrayList() {
		return IntArrayListWrapper.create();
	}

	@Override
	public IntList newIntArrayList(int[] source) {
		return IntArrayListWrapper.create(source);
	}

	@Override
	public IntSet newIntBitSet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IntSet newIntBitSet(int expectedSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IntSet newIntBitSet(int[] source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IntSet newIntBitSet(IntCollection source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <V> IntKeyMap<V> newIntKeyOpenHashMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <V> IntKeyMap<V> newIntKeyOpenHashMap(int expectedSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IntSet newIntOpenHashSet() {
		return IntOpenHashSetWrapper.create();
	}

	@Override
	public IntSet newIntOpenHashSet(int expectedSize) {
		return IntOpenHashSetWrapper.create(expectedSize);
	}

	@Override
	public LongDeque newLongArrayDeque() {
		return LongArrayDequeWrapper.create();
	}

	@Override
	public LongList newLongArrayList() {
		return LongArrayListWrapper.create();
	}

	@Override
	public LongList newLongArrayList(int expectedSize) {
		return LongArrayListWrapper.create(expectedSize);
	}

	@Override
	public LongList newLongArrayList(long[] source) {
		return LongArrayListWrapper.create(source);
	}

	@Override
	public LongList newLongArrayList(LongCollection source) {
		return LongArrayListWrapper.create(source);
	}

	@Override
	public LongSet newLongChainedHashSet(long[] source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Long> newLongCollectionToCollectionAdapter(LongCollection source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LongKeyFloatMap newLongKeyFloatOpenHashMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LongKeyFloatMap newLongKeyFloatOpenHashMap(int expectedSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LongKeyIntMap newLongKeyIntOpenHashMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LongKeyIntMap newLongKeyIntOpenHashMap(int expectedSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LongKeyLongMap newLongKeyLongOpenHashMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LongKeyLongMap newLongKeyLongOpenHashMap(int expectedSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <V> LongKeyMap<V> newLongKeyOpenHashMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <V> LongKeyMap<V> newLongKeyOpenHashMap(HashFunction hashFunction) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <V> LongKeyMap<V> newLongKeyOpenHashMap(int expectedSize) {
		// TODO Auto-generated method stub
		return null;
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
	public LongSet newLongOpenHashSet(int expectedSize) {
		return LongOpenHashSetWrapper.create(expectedSize);
	}

	@Override
	public LongSet newLongOpenHashSet(int expectedSize, double fillFactor) {
		return LongOpenHashSetWrapper.create(expectedSize, fillFactor);
	}

	@Override
	public LongSet newLongOpenHashSet(long[] source) {
		return LongOpenHashSetWrapper.create(source);
	}

	@Override
	public LongSet newLongOpenHashSet(LongCollection source) {
		return LongOpenHashSetWrapper.create(source);
	}

	@Override
	public Set<Long> newLongSetToSetAdapter(LongSet source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K> ByteValueMap<K> newObjectKeyByteOpenHashMap(int expectedSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K> LongValueMap<K> newObjectKeyLongOpenHashMap(int expectedSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IntList newUnmodifiableIntList(IntList source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IntSet newUnmodifiableIntSet(IntSet source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LongSet newUnmodifiableLongSet(LongSet source) {
		// TODO Auto-generated method stub
		return null;
	}
}
