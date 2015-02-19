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

import java.util.Arrays;

public class SortedIntArrayUtils {

	public static int[] addToArray(int[] a, int value) {
		
		if(a == null || a.length == 0) {
			return new int[] {value};
		}
		
		int[] array = Arrays.copyOf(a, a.length + 1);
		array[a.length] = value;
		return array;
	}
	
	public static int[] addToSortedArray(int[] a, int value) {
		
		if(a == null || a.length == 0) {
			return new int[] {value};
		}
		
		int insertionPoint = - Arrays.binarySearch(a, value) - 1;
		if(insertionPoint < 0) {
			throw new IllegalArgumentException(String.format("Element %d already exists in array", value));
		}
		
		int[] array = new int[a.length + 1];
		if(insertionPoint > 0) {
			System.arraycopy(a, 0, array, 0, insertionPoint);
		}
		array[insertionPoint] = value;
		if(insertionPoint < a.length) {
			System.arraycopy(a, insertionPoint, array, insertionPoint + 1, array.length - insertionPoint - 1);
		}
		
		return array;
	}
	
	public static int[] removeFromArray(int[] a, int value) {
		
		int index = -1;
		for(int i = 0; i < a.length; i++) {
			if(a[i] == value) {
				index = i;
				break;
			}
		}
		if(index < 0) {
			throw new IllegalArgumentException(String.format("Element %d not found in array", value));
		}
		
		int[] array = new int[a.length - 1];
		if(index > 0) {
			System.arraycopy(a, 0, array, 0, index);
		}
		if(index < a.length) {
			System.arraycopy(a, index + 1, array, index, array.length - index);
		}
		
		return array;
	}
}