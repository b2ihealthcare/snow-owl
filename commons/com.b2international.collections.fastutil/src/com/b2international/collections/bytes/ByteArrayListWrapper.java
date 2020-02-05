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

import it.unimi.dsi.fastutil.bytes.ByteArrayList;

/**
 * @since 4.7
 */
public final class ByteArrayListWrapper extends ByteCollectionWrapper implements ByteList {

	private ByteArrayListWrapper(it.unimi.dsi.fastutil.bytes.ByteList delegate) {
		super(delegate);
	}

	@Override
	public int hashCode() {
		return AbstractByteCollection.hashCode(this);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (!(obj instanceof ByteList)) { return false; }
		
		ByteList other = (ByteList) obj;
		if (size() != other.size()) { return false; }
		return AbstractByteCollection.elementsEqual(iterator(), other.iterator());
	}
	
	@Override
	protected it.unimi.dsi.fastutil.bytes.ByteList delegate() {
		return (it.unimi.dsi.fastutil.bytes.ByteList) super.delegate();
	}
	
	@Override
	public void trimToSize() {
		final it.unimi.dsi.fastutil.bytes.ByteList list = delegate();
		if (list instanceof ByteArrayList) {
			((ByteArrayList) list).trim();
		} else {
			super.trimToSize();
		}
	}

	@Override
	public byte get(int index) {
		return delegate().getByte(index);
	}

	@Override
	public ByteListIterator listIterator() {
		return new ByteListIteratorWrapper(delegate().listIterator());
	}

	@Override
	public ByteListIterator listIterator(int startIndex) {
		return new ByteListIteratorWrapper(delegate().listIterator(startIndex));
	}

	@Override
	public byte set(int index, byte value) {
		return delegate().set(index, value);
	}
	
	@Override
	public byte removeByte(int index) {
		return delegate().removeByte(index);
	}
	
	// Builder methods
	
	public static ByteList create(ByteCollection collection) {
		if (collection instanceof ByteArrayListWrapper) {
			final it.unimi.dsi.fastutil.bytes.ByteList sourceDelegate = ((ByteArrayListWrapper) collection).delegate();
			return new ByteArrayListWrapper(clone(sourceDelegate));
		} else {
			final ByteList result = createWithExpectedSize(collection.size());
			result.addAll(collection);
			return result;
		}
	}
	
	public static ByteList create(byte[] source) {
		return new ByteArrayListWrapper(new ByteArrayList(source));
	}
	
	public static ByteList createWithExpectedSize(int expectedSize) {
		return new ByteArrayListWrapper(new ByteArrayList(expectedSize));
	}
	
	public static ByteList create() {
		return new ByteArrayListWrapper(new ByteArrayList());
	}
	
	// FastUtil helpers
	
	private static it.unimi.dsi.fastutil.bytes.ByteList clone(it.unimi.dsi.fastutil.bytes.ByteList list) {
		if (list instanceof ByteArrayList) {
			return ((ByteArrayList) list).clone();
		} else {
			throw new UnsupportedOperationException("Unsupported list implementation: " + list.getClass().getSimpleName());
		}
	}
}
