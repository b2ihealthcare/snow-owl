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
package com.b2international.commons.collections.primitive.map;

import com.b2international.commons.collections.primitive.LongCollection;
import com.b2international.commons.collections.primitive.set.LongSet;

/**
 * @since 4.6
 */
public interface LongKeyLongMap extends PrimitiveKeyMap, PrimitiveValueMap {

	boolean containsKey(long key);

	@Override
	LongKeyLongMap dup();

	long get(long key);

	@Override
	LongSet keySet();
	
	@Override
	LongKeyLongMapIterator mapIterator();

	long put(long key, long value);

	long remove(long key);
	
	@Override
	LongCollection values();
}
