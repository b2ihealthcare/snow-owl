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
package com.b2international.collections.set;

import static com.google.common.base.Preconditions.checkNotNull;

import com.b2international.collections.LongCollection;
import com.b2international.collections.LongIterator;
import com.b2international.collections.PrimitiveIterators;

/**
 * @since 4.7
 */
public abstract class ImmutableLongCollection implements LongCollection {

	protected final LongCollection delegate;

	ImmutableLongCollection(LongCollection collection) {
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
		return PrimitiveIterators.unmodifiableLongIterator(delegate.iterator());
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

	@Override
	public LongCollection dup() {
		return wrap(delegate.dup());
	}

	/**
	 * Wraps the given collection in an immutable version corresponding the current collection type.
	 * 
	 * @param collection
	 * @return
	 */
	protected abstract LongCollection wrap(LongCollection collection);

}
