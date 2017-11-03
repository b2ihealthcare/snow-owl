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
package com.b2international.collections.ints;

import it.unimi.dsi.fastutil.ints.IntArrayList;

/**
 * @since 4.7
 */
public class IntArrayListWrapper extends IntCollectionWrapper implements IntList {

	protected IntArrayListWrapper(it.unimi.dsi.fastutil.ints.IntList delegate) {
		super(delegate);
	}
	
	@Override
	public int hashCode() {
		return AbstractIntCollection.hashCode(this);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (!(obj instanceof IntList)) { return false; }
		
		IntList other = (IntList) obj;
		if (size() != other.size()) { return false; }
		return AbstractIntCollection.elementsEqual(iterator(), other.iterator());
	}

	@Override
	protected it.unimi.dsi.fastutil.ints.IntList delegate() {
		return (it.unimi.dsi.fastutil.ints.IntList) super.delegate();
	}
	
	@Override
	public void trimToSize() {
		final it.unimi.dsi.fastutil.ints.IntList list = delegate();
		if (list instanceof IntArrayList) {
			((IntArrayList) list).trim();
		} else {
			super.trimToSize();
		}
	}

	@Override
	public int get(int index) {
		return delegate().getInt(index);
	}

	@Override
	public IntListIterator listIterator() {
		return new IntListIteratorWrapper(delegate().listIterator());
	}

	@Override
	public IntListIterator listIterator(int startIndex) {
		return new IntListIteratorWrapper(delegate().listIterator(startIndex));
	}

	@Override
	public int set(int index, int value) {
		return delegate().set(index, value);
	}
	
	@Override
	public int removeInt(int index) {
		return delegate().removeInt(index);
	}
	
	public static IntList create(IntCollection collection) {
		if (collection instanceof IntArrayListWrapper) {
			final it.unimi.dsi.fastutil.ints.IntList sourceDelegate = ((IntArrayListWrapper) collection).delegate();
			return new IntArrayListWrapper(clone(sourceDelegate));
		} else {
			final IntList result = create(collection.size());
			result.addAll(collection);
			return result;
		}
	}
	
	public static IntList create(int expectedSize) {
		return new IntArrayListWrapper(new it.unimi.dsi.fastutil.ints.IntArrayList(expectedSize));
	}
	
	public static IntList create(int[] source) {
		return new IntArrayListWrapper(new it.unimi.dsi.fastutil.ints.IntArrayList(source));
	}

	public static IntList create() {
		return new IntArrayListWrapper(new it.unimi.dsi.fastutil.ints.IntArrayList());
	}
	
	// FastUtil helpers
	
	private static it.unimi.dsi.fastutil.ints.IntList clone(it.unimi.dsi.fastutil.ints.IntList list) {
		if (list instanceof IntArrayList) {
			return ((IntArrayList) list).clone();
		} else {
			throw new UnsupportedOperationException("Unsupported list implementation: " + list.getClass().getSimpleName());
		}
	}
	
}
