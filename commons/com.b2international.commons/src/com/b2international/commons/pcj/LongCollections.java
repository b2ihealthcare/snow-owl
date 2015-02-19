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
package com.b2international.commons.pcj;

import java.io.Serializable;
import java.util.NoSuchElementException;

import bak.pcj.LongCollection;
import bak.pcj.LongIterator;
import bak.pcj.set.AbstractLongSet;
import bak.pcj.set.LongSet;

/**
 * Utility methods related to pcj collections.
 * 
 */
public abstract class LongCollections {

	protected static final long[] EMPTY_ARRAY = new long[0];

	private static final LongIterator EMPTY_ITERATOR = new EmptyLongIterator();
	
	private static class EmptyLongIterator implements LongIterator, Serializable {

		private static final long serialVersionUID = 1L;

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
	
	private static final LongSet EMPTY_SET = new EmptyLongSet();
	
	/**Returns with a immutable set containing the one and only primitive long argument.*/
	public static final LongSet singletonSet(final long v) {
		return new AbstractLongSet() {
			private final long value = v;
			@Override
			public LongIterator iterator() {
				return getSingletonIterator(value);
			}
			@Override
			public int size() {
				return 1;
			}
			@Override
			public boolean contains(long v) {
				return value == v;
			}
		};
	}
	
	/**Returns with a single element unmodifiable iterator.*/
	public static final LongIterator getSingletonIterator(final long v) {
		return new AbstractLongIterator() {
			private boolean hasNext = true;
			private final long value = v;
			@Override
			protected long computeNext() {
				if (hasNext) {
					hasNext = false;
					return value;
				}
				return endOfData();
			}
		};
	}
	
	private static class EmptyLongSet implements LongSet, Serializable {

		private static final long serialVersionUID = 1L;

		@Override
		public boolean add(final long v) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(final LongCollection c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(final long v) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(final LongCollection c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(final LongCollection c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean contains(final long v) {
			return false;
		}

		@Override
		public boolean containsAll(final LongCollection c) {
			return c.isEmpty();
		}

		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		public LongIterator iterator() {
			return EMPTY_ITERATOR;
		}

		@Override
		public int size() {
			return 0;
		}

		@Override
		public long[] toArray() {
			return EMPTY_ARRAY;
		}

		@Override
		public long[] toArray(final long[] array) {
			return array;
		}

		@Override
		public void trimToSize() {
			// Nothing to do
		}
	}
	
	/**
	 * @return an unmodifiable, empty {@link LongSet}
	 */
	public static final LongSet emptySet() {
		return EMPTY_SET;
	}
	
	/**
	 * @return an unmodifiable, empty {@link LongIterator}
	 */
	public static final LongIterator emptyIterator() {
		return EMPTY_ITERATOR;
	}
	
	private LongCollections() {
		// Prevent instantiation
	}
}