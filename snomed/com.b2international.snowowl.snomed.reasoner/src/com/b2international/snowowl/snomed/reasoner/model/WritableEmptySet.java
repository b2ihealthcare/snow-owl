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
package com.b2international.snowowl.snomed.reasoner.model;

import static com.google.common.collect.Sets.newHashSet;

import java.util.*;

import com.google.common.collect.ImmutableSet;

/**
 * A {@link Set} implementation which is backed by a shared empty set until a write operation is invoked; this replaces the empty, read-only delegate
 * with a writable {@link HashSet}.
 * @param <E> the element type
 */
public final class WritableEmptySet<E> extends AbstractSet<E> implements Set<E> {

	/**
	 * Creates a new {@link WritableEmptySet} instance with the appropriate type parameter.
	 * @return the created instance
	 */
	public static <E> WritableEmptySet<E> create() {
		return new WritableEmptySet<E>();
	}

	private static final Set<?> EMPTY_IMPL = ImmutableSet.of();

	@SuppressWarnings("unchecked")
	private Set<E> delegate = (Set<E>) EMPTY_IMPL;

	/*
	 * (non-Javadoc)
	 * @see java.util.AbstractCollection#size()
	 */
	@Override public int size() {
		return delegate.size();
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.AbstractCollection#isEmpty()
	 */
	@Override public boolean isEmpty() {
		return delegate.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.AbstractCollection#contains(java.lang.Object)
	 */
	@Override public boolean contains(final Object o) {
		return delegate.contains(o);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.AbstractCollection#iterator()
	 */
	@Override public Iterator<E> iterator() {
		return delegate.iterator();
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.AbstractCollection#toArray()
	 */
	@Override public Object[] toArray() {
		return delegate.toArray();
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.AbstractCollection#toArray(T[])
	 */
	@Override public <T> T[] toArray(final T[] a) {
		return delegate.toArray(a);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.AbstractCollection#add(java.lang.Object)
	 */
	@Override public boolean add(final E e) {
		return initDelegate().add(e);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.AbstractCollection#remove(java.lang.Object)
	 */
	@Override public boolean remove(final Object o) {
		return initDelegate().remove(o);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.AbstractCollection#containsAll(java.util.Collection)
	 */
	@Override public boolean containsAll(final Collection<?> c) {
		return delegate.containsAll(c);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.AbstractCollection#addAll(java.util.Collection)
	 */
	@Override public boolean addAll(final Collection<? extends E> c) {
		return initDelegate().addAll(c);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.AbstractCollection#retainAll(java.util.Collection)
	 */
	@Override public boolean retainAll(final Collection<?> c) {
		return initDelegate().retainAll(c);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.AbstractSet#removeAll(java.util.Collection)
	 */
	@Override public boolean removeAll(final Collection<?> c) {
		return initDelegate().removeAll(c);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.AbstractCollection#clear()
	 */
	@Override public void clear() {
		initDelegate().clear();
	}

	private Set<E> initDelegate() {
		if (EMPTY_IMPL == delegate) {
			delegate = newHashSet();
		}

		return delegate;
	}
}