/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.collections.longs;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.NoSuchElementException;

/**
 * Utility methods related to primitive long collections.
 */
public abstract class LongCollections {

	private static class EmptyLongSet extends AbstractLongSet implements Serializable {
	
		private static final EmptyLongSet INSTANCE = new EmptyLongSet();
		
		@Override
		public LongIterator iterator() {
			return EmptyLongIterator.INSTANCE;
		}
		
		@Override
		public boolean add(long value) {
			throw new UnsupportedOperationException("Can't add value " + value + " to an EmptyLongSet.");
		}
	}
	
	private static class EmptyLongList extends AbstractLongList implements Serializable {
		
		private static final EmptyLongList INSTANCE = new EmptyLongList();
		
		@Override
		public LongListIterator listIterator() {
			return EmptyLongListIterator.INSTANCE;
		}
		
		@Override
		public boolean add(long value) {
			throw new UnsupportedOperationException("Can't add value " + value + " to an EmptyLongList.");
		}
	}

	private static class EmptyLongIterator implements LongIterator, Serializable {

		private static final EmptyLongIterator INSTANCE = new EmptyLongIterator();
		
		@Override
		public boolean hasNext() {
			return false;
		}

		@Override
		public long next() {
			throw new NoSuchElementException();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	private static class EmptyLongListIterator implements LongListIterator, Serializable {
		
		private static final EmptyLongListIterator INSTANCE = new EmptyLongListIterator();
		
		@Override
		public boolean hasNext() {
			return false;
		}
		
		@Override
		public long next() {
			throw new NoSuchElementException();
		}
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean hasPrevious() {
			return false;
		}
		
		@Override
		public long previous() {
			throw new NoSuchElementException();
		}
		
		@Override
		public int nextIndex() {
			return 0;
		}
		
		@Override
		public int previousIndex() {
			return -1;
		}
		
		@Override
		public void add(long value) {
			throw new UnsupportedOperationException();			
		}
		
		@Override
		public void set(long value) {
			throw new UnsupportedOperationException();			
		}
	}
	
	private static class SingletonLongSet extends AbstractLongSet implements Serializable {
	
		private final long value;
	
		private SingletonLongSet(long value) {
			this.value = value;
		}
	
		@Override
		public LongIterator iterator() {
			return singletonIterator(value);
		}
	
		@Override
		public boolean add(long value) {
			throw new UnsupportedOperationException("Can't add value " + value + " to a SingletonLongSet.");
		}
	}

	private static class SingletonLongIterator implements LongIterator, Serializable {

		private final long value;
		private boolean visited;

		public SingletonLongIterator(long value) {
			this.value = value;
		}

		@Override
		public boolean hasNext() {
			return !visited;
		}

		@Override
		public long next() {
			if (hasNext()) {
				visited = true;
				return value;
			} else {
				throw new NoSuchElementException();
			}
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	private static abstract class UnmodifiableLongCollection implements LongCollection {

		protected final LongCollection delegate;

		UnmodifiableLongCollection(LongCollection collection) {
			this.delegate = checkNotNull(collection, "collection");
		}

		@Override
		public final void clear() {
			throw new UnsupportedOperationException();
		}

		@Override
		public final boolean isEmpty() {
			return delegate.isEmpty();
		}

		@Override
		public final int size() {
			return delegate.size();
		}

		@Override
		public final void trimToSize() {
			throw new UnsupportedOperationException();
		}

		@Override
		public final boolean add(long value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public final boolean addAll(LongCollection collection) {
			throw new UnsupportedOperationException();
		}

		@Override
		public final boolean contains(long value) {
			return delegate.contains(value);
		}

		@Override
		public final boolean containsAll(LongCollection collection) {
			return delegate.containsAll(collection);
		}

		@Override
		public final LongIterator iterator() {
			return unmodifiableLongIterator(delegate.iterator());
		}

		@Override
		public final boolean remove(long value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public final boolean removeAll(LongCollection collection) {
			throw new UnsupportedOperationException();
		}

		@Override
		public final boolean retainAll(LongCollection collection) {
			throw new UnsupportedOperationException();
		}

		@Override
		public final long[] toArray() {
			return delegate.toArray();
		}
	}

	private static final class UnmodifiableLongSet extends UnmodifiableLongCollection implements LongSet {

		UnmodifiableLongSet(LongCollection collection) {
			super(collection);
		}
		
		@Override
		public boolean equals(Object obj) {
			return AbstractLongSet.equals(this, obj);
		}
		
		@Override
		public int hashCode() {
	        return AbstractLongSet.hashCode(this);
		}
	}
	
	private static class UnmodifiableLongIterator implements LongIterator {
		
		private final LongIterator iterator;

		public UnmodifiableLongIterator(LongIterator iterator) {
			this.iterator = checkNotNull(iterator, "iterator");
		}
		
		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}
		
		@Override
		public long next() {
			return iterator.next();
		}
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	/**
	 * @return an unmodifiable, empty {@link LongSet}
	 */
	public static final LongSet emptySet() {
		return EmptyLongSet.INSTANCE;
	}

	/**
	 * @return an unmodifiable, empty {@link LongList}
	 */
	public static LongList emptyList() {
		return EmptyLongList.INSTANCE;
	}

	/**
	 * @return an unmodifiable, empty {@link LongIterator}
	 */
	public static final LongIterator emptyIterator() {
		return EmptyLongIterator.INSTANCE;
	}

	public static final LongSet singletonSet(final long value) {
		return new SingletonLongSet(value);
	}
	
	public static final LongIterator singletonIterator(final long value) {
		return new SingletonLongIterator(value);
	}
	
	public static final LongSet unmodifiableSet(LongSet source) {
		if (source instanceof UnmodifiableLongSet) {
			return source;
		} else {
			return new UnmodifiableLongSet(source);
		}
	}
	
	public static LongIterator unmodifiableLongIterator(LongIterator iterator) {
		if (iterator instanceof UnmodifiableLongIterator) {
			return iterator;
		} else {
			return new UnmodifiableLongIterator(iterator);
		}
	}
	
	private LongCollections() {
		throw new UnsupportedOperationException(LongCollections.class.getSimpleName() + " is not supposed to be instantiated.");
	}
}
