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

import com.google.common.primitives.Longs;

/**
 * Represents a map with positive longs as keys and values of
 * primitive, non-negative longs.
 * 
 */
public class LongLongMap implements Cloneable {

	private static final float GROWTH_FACTOR = 2.0f;
	private static final float OVERSIZE_FACTOR = 1.3f;
	private static final int DEFAULT_EXPECTED_SIZE = 32;
	private static final long EMPTY = 0L;

	private static int getCapacity(final int expectedSize) {
		return (int) (expectedSize * OVERSIZE_FACTOR);
	}

	private int size;
	private long[] keys;
	private long[] values;
	
	public LongLongMap() {
		this(DEFAULT_EXPECTED_SIZE);
	}
	
	public LongLongMap(final int expectedSize) {
		init(expectedSize);
	}

	public LongLongMap(final LongLongMap oldMap) {
		this(oldMap, DEFAULT_EXPECTED_SIZE);
	}
	
	public LongLongMap(final LongLongMap oldMap, final int expectedSize) {
		
		init(Math.max(oldMap.size(), expectedSize)); 
	
		for (int i = 0; i < oldMap.keys.length; i++) {
			if (oldMap.keys[i] != EMPTY) {
				put(oldMap.keys[i], oldMap.values[i]);
			}
		}
	}

	private void init(final int expectedSize) {
		size = 0;
		keys = new long[getCapacity(expectedSize)];
		values = new long[getCapacity(expectedSize)];
	}

	private void resize() {
		// Clone this map and get all fields
		final LongLongMap resized = new LongLongMap(this, (int) (this.size * GROWTH_FACTOR));
		keys = resized.keys;
		values = resized.values;
		size = resized.size;
	}

	public long put(final long key, final long value) {

		if (key <= 0) {
			throw new IllegalArgumentException(MessageFormat.format("Key must be > 0, was ''{0}''", key));
		}
		
		if (value < 0) {
			throw new IllegalArgumentException(MessageFormat.format("Value must be >= 0, was ''{0}''", value));
		}

		// ensure table is big enough. this will guarantee empty slots
		if (size > keys.length / OVERSIZE_FACTOR) {
			resize();
		}
		
		final int hash = hash(key);
		final int bucketIndex = indexFor(hash, keys.length);

		int i = 0;
		for (i = bucketIndex; keys[i] != EMPTY; i = (i + 1) % keys.length) {
			if (key == keys[i]) {
				final long oldValue = values[i];
				values[i] = value;
				return oldValue;
			}
		}
		
		keys[i] = key;
		values[i] = value;
		size++;
		return -1;
	}
	
	public long get(final long key) {
		
        final int hash = hash(key);
        final int bucketIndex = indexFor(hash, keys.length);
        
		for (int i = bucketIndex; keys[i] != EMPTY; i = (i + 1) % keys.length) {
			if (key == keys[i]) {
				return values[i];
			}
		}
		
        return -1;		
	}
	
	public long remove(final long key) {
		
        if (get(key) < 0) {
        	return -1;
        }

        final int hash = hash(key);
        int i = indexFor(hash, keys.length);

        while (key != keys[i]) {
        	i = (i + 1) % keys.length;
        }
        
        keys[i] = EMPTY;
        final long result = values[i];
        
        for (i = (i + 1) % keys.length; keys[i] != EMPTY; i = (i + 1) % keys.length) {
        	final long tmpKey = keys[i];
        	keys[i] = EMPTY;
        	size--;
        	put(tmpKey, values[i]);
        }
        
        size--;
        
        return result;
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
			
			if (keys[i] != EMPTY) {
				
				if (first) {
					first = false;
				} else {
					buf.append(", ");
				}
				
				buf.append(keys[i]);
				buf.append("=");
				buf.append(values[i]);
			}
		}
		
		buf.append("}");
		return buf.toString();
	}
}