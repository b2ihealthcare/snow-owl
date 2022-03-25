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
package com.b2international.commons.collect;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.Serializable;

import com.b2international.collections.PrimitiveLists;
import com.b2international.collections.PrimitiveMaps;
import com.b2international.collections.longs.*;

/**
 * An implementation of {@link LongOrderedSet} backed by a primitive hashmap and a primitive arraylist.
 */
public class LongOrderedSetImpl extends AbstractLongSet implements LongOrderedSet, Serializable {

	private final LongKeyIntMap elementToIndex;
	private final LongList indexToElement;

	public LongOrderedSetImpl() {
		this(0);
	}
	
	public LongOrderedSetImpl(final int expectedSize) {
		checkArgument(expectedSize >= 0, "Expected size can not be negative.");
		
		if (expectedSize < 1) {
			elementToIndex = PrimitiveMaps.newLongKeyIntOpenHashMap();
			indexToElement = PrimitiveLists.newLongArrayList();
		} else {
			elementToIndex = PrimitiveMaps.newLongKeyIntOpenHashMapWithExpectedSize(expectedSize);
			indexToElement = PrimitiveLists.newLongArrayListWithExpectedSize(expectedSize);
		}
	}

	public LongOrderedSetImpl(final LongOrderedSetImpl original) {
		elementToIndex = PrimitiveMaps.newLongKeyIntOpenHashMap(original.elementToIndex);
		indexToElement = PrimitiveLists.newLongArrayList(original.indexToElement);
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
	public void trimToSize() {
		compact();
		elementToIndex.trimToSize();
		indexToElement.trimToSize();
	}

	@Override
	public void compact() {
		// Check if we need compaction at all
		if (indexToElement.size() == elementToIndex.size()) {
			return;
		}
		
		for (int i = 0; i < indexToElement.size(); i++) {
			final long value = indexToElement.get(i);
			if (!elementToIndex.containsKey(value)) {
				indexToElement.removeLong(i);
				i--; // so this position is inspected again in the next iteration
			} else {
				elementToIndex.put(value, i);
			}
		}
	}

	@Override
	public boolean add(final long value) {
		final int oldSize = size();
		indexedAdd(value);
		return (oldSize != size());
	}

	@Override
	public boolean contains(final long value) {
		return elementToIndex.containsKey(value);
	}

	@Override
	public LongIterator iterator() {
		final LongIterator valueIterator = indexToElement.iterator();
		
		return new AbstractLongIterator() {
			@Override
			protected long computeNext() {
				while (valueIterator.hasNext()) {
					final long value = valueIterator.next();
					if (elementToIndex.containsKey(value)) {
						return value;
					}
				}
				
				return endOfData();
			}
		};
	}

	@Override
	public boolean remove(final long value) {
		final boolean elementRemoved = elementToIndex.containsKey(value);
		elementToIndex.remove(value);
		// indexToElement is not modified
		return elementRemoved;
	}

	@Override
	public boolean removeAll(final LongCollection collection) {
		// Default implementation relies on iterator() supporting remove()
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(final LongCollection collection) {
		// Default implementation relies on iterator() supporting remove()
		throw new UnsupportedOperationException();
	}

	@Override
	public int indexedAdd(final long value) {
		if (elementToIndex.containsKey(value)) {
			return elementToIndex.get(value);
		}
		
		indexToElement.add(value);
		
		final int lastIndex = indexToElement.size() - 1;
		elementToIndex.put(value, lastIndex);
		return lastIndex;
	}

	@Override
	public int indexOf(final long value) {
		final int index = elementToIndex.get(value);
		
		/* 
		 * In case of 0, we need to check if it was returned because no index is associated with the 
		 * value and a default int (0) was returned as a result, or if the index is actually 0.
		 */
		if (index > 0 || elementToIndex.containsKey(value)) { 
			return index;
		} else {
			return -1;
		}
	}

	@Override
	public long get(final int index) {
		final long value = indexToElement.get(index);
		
		if (elementToIndex.containsKey(value)) {
			return value;
		} else {
			throw new IllegalArgumentException("Element at index " + index + " has been removed.");
		}
	}
}
