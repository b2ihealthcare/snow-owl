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
package com.b2international.collections.list;

import com.b2international.collections.ByteCollection;
import com.b2international.collections.FloatCollection;
import com.b2international.collections.LongCollection;
import com.b2international.collections.floats.FloatArrayListWrapper;
import com.b2international.collections.floats.FloatList;

/**
 * @since 4.7
 */
public final class FastUtilPrimitiveListFactory implements PrimitiveListFactory {

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
	public IntList newIntArrayList() {
		return IntArrayListWrapper.create();
	}

	@Override
	public IntList newIntArrayList(int[] source) {
		return IntArrayListWrapper.create(source);
	}
	
	@Override
	public FloatList newFloatArrayList() {
		return FloatArrayListWrapper.create();
	}
	
	@Override
	public FloatList newFloatArrayList(int expectedSize) {
		return FloatArrayListWrapper.create(expectedSize);
	}
	
	@Override
	public FloatList newFloatArrayList(float... source) {
		return FloatArrayListWrapper.create(source);
	}
	
	@Override
	public FloatList newFloatArrayList(FloatCollection source) {
		return FloatArrayListWrapper.create(source);
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
	public LongList newLongArrayList(long... source) {
		return LongArrayListWrapper.create(source);
	}

	@Override
	public LongList newLongArrayList(LongCollection source) {
		return LongArrayListWrapper.create(source);
	}
	
	@Override
	public IntDeque newIntArrayDeque(int[] source) {
		return IntArrayDequeWrapper.create(source);
	}
	
	@Override
	public LongDeque newLongArrayDeque() {
		return LongArrayDequeWrapper.create();
	}
	
}
