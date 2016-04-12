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
package com.b2international.commons.fastutil.list;

import com.b2international.collections.AbstractByteCollection;
import com.b2international.collections.ByteCollection;
import com.b2international.collections.ByteIterator;
import com.b2international.collections.list.ByteList;
import com.b2international.collections.list.ByteListIterator;
import com.b2international.commons.fastutil.ByteIteratorWrapper;

public class ByteArrayListWrapper extends AbstractByteCollection implements ByteList {

	private final it.unimi.dsi.fastutil.bytes.ByteArrayList delegate;

	public static ByteList create(byte[] source) {
		return new ByteArrayListWrapper(new it.unimi.dsi.fastutil.bytes.ByteArrayList(source));
	}

	public static ByteList create(ByteCollection source) {
		if (source instanceof ByteArrayListWrapper) {
			return new ByteArrayListWrapper(((ByteArrayListWrapper) source).delegate.clone());
		} else {
			ByteArrayListWrapper result = new ByteArrayListWrapper(new it.unimi.dsi.fastutil.bytes.ByteArrayList(source.size()));
			result.addAll(source);
			return result;
		}
	}
	
	public static ByteList create(int expectedSize) {
		return new ByteArrayListWrapper(new it.unimi.dsi.fastutil.bytes.ByteArrayList(expectedSize));
	}

	private ByteArrayListWrapper(it.unimi.dsi.fastutil.bytes.ByteArrayList delegate) {
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
		if (collection instanceof ByteArrayListWrapper) {
			return delegate.addAll(((ByteArrayListWrapper) collection).delegate);
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
		if (collection instanceof ByteArrayListWrapper) {
			return delegate.containsAll(((ByteArrayListWrapper) collection).delegate);
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
		if (collection instanceof ByteArrayListWrapper) {
			return delegate.removeAll(((ByteArrayListWrapper) collection).delegate);
		} else {
			return super.removeAll(collection);
		}
	}

	@Override
	public boolean retainAll(ByteCollection collection) {
		if (collection instanceof ByteArrayListWrapper) {
			return delegate.retainAll(((ByteArrayListWrapper) collection).delegate);
		} else {
			return super.retainAll(collection);
		}
	}

	@Override
	public byte[] toArray() {
		return delegate.toArray(new byte[delegate.size()]);
	}

	@Override
	public ByteList dup() {
		return new ByteArrayListWrapper(new it.unimi.dsi.fastutil.bytes.ByteArrayList(delegate));
	}

	@Override
	public byte get(int index) {
		return delegate.getByte(index);
	}

	@Override
	public ByteListIterator listIterator() {
		return new ByteListIteratorWrapper(delegate.listIterator());
	}

	@Override
	public ByteListIterator listIterator(int startIndex) {
		return new ByteListIteratorWrapper(delegate.listIterator(startIndex));
	}

	@Override
	public byte set(int index, byte value) {
		return delegate.set(index, value);
	}
}
