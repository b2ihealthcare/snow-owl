/*******************************************************************************
 * Copyright (c) 2005, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.core.internal.preferences;


/**
 * Hash table of {String --> String}.
 * 
 * This map handles collisions using linear probing.  When elements are
 * removed, the entire table is rehashed.  Thus this map has good space
 * characteristics, good insertion and iteration performance, but slower
 * removal performance than a HashMap.
 * <p>
 * This map is thread safe because it is immutable.  All methods that modify
 * the map create and return a new map, rather than modifying the receiver.
 */
public abstract class ImmutableMap implements Cloneable {
	static class ArrayMap extends ImmutableMap {
		private static final float LOAD_FACTOR = 0.45f;
		/**
		 * number of elements in the table
		 */
		private int elementSize;

		/**
		 * The table keys
		 */
		private String[] keyTable;

		private int threshold;
		private String[] valueTable;

		ArrayMap(int size) {
			this.elementSize = 0;
			//table size must always be a power of two
			int tableLen = 1;
			while (tableLen < size)
				tableLen *= 2;
			this.keyTable = new String[tableLen];
			this.valueTable = new String[tableLen];
			this.threshold = (int) (tableLen * LOAD_FACTOR);
		}

		public String get(String key) {
			int lengthMask = keyTable.length - 1;
			int index = key.hashCode() & lengthMask;
			String currentKey;
			while ((currentKey = keyTable[index]) != null) {
				if (currentKey.equals(key))
					return valueTable[index];
				index = (index + 1) & lengthMask;
			}
			return null;
		}

		/**
		 * This method destructively adds the key/value pair to the table.
		 * The caller must ensure the table has an empty slot before calling
		 * this method.
		 * @param key
		 * @param value
		 */
		protected void internalPut(String key, String value) {
			int lengthMask = keyTable.length - 1;
			int index = key.hashCode() & lengthMask;
			String currentKey;
			while ((currentKey = keyTable[index]) != null) {
				if (currentKey.equals(key)) {
					valueTable[index] = value;
					return;
				}
				index = (index + 1) & lengthMask;
			}
			keyTable[index] = key;
			valueTable[index] = value;
			++elementSize;
		}

		/**
		 * Returns an array of all keys in this map.
		 */
		public String[] keys() {
			if (elementSize == 0)
				return EMPTY_STRING_ARRAY;
			String[] result = new String[elementSize];
			int next = 0;
			for (int i = 0; i < keyTable.length; i++)
				if (keyTable[i] != null)
					result[next++] = keyTable[i];
			return result;
		}

		public ImmutableMap put(String key, String value) {
			ArrayMap result;
			final int oldLen = keyTable.length;
			if (elementSize + 1 > threshold) {
				//rehash case
				String currentKey;
				result = new ArrayMap(oldLen * 2);
				for (int i = oldLen; --i >= 0;)
					if ((currentKey = keyTable[i]) != null)
						result.internalPut(currentKey, valueTable[i]);
			} else {
				result = new ArrayMap(oldLen);
				System.arraycopy(this.keyTable, 0, result.keyTable, 0, this.keyTable.length);
				System.arraycopy(this.valueTable, 0, result.valueTable, 0, this.valueTable.length);
				result.elementSize = this.elementSize;
			}
			result.internalPut(key, value);
			return result;
		}

		public ImmutableMap removeKey(String key) {
			final int lengthMask = keyTable.length - 1;
			int index = key.hashCode() & lengthMask;
			String currentKey;
			while ((currentKey = keyTable[index]) != null) {
				if (currentKey.equals(key)) {
					if (elementSize <= 1)
						return EMPTY;
					//return a new map that includes all keys except the current one
					ImmutableMap result = createMap((int) (elementSize / LOAD_FACTOR));
					for (int i = 0; i < index; i++)
						if ((currentKey = keyTable[i]) != null)
							result.internalPut(currentKey, valueTable[i]);
					for (int i = index + 1; i <= lengthMask; i++)
						if ((currentKey = keyTable[i]) != null)
							result.internalPut(currentKey, valueTable[i]);
					return result;
				}
				index = (index + 1) & lengthMask;
			}
			return this;
		}

		/* (non-Javadoc
		 * Method declared on IStringPoolParticipant
		 */
		public void shareStrings(StringPool set) {
			//copy elements for thread safety
			String[] array = keyTable;
			if (array == null)
				return;
			for (int i = 0; i < array.length; i++) {
				String o = array[i];
				if (o != null)
					array[i] = set.add(o);
			}
			array = valueTable;
			if (array == null)
				return;
			for (int i = 0; i < array.length; i++) {
				String o = array[i];
				if (o != null)
					array[i] = set.add(o);
			}
		}

		public int size() {
			return elementSize;
		}

	}

	static class EmptyMap extends ImmutableMap {
		public String get(String value) {
			return null;
		}

		public ImmutableMap removeKey(String key) {
			return this;
		}

		protected void internalPut(String key, String value) {
			throw new IllegalStateException();//cannot put elements in the empty map
		}

		public String[] keys() {
			return EMPTY_STRING_ARRAY;
		}

		public ImmutableMap put(String key, String value) {
			ImmutableMap result = createMap(4);
			result.internalPut(key, value);
			return result;
		}

		public int size() {
			return 0;
		}
	}

	/**
	 * The empty hash map.  Since instances are immutable, the empty map
	 * can be a singleton, with accessor methods optimized for the empty map case.
	 */
	public static final ImmutableMap EMPTY = new EmptyMap();

	protected static final String[] EMPTY_STRING_ARRAY = new String[0];

	/**
	 * Returns the value associated with this key in the map, or 
	 * <code>null</code> if the key is not present in the map.
	 * @param key
	 * @return The value associated with this key, or <code>null</code>
	 */
	public abstract String get(String key);

	protected static ImmutableMap createMap(int i) {
		if (i <= 0)
			return EMPTY;
		return new ArrayMap(i);
	}

	/**
	 * Destructively adds a key/value pair to this map. The caller must ensure
	 * there is enough room in this map to proceed.
	 * 
	 * @param key
	 * @param value
	 */
	protected abstract void internalPut(String key, String value);

	/**
	 * Returns an array of all keys in this map.
	 */
	public abstract String[] keys();

	/**
	 * Returns a new map that is equal to this one, except with the given
	 * key/value pair added.
	 * 
	 * @param key
	 * @param value
	 * @return The map containing the given key/value pair
	 */
	public abstract ImmutableMap put(String key, String value);

	/**
	 * Returns a map that is equal to this one, except without the given
	 * key.
	 * @param key
	 * @return A map with the given key removed
	 */
	public abstract ImmutableMap removeKey(String key);

	/* (non-Javadoc
	 * Method declared on IStringPoolParticipant
	 */
	public void shareStrings(StringPool set) {
		// nothing to do
	}

	/**
	 * Returns the number of keys in this map.
	 * @return the number of keys in this map.
	 */
	public abstract int size();

	public String toString() {
		StringBuffer s = new StringBuffer();
		String[] keys = keys();
		for (int i = 0, length = keys.length; i < length; i++)
			s.append(keys[i]).append(" -> ").append(get(keys[i])).append("\n"); //$NON-NLS-2$ //$NON-NLS-1$
		return s.toString();
	}
}
