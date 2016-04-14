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
import com.b2international.collections.PrimitiveListFactory;
import com.b2international.collections.bytes.ByteCollection;
import com.b2international.collections.bytes.ByteList;
import com.b2international.collections.ints.IntDeque;
import com.b2international.collections.ints.IntList;
import com.b2international.collections.longs.LongCollection;
import com.b2international.collections.longs.LongDeque;
import com.b2international.collections.longs.LongList;

/**
 * @since 4.7
 */
public class PrimitiveLists {

	private static final PrimitiveListFactory FACTORY = FastUtilPrimitiveCollections.lists();
	
	private PrimitiveLists() {
	}

	public static ByteList newByteArrayList(byte[] source) {
		return FACTORY.newByteArrayList(source);
	}

	public static ByteList newByteArrayList(ByteCollection source) {
		return FACTORY.newByteArrayList(source);
	}

	public static ByteList newByteArrayList(int expectedSize) {
		return FACTORY.newByteArrayList(expectedSize);
	}

	public static IntList newIntArrayList() {
		return FACTORY.newIntArrayList();
	}

	public static IntList newIntArrayList(int[] source) {
		return FACTORY.newIntArrayList(source);
	}

	public static LongList newLongArrayList() {
		return FACTORY.newLongArrayList();
	}

	public static LongList newLongArrayList(int expectedSize) {
		return FACTORY.newLongArrayList(expectedSize);
	}

	public static LongList newLongArrayList(long... source) {
		return FACTORY.newLongArrayList(source);
	}
	
	public static LongList newLongArrayList(LongCollection source) {
		return FACTORY.newLongArrayList(source);
	}

	public static IntDeque newIntArrayDeque(int[] source) {
		return FACTORY.newIntArrayDeque(source);
	}

	public static LongDeque newLongArrayDeque() {
		return FACTORY.newLongArrayDeque();
	}
	
	
	
}
