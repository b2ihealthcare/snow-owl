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
package com.b2international.collections.map;

import com.google.common.hash.HashFunction;

/**
 * @since 4.7
 */
public interface PrimitiveMapFactory {

	ByteKeyLongMap newByteKeyLongOpenHashMap();

	<V> ByteKeyMap<V> newByteKeyOpenHashMap(int expectedSize);
	
	<V> IntKeyMap<V> newIntKeyOpenHashMap();

	<V> IntKeyMap<V> newIntKeyOpenHashMap(int expectedSize);
	
	LongKeyFloatMap newLongKeyFloatOpenHashMap();

	LongKeyFloatMap newLongKeyFloatOpenHashMap(int expectedSize);

	LongKeyIntMap newLongKeyIntOpenHashMap();

	LongKeyIntMap newLongKeyIntOpenHashMap(int expectedSize);

	LongKeyLongMap newLongKeyLongOpenHashMap();

	LongKeyLongMap newLongKeyLongOpenHashMap(int expectedSize);

	<V> LongKeyMap<V> newLongKeyOpenHashMap();

	<V> LongKeyMap<V> newLongKeyOpenHashMap(HashFunction hashFunction);

	<V> LongKeyMap<V> newLongKeyOpenHashMap(int expectedSize);
	
	<K> ByteValueMap<K> newObjectKeyByteOpenHashMap(int expectedSize);

	<K> LongValueMap<K> newObjectKeyLongOpenHashMap(int expectedSize);

}
