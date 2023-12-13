/*
 * Copyright 2022 B2i Healthcare, https://b2ihealthcare.com
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

import com.b2international.collections.longs.LongSet;

/**
 * A set storing primitive long values that preserves insertion order.  
 * 
 * @since 8.0.1
 */
public interface LongOrderedSet extends LongSet {

	/**
	 * Adds the specified element to this set if it is not already present.
	 * <p>
	 * If this set already contains the element, the call leaves the set unchanged and
	 * returns the existing insertion order index of the element.
	 *
	 * @param l element to be added to this set
	 * @return the index of the specified element in insertion order
	 */
	int indexedAdd(long l);
	
	/**
	 * Returns the index of the first occurrence of the specified element in this
	 * ordered set, or -1 if this ordered set does not contain the element.
	 *
	 * @param l element to search for
	 * @return the index of the first occurrence of the specified element in this
	 *         set, or -1 if this set does not contain the element
	 */
    int indexOf(long l);
    
	/**
	 * Returns the element at the specified insertion order index in this ordered
	 * set. Note that insertion indexes are stable and so do not change when
	 * elements are removed from the set.
	 *
	 * @param index index of the element to return
	 * @return the element at the specified position in this set
	 * @throws IndexOutOfBoundsException if the index is out of range or points to
	 *         an unoccupied slot
	 */
    long get(int index);

	/**
	 * Compacts the ordered set so that insertion order indexes start from 0 and
	 * become sequential.
	 */
	void compact();
}
