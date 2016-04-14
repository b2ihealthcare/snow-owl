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

import com.b2international.collections.LongIteratorWrapper;
import com.b2international.collections.longs.AbstractLongCollection;
import com.b2international.collections.longs.LongCollection;
import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongSet;
import com.google.common.hash.HashFunction;

import it.unimi.dsi.fastutil.longs.LongOpenCustomHashSet;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;

/**
 * @since 4.7
 */
public final class LongOpenHashSetWrapper extends AbstractLongCollection implements LongSet {

	private final it.unimi.dsi.fastutil.longs.LongSet delegate;

	public static LongSet create(HashFunction hashFunction) {
		return wrap(new it.unimi.dsi.fastutil.longs.LongOpenCustomHashSet(new StrategyWrapper(hashFunction)));
	}
	
	public static LongSet create(long[] source) {
		return wrap(new it.unimi.dsi.fastutil.longs.LongOpenHashSet(source));
	}
	
	public static LongSet create(LongCollection source) {
		if (source instanceof LongOpenHashSetWrapper) {
			final it.unimi.dsi.fastutil.longs.LongSet sourceDelegate = ((LongOpenHashSetWrapper) source).delegate;
			return wrap(clone(sourceDelegate));
		} else {
			final LongSet result = create(source.size());
			result.addAll(source);
			return result;
		}
	}
	
	public static LongSet create(int expectedSize) {
		return wrap(new it.unimi.dsi.fastutil.longs.LongOpenHashSet(expectedSize));
	}
	
	// XXX: Fill factor parameter loses precision on API boundary
	public static LongSet create(int expectedSize, double fillFactor) {
		return wrap(new it.unimi.dsi.fastutil.longs.LongOpenHashSet(expectedSize, (float) fillFactor));
	}

	public static LongSet create() {
		return wrap(new it.unimi.dsi.fastutil.longs.LongOpenHashSet());
	}
	
	public static LongSet wrap(it.unimi.dsi.fastutil.longs.LongSet delegate) {
		return new LongOpenHashSetWrapper(delegate);
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
		trim(delegate);
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
		return create(this);
	}
	
	// FastUtil helpers
	
	private static void trim(it.unimi.dsi.fastutil.longs.LongSet set) {
		if (set instanceof LongOpenHashSet) {
			((LongOpenHashSet)set).trim();
		} else if (set instanceof LongOpenCustomHashSet) {
			((LongOpenCustomHashSet) set).trim();
		} else {
			throw new IllegalStateException("Don't know how to trim long set of type " + set.getClass().getSimpleName() + ".");
		}		
	}
	
	private static it.unimi.dsi.fastutil.longs.LongSet clone(it.unimi.dsi.fastutil.longs.LongSet set) {
		if (set instanceof LongOpenCustomHashSet) {
			return ((LongOpenCustomHashSet) set).clone();
		} else if (set instanceof LongOpenHashSet) {
			return ((LongOpenHashSet) set).clone();
		} else {
			throw new IllegalStateException("Don't know how to clone wrapped long set of type " + set.getClass().getSimpleName() + ".");
		}
	}
	
}
