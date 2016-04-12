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
package com.b2international.commons.pcj;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkPositionIndexes;

import java.util.NoSuchElementException;

import com.b2international.collections.ByteCollection;
import com.b2international.collections.ByteIterator;
import com.b2international.collections.list.ByteList;
import com.b2international.collections.list.ByteListIterator;
import com.b2international.collections.set.ByteSet;

/**
 * Utility class for {@link ByteCollection}s.
 */
public class ByteCollections {

	private static final int REVERSE_THRESHOLD = 18;
	
	/**Returns with {@code true} if the collection argument is {@code null} or empty.*/
	public static boolean isEmpty(final ByteCollection collection) {
		return null == collection || collection.isEmpty();
	}

	/**Returns with the last element of the collection argument.*/
	public static byte getLast(final ByteCollection collection) {
		
		if (checkNotNull(collection, "collection").isEmpty()) {
			throw new NoSuchElementException();
		}
		
		if (collection instanceof ByteList) {
			return ((ByteList) collection).get(collection.size() - 1); 
		}
		
		final ByteIterator itr = collection.iterator();
		while (true) {
			final byte $ = itr.next(); 
			if (!itr.hasNext()) {
				return $;
			}
		}
		
	}
	
	/**
	 * Returns the elements of {@code unfiltered} that satisfies the given {@link BytePredicate predicate}. 
	 * @param unfiltered the collection to filter.
	 * @param predicate the predicate to apply on each element of the {@code unfiltered}.
	 * @return a modifiable filtered collection based on the {@code unfiltered} one.
	 */
	public static ByteCollection filter(final ByteCollection unfiltered, final BytePredicate predicate) {
		
		checkNotNull(unfiltered, "unfiltered");
		checkNotNull(predicate, "predicate");
		
		final ByteCollection copy = unfiltered instanceof ByteSet ? PrimitiveCollections.newByteOpenHashSet(unfiltered) : PrimitiveCollections.newByteArrayList(unfiltered);
		for (final ByteIterator itr = copy.iterator(); itr.hasNext(); /* */) {
			final byte input = itr.next();
			if (!predicate.apply(input)) {
				itr.remove();
			}
		} 
		return copy;
	}
	
	/**
	 * Sugar for creating a sublist from a given list.<br>Same as:
	 * <pre>
	 * subListOf(list, fromIndex, list.size())
	 * </pre>
	 * @param list the list to shrink.
	 * @param fromIndex the inclusive starting index.
	 * @return a sub list of the given list argument.
	 */
	public static ByteList subListOf(final ByteList list, final int fromIndex) {
		return subListOf(checkNotNull(list, "list"), fromIndex, list.size());
	}
	
	/**
	 * Returns a with a new byte list from the given list between the specified 
	 * <tt>fromIndex</tt>, inclusive, and <tt>toIndex</tt>, exclusive.<p>(If
     * <tt>fromIndex</tt> and <tt>toIndex</tt> are equal, the returned list is
     * empty.)
	 * @param list the list to truncate.
	 * @param fromIndex the inclusive from index.
	 * @param toIndex the exclusive to index.
	 * @return a sub list of the given list between the given indexes.
	 */
	public static ByteList subListOf(final ByteList list, final int fromIndex, final int toIndex) {
		checkPositionIndexes(fromIndex, toIndex, checkNotNull(list, "list").size());
		final byte[] array = list.toArray();
		final byte[] copy = new byte[toIndex - fromIndex];
		System.arraycopy(array, fromIndex, copy, 0, toIndex - fromIndex);
		return PrimitiveCollections.newByteArrayList(copy);
	}
	
	/**
     * Reverses the order of the elements in the specified byte list.<p>
     * This method runs in linear time.
     * @param  list the list whose elements are to be reversed.
     */
	public static void reverse(final ByteList list) {
		final int size = checkNotNull(list, "list").size();
        if (size < REVERSE_THRESHOLD) {
			for (int i = 0, mid = size >> 1, j = size - 1; i < mid; i++, j--)
				swap(list, i, j);
        } else {
			final ByteListIterator fwd = list.listIterator();
			final ByteListIterator rev = list.listIterator(size);
			for (int i = 0, mid = list.size() >> 1; i < mid; i++) {
				final byte tmp = fwd.next();
				fwd.set(rev.previous());
				rev.set(tmp);
			}
        }
	} 
	
    /**
     * Swaps the elements at the specified positions in the specified byte list.
     * (If the specified positions are equal, invoking this method leaves
     * the list unchanged.)
     *
     * @param list The list in which to swap elements.
     * @param i the index of one element to be swapped.
     * @param j the index of the other element to be swapped.
     * @throws IndexOutOfBoundsException if either <tt>i</tt> or <tt>j</tt>
     *         is out of range (i &lt; 0 || i &gt;= list.size()
     *         || j &lt; 0 || j &gt;= list.size()).
     */
    public static void swap(final ByteList list, final int i, final int j) {
        final ByteList _list = checkNotNull(list, "list");
        _list.set(i, _list.set(j, _list.get(i)));
    }
	
	/**
	 * Evaluates the procedure argument on each element of the given collection.
	 * @param collection the collection which each element will be evaluated against the procedure.
	 * @param procedure the procedure to apply on each element of the given collection of primitive long elements.
	 */
	public static void forEach(final ByteCollection collection, final ByteCollectionProcedure procedure) {
		
		checkNotNull(procedure, "procedure");
		checkNotNull(collection, "collection");

		for (final ByteIterator itr = collection.iterator(); itr.hasNext(); /**/) {
			procedure.apply(itr.next());
		}
	}
	
	/**
	 * Procedure working on primitive bytes.
	 */
	public static interface ByteCollectionProcedure {
		
		/**
		 * Applies the current function to the primitive byte {@code input}.
		 * @param input the input primitive byte to apply on the current procedure.
		 */
		void apply(final byte input);
	}
	
	/**
	 * Determines a {@code true} or {@code false} value for a given primitive byte input.
	 */
	public static interface BytePredicate {

		/**
		 * Returns the result of applying this predicate to {@code input} primitive byte.
		 */
		boolean apply(final byte input);

		/**Predicate always producing {@code true} result.*/
		public static final BytePredicate ALL_PREDICATE = new BytePredicate() {
			@Override public boolean apply(final byte input) {
				return true;
			}
		};
		
	}
	
}