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
import java.util.NoSuchElementException;

/**
 * Empty iterator.
 *
 */
public final class EmptyIterator implements Iterator<Object> {

	/**Shared instance.*/
	private static final EmptyIterator INSTANCE = new EmptyIterator();
	
	/**Returns with the empty iterator instance.*/
	@SuppressWarnings("unchecked")
	public static <E> Iterator<E> getInstance() {
		return (Iterator<E>) INSTANCE;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return false;
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	@Override
	public Object next() {
		throw new NoSuchElementException();
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	private EmptyIterator() { /*no*/ }
	
	
}