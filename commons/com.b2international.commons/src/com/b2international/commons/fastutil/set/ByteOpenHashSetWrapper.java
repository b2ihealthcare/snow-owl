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
package com.b2international.commons.fastutil.set;

import com.b2international.commons.collections.primitive.AbstractByteCollection;
import com.b2international.commons.collections.primitive.ByteCollection;
import com.b2international.commons.collections.primitive.ByteIterator;
import com.b2international.commons.collections.primitive.set.ByteSet;
import com.b2international.commons.fastutil.ByteIteratorWrapper;

public class ByteOpenHashSetWrapper extends AbstractByteCollection implements ByteSet {

	private final it.unimi.dsi.fastutil.bytes.ByteOpenHashSet delegate;

	public static ByteSet create(ByteCollection source) {
		if (source instanceof ByteOpenHashSetWrapper) {
			return new ByteOpenHashSetWrapper(((ByteOpenHashSetWrapper) source).delegate.clone());
		} else {
			ByteOpenHashSetWrapper result = new ByteOpenHashSetWrapper(new it.unimi.dsi.fastutil.bytes.ByteOpenHashSet(source.size()));
			result.addAll(source);
			return result;
		}
	}
	
	public static ByteSet create(int expectedSize) {
		return new ByteOpenHashSetWrapper(new it.unimi.dsi.fastutil.bytes.ByteOpenHashSet(expectedSize));
	}

	private ByteOpenHashSetWrapper(it.unimi.dsi.fastutil.bytes.ByteOpenHashSet delegate) {
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
		delegate.trim();
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
		return new ByteOpenHashSetWrapper(new it.unimi.dsi.fastutil.bytes.ByteOpenHashSet(delegate));
	}
}
