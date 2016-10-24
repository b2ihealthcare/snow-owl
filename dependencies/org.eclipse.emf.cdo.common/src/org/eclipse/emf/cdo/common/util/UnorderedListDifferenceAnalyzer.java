/**
 * Copyright (c) 2007-2011 IBM Corporation and others.
 * All rights reserved.  This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   IBM - Initial API and implementation
 *   B2i Healthcare - Adapted from ListDifferenceAnalyzer
 */
package org.eclipse.emf.cdo.common.util;

import java.util.BitSet;

import org.eclipse.emf.cdo.common.revision.CDOList;

/**
 * Abstract class implementing the methods required to compute differences
 * between unordered lists.
 */
public class UnorderedListDifferenceAnalyzer {
	
	public void createListChanges(CDOList oldList, CDOList newList) {
		// Keep track of the list sizes.
		//
		int oldListSize = oldList.size();
		int newListSize = newList.size();

		BitSet foundInNewList = new BitSet(newListSize);

		// Iterate over the old list.
		// We'll remove unmatched items as we proceed.
		// Also keep track of which entry in the sources at which to start,
		// so we can skip over all the already matched values at the start of
		// the list.
		//
		for (int i = 0, start = 0; i < oldListSize;) {
			// Get the value at that the index.
			//
			Object oldValue = oldList.get(i);

			// Mark it as one that needs to be removed until we find a match.
			//
			boolean remove = true;

			// Keep track when all slots in the new list have been consumed.
			//
			boolean allSlotsMatched = true;

			// Look for a match for the old value in the new list.
			//
			LOOP: for (int j = start; j < newListSize; ++j) {
				// If the tracked entry is uninitialized...
				//
				boolean source = foundInNewList.get(j);
				if (!source) {
					// Get the new value at the index and compare it to the old
					// value.
					//
					Object newValue = newList.get(j);
					if (equal(oldValue, newValue)) {
						// If they're equal, indicate that the new value at the
						// index j matches the old value at index i.
						//
						foundInNewList.set(j);

						// If this index was the start, increment the start.
						//
						if (start == j) {
							++start;
						}

						// The value is matched so don't remove it when exiting
						// the loop.
						//
						remove = false;
						break LOOP;
					}
					// If all slots might be matched, but we just hit one that
					// wasn't...
					//
					else if (allSlotsMatched) {
						// Make that the starting slot and make sure no
						// subsequent slot is marked as the starting slot.
						//
						start = j;
						allSlotsMatched = false;
					}
				}
			}

			// If we're done the loop without finding a match...
			//
			if (remove) {
				// Remove the old value thereby reducing the size of the list.
				//
				createRemoveListChange(oldList, oldValue, i);
				--oldListSize;
			} else {
				// Proceed with the next old value.
				//
				++i;
			}
		}

		// If there are objects to add.
		//
		for (int j = foundInNewList.nextClearBit(0); j >= 0 && j < newListSize; j = foundInNewList.nextClearBit(j + 1)) {
			createAddListChange(oldList, newList.get(j), oldListSize);
			++oldListSize;
		}
	}

	/**
	 * Used by {@link #createListChanges(CDOList, CDOList)} to decide whether the
	 * old value is considered equal to the new value.
	 */
	protected boolean equal(Object oldValue, Object newValue) {
		return oldValue == null ? newValue == null : oldValue == newValue || oldValue.equals(newValue);
	}

	/**
	 * Convenience method added to allow subclasses to modify the default
	 * implementation for the scenario in which an element was added to the
	 * monitored list.
	 */
	protected void createAddListChange(CDOList oldList, Object newObject, int index) {
		oldList.add(index, newObject);
	}

	/**
	 * Convenience method added to allow subclasses to modify the default
	 * implementation for the scenario in which an element was removed from the
	 * monitored list.
	 */
	protected void createRemoveListChange(CDOList oldList, Object oldObject, int index) {
		oldList.remove(index);
	}
}
