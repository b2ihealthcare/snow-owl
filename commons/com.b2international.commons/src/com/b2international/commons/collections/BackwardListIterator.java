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

import java.util.List;
import java.util.ListIterator;

/**
 * Iterator providing backward traverse on a {@link List list} from the last element.
 */
public class BackwardListIterator<E> implements ListIterator<E> {

	private final ListIterator<E> delegate;
	
	@SuppressWarnings("unchecked")
	public BackwardListIterator(final List<? extends E> commits) {
		
		if (null == commits) {
			
			throw new NullPointerException("List argument cannot be null.");
			
		}
		
		delegate = (ListIterator<E>) commits.listIterator(commits.size());		
	}

	/* (non-Javadoc)
	 * @see java.util.ListIterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return delegate.hasPrevious();
	}

	/* (non-Javadoc)
	 * @see java.util.ListIterator#next()
	 */
	@Override
	public E next() {
		return delegate.previous();
	}

	/* (non-Javadoc)
	 * @see java.util.ListIterator#hasPrevious()
	 */
	@Override
	public boolean hasPrevious() {
		return delegate.hasNext();
	}

	/* (non-Javadoc)
	 * @see java.util.ListIterator#previous()
	 */
	@Override
	public E previous() {
		return delegate.next();
	}

	/* (non-Javadoc)
	 * @see java.util.ListIterator#nextIndex()
	 */
	@Override
	public int nextIndex() {
		return delegate.previousIndex();
	}

	/* (non-Javadoc)
	 * @see java.util.ListIterator#previousIndex()
	 */
	@Override
	public int previousIndex() {
		return delegate.nextIndex();
	}

	/* (non-Javadoc)
	 * @see java.util.ListIterator#remove()
	 */
	@Override
	public void remove() {
		delegate.remove();
	}

	/* (non-Javadoc)
	 * @see java.util.ListIterator#set(java.lang.Object)
	 */
	@Override
	public void set(final E e) {
		delegate.set(e);
	}

	/* (non-Javadoc)
	 * @see java.util.ListIterator#add(java.lang.Object)
	 */
	@Override
	public void add(final E e) {
		delegate.add(e);
	}
	
}