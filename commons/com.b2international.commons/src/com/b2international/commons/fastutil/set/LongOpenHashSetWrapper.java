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

import com.b2international.collections.AbstractLongCollection;
import com.b2international.collections.LongCollection;
import com.b2international.collections.LongIterator;
import com.b2international.collections.set.LongSet;
import com.b2international.commons.fastutil.LongIteratorWrapper;
import com.google.common.hash.HashFunction;

import it.unimi.dsi.fastutil.longs.LongOpenCustomHashSet;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;

public class LongOpenHashSetWrapper extends AbstractLongCollection implements LongSet {

	private final com.b2international.collections.set.LongSet delegate;

	public static LongSet create(HashFunction hashFunction) {
		return new LongOpenHashSetWrapper(new it.unimi.dsi.fastutil.longs.LongOpenCustomHashSet(new StrategyWrapper(hashFunction)));
	}
	
	public static LongSet create(long[] source) {
		return new LongOpenHashSetWrapper(new it.unimi.dsi.fastutil.longs.LongOpenHashSet(source));
	}
	
	public static LongSet create(LongCollection source) {
		if (source instanceof LongOpenHashSetWrapper) {
			final it.unimi.dsi.fastutil.longs.LongSet sourceDelegate = ((LongOpenHashSetWrapper) source).delegate;
			
			if (sourceDelegate instanceof LongOpenCustomHashSet) {
				return new LongOpenHashSetWrapper(((LongOpenCustomHashSet) sourceDelegate).clone());
			} else if (sourceDelegate instanceof LongOpenHashSet) {
				return new LongOpenHashSetWrapper(((LongOpenHashSet) sourceDelegate).clone());
			} else {
				throw new IllegalStateException("Don't know how to clone wrapped long set of type " + sourceDelegate.getClass().getSimpleName() + ".");
			}
		} else {
			LongOpenHashSetWrapper result = new LongOpenHashSetWrapper(new it.unimi.dsi.fastutil.longs.LongOpenHashSet(source.size()));
			result.addAll(source);
			return result;
		}
	}
	
	public static LongSet create(int expectedSize) {
		return new LongOpenHashSetWrapper(new it.unimi.dsi.fastutil.longs.LongOpenHashSet(expectedSize));
	}
	
	// XXX: Fill factor parameter loses precision on API boundary
	public static LongSet create(int expectedSize, double fillFactor) {
		return new LongOpenHashSetWrapper(new it.unimi.dsi.fastutil.longs.LongOpenHashSet(expectedSize, (float) fillFactor));
	}

	public static LongSet create() {
		return new LongOpenHashSetWrapper(new it.unimi.dsi.fastutil.longs.LongOpenHashSet());
	}
	
	private LongOpenHashSetWrapper(it.unimi.dsi.fastutil.longs.LongSet delegate) {
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
		if (delegate instanceof LongOpenHashSet) {
			((LongOpenHashSet)delegate).trim();
		} else if (delegate instanceof LongOpenCustomHashSet) {
			((LongOpenCustomHashSet) delegate).trim();
		} else {
			throw new IllegalStateException("Don't know how to trim long set of type " + delegate.getClass().getSimpleName() + ".");
		}
	}

	@Override
	public boolean add(long value) {
		return delegate.add(value);
	}

	@Override
	public boolean addAll(LongCollection collection) {
		if (collection instanceof LongOpenHashSetWrapper) {
			return delegate.addAll(((LongOpenHashSetWrapper) collection).delegate);
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
		if (collection instanceof LongOpenHashSetWrapper) {
			return delegate.containsAll(((LongOpenHashSetWrapper) collection).delegate);
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
		if (collection instanceof LongOpenHashSetWrapper) {
			return delegate.removeAll(((LongOpenHashSetWrapper) collection).delegate);
		} else {
			return super.removeAll(collection);
		}
	}

	@Override
	public boolean retainAll(LongCollection collection) {
		if (collection instanceof LongOpenHashSetWrapper) {
			return delegate.retainAll(((LongOpenHashSetWrapper) collection).delegate);
		} else {
			return super.retainAll(collection);
		}
	}

	@Override
	public long[] toArray() {
		return delegate.toArray(new long[delegate.size()]);
	}

	@Override
	public LongSet dup() {
		return new LongOpenHashSetWrapper(new it.unimi.dsi.fastutil.longs.LongOpenHashSet(delegate));
	}
}
