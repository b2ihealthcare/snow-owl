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
package com.b2international.collections.bytes;

/**
 * @since 4.7
 */
public class ByteCollectionWrapper extends AbstractByteCollection {

	private final it.unimi.dsi.fastutil.bytes.ByteCollection delegate;

	protected ByteCollectionWrapper(it.unimi.dsi.fastutil.bytes.ByteCollection delegate) {
		this.delegate = delegate;
	}
	
	protected it.unimi.dsi.fastutil.bytes.ByteCollection delegate() {
		return delegate;
	}
	
	@Override
	public final ByteIterator iterator() {
		return new ByteIteratorWrapper<>(delegate.iterator());
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
	public final boolean add(byte value) {
		return delegate.add(value);
	}

	@Override
	public final boolean addAll(ByteCollection collection) {
		if (collection instanceof ByteCollectionWrapper) {
			return delegate.addAll(((ByteCollectionWrapper) collection).delegate);
		} else {
			return super.addAll(collection);
		}
	}

	@Override
	public final boolean contains(byte value) {
		return delegate.contains(value);
	}
	
	@Override
	public final boolean containsAll(ByteCollection collection) {
		if (collection instanceof ByteCollectionWrapper) {
			return delegate.containsAll(((ByteCollectionWrapper) collection).delegate);
		} else {
			return super.containsAll(collection);
		}
	}

	@Override
	public final boolean remove(byte value) {
		return delegate.rem(value);
	}

	@Override
	public final boolean removeAll(ByteCollection collection) {
		if (collection instanceof ByteCollectionWrapper) {
			return delegate.removeAll(((ByteCollectionWrapper) collection).delegate);
		} else {
			return super.removeAll(collection);
		}
	}

	@Override
	public final boolean retainAll(ByteCollection collection) {
		if (collection instanceof ByteCollectionWrapper) {
			return delegate.retainAll(((ByteCollectionWrapper) collection).delegate);
		} else {
			return super.retainAll(collection);
		}
	}

	@Override
	public final byte[] toArray() {
		return delegate.toByteArray();
	}
	
	@Override
	public void trimToSize() {
		throw new UnsupportedOperationException("Unsupported collection implementation: " + delegate.getClass().getSimpleName());
	}
	
	// Builder methods
	
	public static ByteCollection wrap(it.unimi.dsi.fastutil.bytes.ByteCollection collection) {
		return new ByteCollectionWrapper(collection);
	}

}
