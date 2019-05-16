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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Iterator;
import java.util.ServiceLoader;

import com.b2international.collections.bytes.ByteKeyLongMap;
import com.b2international.collections.bytes.ByteKeyMap;
import com.b2international.collections.bytes.ByteValueMap;
import com.b2international.collections.ints.IntKeyMap;
import com.b2international.collections.ints.IntValueMap;
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

	private static final PrimitiveMapFactory FACTORY;
	
	static {
		final ServiceLoader<PrimitiveMapFactory> loader = ServiceLoader.load(PrimitiveMapFactory.class, PrimitiveMaps.class.getClassLoader());
		final Iterator<PrimitiveMapFactory> services = loader.iterator();
		checkState(services.hasNext(), "No %s implementation has been found", PrimitiveMapFactory.class.getName());
		FACTORY = services.next();
	}
	
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
	
	public static <V> IntKeyMap<V> newIntKeyOpenHashMap(IntKeyMap<V> source) {
		return FACTORY.newIntKeyOpenHashMap(source);
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
	
	public static LongKeyFloatMap newLongKeyFloatOpenHashMap(LongKeyFloatMap source) {
		if (source == null) {
			return newLongKeyFloatOpenHashMap();
		} else {
			return FACTORY.newLongKeyFloatOpenHashMap(source);
		}
	}

	public static LongKeyIntMap newLongKeyIntOpenHashMap() {
		return FACTORY.newLongKeyIntOpenHashMap();
	}

	public static LongKeyIntMap newLongKeyIntOpenHashMapWithExpectedSize(int expectedSize) {
		return FACTORY.newLongKeyIntOpenHashMapWithExpectedSize(expectedSize);
	}
	
	public static LongKeyIntMap newLongKeyIntOpenHashMap(LongKeyIntMap source) {
		if (source == null) {
			return newLongKeyIntOpenHashMap();
		} else {
			return FACTORY.newLongKeyIntOpenHashMap(source);
		}
	}

	public static LongKeyLongMap newLongKeyLongOpenHashMap() {
		return FACTORY.newLongKeyLongOpenHashMap();
	}

	public static LongKeyLongMap newLongKeyLongOpenHashMapWithExpectedSize(int expectedSize) {
		return FACTORY.newLongKeyLongOpenHashMapWithExpectedSize(expectedSize);
	}

	public static LongKeyLongMap newLongKeyLongOpenHashMap(LongKeyLongMap source) {
		if (source == null) {
			return newLongKeyLongOpenHashMap();
		} else {
			return FACTORY.newLongKeyLongOpenHashMap(source);
		}
	}
	
	public static <V> LongKeyMap<V> newLongKeyOpenHashMap() {
		return FACTORY.newLongKeyOpenHashMap();
	}

	public static <V> LongKeyMap<V> newLongKeyOpenHashMap(HashFunction hashFunction) {
		checkNotNull(hashFunction, "hashFunction may not be null.");
		return FACTORY.newLongKeyOpenHashMap(hashFunction);
	}

	public static <V> LongKeyMap<V> newLongKeyOpenHashMapWithExpectedSize(int expectedSize) {
		return FACTORY.newLongKeyOpenHashMapWithExpectedSize(expectedSize);
	}
	
	public static <V> LongKeyMap<V> newLongKeyOpenHashMap(LongKeyMap<V> source) {
		if (source == null) {
			return newLongKeyOpenHashMap();
		} else {
			return FACTORY.newLongKeyOpenHashMap(source);
		}
	}

	public static <K> ByteValueMap<K> newObjectKeyByteOpenHashMap() {
		return FACTORY.newObjectKeyByteOpenHashMap();
	}
	
	public static <K> ByteValueMap<K> newObjectKeyByteOpenHashMapWithExpectedSize(int expectedSize) {
		return FACTORY.newObjectKeyByteOpenHashMapWithExpectedSize(expectedSize);
	}
	
	public static <K> IntValueMap<K> newObjectKeyIntOpenHashMap() {
		return FACTORY.newObjectKeyIntOpenHashMap();
	}
	
	public static <K> IntValueMap<K> newObjectKeyIntOpenHashMapWithExpectedSize(int expectedSize) {
		return FACTORY.newObjectKeyIntOpenHashMapWithExpectedSize(expectedSize);
	}
	
	public static <K> LongValueMap<K> newObjectKeyLongOpenHashMap() {
		return FACTORY.newObjectKeyLongOpenHashMap();
	}

	public static <K> LongValueMap<K> newObjectKeyLongOpenHashMapWithExpectedSize(int expectedSize) {
		return FACTORY.newObjectKeyLongOpenHashMapWithExpectedSize(expectedSize);
	}
}
