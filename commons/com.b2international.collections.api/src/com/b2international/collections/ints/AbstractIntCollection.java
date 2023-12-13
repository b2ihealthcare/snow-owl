/*
 * Copyright 2011-2016 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.collections.ints;

/**
 * @since 4.7
 */
public abstract class AbstractIntCollection implements IntCollection {

	@Override
	public boolean addAll(IntCollection collection) {
		boolean changed = false;
		
		final IntIterator itr = collection.iterator();
		while (itr.hasNext()) {
			changed |= add(itr.next());
		}
		
		return changed;
	}

	@Override
	public boolean contains(int value) {
		final IntIterator itr = iterator();
		while (itr.hasNext()) {
			int current = itr.next();
			if (current == value) {
				return true;
			}
		}
	
		return false;
	}

	@Override
	public boolean containsAll(IntCollection collection) {
		final IntIterator itr;
		final IntCollection other;
		
		if (size() < collection.size()) {
			itr = this.iterator();
			other = collection;
		} else {
			itr = collection.iterator();
			other = this;
		}
		
		while (itr.hasNext()) {
			if (!other.contains(itr.next())) {
				return false;
			}
		}
		
		return true;
	}

	@Override
	public boolean remove(int value) {
		boolean changed = false;
		
		final IntIterator itr = iterator();
		while (itr.hasNext()) {
			int current = itr.next();
			if (current == value) {
				itr.remove();
				changed = true;
			}
		}
		
		return changed;
	}

	@Override
	public boolean removeAll(IntCollection collection) {
		return retainOrRemoveAll(collection, true);
	}

	@Override
	public boolean retainAll(IntCollection collection) {
		return retainOrRemoveAll(collection, true);
	}

	private boolean retainOrRemoveAll(IntCollection collection, boolean retain) {
		boolean changed = false;
		
		final IntIterator itr = iterator();
		while (itr.hasNext()) {
			int current = itr.next();
			if (collection.contains(current) ^ retain) {
				itr.remove();
				changed = true;
			}
		}
		
		return changed;
	}

	@Override
	public int[] toArray() {
		final int[] result = new int[size()];
		final IntIterator itr = iterator();
		int i = 0;
		
		while (itr.hasNext()) {
			result[i++] = itr.next();
		}
		
		return result;
	}

	@Override
	public void clear() {
		final IntIterator itr = iterator();
		while (itr.hasNext()) {
			itr.next();
			itr.remove();
		}
	}

	@Override
	public boolean isEmpty() {
		final IntIterator itr = iterator();
		return !itr.hasNext();
	}

	@Override
	public int size() {
		int size = 0;
		final IntIterator itr = iterator();
		
		while (itr.hasNext()) {
			itr.next();
			size++;
		}
		
		return size;
	}
	
	@Override
	public String toString() {
		IntIterator it = iterator();
        if (! it.hasNext())
            return "[]";

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (;;) {
            int e = it.next();
            sb.append(e);
            if (! it.hasNext())
                return sb.append(']').toString();
            sb.append(',').append(' ');
        }
	}
	
	public static int hashCode(IntCollection intCollection) {
		int result = 1;
		IntIterator itr = intCollection.iterator();
	    
	    while (itr.hasNext()) {
	    	int element = itr.next();
	        result = 31 * result + element;
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
	public static boolean elementsEqual(IntIterator iterator1, IntIterator iterator2) {
		while (iterator1.hasNext()) {
			if (!iterator2.hasNext()) {
				return false;
			}
			int b1 = iterator1.next();
			int b2 = iterator2.next();
			if (b1 != b2) {
				return false;
			}
		}
		return !iterator2.hasNext();
	}

}
