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
package com.b2international.collections.bytes;

import it.unimi.dsi.fastutil.bytes.ByteOpenHashSet;

/**
 * @since 4.7
 */
public final class ByteOpenHashSetWrapper extends ByteSetWrapper {

	private ByteOpenHashSetWrapper(it.unimi.dsi.fastutil.bytes.ByteSet delegate) {
		super(delegate);
	}

	@Override
	public void trimToSize() {
		final it.unimi.dsi.fastutil.bytes.ByteSet delegate = delegate();
		if (delegate instanceof ByteOpenHashSet) {
			((ByteOpenHashSet) delegate).trim();
		} else {
			super.trimToSize();
		}
	}

	public static ByteSet create(ByteCollection source) {
		if (source instanceof ByteOpenHashSetWrapper) {
			final it.unimi.dsi.fastutil.bytes.ByteSet sourceDelegate = ((ByteOpenHashSetWrapper) source).delegate();
			return new ByteOpenHashSetWrapper(clone(sourceDelegate));
		} else {
			final ByteSet result = create(source.size());
			result.addAll(source);
			return result;
		}
	}
	
	public static ByteSet create(int expectedSize) {
		return new ByteOpenHashSetWrapper(new it.unimi.dsi.fastutil.bytes.ByteOpenHashSet(expectedSize));
	}

	public static ByteSet create() {
		return new ByteOpenHashSetWrapper(new it.unimi.dsi.fastutil.bytes.ByteOpenHashSet());
	}
	
	// FastUtil helpers
	
	private static it.unimi.dsi.fastutil.bytes.ByteSet clone(it.unimi.dsi.fastutil.bytes.ByteSet set) {
		if (set instanceof ByteOpenHashSet) {
			return ((ByteOpenHashSet) set).clone();
		} else {
			throw new UnsupportedOperationException("Unsupported set implementation: " + set.getClass().getSimpleName());
		}
	}
}
