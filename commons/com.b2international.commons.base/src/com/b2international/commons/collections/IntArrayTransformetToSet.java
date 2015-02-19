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

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.b2international.commons.arrays.IntegerArrayIterator;
import com.google.common.base.Function;
import com.google.common.collect.Iterators;

public class IntArrayTransformetToSet<E> extends AbstractSet<E> {

	private final Function<Integer, E> fromIndex;
	private final Function<E, Integer> toIndex;
	private final int[] elements;
	private boolean sorted;
	
	public IntArrayTransformetToSet(Function<Integer, E> fromIndex, Function<E, Integer> toIndex, int... elements) {
		this.fromIndex = fromIndex;
		this.toIndex = toIndex;
		this.elements = elements;
	}
	
	@Override
	public Iterator<E> iterator() {
		return Iterators.transform(new IntegerArrayIterator(elements), fromIndex);
	}
	
	@Override
	public boolean contains(Object o) {
		int index = toIndex.apply((E) o);
		if (!sorted) {
			synchronized (this) {
				if (!sorted) {
					Arrays.sort(this.elements);
					sorted = true;
				}
			}
		}
		return Arrays.binarySearch(elements, index) >= 0;
	}

	@Override
	public int size() {
		return elements.length;
	}
	
	public <T extends Object> T[] toArray(T[] dest) {
		System.arraycopy(elements, 0, dest, 0, elements.length);
		return dest;
	};
	
	public List<E> asList() {
		return new IntArrayTransformedToList<E>(fromIndex, elements);
	}
}