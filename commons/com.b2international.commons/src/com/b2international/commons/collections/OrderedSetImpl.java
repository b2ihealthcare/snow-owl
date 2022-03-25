/*
 * Copyright 2022 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newArrayListWithExpectedSize;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.b2international.collections.PrimitiveMaps;
import com.b2international.collections.ints.IntValueMap;
import com.google.common.collect.AbstractIterator;

/**
 * An implementation of {@link OrderedSet} backed by a hashmap and an arraylist.
 */
public class OrderedSetImpl<E> extends AbstractSet<E> implements OrderedSet<E>, Serializable {

	private final IntValueMap<E> elementToIndex;
	private final List<E> indexToElement;

	public OrderedSetImpl() {
		this(0);
	}
	
	public OrderedSetImpl(final int expectedSize) {
		checkArgument(expectedSize >= 0, "Expected size can not be negative.");
		
		if (expectedSize < 1) {
			elementToIndex = PrimitiveMaps.newObjectKeyIntOpenHashMap();
			indexToElement = newArrayList();
		} else {
			elementToIndex = PrimitiveMaps.newObjectKeyIntOpenHashMapWithExpectedSize(expectedSize);
			indexToElement = newArrayListWithExpectedSize(expectedSize);
		}
	}

	public OrderedSetImpl(final OrderedSetImpl<E> original) {
		elementToIndex = PrimitiveMaps.newObjectKeyIntOpenHashMap(original.elementToIndex);
		indexToElement = newArrayList(original.indexToElement);
	}

	@Override
	public void clear() {
		elementToIndex.clear();
		indexToElement.clear();
	}

	@Override
	public boolean isEmpty() {
		return elementToIndex.isEmpty();
	}

	@Override
	public int size() {
		// Returns number of existing mappings, so can not use the list which may contain unused slots
		return elementToIndex.size();
	}

	@Override
	public boolean add(final E value) {
		final int oldSize = size();
		indexedAdd(value);
		return (oldSize != size());
	}

	@Override
	public boolean contains(final Object value) {
		return elementToIndex.containsKey(castToType(value));
	}

	@Override
	public Iterator<E> iterator() {
		final Iterator<E> valueIterator = indexToElement.iterator();
		
		return new AbstractIterator<E>() {
			@Override
			protected E computeNext() {
				while (valueIterator.hasNext()) {
					final E value = valueIterator.next();
					if (elementToIndex.containsKey(value)) {
						return value;
					}
				}
				
				return endOfData();
			}
		};
	}

	@Override
	public boolean remove(final Object value) {
		final E value2 = castToType(value);
		final boolean elementRemoved = elementToIndex.containsKey(value2);
		elementToIndex.remove(value2);
		// indexToElement is not modified
		return elementRemoved;
	}

	@Override
	public boolean removeAll(final Collection<?> collection) {
		// Default implementation relies on iterator() supporting remove()
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(final Collection<?> collection) {
		// Default implementation relies on iterator() supporting remove()
		throw new UnsupportedOperationException();
	}

	@Override
	public int indexedAdd(final E value) {
		if (elementToIndex.containsKey(value)) {
			return elementToIndex.get(value);
		}
		
		indexToElement.add(value);
		
		final int lastIndex = indexToElement.size() - 1;
		elementToIndex.put(value, lastIndex);
		return lastIndex;
	}

	@Override
	public int indexOf(final Object value) {
		final E value2 = castToType(value);
		final int index = elementToIndex.get(value2);
		
		/* 
		 * In case of 0, we need to check if it was returned because no index is associated with the 
		 * value and a default int (0) was returned as a result, or if the index is actually 0.
		 */
		if (index > 0 || elementToIndex.containsKey(value2)) { 
			return index;
		} else {
			return -1;
		}
	}

	@Override
	public E get(final int index) {
		final E value = indexToElement.get(index);
		
		if (elementToIndex.containsKey(value)) {
			return value;
		} else {
			throw new IllegalArgumentException("Element at index " + index + " has been removed.");
		}
	}
	
	@Override
	public void trimToSize() {
		compact();
		elementToIndex.trimToSize();
	}

	@Override
	public void compact() {
		// Check if we need compaction at all
		if (indexToElement.size() == elementToIndex.size()) {
			return;
		}

		for (int i = 0; i < indexToElement.size(); i++) {
			final E value = indexToElement.get(i);
			if (!elementToIndex.containsKey(value)) {
				indexToElement.remove(i);
				i--; // so this position is inspected again in the next iteration
			} else {
				elementToIndex.put(value, i);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private E castToType(final Object value) {
		return (E) value;
	}
}
