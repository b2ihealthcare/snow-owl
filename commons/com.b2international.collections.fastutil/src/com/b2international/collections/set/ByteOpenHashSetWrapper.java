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

import com.b2international.collections.AbstractByteCollection;
import com.b2international.collections.ByteCollection;
import com.b2international.collections.ByteIterator;
import com.b2international.collections.ByteIteratorWrapper;
import com.b2international.collections.set.ByteSet;

import it.unimi.dsi.fastutil.bytes.ByteOpenHashSet;

/**
 * @since 4.7
 */
public final class ByteOpenHashSetWrapper extends AbstractByteCollection implements ByteSet {

	private final it.unimi.dsi.fastutil.bytes.ByteSet delegate;

	public static ByteSet create(ByteCollection source) {
		if (source instanceof ByteOpenHashSetWrapper) {
			final it.unimi.dsi.fastutil.bytes.ByteSet sourceDelegate = ((ByteOpenHashSetWrapper) source).delegate;
			return wrap(clone(sourceDelegate));
		} else {
			final ByteSet result = create(source.size());
			result.addAll(source);
			return result;
		}
	}
	
	public static ByteSet create(int expectedSize) {
		return wrap(new it.unimi.dsi.fastutil.bytes.ByteOpenHashSet(expectedSize));
	}
	
	public static ByteSet wrap(it.unimi.dsi.fastutil.bytes.ByteSet keySet) {
		return new ByteOpenHashSetWrapper(keySet);
	}
	
	private ByteOpenHashSetWrapper(it.unimi.dsi.fastutil.bytes.ByteSet delegate) {
		this.delegate = delegate;
	}

	@Override
	public void clear() {
		delegate.clear();
	}

	@Override
	public boolean isEmpty() {
		return delegate.isEmpty();
	}

	@Override
	public int size() {
		return delegate.size();
	}

	@Override
	public void trimToSize() {
		trim(delegate);
	}

	@Override
	public boolean add(byte value) {
		return delegate.add(value);
	}

	@Override
	public boolean addAll(ByteCollection collection) {
		if (collection instanceof ByteOpenHashSetWrapper) {
			return delegate.addAll(((ByteOpenHashSetWrapper) collection).delegate);
		} else {
			return super.addAll(collection);
		}
	}

	@Override
	public boolean contains(byte value) {
		return delegate.contains(value);
	}

	@Override
	public boolean containsAll(ByteCollection collection) {
		if (collection instanceof ByteOpenHashSetWrapper) {
			return delegate.containsAll(((ByteOpenHashSetWrapper) collection).delegate);
		} else {
			return super.containsAll(collection);
		}
	}

	@Override
	public ByteIterator iterator() {
		return new ByteIteratorWrapper<>(delegate.iterator());
	}

	@Override
	public boolean remove(byte value) {
		return delegate.rem(value);
	}

	@Override
	public boolean removeAll(ByteCollection collection) {
		if (collection instanceof ByteOpenHashSetWrapper) {
			return delegate.removeAll(((ByteOpenHashSetWrapper) collection).delegate);
		} else {
			return super.removeAll(collection);
		}
	}

	@Override
	public boolean retainAll(ByteCollection collection) {
		if (collection instanceof ByteOpenHashSetWrapper) {
			return delegate.retainAll(((ByteOpenHashSetWrapper) collection).delegate);
		} else {
			return super.retainAll(collection);
		}
	}

	@Override
	public byte[] toArray() {
		return delegate.toArray(new byte[delegate.size()]);
	}

	@Override
	public ByteSet dup() {
		return create(this);
	}
	
	// FastUtil helpers
	
	public static it.unimi.dsi.fastutil.bytes.ByteSet clone(it.unimi.dsi.fastutil.bytes.ByteSet set) {
		if (set instanceof ByteOpenHashSet) {
			return ((ByteOpenHashSet) set).clone();
		} else {
			throw new UnsupportedOperationException("Unsupported set implementation: " + set.getClass().getSimpleName());
		}
	}
	
	public static void trim(it.unimi.dsi.fastutil.bytes.ByteSet set) {
		if (set instanceof ByteOpenHashSet) {
			((ByteOpenHashSet) set).trim();
		} else {
			throw new UnsupportedOperationException("Unsupported set implementation: " + set.getClass().getSimpleName());
		}
	}

}
