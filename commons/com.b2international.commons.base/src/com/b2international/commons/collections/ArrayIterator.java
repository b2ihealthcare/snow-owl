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

import java.util.Iterator;

import com.google.common.base.Preconditions;

/**
 * Iterator working on an array.
 * <br>Remove is no supported on this iterator implementation.
 */
public class ArrayIterator<E> implements Iterator<E> {

	private final E[] array;
	private final int length;
	private int index;

	public ArrayIterator(final E[] array) {
		this.array = Preconditions.checkNotNull(array, "Arrays argument cannot be null.");
		length = array.length;
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return index < length;
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	@Override
	public E next() {
		return array[index++];
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
}