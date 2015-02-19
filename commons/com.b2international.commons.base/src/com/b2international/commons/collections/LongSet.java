/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.commons.collections;

import java.text.MessageFormat;
import java.util.Arrays;

import com.google.common.primitives.Longs;

/**
 * Represents a set with positive longs as keys. Keys can only be added to the set.
 * 
 */
public class LongSet implements Cloneable {

	private static final float GROWTH_FACTOR = 2.0f;
	private static final float OVERSIZE_FACTOR = 1.3f;
	private static final int DEFAULT_EXPECTED_SIZE = 32;
	private static final long EMPTY = 0L;

	private static int getCapacity(final int expectedSize) {
		return (int) (expectedSize * OVERSIZE_FACTOR);
	}

	private int size;
	private long[] keys;
	
	public LongSet() {
		this(DEFAULT_EXPECTED_SIZE);
	}
	
	public LongSet(final int expectedSize) {
		init(expectedSize);
	}

	public LongSet(final LongSet oldSet) {
		this(oldSet, DEFAULT_EXPECTED_SIZE);
	}
	
	public LongSet(final LongSet oldSet, final int expectedSize) {
		
		init(Math.max(oldSet.size(), expectedSize)); 
	
		for (int i = 0; i < oldSet.keys.length; i++) {
			if (EMPTY != oldSet.keys[i]) {
				add(oldSet.keys[i]);
			}
		}
	}

	private void init(final int expectedSize) {
		size = 0;
		keys = new long[getCapacity(expectedSize)];
	}

	private void resize() {
		// Clone this map and get all fields
		final LongSet resized = new LongSet(this, (int) (this.size * GROWTH_FACTOR));
		keys = resized.keys;
		size = resized.size;
	}

	public void add(final long key) {

		if (key <= 0) {
			throw new IllegalArgumentException(MessageFormat.format("Key must be > 0, was ''{0}''", key));
		}
		
		// ensure table is big enough. this will guarantee empty slots
		if (size > keys.length / OVERSIZE_FACTOR) {
			resize();
		}
		
		final int hash = hash(key);
		final int bucketIndex = indexFor(hash, keys.length);

		int i = 0;
		for (i = bucketIndex; EMPTY != keys[i]; i = (i + 1) % keys.length) {
			if (key == keys[i]) {
				return;
			}
		}
		
		keys[i] = key;
		size++;
	}

	private int hash(final long key) {
		int h = Longs.hashCode(key);
		h ^= (h >>> 20) ^ (h >>> 12);
		return h ^ (h >>> 7) ^ (h >>> 4);
	}

	private int indexFor(final int h, final int length) {
		return Math.abs(h % length);
	}
	
	public int size() {
		return size;
	}
	
	public String toString() {
		
		final StringBuilder buf = new StringBuilder();
		
		buf.append("{");
		boolean first = true;
		
		for (int i = 0; i < keys.length; i++) {
			
			if (EMPTY != keys[i]) {
				
				if (first) {
					first = false;
				} else {
					buf.append(", ");
				}
				
				buf.append(keys[i]);
			}
		}
		
		buf.append("}");
		return buf.toString();
	}
	
	public long[] toSortedArray() {
		
		final long[] result = new long[size];
		
		int i = 0;
		
		for (long candidate : keys) {
			if (EMPTY != candidate) {
				result[i++] = candidate;
			}
		}
		
		Arrays.sort(result);
		
		return result;
	}
}