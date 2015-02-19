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

import java.util.ArrayList;
import java.util.Iterator;

import com.b2international.commons.DelegatingIterator;

/**
 * Bidirectional map with int internal ids
 * 
 *
 * @param <K> key type for the map
 * @param <E> element type for the map
 */
public class BidiMapWithInternalId<K, E> {

	private IntMap<K> keyMap;
	private ArrayList<E> elements;

	public BidiMapWithInternalId(int expectedSize) {
		keyMap = new IntMap<K>(expectedSize);
		elements = new ArrayList<E>(expectedSize);
	}
	
	@SuppressWarnings("unchecked")
	public BidiMapWithInternalId(BidiMapWithInternalId<K, E> original) {
		keyMap = new IntMap<K>(original.keyMap, original.keyMap.size());
		elements = (ArrayList<E>) original.elements.clone();
	}
	
	/**
	 * Add element to the map with key
	 * 
	 * @param key
	 * @param element
	 * @return internal id
	 */
	public int put(K key, E element) {
		
		if(key == null) {
			throw new NullPointerException("Key was null");
		}
		if(element == null) {
			throw new NullPointerException("Element was null");
		}

		int id = keyMap.get(key);
		if(id < 0) {
			elements.add(element);
			keyMap.put(key, elements.size() - 1);
		} else {
			elements.set(id, element);
		}
		return id;
	}
	
	/**
	 * @return element for the specified key, or null if not found
	 */
	public E get(K key) {
		int id = keyMap.get(key);
		return id < 0 ? null : elements.get(id);
	}
	
	/**
	 * @return element for the specified internal id
	 * @throws IndexOutOfBoundsException
	 */
	public E get(int internalId) {
		return elements.get(internalId);
	}
	
	/** @return internal id of the removed element, id -1 if not found */
	public int remove(K key) {
		int id = keyMap.remove(key);
		if(id >= 0) {
			keyMap.shiftValues(id, -1);
			elements.remove(id);
		}
		return id;
	}
	
	public int getInternalId(K key) {
		return keyMap.get(key);
	}
	
	public int size() {
		return elements.size();
	}
	
	public Iterable<E> getElements() {
		
		return new Iterable<E>() {
			@Override
			public Iterator<E> iterator() {
				
				final Iterator<E> delegate = elements.iterator();
				
				// this iterator also removes entry from keyMap
				return new DelegatingIterator<E>(delegate) {
					
					// first next() will set this to 0 
					private int i = -1;
					boolean valid = false;
					
					@Override
					public E next() {
						i++;
						valid = true;
						return super.next();
					}
					@Override
					public void remove() {

						if(!valid) {
							throw new IllegalStateException();
						}
						
						valid = false;
						
						// try to remove from keymap, should always return true but you never know...
						if(keyMap.remove(i)) {
							keyMap.shiftValues(i, -1);
							// remove from elements
							delegate.remove();
							// element indexes in ArrayList are shifted with remove
							i--;
						} else {
							throw new IllegalStateException("Error while removing element, no key found at index " + i);
						}
					}
				};
			}
		};
	}
}