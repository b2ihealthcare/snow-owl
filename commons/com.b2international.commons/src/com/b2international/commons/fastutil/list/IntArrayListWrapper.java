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

import com.b2international.commons.collections.primitive.AbstractIntCollection;
import com.b2international.commons.collections.primitive.IntCollection;
import com.b2international.commons.collections.primitive.IntIterator;
import com.b2international.commons.collections.primitive.list.IntList;
import com.b2international.commons.collections.primitive.list.IntListIterator;
import com.b2international.commons.fastutil.IntIteratorWrapper;

public class IntArrayListWrapper extends AbstractIntCollection implements IntList {

	protected final it.unimi.dsi.fastutil.ints.IntArrayList delegate;

	public static IntList create(int[] source) {
		return new IntArrayListWrapper(new it.unimi.dsi.fastutil.ints.IntArrayList(source));
	}

	public static IntList create() {
		return new IntArrayListWrapper(new it.unimi.dsi.fastutil.ints.IntArrayList());
	}

	protected IntArrayListWrapper(it.unimi.dsi.fastutil.ints.IntArrayList delegate) {
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
	public boolean add(int value) {
		return delegate.add(value);
	}

	@Override
	public boolean addAll(IntCollection collection) {
		if (collection instanceof IntArrayListWrapper) {
			return delegate.addAll(((IntArrayListWrapper) collection).delegate);
		} else {
			return super.addAll(collection);
		}
	}

	@Override
	public boolean contains(int value) {
		return delegate.contains(value);
	}

	@Override
	public boolean containsAll(IntCollection collection) {
		if (collection instanceof IntArrayListWrapper) {
			return delegate.containsAll(((IntArrayListWrapper) collection).delegate);
		} else {
			return super.containsAll(collection);
		}
	}

	@Override
	public IntIterator iterator() {
		return new IntIteratorWrapper<>(delegate.iterator());
	}

	@Override
	public boolean remove(int value) {
		return delegate.rem(value);
	}

	@Override
	public boolean removeAll(IntCollection collection) {
		if (collection instanceof IntArrayListWrapper) {
			return delegate.removeAll(((IntArrayListWrapper) collection).delegate);
		} else {
			return super.removeAll(collection);
		}
	}

	@Override
	public boolean retainAll(IntCollection collection) {
		if (collection instanceof IntArrayListWrapper) {
			return delegate.retainAll(((IntArrayListWrapper) collection).delegate);
		} else {
			return super.retainAll(collection);
		}
	}

	@Override
	public int[] toArray() {
		return delegate.toArray(new int[delegate.size()]);
	}

	@Override
	public IntList dup() {
		return new IntArrayListWrapper(new it.unimi.dsi.fastutil.ints.IntArrayList(delegate));
	}

	@Override
	public int get(int index) {
		return delegate.getInt(index);
	}

	@Override
	public IntListIterator listIterator() {
		return new IntListIteratorWrapper(delegate.listIterator());
	}

	@Override
	public IntListIterator listIterator(int startIndex) {
		return new IntListIteratorWrapper(delegate.listIterator(startIndex));
	}

	@Override
	public int set(int index, int value) {
		return delegate.set(index, value);
	}
}
