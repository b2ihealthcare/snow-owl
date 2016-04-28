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
package com.b2international.collections.floats;

import com.b2international.collections.PrimitiveCollection;

/**
 * @since 4.7
 */
public interface FloatCollection extends PrimitiveCollection {

	boolean add(float value);

	boolean addAll(FloatCollection collection);

	boolean contains(float value);

	boolean containsAll(FloatCollection collection);

	@Override
	FloatIterator iterator();

	boolean remove(float value);

	boolean removeAll(FloatCollection collection);

	boolean retainAll(FloatCollection collection);
	
	float[] toArray();
}
