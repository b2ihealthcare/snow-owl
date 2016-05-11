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
package com.b2international.collections.longs;

import com.google.common.hash.HashFunction;

import it.unimi.dsi.fastutil.longs.LongOpenCustomHashSet;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;

/**
 * @since 4.7
 */
public final class LongOpenHashSetWrapper extends LongSetWrapper {

	private LongOpenHashSetWrapper(it.unimi.dsi.fastutil.longs.LongSet delegate) {
		super(delegate);
	}

	@Override
	public void trimToSize() {
		final it.unimi.dsi.fastutil.longs.LongSet set = delegate();
		if (set instanceof LongOpenHashSet) {
			((LongOpenHashSet)set).trim();
		} else if (set instanceof LongOpenCustomHashSet) {
			((LongOpenCustomHashSet) set).trim();
		} else {
			super.trimToSize();
		}		
	}

	// Builder methods
	
	public static LongSet create(HashFunction hashFunction) {
		return new LongOpenHashSetWrapper(new it.unimi.dsi.fastutil.longs.LongOpenCustomHashSet(new LongHashStrategyWrapper(hashFunction)));
	}
	
	public static LongSet create(LongCollection source) {
		if (source instanceof LongOpenHashSetWrapper) {
			final it.unimi.dsi.fastutil.longs.LongSet sourceDelegate = ((LongOpenHashSetWrapper) source).delegate();
			return new LongOpenHashSetWrapper(clone(sourceDelegate));
		} else {
			final LongSet result = createWithExpectedSize(source.size());
			result.addAll(source);
			return result;
		}
	}
	
	public static LongSet create(long... source) {
		return new LongOpenHashSetWrapper(new it.unimi.dsi.fastutil.longs.LongOpenHashSet(source));
	}

	// XXX: Fill factor parameter loses precision on API boundary
	public static LongSet createWithExpectedSize(int expectedSize, double fillFactor) {
		return new LongOpenHashSetWrapper(new it.unimi.dsi.fastutil.longs.LongOpenHashSet(expectedSize, (float) fillFactor));
	}
	
	public static LongSet createWithExpectedSize(int expectedSize) {
		return new LongOpenHashSetWrapper(new it.unimi.dsi.fastutil.longs.LongOpenHashSet(expectedSize));
	}
	
	public static LongSet create() {
		return new LongOpenHashSetWrapper(new it.unimi.dsi.fastutil.longs.LongOpenHashSet());
	}
	
	// FastUtil helpers
	
	private static it.unimi.dsi.fastutil.longs.LongSet clone(it.unimi.dsi.fastutil.longs.LongSet set) {
		if (set instanceof LongOpenCustomHashSet) {
			return ((LongOpenCustomHashSet) set).clone();
		} else if (set instanceof LongOpenHashSet) {
			return ((LongOpenHashSet) set).clone();
		} else {
			throw new UnsupportedOperationException("Unsupported set implementation: " + set.getClass().getSimpleName());
		}
	}
	
}
