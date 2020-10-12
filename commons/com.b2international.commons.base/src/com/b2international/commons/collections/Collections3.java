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
package com.b2international.commons.collections;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;

/**
 * Yet another collection utility class.
 */
public abstract class Collections3 {

	/**Executes the given procedure on every element of the iterable. More like a closure.*/
	public static <F> void forEach(final Iterable<? extends F> fromIterable, final Procedure<? super F> f) {
		
		Iterables.transform(
				Preconditions.checkNotNull(fromIterable, "Iterable argument cannot be null."), 
				Preconditions.checkNotNull(f, "Function argument cannot be null")).toString();
		
	}

	/**Applies the given procedure on each element of the specified iterator.*/
	public static <F> void forEach(final Iterator<? extends F> fromIterator, final Procedure<? super F> f) {
		
		Preconditions.checkNotNull(fromIterator, "Iterator argument cannot be null.");
		Preconditions.checkNotNull(f, "Function argument cannot be null");
		
		final Iterator<? extends F> copy = Iterators.unmodifiableIterator(fromIterator);
		
		while (copy.hasNext()) {
			f.apply(copy.next());
		}
		
	}
	
	/**
	 * Adds each element of the iterable to the map argument after applying the unique key function. This function allows duplicates.
	 */
	public static <K, V> void putAllOverrideDuplicates(final Map<K, V> to, final Iterable<? extends V> toAdd, final Function<? super V, K> f) {
		for (final V item : Preconditions.checkNotNull(toAdd)) {
			Preconditions.checkNotNull(to).put(Preconditions.checkNotNull(f).apply(Preconditions.checkNotNull(item)), item);
		}
	}

	/**
	 * Adds each element of the iterable to the map argument after applying the given unique key index function. Duplicates are prohibited.
	 */
	public static <K, V> void putAllWithNoDuplicates(final Map<K, V> to, final Iterable<? extends V> toAdd, final Function<? super V, K> f) {
		Preconditions.checkNotNull(to).putAll(Maps.uniqueIndex(Preconditions.checkNotNull(toAdd), Preconditions.checkNotNull(f)));
	}
	
	/**Wraps the given iterable into a set. If the runtime type of the given argument was a 
	 *{@code Set} this method simply returns with the argument, otherwise a new {@code Set} instance will be created.*/
	@SuppressWarnings("unchecked")
	public static <K> Set<K> toSet(final Iterable<? super K> iterable) {
		return (Set<K>) (iterable instanceof Set ? iterable : newHashSet(iterable));
	}
	
	/**Wraps the given iterable into a list. If the runtime type of the given argument was a 
	 *{@code List} this method simply returns with the argument, otherwise a new {@code List} instance will be created.*/
	@SuppressWarnings("unchecked")
	public static <K> List<K> toList(final Iterable<? super K> iterable) {
		return (List<K>) (iterable instanceof List ? iterable : newArrayList(iterable));
	}

	/**
	 * Sorts the given unsorted set argument with the comparator and returns with a new sorted set instance. 
	 * @param unsorted the unsorted set.
	 * @param comparator the comparator used for the sorting.
	 * @return the new sorted set instance. 
	 */
	public static <E> Set<E> sort(final Set<? extends E> unsorted, final Comparator<E> comparator) {
		final TreeSet<E> sortedSet = new TreeSet<E>(checkNotNull(comparator, "comparator"));
		sortedSet.addAll(checkNotNull(unsorted, "unsorted"));
		return sortedSet;
	}
	
	public static <T> Set<T> toImmutableSet(Iterable<T> values) {
		return values != null ? ImmutableSet.copyOf(values) : Collections.emptySet();
	}
	
	public static <T> List<T> toImmutableList(Iterable<T> values) {
		return values != null ? ImmutableList.copyOf(values) : Collections.emptyList();
	}

	public static <T> Set<T> toImmutableSet(T[] values) {
		return values != null ? ImmutableSet.copyOf(values) : Collections.emptySet();
	}
	
	public static <T> List<T> toImmutableList(T[] values) {
		return values != null ? ImmutableList.copyOf(values) : Collections.emptyList();
	}
	
	private Collections3() { /*suppress instantiation*/ }

	/**
	 * Returns <code>true</code> if the two {@link Collection}s are equal, ignoring element order and count. This method is essentially the same as
	 * {@link Set#equals(Object)}.
	 * 
	 * @param <T>
	 * @param left
	 * @param right
	 * @return
	 * @see Set#equals(Object)
	 */
	public static <T> boolean equals(Collection<T> left, Collection<T> right) {
		if (left == right) return true;
		if (left == null || right == null) return false;
		if (left.size() != right.size()) return false;
		return left.containsAll(right);
	}
}