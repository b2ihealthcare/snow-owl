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
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;

/**
 * A sorted collection that contains no duplicate elements.
 */
public class SortedArraySet<E> implements Set<E> {

	private static final Comparator<Object> DEFAULT_COMPARATOR = new Comparator<Object>() {
		@Override public int compare(final Object o1, final Object o2) {
			return Ints.compare(o1.hashCode(), o2.hashCode());
		};
	};
	
	private E[] elements;
	private Comparator<? super E> comparator;
	
	public SortedArraySet(final E... elements) {
		this(DEFAULT_COMPARATOR, elements);
	}

	public SortedArraySet(final Set<? extends E> set) {
		this(DEFAULT_COMPARATOR, set);
	}
	
	public SortedArraySet(final Comparator<? super E> comparator, final E... elements) {
		this(comparator, Sets.newHashSet(elements));
	}

	public SortedArraySet(final Comparator<? super E> comparator, final Set<? extends E> set) {
		Preconditions.checkNotNull(comparator, "Comparator argument cannot be null.");
		Preconditions.checkNotNull(set, "Set argument cannot be null.");
		this.comparator = comparator;
		elements = (E[]) new Object[set.size()];
		if (elements.length > 0) {
			int i = 0;
			for (final E element : set) {
				elements[i] = element; 
				i++;
			}
		}
		Arrays.sort(elements, this.comparator);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Set#size()
	 */
	@Override
	public int size() {
		return elements.length;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.Set#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return 0 == elements.length;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.Set#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(final Object o) {
		return Arrays.binarySearch(elements, o) > -1;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.Set#iterator()
	 */
	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {
		    
			private int index = 0;
		    private boolean lastRemoved = false;

		    @Override public boolean hasNext() {
				return (index < elements.length);
			}
			@Override public E next() {
		        if (index >= elements.length)
		        	throw new NoSuchElementException("Array index: " + index);
		        final E object = elements[index];
		        index++;
		        lastRemoved = false;
		        return object;
			}
			@Override public void remove() {
		        if (0 == index || lastRemoved) 
		        	throw new IllegalStateException();
		        SortedArraySet.this.remove(elements[index - 1]);
		        index--;
		        lastRemoved = true;
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.Set#toArray()
	 */
	@Override
	public Object[] toArray() {
		return elements;
	}

	/**
	 * <b>Warning:</b> throws UnsupportedOperationException.<br><br>
	 * {@inheritDoc}
	 */
	/*
	 * (non-Javadoc)
	 * @see java.util.Set#toArray(T[])
	 */
	@Override
	public <T> T[] toArray(final T[] a) {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.Set#add(java.lang.Object)
	 */
	@Override
	public boolean add(final E e) {
		final int newPosition = Arrays.binarySearch(elements, e);
		if (newPosition > -1) { //object already exists just replace the value
			elements[newPosition] = e;
			return true;
		}
		
		final int toPosition = -(newPosition + 1);
		final E[] startSection = Arrays.copyOf(elements, toPosition);
		final E[] endSection = Arrays.copyOfRange(elements, toPosition, elements.length);
		for (int i = 0; i < elements.length; i++) {
			elements[i] = null;
		}
		
		elements = (E[]) new Object[elements.length + 1];
		System.arraycopy(startSection, 0, elements, 0, toPosition);
		System.arraycopy(endSection, 0, elements, toPosition + 1 , endSection.length);
		elements[toPosition] = e;
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Set#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(final Object o) {
		final int fromRemove = Arrays.binarySearch(elements, o);
		if (fromRemove > -1) {
			final E[] startSection = Arrays.copyOf(elements, fromRemove);
			final E[] endSection = Arrays.copyOfRange(elements, fromRemove + 1, elements.length);
			for (int i = 0; i < elements.length; i++) {
				elements[i] = null;
			}
			elements = (E[]) new Object[elements.length - 1];
			System.arraycopy(startSection, 0, elements, 0, startSection.length);
			System.arraycopy(endSection, 0, elements, startSection.length, endSection.length);
			return true;
		}
		return false; //not an existing object
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.Set#containsAll(java.util.Collection)
	 */
	@Override
	public boolean containsAll(final Collection<?> c) {
		boolean contains = true;
		for (final Object o : c)
			contains &= contains(o);
		return contains;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.Set#addAll(java.util.Collection)
	 */
	@Override
	public boolean addAll(final Collection<? extends E> c) {
		boolean isNew = true;
		for (final E o : c)
			isNew &= add(o); 
		return isNew;
	}

	/**
	 * <b>Warning:</b> throws UnsupportedOperationException.<br><br>
	 * {@inheritDoc}
	 */
	/*
	 * (non-Javadoc)
	 * @see java.util.Set#retainAll(java.util.Collection)
	 */
	@Override
	public boolean retainAll(final Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.Set#removeAll(java.util.Collection)
	 */
	@Override
	public boolean removeAll(final Collection<?> c) {
		boolean contained = true;
		for (final Object o : c)
			contained &= remove(o);
		return contained;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.Set#clear()
	 */
	@Override
	public void clear() {
		for (int i = 0; i < elements.length; i++)
			elements[i] = null;
		elements = (E[]) new Object[] {};
	}
	
	@Override
	public String toString() {
		return Arrays.toString(elements);
	}
	
}