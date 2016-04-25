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

import com.b2international.collections.FastUtilPrimitiveCollections;
import com.b2international.collections.PrimitiveMapFactory;
import com.b2international.collections.bytes.ByteKeyLongMap;
import com.b2international.collections.bytes.ByteKeyMap;
import com.b2international.collections.bytes.ByteValueMap;
import com.b2international.collections.ints.IntKeyMap;
import com.b2international.collections.longs.LongKeyFloatMap;
import com.b2international.collections.longs.LongKeyIntMap;
import com.b2international.collections.longs.LongKeyLongMap;
import com.b2international.collections.longs.LongKeyMap;
import com.b2international.collections.longs.LongValueMap;
import com.google.common.hash.HashFunction;

/**
 * @since 4.7
 */
public abstract class PrimitiveMaps {

	private static final PrimitiveMapFactory FACTORY = FastUtilPrimitiveCollections.maps();
	
	private PrimitiveMaps() {}
	
	public static <V> ByteKeyMap<V> newByteKeyOpenHashMapWithExpectedSize(int expectedSize) {
		return FACTORY.newByteKeyOpenHashMapWithExpectedSize(expectedSize);
	}
	
	public static ByteKeyLongMap newByteKeyLongOpenHashMap() {
		return FACTORY.newByteKeyLongOpenHashMap();
	}
	
	public static ByteKeyLongMap newByteKeyLongOpenHashMapWithExpectedSize(int expectedSize) {
		return FACTORY.newByteKeyLongOpenHashMapWithExpectedSize(expectedSize);
	}

	public static <V> IntKeyMap<V> newIntKeyOpenHashMap() {
		return FACTORY.newIntKeyOpenHashMap();
	}

	public static <V> IntKeyMap<V> newIntKeyOpenHashMapWithExpectedSize(int expectedSize) {
		return FACTORY.newIntKeyOpenHashMapWithExpectedSize(expectedSize);
	}

	public static LongKeyFloatMap newLongKeyFloatOpenHashMap() {
		return FACTORY.newLongKeyFloatOpenHashMap();
	}

	public static LongKeyFloatMap newLongKeyFloatOpenHashMapWithExpectedSize(int expectedSize) {
		return FACTORY.newLongKeyFloatOpenHashMapWithExpectedSize(expectedSize);
	}

	public static LongKeyIntMap newLongKeyIntOpenHashMap() {
		return FACTORY.newLongKeyIntOpenHashMap();
	}

	public static LongKeyIntMap newLongKeyIntOpenHashMapWithExpectedSize(int expectedSize) {
		return FACTORY.newLongKeyIntOpenHashMapWithExpectedSize(expectedSize);
	}

	public static LongKeyLongMap newLongKeyLongOpenHashMap() {
		return FACTORY.newLongKeyLongOpenHashMap();
	}

	public static LongKeyLongMap newLongKeyLongOpenHashMapWithExpectedSize(int expectedSize) {
		return FACTORY.newLongKeyLongOpenHashMapWithExpectedSize(expectedSize);
	}

	public static <V> LongKeyMap<V> newLongKeyOpenHashMap() {
		return FACTORY.newLongKeyOpenHashMap();
	}

	public static <V> LongKeyMap<V> newLongKeyOpenHashMap(HashFunction hashFunction) {
		return FACTORY.newLongKeyOpenHashMap(hashFunction);
	}

	public static <V> LongKeyMap<V> newLongKeyOpenHashMapWithExpectedSize(int expectedSize) {
		return FACTORY.newLongKeyOpenHashMapWithExpectedSize(expectedSize);
	}

	public static <K> ByteValueMap<K> newObjectKeyByteOpenHashMap() {
		return FACTORY.newObjectKeyByteOpenHashMap();
	}
	
	public static <K> ByteValueMap<K> newObjectKeyByteOpenHashMapWithExpectedSize(int expectedSize) {
		return FACTORY.newObjectKeyByteOpenHashMapWithExpectedSize(expectedSize);
	}
	
	public static <K> LongValueMap<K> newObjectKeyLongOpenHashMap() {
		return FACTORY.newObjectKeyLongOpenHashMap();
	}

	public static <K> LongValueMap<K> newObjectKeyLongOpenHashMapWithExpectedSize(int expectedSize) {
		return FACTORY.newObjectKeyLongOpenHashMapWithExpectedSize(expectedSize);
	}
}
