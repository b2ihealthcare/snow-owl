/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.commons.collections;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Iterator;

import com.google.common.base.Functions;
import com.google.common.collect.Iterators;
import com.google.common.primitives.Longs;

/**
 * Set representation of an array containing primitive type of {@code long}s.
 * <p>
 * <b>NOTE:&nbsp;</b>Clients must make sure that the specified long elements are unique and the elements are sorted in ascending numerical order.
 */
public class LongArrayTransformedToSet extends AbstractSet<String> implements Serializable {

	private final long[] elements;
	
	public LongArrayTransformedToSet(long... elements) {
		this.elements = elements;
	}

	@Override
	public Iterator<String> iterator() {
		return Iterators.transform(Longs.asList(elements).iterator(), Functions.toStringFunction());
	}

	@Override
	public int size() {
		return elements.length;
	}
	
	@Override
	public boolean contains(Object o) {
		long index = Long.valueOf((String) o);
		return Arrays.binarySearch(elements, index) > -1;
	}
	
}