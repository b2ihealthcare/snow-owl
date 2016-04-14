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
package com.b2international.collections.list;

import com.b2international.collections.AbstractLongCollection;
import com.b2international.collections.LongCollection;
import com.b2international.collections.LongIterator;
import com.b2international.collections.LongIteratorWrapper;
import com.b2international.collections.longs.LongList;
import com.b2international.collections.longs.LongListIterator;

public class LongArrayListWrapper extends AbstractLongCollection implements LongList {

	protected final it.unimi.dsi.fastutil.longs.LongArrayList delegate;

	public static LongList create(long[] source) {
		return new LongArrayListWrapper(new it.unimi.dsi.fastutil.longs.LongArrayList(source));
	}

	public static LongList create(LongCollection source) {
		if (source instanceof LongArrayListWrapper) {
			return new LongArrayListWrapper(((LongArrayListWrapper) source).delegate.clone());
		} else {
			LongArrayListWrapper result = new LongArrayListWrapper(new it.unimi.dsi.fastutil.longs.LongArrayList(source.size()));
			result.addAll(source);
			return result;
		}
	}
	
	public static LongList create(int expectedSize) {
		return new LongArrayListWrapper(new it.unimi.dsi.fastutil.longs.LongArrayList(expectedSize));
	}

	public static LongList create() {
		return new LongArrayListWrapper(new it.unimi.dsi.fastutil.longs.LongArrayList());
	}

	protected LongArrayListWrapper(it.unimi.dsi.fastutil.longs.LongArrayList delegate) {
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
	public boolean add(long value) {
		return delegate.add(value);
	}

	@Override
	public boolean addAll(LongCollection collection) {
		if (collection instanceof LongArrayListWrapper) {
			return delegate.addAll(((LongArrayListWrapper) collection).delegate);
		} else {
			return super.addAll(collection);
		}
	}

	@Override
	public boolean contains(long value) {
		return delegate.contains(value);
	}

	@Override
	public boolean containsAll(LongCollection collection) {
		if (collection instanceof LongArrayListWrapper) {
			return delegate.containsAll(((LongArrayListWrapper) collection).delegate);
		} else {
			return super.containsAll(collection);
		}
	}

	@Override
	public LongIterator iterator() {
		return new LongIteratorWrapper<>(delegate.iterator());
	}

	@Override
	public boolean remove(long value) {
		return delegate.rem(value);
	}

	@Override
	public boolean removeAll(LongCollection collection) {
		if (collection instanceof LongArrayListWrapper) {
			return delegate.removeAll(((LongArrayListWrapper) collection).delegate);
		} else {
			return super.removeAll(collection);
		}
	}

	@Override
	public boolean retainAll(LongCollection collection) {
		if (collection instanceof LongArrayListWrapper) {
			return delegate.retainAll(((LongArrayListWrapper) collection).delegate);
		} else {
			return super.retainAll(collection);
		}
	}

	@Override
	public long[] toArray() {
		return delegate.toArray(new long[delegate.size()]);
	}

	@Override
	public LongList dup() {
		return new LongArrayListWrapper(new it.unimi.dsi.fastutil.longs.LongArrayList(delegate));
	}

	@Override
	public long get(int index) {
		return delegate.getLong(index);
	}

	@Override
	public LongListIterator listIterator() {
		return new LongListIteratorWrapper(delegate.listIterator());
	}

	@Override
	public LongListIterator listIterator(int startIndex) {
		return new LongListIteratorWrapper(delegate.listIterator(startIndex));
	}

	@Override
	public long set(int index, long value) {
		return delegate.set(index, value);
	}
}
