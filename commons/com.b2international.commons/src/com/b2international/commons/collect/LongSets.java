/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.AbstractLongIterator;
import com.b2international.collections.longs.LongCollection;
import com.b2international.collections.longs.LongCollections;
import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongSet;
import com.b2international.commons.CompareUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Utility class for {@link LongSet}s.
 */
public class LongSets {

	/**
	 * Returns with the difference of two sets. The
	 * returned set contains all elements that are contained by {@code set1} and
	 * not contained by {@code set2}. {@code set2} may also contain elements not
	 * present in {@code set1}; these are simply ignored. The iteration order of
	 * the returned set matches that of {@code set1}.
	 * @param set1 Cannot be {@code null}.
	 * @param set2 Cannot be {@code null}.
	 * @return a view of the difference of the two sets.
	 */
	public static LongSet difference(final LongSet set1, final LongSet set2) {
		
		checkNotNull(set1, "The set1 argument cannot be null.");
		checkNotNull(set2, "The set2 argument cannot be null.");

		if (CompareUtils.isEmpty(set1)) { //nothing to do
			return LongCollections.emptySet();
		}

		final LongSet result = PrimitiveSets.newLongOpenHashSet(set1);
		result.removeAll(set2);
		return result;
	}

   /**
    * Returns with the intersection of two sets. The
    * returned set contains all elements that are contained by both backing sets.
    * The iteration order of the returned set matches that of {@code set1}.
    *
    * <p><b>Note:</b> The returned view performs slightly better when {@code
    * set1} is the smaller of the two sets. If you have reason to believe one of
    * your sets will generally be smaller than the other, pass it first.
    * 
    * @param set1 Cannot be {@code null}.
    * @param set2 Cannot be {@code null}.
    * @return a view of the intersection of the two sets.
    */
	public static LongSet intersection(final LongSet set1, final LongSet set2) {
		
		checkNotNull(set1, "The set1 argument cannot be null.");
		checkNotNull(set2, "The set2 argument cannot be null.");

		if (CompareUtils.isEmpty(set1)) { //nothing to do
			return LongCollections.emptySet();
		}

		final LongSet result = PrimitiveSets.newLongOpenHashSet(set1);
		result.retainAll(set2);
		return result;
	}
	
	/**
	 * Returns with an array containing all long values as a string.
	 * @param collection the collection of primitive longs.
	 * @return an array of strings.
	 */
	public static String[] toStringArray(final LongCollection collection) {
		checkNotNull(collection, "Long collection argument cannot be null.");
		final String[] ids = new String[collection.size()];
		int i = 0;
		for (final LongIterator iterator = collection.iterator(); iterator.hasNext(); /*nothing*/) {
			ids[i++] = Long.toString(iterator.next());
		}
		return ids;
	}
	
	/**
	 * Returns with an {@link List} containing all long values as a string.
	 * @param collection the collection of primitive longs.
	 * @return an list of strings.
	 */
	public static List<String> toStringList(final LongCollection collection) {
		checkNotNull(collection, "Long collection argument cannot be null.");
		return Lists.newArrayList(toStringArray(collection));
	}
	
	/**
	 * Returns with an {@link Set} containing all long values as a string.
	 * @param collection the collection of primitive longs.
	 * @return a set of strings.
	 */
	public static Set<String> toStringSet(final LongCollection collection) {
		checkNotNull(collection, "Long collection argument cannot be null.");
		return newHashSet(toStringArray(collection));
	}

	/**
	 * Returns with an iterator instance that applies the {@code function} to each element of the
	 * given {@code from} iterable. 
	 * @param from the iterable to transform each element.
	 * @param function the function that is evaluated for each element of the {@code from} iterable.
	 * @return an the iterator.
	 */
	public static <F> LongIterator transform(final Iterable<F> from, final LongFunction<? super F> function) {
		
		checkNotNull(from, "Iterable argument cannot be null.");
		checkNotNull(function, "Function argument cannot be null.");
		
		return new AbstractLongIterator() {
			
			private final Iterator<F> delegate = from.iterator();
			private final LongFunction<?  super F> _function = function;
			
			@Override protected long computeNext() {
				
				while (delegate.hasNext()) {

					return _function.apply(delegate.next());

				}

				return endOfData();
				
			}
		};
		
	}
	
	/**
	 * Transforms the given {@link LongCollection} into a collection of object based on the {@link InverseLongFunction function} argument.
	 * @param fromCollection the collection of primitive long values to transform.
	 * @param function the function for the transformation.
	 * @return the transformed collection of long values.
	 */
	public static <T> Collection<T> transform(final LongCollection fromCollection, final InverseLongFunction<? extends T> function) {
		checkNotNull(fromCollection, "fromCollection");
		checkNotNull(function, "function");
		
		@SuppressWarnings("unchecked")
		final Collection<T> toCollection = (Collection<T>) (fromCollection instanceof LongSet ? Sets.newHashSetWithExpectedSize(fromCollection.size()) : Lists.newArrayListWithExpectedSize(fromCollection.size()));
		
		for (final LongIterator itr = fromCollection.iterator(); itr.hasNext();  /**/) {
			toCollection.add(function.apply(itr.next()));
		}

		return toCollection;
	}

	private LongSets() { /*suppress instantiation*/
	}
	
	/**
	 * Transformation from an object to a primitive long value.
	 *
	 * @param <F> type of the function input
	 * @see InverseLongFunction
	 */
	public static interface LongFunction<F> {
		
		/**
		 * Applies the current function to the object of type {@code F} and returns with 
		 * a primitive long value.
		 * @param input the input object to transform into a primitive long value.
		 * @return the transformed value.
		 */
		long apply(final F input);
		
	}
	
	/**
	 * Transformation from a primitive long value into an object.
	 *
	 * @param <T> type of the function output
	 * @see LongFunction
	 */
	public static interface InverseLongFunction<T> {
		
		/**
		 * Applies the current function to the primitive long value and returns with 
		 * an object of type {@code T}
		 * @param input the primitive long value to transform into an object.
		 * @return the transformed value.
		 */
		T apply(final long input);
		
	}
	
}