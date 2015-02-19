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
/**
 * 
 */
package com.b2international.snowowl.semanticengine.simpleast.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Generic superclass of collection equality testers. 
 * Subclasses should implement {@link CollectionComparator#itemsEqual(T, T)}.
 * 
 */
public abstract class CollectionComparator<T> {
	
	public static final class CollectionDiff<T> {
		private final Collection<T> addedItems;
		private final Collection<T> removedItems;
		
		public CollectionDiff(Collection<T> addedItems, Collection<T> removedItems) {
			this.addedItems = new ArrayList<T>(addedItems);
			this.removedItems = new ArrayList<T>(removedItems);
		}
		
		public Collection<T> getAddedItems() {
			return addedItems;
		}

		public Collection<T> getRemovedItems() {
			return removedItems;
		}

		public boolean isEmpty() {
			return getAddedItems().isEmpty() && getRemovedItems().isEmpty();
		}
		
		@Override
		public String toString() {
			StringBuilder stringBuilder = new StringBuilder();
			
			if (!getAddedItems().isEmpty()) {
				stringBuilder.append("Added: ");
				for (Iterator<T> addedItemIterator = getAddedItems().iterator(); addedItemIterator.hasNext();) {
					T item = addedItemIterator.next();
					stringBuilder.append(item.toString());
					if (addedItemIterator.hasNext())
						stringBuilder.append(',');
				}
				stringBuilder.append("; ");
			}
			
			if (!getRemovedItems().isEmpty()) {
				stringBuilder.append("Removed: ");
				for (Iterator<T> removedItemIterator = getRemovedItems().iterator(); removedItemIterator.hasNext();) {
					T item = removedItemIterator.next();
					stringBuilder.append(item.toString());
					if (removedItemIterator.hasNext())
						stringBuilder.append(',');
				}
			}
			
			return stringBuilder.toString();
		}
	}
	
	protected CollectionDiff<T> diff = new CollectionDiff<T>(new ArrayList<T>(), new ArrayList<T>());

	public CollectionDiff<T> getDiff(Collection<T> expected, Collection<T> actual) {
		Collection<T> addedItems = new ArrayList<T>();
		Collection<T> removedItems = new ArrayList<T>();
		
		for (T actualItem : actual) {
			boolean found = false; 
			for (T expectedItem : expected) {
				if (itemsEqual(expectedItem, actualItem)) {
					found = true;
					break;
				}
			}
			
			if (!found)
				addedItems.add(actualItem);
		}
		
		for (T expectedItem : expected) {
			boolean found = false; 
			for (T actualItem : actual) {
				if (itemsEqual(actualItem, expectedItem)) {
					found = true;
					break;
				}
			}
			
			if (!found)
				removedItems.add(expectedItem);
		}
		
		diff.getAddedItems().addAll(addedItems);
		diff.getRemovedItems().addAll(removedItems);
		return diff;
	}
	
	/**
	 * @param expected
	 * @param actual
	 * @return true if the two collections are found to be equal, false otherwise
	 */
	public boolean equal(Collection<T> expected, Collection<T> actual) {
		CollectionDiff<T> collectionDiff = getDiff(expected, actual);
		return collectionDiff.isEmpty();
	}
	
	/**
	 * @param expected
	 * @param actual
	 * @return true if the two objects are found to be equal, false otherwise
	 */
	abstract protected boolean itemsEqual(T expected, T actual);
}