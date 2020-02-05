/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;

/**
 * @since 4.7
 */
public final class IntOpenHashSetWrapper extends IntSetWrapper {

	private IntOpenHashSetWrapper(it.unimi.dsi.fastutil.ints.IntSet delegate) {
		super(delegate);
	}
	
	@Override
	public void trimToSize() {
		final it.unimi.dsi.fastutil.ints.IntSet delegate = delegate();
		if (delegate instanceof IntOpenHashSet) {
			((IntOpenHashSet) delegate).clone();
		} else {
			super.trimToSize();
		}
	}

	// Builder methods
	
	public static IntSet create(IntCollection source) {
		if (source instanceof IntOpenHashSetWrapper) {
			final it.unimi.dsi.fastutil.ints.IntSet sourceDelegate = ((IntOpenHashSetWrapper) source).delegate();
			return new IntOpenHashSetWrapper(clone(sourceDelegate));
		} else {
			final IntSet result = createWithExpectedSize(source.size());
			result.addAll(source);
			return result;
		}
	}
	
	public static IntSet createWithExpectedSize(int expectedSize) {
		return new IntOpenHashSetWrapper(new it.unimi.dsi.fastutil.ints.IntOpenHashSet(expectedSize));
	}
	
	public static IntSet create() {
		return new IntOpenHashSetWrapper(new it.unimi.dsi.fastutil.ints.IntOpenHashSet());
	}
	
	// FastUtil helpers
	
	private static it.unimi.dsi.fastutil.ints.IntSet clone(it.unimi.dsi.fastutil.ints.IntSet set) {
		if (set instanceof IntOpenHashSet) {
			return ((IntOpenHashSet) set).clone();
		} else {
			throw new UnsupportedOperationException("Unsupported set implementation: " + set.getClass().getSimpleName());
		}
	}
	
}
