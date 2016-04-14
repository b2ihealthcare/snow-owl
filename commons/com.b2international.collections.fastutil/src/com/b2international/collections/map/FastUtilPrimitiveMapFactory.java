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
public class FastUtilPrimitiveMapFactory implements PrimitiveMapFactory {

	@Override
	public <V> ByteKeyMap<V> newByteKeyOpenHashMap(int expectedSize) {
		return ByteKeyMapWrapper.create(expectedSize);
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
		throw new UnsupportedOperationException();
	}

	@Override
	public LongKeyIntMap newLongKeyIntOpenHashMap(int expectedSize) {
		throw new UnsupportedOperationException();
	}

	@Override
	public LongKeyLongMap newLongKeyLongOpenHashMap() {
		throw new UnsupportedOperationException();
	}

	@Override
	public LongKeyLongMap newLongKeyLongOpenHashMap(int expectedSize) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <V> LongKeyMap<V> newLongKeyOpenHashMap() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <V> LongKeyMap<V> newLongKeyOpenHashMap(HashFunction hashFunction) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <V> LongKeyMap<V> newLongKeyOpenHashMap(int expectedSize) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public <K> ByteValueMap<K> newObjectKeyByteOpenHashMap(int expectedSize) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <K> LongValueMap<K> newObjectKeyLongOpenHashMap(int expectedSize) {
		throw new UnsupportedOperationException();
	}
	
}
