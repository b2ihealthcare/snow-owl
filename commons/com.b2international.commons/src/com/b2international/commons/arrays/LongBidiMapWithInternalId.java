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

import java.io.Serializable;

import com.b2international.collections.PrimitiveLists;
import com.b2international.collections.PrimitiveMaps;
import com.b2international.collections.longs.LongKeyIntMap;
import com.b2international.collections.longs.LongList;

/**
 * Bidirectional map for primitive longs with primitive integer internal IDs.
 * 
 *
 */
public class LongBidiMapWithInternalId implements Serializable {

	private static final long serialVersionUID = -101308853617017546L;

	private LongKeyIntMap keyMap;
	private LongList elements;

	public LongBidiMapWithInternalId(int expectedSize) {
		keyMap = expectedSize < 1 ? PrimitiveMaps.newLongKeyIntOpenHashMap() : PrimitiveMaps.newLongKeyIntOpenHashMapWithExpectedSize(expectedSize);
		elements = expectedSize < 1 ? PrimitiveLists.newLongArrayList() : PrimitiveLists.newLongArrayListWithExpectedSize(expectedSize);
	}

	public LongBidiMapWithInternalId(final LongBidiMapWithInternalId original) {
		keyMap = PrimitiveMaps.newLongKeyIntOpenHashMap(original.keyMap);
		elements = PrimitiveLists.newLongArrayList(original.elements);
	}

	/**
	 * Add element to the map with key
	 * 
	 * @param key
	 * @param element
	 * @return internal id
	 */
	public int put(long key, long element) {
		
		if (1 >= key) {
			throw new IllegalArgumentException("Key value should be a positive integer.");
		}
		
		if (0 > element) {
			throw new IllegalArgumentException("Value of the elements should be a positive integer or zero.");
		}
		
		int id = _internalGet(key);
		if (id < 0) {
			
			elements.add(element);
			keyMap.put(key, elements.size() - 1);
			
		} else {
			
			elements.set(id, element);
			
		}
		
		return id;
	}

	/**
	 * @return element for the specified key, or null if not found
	 */
	public long get(long key) {
		int id = getInternalId(key);
		return id < 0 ? -1 : elements.get(id);
	}

	/**
	 * @return element for the specified internal id
	 * @throws IndexOutOfBoundsException
	 */
	public long get(int internalId) {
		return elements.get(internalId);
	}

	/** @return internal id of the removed element, id -1 if not found */
	public int remove(long key) {
		return keyMap.remove(key);
	}

	public int getInternalId(long key) {
		return _internalGet(key);
	}

	public int size() {
		return elements.size();
	}
	
	public void clear() {
		if (null != keyMap) {
			keyMap.clear();
		}
		if (null != elements) {
			elements.clear();
		}
	}

	/*a highly customized method for getting the value from the backing long-integer map for the specified key.*/
	/*by default this method could return with 0 value (as internal ID) for a given key.*/
	/*if does not contain the key, returns with default map long value; 0, we make a second attempt with contains*/ 
	/*in this case we do not throw no such mapping exception which is the ~20% of the whole taxonomy building process*/
	private int _internalGet(final long key) {
		
		final int internalId = keyMap.get(key);

		//value found for the given key
		if (0 != internalId) {
			
			return internalId;
			
		}
		
		//0 could mean; item is missing, or really the first one
		return keyMap.containsKey(key) ? internalId : -1;
		
	} 
	
}