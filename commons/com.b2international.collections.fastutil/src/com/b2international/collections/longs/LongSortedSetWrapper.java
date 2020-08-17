/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

/**
 * @since 7.8.4
 */
public final class LongSortedSetWrapper extends LongSetWrapper implements LongSortedSet {

	private LongSortedSetWrapper(it.unimi.dsi.fastutil.longs.LongSortedSet delegate) {
		super(delegate);
	}

	@Override
	public void trimToSize() {
		// nothing to do
	}

	// Builder methods
	
	public static LongSortedSet create(LongCollection source) {
		final LongSortedSet result = create();
		result.addAll(source);
		return result;
	}
	
	public static LongSortedSet create(long... source) {
		return new LongSortedSetWrapper(new it.unimi.dsi.fastutil.longs.LongAVLTreeSet(source));
	}

	public static LongSortedSet create() {
		return new LongSortedSetWrapper(new it.unimi.dsi.fastutil.longs.LongAVLTreeSet());
	}
	
}
