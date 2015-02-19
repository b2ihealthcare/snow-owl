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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.collections.ArrayIterator;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;

/**
 * Contains additional utility methods related to arrays.
 *
 * @see Arrays
 */
public abstract class Arrays2 {

	public static final Object[] EMPTY_ARRAY = new Object[0];
	
	/**
	 * Non-lazily computes a copy of the unfiltered array where all elements are applicable to the given predicate. The result array size is trimmed
	 * to fit matching members only.
	 * <p>
	 * If the given source array is empty, it is returned unchanged. If it is not empty, but no elements match the supplied predicate, a new empty
	 * array will be returned.
	 *
	 * @param unfiltered the source array (may not be {@code null})
	 * @param predicate the filtering predicate (may not be {@code null})
	 * @return the filtered array
	 */
	public static <T> T[] filter(final T[] unfiltered, final Predicate<? super T> predicate) {
		checkNotNull(unfiltered, "unfiltered");
		checkNotNull(predicate, "predicate");

		if (0 == unfiltered.length) {
			return unfiltered;
		}

		final Collection<T> wrapper = Arrays.asList(unfiltered);
		final Collection<T> filteredCollection = Collections2.filter(wrapper, predicate);
		@SuppressWarnings("unchecked")
		final Class<T> componentType = (Class<T>) unfiltered.getClass().getComponentType();
		return Iterables.toArray(filteredCollection, componentType);
	}

	/**
	 * Duplicates the specified 2D matrix or jagged array.
	 *
	 * @param original the array to copy (may be {@code null})
	 * @return the copied array, or {@code null}
	 */
	public static int[][] copy(final int[][] original) {

		if (CompareUtils.isEmpty(original)) {
	        return null;
	    }

	    final int[][] copy = new int[original.length][];
	    for (int i = 0; i < original.length; i++) {
	        copy[i] = Arrays.copyOf(original[i], original[i].length);
	    }

	    return copy;
	}

	/**
	 * Creates an array of the given arguments.
	 */
	public static <T> T[] of(final T... t) {
		return t;
	} 
	
	/**
	 * Returns with an iterator on the given array.
	 */
	public static <T> Iterator<T> iterator(final T[] array) {
		return new ArrayIterator<T>(Preconditions.checkNotNull(array));
	}
	
	
	private Arrays2() {
		// Prevent instantiation
	}
}