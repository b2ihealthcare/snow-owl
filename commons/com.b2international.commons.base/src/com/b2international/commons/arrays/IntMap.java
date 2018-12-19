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
package com.b2international.commons.arrays;

import com.b2international.commons.StopWatch;

public class IntMap<K> implements Cloneable {

	private static final float OVERSIZE_FACTOR = 1.3f;
	
	private int size;

	private K[] keys;
	private int[] values;
	
	@SuppressWarnings("unchecked")
	public IntMap(int expectedSize) {
		size = 0;
		int capacity = (int) (expectedSize * OVERSIZE_FACTOR); 
		keys = (K[]) new Object[capacity];
		values = new int[capacity];
	}
	
	public IntMap(IntMap<K> oldMap, int expectedSize) {
		init(oldMap, expectedSize);
	}
	
	@SuppressWarnings("unchecked")
	protected void init(IntMap<K> oldMap, int expectedSize) {
		int capacity = (int) (expectedSize * OVERSIZE_FACTOR); 
		keys = (K[]) new Object[capacity];
		values = new int[capacity];

		size = 0;
		for(int i = 0; i < oldMap.keys.length; i++) {
			if(oldMap.keys[i] != null) {
				put(oldMap.keys[i], oldMap.values[i]);
			}
		}
	}
	
	public int put(K key, int value) {

		if(key == null) {
			throw new NullPointerException("Key was null");
		}
		if(value < 0) {
			throw new IllegalArgumentException("Value must be >= 0, was " + value);
		}

		// ensure table is big enough. this will guarantee empty slots
		if(size > keys.length / OVERSIZE_FACTOR) {
			resize();
		}
		
		int hash = hash(key);
		int bucketIndex = indexFor(hash, keys.length);

		int probeCount = 0;
		
		int i = 0;
		for (i = bucketIndex; keys[i] != null; i = (i + 1) % keys.length) {
			probeCount++;
			if (key.equals(keys[i])) {
				int oldValue = values[i];
				values[i] = value;
				checkProbe(probeCount);
				return oldValue;
			}
		}
		
		keys[i] = key;
		values[i] = value;
		size++;
		checkProbe(probeCount);
		return -1;
	}
	
	protected void checkProbe(int probe) {
//		if(probe > maxProbe) {
//			maxProbe = probe;
//			System.out.format("Maxprobe: %d at %d\n", probe, size);
//		}
	}
	
	protected void resize() {
    	
		//TODO: log this if important
    	//System.out.format("Resizing from %d to %d\n", keys.length, (int) (keys.length * OVERSIZE_FACTOR));
    	
		try {
			@SuppressWarnings("unchecked")
			IntMap<K> resized = (IntMap<K>) this.clone();
			resized.init(this, keys.length);
			keys = resized.keys;
			values = resized.values;
			size = resized.size;
		} catch (CloneNotSupportedException e) {
			System.out.println(e);
		}
    }
	
	public int get(K key) {
        int hash = hash(key);
        int bucketIndex = indexFor(hash, keys.length);
		for (int i = bucketIndex; keys[i] != null; i = (i + 1) % keys.length) {
			if(key.equals(keys[i])) {
				return values[i];
			}
		}
        return -1;		
	}
	
	public int remove(K key) {
        int hash = hash(key);
        int i = indexFor(hash, keys.length);
        
        if(get(key) < 0) {
        	return -1;
        }
        
        while(!key.equals(keys[i])) {
        	i = (i + 1) % keys.length;
        }
        
        keys[i] = null;
        int result = values[i];
        
        for(i = (i + 1) % keys.length; keys[i] != null; i = (i + 1) % keys.length) {
        	key = keys[i];
        	keys[i] = null;
        	size--;
        	put(key, values[i]);
        }
        size--;
        
        return result;
	}
	
	public boolean remove(int internalId) {
		for(int i = 0; i < values.length; i++) {
			if(keys[i] != null && values[i] == internalId) {
				remove(keys[i]);
				return true;
			}
		}
		return false;
	}
	

	protected int hash(K key) {
		int h = key.hashCode();
		h ^= (h >>> 20) ^ (h >>> 12);
		return h ^ (h >>> 7) ^ (h >>> 4);
	}

	protected int indexFor(int h, int length) {
		return Math.abs(h % length);
	}
	
	public int size() {
		return size;
	}
	
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("{");
		boolean first = true;
		for(int i = 0; i < keys.length; i++) {
			if(keys[i] != null) {
				if(first) {
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
	
	/**
	 * Shifts values > after by value
	 * @param after
	 * @param value
	 */
	public void shiftValues(int after, int value) {
		for(int i = 0; i < values.length; i++) {
			if(values[i] >= after) {
				values[i] += value;
			}
		}
	}
	
	public static void main(String[] args) {

		
		long start = StopWatch.time();
		IntMap<String> map = new IntMap<String>(8) {
			@Override
			protected int hash(String key) {
				return Integer.parseInt(key);
			}
		};
		
		for(int i = 0; i < 10; i++) {
			String key = Integer.toString(10-i);
			map.put(key, i);
		}
		StopWatch.time("Complete", start);
		
		System.out.println(map);
		System.out.format("%d\n", map.values.length);
		
		map.remove("4");
		map.remove("4");
		map.remove("5");
		System.out.println(map);
		System.out.println(map.size());
	}
}