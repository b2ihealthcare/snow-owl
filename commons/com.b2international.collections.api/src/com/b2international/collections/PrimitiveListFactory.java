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

import com.b2international.collections.bytes.ByteCollection;
import com.b2international.collections.bytes.ByteList;
import com.b2international.collections.floats.FloatCollection;
import com.b2international.collections.floats.FloatList;
import com.b2international.collections.ints.IntDeque;
import com.b2international.collections.ints.IntList;
import com.b2international.collections.longs.LongCollection;
import com.b2international.collections.longs.LongDeque;
import com.b2international.collections.longs.LongList;

/**
 * @since 4.7
 */
public interface PrimitiveListFactory {

	ByteList newByteArrayList(byte... source);

	ByteList newByteArrayList(ByteCollection source);

	ByteList newByteArrayListWithExpectedSize(int expectedSize);
	
	IntList newIntArrayList();

	IntList newIntArrayList(int... source);
	
	IntDeque newIntArrayDeque(int... source);
	
	FloatList newFloatArrayList();
	
	FloatList newFloatArrayListWithExpectedSize(int expectedSize);
	
	FloatList newFloatArrayList(float... source);
	
	FloatList newFloatArrayList(FloatCollection source);
	
	LongList newLongArrayList();

	LongList newLongArrayListWithExpectedSize(int expectedSize);

	LongList newLongArrayList(long... source);

	LongList newLongArrayList(LongCollection source);

	LongDeque newLongArrayDeque();
}
