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
package com.b2international.commons.arrays;

import java.util.Arrays;
import java.util.Iterator;

/**
 * <p>An unmodifiable set of <code>int</code>s backed by an <code>int[]</code>.
 * Elements are sorted for speedy lookups.</p>
 * 
 * 
 *
 */
public class SortedIntArraySet implements Iterable<Integer> {

	private int[] array;
	
	public SortedIntArraySet(int... values) {
		array = values;
		Arrays.sort(array);
	}
	
	/** @return true if set contains <code>key</code>, false otherwise */
	public boolean contains(int key) {
		return Arrays.binarySearch(array, key) > 0; 
	}
	
	/** @return index of <key>key</code>, if it is contained in the array; otherwise, (-(insertion point) - 1) */
	public int indexOf(int key) {
		return Arrays.binarySearch(array, key);
	}
	
	public int get(int index) {
		return array[index];
	}
	
	public int size() {
		return array.length;
	}
	
	@Override
	public Iterator<Integer> iterator() {
		return new IntegerArrayIterator(array);
	}
}