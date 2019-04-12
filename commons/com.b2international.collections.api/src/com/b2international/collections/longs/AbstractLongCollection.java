/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.collections.longs;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @since 4.7
 */
public abstract class AbstractLongCollection implements LongCollection {

	@Override
	public boolean addAll(LongCollection collection) {
		boolean changed = false;
		
		final LongIterator itr = collection.iterator();
		while (itr.hasNext()) {
			changed |= add(itr.next());
		}
		
		return changed;
	}

	@Override
	public boolean contains(long value) {
		final LongIterator itr = iterator();
		while (itr.hasNext()) {
			long current = itr.next();
			if (current == value) {
				return true;
			}
		}
	
		return false;
	}

	@Override
	public boolean containsAll(LongCollection collection) {
		if (size() < collection.size()) {
			return false;
		}
		
		final LongIterator itr = collection.iterator();

		while (itr.hasNext()) {
			if (!contains(itr.next())) {
				return false;
			}
		}
		
		return true;
	}

	@Override
	public boolean remove(long value) {
		final LongIterator itr = iterator();
		while (itr.hasNext()) {
			long current = itr.next();
			if (current == value) {
				itr.remove();
				return true;
			}
		}
		
		return false;
	}

	@Override
	public boolean removeAll(LongCollection collection) {
		return retainOrRemoveAll(collection, false);
	}

	@Override
	public boolean retainAll(LongCollection collection) {
		return retainOrRemoveAll(collection, true);
	}

	private boolean retainOrRemoveAll(LongCollection collection, boolean retain) {
		checkNotNull(collection);
		
		boolean changed = false;
		
		final LongIterator itr = iterator();
		while (itr.hasNext()) {
			long current = itr.next();
			
			/* 
			 * Remove "current" from this collection if:
			 * - the other collection contains "current", in "retain" mode
			 * - the other collection does not contain "current", in "remove" mode 
			 */
			if (collection.contains(current) != retain) {
				itr.remove();
				changed = true;
			}
		}
		
		return changed;
	}

	@Override
	public long[] toArray() {
		final long[] result = new long[size()];
		final LongIterator itr = iterator();
		int i = 0;
		
		while (itr.hasNext()) {
			result[i++] = itr.next();
		}
		
		return result;
	}

	@Override
	public void clear() {
		final LongIterator itr = iterator();
		while (itr.hasNext()) {
			itr.next();
			itr.remove();
		}
	}

	@Override
	public boolean isEmpty() {
		final LongIterator itr = iterator();
		return !itr.hasNext();
	}

	@Override
	public int size() {
		int size = 0;
		final LongIterator itr = iterator();
		
		while (itr.hasNext()) {
			itr.next();
			size++;
		}
		
		return size;
	}
	
	@Override
	public String toString() {
		LongIterator it = iterator();
        if (! it.hasNext())
            return "[]";

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (;;) {
            long e = it.next();
            sb.append(e);
            if (! it.hasNext())
                return sb.append(']').toString();
            sb.append(',').append(' ');
        }
	}
	
	public static int hashCode(LongCollection longCollection) {
		int result = 1;
		LongIterator itr = longCollection.iterator();
	    
	    while (itr.hasNext()) {
	    	long element = itr.next();
            int elementHash = (int)(element ^ (element >>> 32));
            result = 31 * result + elementHash;
	    }
	
	    return result;
	}
	
	/**
	 * Determines whether two iterators contain equal elements in the same order.
	 * More specifically, this method returns {@code true} if {@code iterator1}
	 * and {@code iterator2} contain the same number of elements and every element
	 * of {@code iterator1} is equal to the corresponding element of
	 * {@code iterator2}.
	 *
	 * <p>Note that this will modify the supplied iterators, since they will have
	 * been advanced some number of elements forward.
	 */
	public static boolean elementsEqual(LongIterator iterator1, LongIterator iterator2) {
		while (iterator1.hasNext()) {
			if (!iterator2.hasNext()) {
				return false;
			}
			long b1 = iterator1.next();
			long b2 = iterator2.next();
			if (b1 != b2) {
				return false;
			}
		}
		return !iterator2.hasNext();
	}

}
