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

import com.b2international.collections.bytes.ByteKeyLongMap;
import com.b2international.collections.bytes.ByteKeyLongMapWrapper;
import com.b2international.collections.bytes.ByteKeyMap;
import com.b2international.collections.bytes.ByteKeyMapWrapper;
import com.b2international.collections.bytes.ByteValueMap;
import com.b2international.collections.ints.IntKeyMap;
import com.b2international.collections.ints.IntKeyMapWrapper;
import com.b2international.collections.longs.LongKeyFloatMap;
import com.b2international.collections.longs.LongKeyFloatMapWrapper;
import com.b2international.collections.longs.LongKeyIntMap;
import com.b2international.collections.longs.LongKeyIntMapWrapper;
import com.b2international.collections.longs.LongKeyLongMap;
import com.b2international.collections.longs.LongKeyLongMapWrapper;
import com.b2international.collections.longs.LongKeyMap;
import com.b2international.collections.longs.LongKeyMapWrapper;
import com.b2international.collections.longs.LongValueMap;
import com.b2international.collections.objects.ObjectKeyByteMapWrapper;
import com.b2international.collections.objects.ObjectKeyLongMapWrapper;
import com.google.common.hash.HashFunction;

/**
 * @since 4.7
 */
public class FastUtilPrimitiveMapFactory implements PrimitiveMapFactory {

	@Override
	public <V> ByteKeyMap<V> newByteKeyOpenHashMap(int expectedSize) {
		return ByteKeyMapWrapper.create(expectedSize);
	}
	
	@Override
	public ByteKeyLongMap newByteKeyLongOpenHashMap() {
		return ByteKeyLongMapWrapper.create();
	}
	
	@Override
	public ByteKeyLongMap newByteKeyLongOpenHashMap(int expectedSize) {
		return ByteKeyLongMapWrapper.create(expectedSize);
	}
	
	@Override
	public <V> IntKeyMap<V> newIntKeyOpenHashMap() {
		return IntKeyMapWrapper.create();
	}

	@Override
	public <V> IntKeyMap<V> newIntKeyOpenHashMap(int expectedSize) {
		return IntKeyMapWrapper.create(expectedSize);
	}
	
	@Override
	public LongKeyFloatMap newLongKeyFloatOpenHashMap() {
		return LongKeyFloatMapWrapper.create();
	}

	@Override
	public LongKeyFloatMap newLongKeyFloatOpenHashMap(int expectedSize) {
		return LongKeyFloatMapWrapper.create(expectedSize);
	}

	@Override
	public LongKeyIntMap newLongKeyIntOpenHashMap() {
		return LongKeyIntMapWrapper.create();
	}

	@Override
	public LongKeyIntMap newLongKeyIntOpenHashMap(int expectedSize) {
		return LongKeyIntMapWrapper.create(expectedSize);
	}

	@Override
	public LongKeyLongMap newLongKeyLongOpenHashMap() {
		return LongKeyLongMapWrapper.create();
	}

	@Override
	public LongKeyLongMap newLongKeyLongOpenHashMap(int expectedSize) {
		return LongKeyLongMapWrapper.create(expectedSize);
	}

	@Override
	public <V> LongKeyMap<V> newLongKeyOpenHashMap() {
		return LongKeyMapWrapper.create();
	}

	@Override
	public <V> LongKeyMap<V> newLongKeyOpenHashMap(int expectedSize) {
		return LongKeyMapWrapper.create(expectedSize);
	}
	
	@Override
	public <V> LongKeyMap<V> newLongKeyOpenHashMap(HashFunction hashFunction) {
		return LongKeyMapWrapper.create(hashFunction);
	}

	@Override
	public <K> ByteValueMap<K> newObjectKeyByteOpenHashMap(int expectedSize) {
		return ObjectKeyByteMapWrapper.create(expectedSize);
	}

	@Override
	public <K> LongValueMap<K> newObjectKeyLongOpenHashMap(int expectedSize) {
		return ObjectKeyLongMapWrapper.create(expectedSize);
	}
	
}
