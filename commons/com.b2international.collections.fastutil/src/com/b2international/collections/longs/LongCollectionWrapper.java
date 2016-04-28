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

/**
 * @since 4.7
 */
public class LongCollectionWrapper extends AbstractLongCollection implements LongCollection {

	private final it.unimi.dsi.fastutil.longs.LongCollection delegate;

	protected LongCollectionWrapper(it.unimi.dsi.fastutil.longs.LongCollection delegate) {
		this.delegate = delegate;
	}
	
	protected it.unimi.dsi.fastutil.longs.LongCollection delegate() {
		return delegate;
	}
	
	@Override
	public final LongIterator iterator() {
		return new LongIteratorWrapper<>(delegate.iterator());
	}
	
	@Override
	public final void clear() {
		delegate.clear();
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
	public final boolean add(long value) {
		return delegate.add(value);
	}

	@Override
	public final boolean addAll(LongCollection collection) {
		if (collection instanceof LongCollectionWrapper) {
			return delegate.addAll(((LongCollectionWrapper) collection).delegate);
		} else {
			return super.addAll(collection);
		}
	}

	@Override
	public final boolean contains(long value) {
		return delegate.contains(value);
	}
	
	@Override
	public final boolean containsAll(LongCollection collection) {
		if (collection instanceof LongCollectionWrapper) {
			return delegate.containsAll(((LongCollectionWrapper) collection).delegate);
		} else {
			return super.containsAll(collection);
		}
	}

	@Override
	public final boolean remove(long value) {
		return delegate.rem(value);
	}

	@Override
	public final boolean removeAll(LongCollection collection) {
		if (collection instanceof LongCollectionWrapper) {
			return delegate.removeAll(((LongCollectionWrapper) collection).delegate);
		} else {
			return super.removeAll(collection);
		}
	}

	@Override
	public final boolean retainAll(LongCollection collection) {
		if (collection instanceof LongCollectionWrapper) {
			return delegate.retainAll(((LongCollectionWrapper) collection).delegate);
		} else {
			return super.retainAll(collection);
		}
	}

	@Override
	public final long[] toArray() {
		return delegate.toLongArray();
	}
	
	@Override
	public void trimToSize() {
		throw new UnsupportedOperationException("Unsupported collection implementation: " + delegate.getClass().getSimpleName());
	}
	
	public static LongCollection wrap(it.unimi.dsi.fastutil.longs.LongCollection collection) {
		return new LongCollectionWrapper(collection);
	}

}
