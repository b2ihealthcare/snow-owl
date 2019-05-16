/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
public interface PrimitiveMapFactory {

	<V> ByteKeyMap<V> newByteKeyOpenHashMapWithExpectedSize(int expectedSize);
	
	ByteKeyLongMap newByteKeyLongOpenHashMap();
	
	ByteKeyLongMap newByteKeyLongOpenHashMapWithExpectedSize(int expectedSize);
	
	<V> IntKeyMap<V> newIntKeyOpenHashMap();
	
	<V> IntKeyMap<V> newIntKeyOpenHashMap(IntKeyMap<V> source);

	<V> IntKeyMap<V> newIntKeyOpenHashMapWithExpectedSize(int expectedSize);
	
	LongKeyFloatMap newLongKeyFloatOpenHashMap();

	LongKeyFloatMap newLongKeyFloatOpenHashMapWithExpectedSize(int expectedSize);

	LongKeyFloatMap newLongKeyFloatOpenHashMap(LongKeyFloatMap source);

	LongKeyIntMap newLongKeyIntOpenHashMap();

	LongKeyIntMap newLongKeyIntOpenHashMapWithExpectedSize(int expectedSize);

	LongKeyIntMap newLongKeyIntOpenHashMap(LongKeyIntMap source);
	
	LongKeyLongMap newLongKeyLongOpenHashMap();

	LongKeyLongMap newLongKeyLongOpenHashMapWithExpectedSize(int expectedSize);

	LongKeyLongMap newLongKeyLongOpenHashMap(LongKeyLongMap source);
	
	<V> LongKeyMap<V> newLongKeyOpenHashMap();

	<V> LongKeyMap<V> newLongKeyOpenHashMap(HashFunction hashFunction);

	<V> LongKeyMap<V> newLongKeyOpenHashMapWithExpectedSize(int expectedSize);
	
	<V> LongKeyMap<V> newLongKeyOpenHashMap(LongKeyMap<V> source);
	
	<K> ByteValueMap<K> newObjectKeyByteOpenHashMap();
	
	<K> ByteValueMap<K> newObjectKeyByteOpenHashMapWithExpectedSize(int expectedSize);

	<K> IntValueMap<K> newObjectKeyIntOpenHashMap();
	
	<K> IntValueMap<K> newObjectKeyIntOpenHashMapWithExpectedSize(int expectedSize);
	
	<K> LongValueMap<K> newObjectKeyLongOpenHashMap();
	
	<K> LongValueMap<K> newObjectKeyLongOpenHashMapWithExpectedSize(int expectedSize);
}
