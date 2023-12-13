/*
 * Copyright 2011-2016 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.collections.longs;

import java.util.Collection;

import com.b2international.collections.PrimitiveKeyMap;

/**
 * @since 4.7
 * 
 * @param <V>
 */
public interface LongKeyMap<V> extends PrimitiveKeyMap {

	boolean containsKey(long key);

	V get(long key);

	@Override
	LongSet keySet();

	V put(long key, V value);

	V remove(long key);

	Collection<V> values();
}
