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
package com.b2international.commons.collections;

import java.util.Set;

/**
 * A set that preserves insertion order.  
 * 
 * @since 8.0.1
 */
public interface OrderedSet<E> extends Set<E> {

	/**
	 * Adds the specified element to this set if it is not already present.
	 * <p>
	 * More formally, adds the specified element {@code e} to this set if the set
	 * contains no element {@code e2} such that {@code Objects.equals(e, e2)}. If
	 * this set already contains the element, the call leaves the set unchanged and
	 * returns the existing insertion order index of the element.
	 *
	 * @param e element to be added to this set
	 * @return the index of the specified element in insertion order
	 * @throws NullPointerException if the specified element is null
	 */
	int indexedAdd(E e);
	
	/**
	 * Returns the index of the first occurrence of the specified element in this
	 * ordered set, or -1 if this ordered set does not contain the element.
	 *
	 * @param o element to search for
	 * @return the index of the first occurrence of the specified element in this
	 *         set, or -1 if this set does not contain the element
     * @throws ClassCastException if the type of the specified element is incompatible 
     *         with this list
	 * @throws NullPointerException if the specified element is null
	 */
    int indexOf(Object o);
    
	/**
	 * Returns the element at the specified insertion order index in this ordered
	 * set. Note that insertion indexes are stable and so do not change when
	 * elements are removed from the set.
	 *
	 * @param index index of the element to return
	 * @return the element at the specified position in this set
	 * @throws IndexOutOfBoundsException if the index is out of range
	 * @throws IllegalArgumentException  if the index is within range, but the
	 *         corresponding element has been removed
	 */
    E get(int index);
    
    /**
     * Compacts, then trims internal data structures used in this set. 
     */
    void trimToSize();

	/**
	 * Compacts the ordered set so that insertion order indexes start from 0 and
	 * become sequential.
	 */
	void compact();
}
