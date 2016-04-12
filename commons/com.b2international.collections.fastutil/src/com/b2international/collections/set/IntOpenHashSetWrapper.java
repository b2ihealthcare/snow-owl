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

import com.b2international.collections.AbstractIntCollection;
import com.b2international.collections.IntCollection;
import com.b2international.collections.IntIterator;
import com.b2international.collections.set.IntSet;
import com.b2international.commons.fastutil.IntIteratorWrapper;

public class IntOpenHashSetWrapper extends AbstractIntCollection implements IntSet {

	private final it.unimi.dsi.fastutil.ints.IntOpenHashSet delegate;

	public static IntSet create(IntCollection source) {
		if (source instanceof IntOpenHashSetWrapper) {
			return new IntOpenHashSetWrapper(((IntOpenHashSetWrapper) source).delegate.clone());
		} else {
			IntOpenHashSetWrapper result = new IntOpenHashSetWrapper(new it.unimi.dsi.fastutil.ints.IntOpenHashSet(source.size()));
			result.addAll(source);
			return result;
		}
	}
	
	public static IntSet create(int expectedSize) {
		return new IntOpenHashSetWrapper(new it.unimi.dsi.fastutil.ints.IntOpenHashSet(expectedSize));
	}
	
	public static IntSet create() {
		return new IntOpenHashSetWrapper(new it.unimi.dsi.fastutil.ints.IntOpenHashSet());
	}

	private IntOpenHashSetWrapper(it.unimi.dsi.fastutil.ints.IntOpenHashSet delegate) {
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
		if (collection instanceof IntOpenHashSetWrapper) {
			return delegate.addAll(((IntOpenHashSetWrapper) collection).delegate);
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
		if (collection instanceof IntOpenHashSetWrapper) {
			return delegate.containsAll(((IntOpenHashSetWrapper) collection).delegate);
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
		if (collection instanceof IntOpenHashSetWrapper) {
			return delegate.removeAll(((IntOpenHashSetWrapper) collection).delegate);
		} else {
			return super.removeAll(collection);
		}
	}

	@Override
	public boolean retainAll(IntCollection collection) {
		if (collection instanceof IntOpenHashSetWrapper) {
			return delegate.retainAll(((IntOpenHashSetWrapper) collection).delegate);
		} else {
			return super.retainAll(collection);
		}
	}

	@Override
	public int[] toArray() {
		return delegate.toArray(new int[delegate.size()]);
	}

	@Override
	public IntSet dup() {
		return new IntOpenHashSetWrapper(new it.unimi.dsi.fastutil.ints.IntOpenHashSet(delegate));
	}
}
