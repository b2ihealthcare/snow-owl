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
import com.b2international.collections.map.ByteKeyLongMap;
import com.b2international.collections.map.ByteKeyMap;
import com.b2international.collections.map.ByteValueMap;
import com.b2international.collections.map.IntKeyMap;
import com.b2international.collections.map.LongKeyFloatMap;
import com.b2international.collections.map.LongKeyIntMap;
import com.b2international.collections.map.LongKeyLongMap;
import com.b2international.collections.map.LongKeyMap;
import com.b2international.collections.map.LongValueMap;
import com.b2international.collections.map.PrimitiveMapFactory;
import com.google.common.hash.HashFunction;

/**
 * @since 4.7
 */
public abstract class PrimitiveMaps {

	private static final PrimitiveMapFactory FACTORY = FastUtilPrimitiveCollections.maps();
	
	public static ByteKeyLongMap newByteKeyLongOpenHashMap() {
		return FACTORY.newByteKeyLongOpenHashMap();
	}

	public static <V> ByteKeyMap<V> newByteKeyOpenHashMap(int expectedSize) {
		return FACTORY.newByteKeyOpenHashMap(expectedSize);
	}

	public static <V> IntKeyMap<V> newIntKeyOpenHashMap() {
		return FACTORY.newIntKeyOpenHashMap();
	}

	public static <V> IntKeyMap<V> newIntKeyOpenHashMap(int expectedSize) {
		return FACTORY.newIntKeyOpenHashMap(expectedSize);
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

	public static <K> ByteValueMap<K> newObjectKeyByteOpenHashMap(int expectedSize) {
		return FACTORY.newObjectKeyByteOpenHashMap(expectedSize);
	}

	public static <K> LongValueMap<K> newObjectKeyLongOpenHashMap(int expectedSize) {
		return FACTORY.newObjectKeyLongOpenHashMap(expectedSize);
	}

	private PrimitiveMaps() {
	}
	
}
