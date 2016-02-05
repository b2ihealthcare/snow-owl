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
package com.b2international.commons.pcj;

import java.util.Collection;
import java.util.Set;

import com.b2international.commons.collections.primitive.ByteCollection;
import com.b2international.commons.collections.primitive.IntCollection;
import com.b2international.commons.collections.primitive.LongCollection;
import com.b2international.commons.collections.primitive.PrimitiveFactory;
import com.b2international.commons.collections.primitive.list.ByteList;
import com.b2international.commons.collections.primitive.list.IntDeque;
import com.b2international.commons.collections.primitive.list.IntList;
import com.b2international.commons.collections.primitive.list.LongDeque;
import com.b2international.commons.collections.primitive.list.LongList;
import com.b2international.commons.collections.primitive.map.ByteKeyLongMap;
import com.b2international.commons.collections.primitive.map.ByteKeyMap;
import com.b2international.commons.collections.primitive.map.ByteValueMap;
import com.b2international.commons.collections.primitive.map.IntKeyMap;
import com.b2international.commons.collections.primitive.map.LongKeyFloatMap;
import com.b2international.commons.collections.primitive.map.LongKeyIntMap;
import com.b2international.commons.collections.primitive.map.LongKeyLongMap;
import com.b2international.commons.collections.primitive.map.LongKeyMap;
import com.b2international.commons.collections.primitive.map.LongValueMap;
import com.b2international.commons.collections.primitive.set.ByteSet;
import com.b2international.commons.collections.primitive.set.IntSet;
import com.b2international.commons.collections.primitive.set.LongSet;
import com.b2international.commons.fastutil.FastUtilPrimitiveFactory;
import com.google.common.hash.HashFunction;

/**
 * @since 4.6
 */
public class PrimitiveCollections {

	// TODO: Make this configurable?
	private static final PrimitiveFactory FACTORY = new FastUtilPrimitiveFactory();

	public static ByteList newByteArrayList(byte[] source) {
		return FACTORY.newByteArrayList(source);
	}

	public static ByteList newByteArrayList(ByteCollection source) {
		return FACTORY.newByteArrayList(source);
	}

	public static ByteList newByteArrayList(int expectedSize) {
		return FACTORY.newByteArrayList(expectedSize);
	}

	public static ByteKeyLongMap newByteKeyLongOpenHashMap() {
		return FACTORY.newByteKeyLongOpenHashMap();
	}

	public static <V> ByteKeyMap<V> newByteKeyOpenHashMap(int expectedSize) {
		return FACTORY.newByteKeyOpenHashMap(expectedSize);
	}

	public static ByteSet newByteOpenHashSet(ByteCollection source) {
		return FACTORY.newByteOpenHashSet(source);
	}

	public static IntDeque newIntArrayDeque(int[] source) {
		return FACTORY.newIntArrayDeque(source);
	}

	public static IntList newIntArrayList() {
		return FACTORY.newIntArrayList();
	}

	public static IntList newIntArrayList(int[] source) {
		return FACTORY.newIntArrayList(source);
	}

	public static IntSet newIntBitSet() {
		return FACTORY.newIntBitSet();
	}

	public static IntSet newIntBitSet(int expectedSize) {
		return FACTORY.newIntBitSet(expectedSize);
	}

	public static IntSet newIntBitSet(int[] source) {
		return FACTORY.newIntBitSet(source);
	}

	public static IntSet newIntBitSet(IntCollection source) {
		return FACTORY.newIntBitSet(source);
	}

	public static <V> IntKeyMap<V> newIntKeyOpenHashMap() {
		return FACTORY.newIntKeyOpenHashMap();
	}

	public static <V> IntKeyMap<V> newIntKeyOpenHashMap(int expectedSize) {
		return FACTORY.newIntKeyOpenHashMap(expectedSize);
	}

	public static IntSet newIntOpenHashSet() {
		return FACTORY.newIntOpenHashSet();
	}

	public static IntSet newIntOpenHashSet(int expectedSize) {
		return FACTORY.newIntOpenHashSet(expectedSize);
	}

	public static LongDeque newLongArrayDeque() {
		return FACTORY.newLongArrayDeque();
	}

	public static LongList newLongArrayList() {
		return FACTORY.newLongArrayList();
	}

	public static LongList newLongArrayList(int expectedSize) {
		return FACTORY.newLongArrayList(expectedSize);
	}

	public static LongList newLongArrayList(long[] source) {
		return FACTORY.newLongArrayList(source);
	}

	public static LongList newLongArrayList(LongCollection source) {
		return FACTORY.newLongArrayList(source);
	}

	public static LongSet newLongChainedHashSet(long[] source) {
		return FACTORY.newLongChainedHashSet(source);
	}

	public static Collection<Long> newLongCollectionToCollectionAdapter(LongCollection source) {
		return FACTORY.newLongCollectionToCollectionAdapter(source);
	}

	public static LongKeyFloatMap newLongKeyFloatOpenHashMap() {
		return FACTORY.newLongKeyFloatOpenHashMap();
	}

	public static LongKeyFloatMap newLongKeyFloatOpenHashMap(int expectedSize) {
		return FACTORY.newLongKeyFloatOpenHashMap(expectedSize);
	}

	public static LongKeyIntMap newLongKeyIntOpenHashMap() {
		return FACTORY.newLongKeyIntOpenHashMap();
	}

	public static LongKeyIntMap newLongKeyIntOpenHashMap(int expectedSize) {
		return FACTORY.newLongKeyIntOpenHashMap(expectedSize);
	}

	public static LongKeyLongMap newLongKeyLongOpenHashMap() {
		return FACTORY.newLongKeyLongOpenHashMap();
	}

	public static LongKeyLongMap newLongKeyLongOpenHashMap(int expectedSize) {
		return FACTORY.newLongKeyLongOpenHashMap(expectedSize);
	}

	public static <V> LongKeyMap<V> newLongKeyOpenHashMap() {
		return FACTORY.newLongKeyOpenHashMap();
	}

	public static <V> LongKeyMap<V> newLongKeyOpenHashMap(HashFunction hashFunction) {
		return FACTORY.newLongKeyOpenHashMap(hashFunction);
	}

	public static <V> LongKeyMap<V> newLongKeyOpenHashMap(int expectedSize) {
		return FACTORY.newLongKeyOpenHashMap(expectedSize);
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
		return FACTORY.newLongOpenHashSet(source);
	}

	public static LongSet newLongOpenHashSet(LongCollection source) {
		return FACTORY.newLongOpenHashSet(source);
	}

	public static Set<Long> newLongSetToSetAdapter(LongSet source) {
		return FACTORY.newLongSetToSetAdapter(source);
	}

	public static <K> ByteValueMap<K> newObjectKeyByteOpenHashMap(int expectedSize) {
		return FACTORY.newObjectKeyByteOpenHashMap(expectedSize);
	}

	public static <K> LongValueMap<K> newObjectKeyLongOpenHashMap(int expectedSize) {
		return FACTORY.newObjectKeyLongOpenHashMap(expectedSize);
	}

	public static IntList newUnmodifiableIntList(IntList source) {
		return FACTORY.newUnmodifiableIntList(source);
	}

	public static IntSet newUnmodifiableIntSet(IntSet source) {
		return FACTORY.newUnmodifiableIntSet(source);
	}

	public static LongSet newUnmodifiableLongSet(LongSet source) {
		return FACTORY.newUnmodifiableLongSet(source);
	}
}
