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

import static com.google.common.base.Preconditions.checkNotNull;

import java.text.MessageFormat;
import java.util.UUID;

import com.google.common.primitives.Longs;

/**
 * Represents a map with {@link UUID}s as keys and values of primitive,
 * non-negative longs. UUIDs are represented internally as a pair of primitive
 * longs as well. Both the MSB and the LSB long parts of the UUID are assumed to
 * be non-zero for all values.
 * 
 * @see UUID#variant()
 * @see UUID#version()
 * 
 */
public class UuidLongMap implements Cloneable {

	private static final float GROWTH_FACTOR = 2.0f;
	private static final float OVERSIZE_FACTOR = 1.3f;
	private static final int DEFAULT_EXPECTED_SIZE = 32;
	private static final long EMPTY = 0L;

	private static int getCapacity(final int expectedSize) {
		return (int) (expectedSize * OVERSIZE_FACTOR);
	}

	private int size;
	private long[] lsbKeys;
	private long[] msbKeys;
	private long[] values;
	
	public UuidLongMap() {
		this(DEFAULT_EXPECTED_SIZE);
	}
	
	public UuidLongMap(final int expectedSize) {
		init(expectedSize);
	}

	public UuidLongMap(final UuidLongMap oldMap) {
		this(oldMap, DEFAULT_EXPECTED_SIZE);
	}
	
	public UuidLongMap(final UuidLongMap oldMap, final int expectedSize) {
		
		init(Math.max(oldMap.size(), expectedSize)); 
	
		for (int i = 0; i < oldMap.lsbKeys.length; i++) {
			if (oldMap.lsbKeys[i] != EMPTY) {
				put(oldMap.lsbKeys[i], oldMap.msbKeys[i], oldMap.values[i]);
			}
		}
	}

	private void init(final int expectedSize) {
		size = 0;
		lsbKeys = new long[getCapacity(expectedSize)];
		msbKeys = new long[getCapacity(expectedSize)];
		values = new long[getCapacity(expectedSize)];
	}

	private void resize() {
		// Clone this map and get all fields
		final UuidLongMap resized = new UuidLongMap(this, (int) (this.size * GROWTH_FACTOR));
		lsbKeys = resized.lsbKeys;
		msbKeys = resized.msbKeys;
		values = resized.values;
		size = resized.size;
	}

	public long put(final UUID key, final long value) {
		return put(checkNotNull(key, "key").getLeastSignificantBits(), key.getMostSignificantBits(), value);
	}
	
	private long put(final long lsbKey, final long msbKey, final long value) {

		if (lsbKey == 0) {
			throw new IllegalArgumentException("lsbKey must be != 0");
		}
		
		if (msbKey == 0) {
			throw new IllegalArgumentException("msbKey must be != 0");
		}
		
		if (value < 0) {
			throw new IllegalArgumentException(MessageFormat.format("Value must be >= 0, was ''{0}''", value));
		}

		// ensure table is big enough. this will guarantee empty slots
		if (size > lsbKeys.length / OVERSIZE_FACTOR) {
			resize();
		}
		
		final int hash = hash(lsbKey, msbKey);
		final int bucketIndex = indexFor(hash, lsbKeys.length);

		int i = 0;
		for (i = bucketIndex; lsbKeys[i] != EMPTY && msbKeys[i] != EMPTY; i = (i + 1) % lsbKeys.length) {
			if (lsbKey == lsbKeys[i] && msbKey == msbKeys[i]) {
				final long oldValue = values[i];
				values[i] = value;
				return oldValue;
			}
		}
		
		lsbKeys[i] = lsbKey;
		msbKeys[i] = msbKey;
		values[i] = value;
		size++;
		return -1;
	}
	
	public long get(final UUID key) {
		return get(checkNotNull(key, "key").getLeastSignificantBits(), key.getMostSignificantBits());
	}
	
	private long get(final long lsbKey, long msbKey) {
		
        final int hash = hash(lsbKey, msbKey);
        final int bucketIndex = indexFor(hash, lsbKeys.length);
        
		for (int i = bucketIndex; lsbKeys[i] != EMPTY && msbKeys[i] != EMPTY; i = (i + 1) % lsbKeys.length) {
			if (lsbKey == lsbKeys[i] && msbKey == msbKeys[i]) {
				return values[i];
			}
		}
		
        return -1;		
	}
	
	public long remove(final UUID key) {
		return remove(checkNotNull(key, "key").getLeastSignificantBits(), key.getMostSignificantBits());
	}
	
	private long remove(final long lsbKey, long msbKey) {
		
        if (get(lsbKey, msbKey) < 0) {
        	return -1;
        }

        final int hash = hash(lsbKey, msbKey);
        int i = indexFor(hash, lsbKeys.length);

        while (lsbKey != lsbKeys[i] && msbKey != msbKeys[i]) {
        	i = (i + 1) % lsbKeys.length;
        }
        
        lsbKeys[i] = EMPTY;
        msbKeys[i] = EMPTY;
        final long result = values[i];
        
        for (i = (i + 1) % lsbKeys.length; lsbKeys[i] != EMPTY && msbKeys[i] != EMPTY; i = (i + 1) % lsbKeys.length) {
        	long tmpLsbKey = lsbKeys[i];
        	long tmpMsbKey = msbKeys[i];
        	lsbKeys[i] = EMPTY;
        	msbKeys[i] = EMPTY;
        	size--;
        	put(tmpLsbKey, tmpMsbKey, values[i]);
        }
        
        size--;
        
        return result;
	}

	private int hash(final long lsbKey, final long msbKey) {
		int h = 31 * Longs.hashCode(lsbKey) + Longs.hashCode(msbKey); 
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
		
		for (int i = 0; i < lsbKeys.length; i++) {
			
			if (lsbKeys[i] != EMPTY && msbKeys[i] != EMPTY) {
				
				if (first) {
					first = false;
				} else {
					buf.append(", ");
				}
				
				buf.append(new UUID(msbKeys[i], lsbKeys[i]));
				buf.append("=");
				buf.append(values[i]);
			}
		}
		
		buf.append("}");
		return buf.toString();
	}
}