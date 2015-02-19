/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.core.internal.preferences;

import org.eclipse.core.runtime.ListenerList;

/**
 * A class which holds onto a listener list object for a given path.
 * Typically the path is the absolute path of a preference node.
 * 
 * @since 3.1
 */
public class ListenerRegistry {

	/**
	 * Specialized map-like data structure for storing change listeners. 
	 */
	private static class ListenerMap {
		private static final int GROW_SIZE = 10;
		String[] keys;
		ListenerList[] values;

		/**
		 * Create a map of exactly the specified size.
		 */
		ListenerMap(int size) {
			super();
			this.keys = new String[size];
			this.values = new ListenerList[size];
		}

		/**
		 * Return the listener list associated with the given key,
		 * or <code>null</code> if it doesn't exist.
		 */
		ListenerList get(String key) {
			if (key == null)
				throw new NullPointerException();
			for (int i = 0; i < keys.length; i++)
				if (key.equals(keys[i]))
					return values[i];
			return null;
		}

		/**
		 * Associate the given listener list with the specified key. Overwrite
		 * an existing association, if applicable.
		 */
		void put(String key, ListenerList value) {
			if (key == null)
				throw new NullPointerException();
			if (value == null) {
				remove(key);
				return;
			}
			// replace if exists, keeping track of an empty position
			int emptyIndex = -1;
			for (int i = 0; i < keys.length; i++) {
				String existing = keys[i];
				if (existing == null) {
					emptyIndex = i;
					continue;
				}
				if (existing.equals(key)) {
					values[i] = value;
					return;
				}
			}
			if (emptyIndex == -1)
				emptyIndex = grow();
			keys[emptyIndex] = key;
			values[emptyIndex] = value;
		}

		/*
		 * Make the backing arrays larger
		 */
		private int grow() {
			int size = keys.length;
			String[] tempKeys = new String[size + GROW_SIZE];
			System.arraycopy(keys, 0, tempKeys, 0, size);
			keys = tempKeys;
			ListenerList[] tempValues = new ListenerList[size + GROW_SIZE];
			System.arraycopy(values, 0, tempValues, 0, size);
			values = tempValues;
			return size;
		}

		/**
		 * Remove the association specified by the given key.
		 * Do nothing if none exists.
		 * 
		 * Note: Should consider shrinking the array. Hold off for now
		 * as we don't expect #remove to be a common code path.
		 */
		void remove(String key) {
			if (key == null)
				throw new NullPointerException();
			for (int i = 0; i < keys.length; i++)
				if (key.equals(keys[i])) {
					keys[i] = null;
					values[i] = null;
					return;
				}
		}
	}

	static final Object[] EMPTY_LIST = new Object[0];
	ListenerMap registry = new ListenerMap(25);

	/**
	 * Return the listeners for this path or an empty list if none.
	 */
	public synchronized Object[] getListeners(String path) {
		ListenerList list = registry.get(path);
		return list == null ? EMPTY_LIST : list.getListeners();
	}

	/**
	 * Add the given listener to the listeners registered for this path.
	 * If the listener already exists, then do nothing.
	 */
	public synchronized void add(String path, Object listener) {
		ListenerList list = registry.get(path);
		if (list == null)
			list = new ListenerList(ListenerList.IDENTITY);
		list.add(listener);
		registry.put(path, list);
	}

	/**
	 * Remove the given listener from this path's collection of 
	 * listeners. If it is not associated with this path, then do nothing.
	 */
	public synchronized void remove(String path, Object listener) {
		ListenerList list = registry.get(path);
		if (list == null)
			return;
		list.remove(listener);
		if (list.isEmpty())
			registry.remove(path);
	}

	/**
	 * Remove all of the listeners registered under the given path.
	 */
	public synchronized void clear(String path) {
		registry.remove(path);
	}

}
