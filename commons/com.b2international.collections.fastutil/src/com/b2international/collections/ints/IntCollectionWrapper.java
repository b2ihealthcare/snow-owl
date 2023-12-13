/*
 * Copyright 2011-2016 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.collections.ints;

/**
 * @since 4.7
 */
public class IntCollectionWrapper extends AbstractIntCollection implements IntCollection {

	private final it.unimi.dsi.fastutil.ints.IntCollection delegate;

	protected IntCollectionWrapper(it.unimi.dsi.fastutil.ints.IntCollection delegate) {
		this.delegate = delegate;
	}
	
	protected it.unimi.dsi.fastutil.ints.IntCollection delegate() {
		return delegate;
	}
	
	@Override
	public final IntIterator iterator() {
		return new IntIteratorWrapper<>(delegate.iterator());
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
	public final boolean add(int value) {
		return delegate.add(value);
	}

	@Override
	public final boolean addAll(IntCollection collection) {
		if (collection instanceof IntCollectionWrapper) {
			return delegate.addAll(((IntCollectionWrapper) collection).delegate);
		} else {
			return super.addAll(collection);
		}
	}

	@Override
	public final boolean contains(int value) {
		return delegate.contains(value);
	}
	
	@Override
	public final boolean containsAll(IntCollection collection) {
		if (collection instanceof IntCollectionWrapper) {
			return delegate.containsAll(((IntCollectionWrapper) collection).delegate);
		} else {
			return super.containsAll(collection);
		}
	}

	@Override
	public final boolean remove(int value) {
		return delegate.rem(value);
	}

	@Override
	public final boolean removeAll(IntCollection collection) {
		if (collection instanceof IntCollectionWrapper) {
			return delegate.removeAll(((IntCollectionWrapper) collection).delegate);
		} else {
			return super.removeAll(collection);
		}
	}

	@Override
	public final boolean retainAll(IntCollection collection) {
		if (collection instanceof IntCollectionWrapper) {
			return delegate.retainAll(((IntCollectionWrapper) collection).delegate);
		} else {
			return super.retainAll(collection);
		}
	}

	@Override
	public final int[] toArray() {
		return delegate.toIntArray();
	}
	
	@Override
	public void trimToSize() {
		throw new UnsupportedOperationException("Unsupported collection implementation: " + delegate.getClass().getName());
	}

	public static IntCollection wrap(it.unimi.dsi.fastutil.ints.IntCollection collection) {
		return new IntCollectionWrapper(collection);
	}

}
