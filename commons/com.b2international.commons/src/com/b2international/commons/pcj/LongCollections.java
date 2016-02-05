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

import com.b2international.commons.collections.primitive.LongIterator;
import com.b2international.commons.collections.primitive.set.AbstractLongSet;
import com.b2international.commons.collections.primitive.set.LongSet;

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
		public EmptyLongSet dup() {
			return this;
		}
	
		@Override
		public boolean add(long value) {
			throw new UnsupportedOperationException("Can't add value " + value + " to a SingletonLongSet.");
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
		public LongSet dup() {
			return this;
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
	
	/**
	 * @return an unmodifiable, empty {@link LongSet}
	 */
	public static final LongSet emptySet() {
		return EmptyLongSet.INSTANCE;
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
	
	private LongCollections() {
		throw new UnsupportedOperationException(LongCollections.class.getSimpleName() + " is not supposed to be instantiated.");
	}
}
