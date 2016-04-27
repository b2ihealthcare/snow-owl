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
package com.b2international.commons.collect;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.NoSuchElementException;

import com.b2international.collections.PrimitiveLists;
import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.bytes.ByteCollection;
import com.b2international.collections.bytes.ByteIterator;
import com.b2international.collections.bytes.ByteList;
import com.b2international.collections.bytes.ByteSet;

/**
 * Utility class for {@link ByteCollection}s.
 */
public class ByteCollections {

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
		
		final ByteCollection copy = unfiltered instanceof ByteSet ? PrimitiveSets.newByteOpenHashSet(unfiltered) : PrimitiveLists.newByteArrayList(unfiltered);
		for (final ByteIterator itr = copy.iterator(); itr.hasNext(); /* */) {
			final byte input = itr.next();
			if (!predicate.apply(input)) {
				itr.remove();
			}
		} 
		return copy;
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