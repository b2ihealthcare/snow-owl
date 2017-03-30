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
import static com.google.common.collect.ImmutableSet.copyOf;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.difference;
import static com.google.common.collect.Sets.intersection;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import com.google.common.base.Equivalence;
import com.google.common.base.Equivalence.Wrapper;
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
	
	/**
	 * Returns with the {@link SetDifference difference} of the two sets.
	 * @param left the left set.
	 * @param right the other set.
	 * @return the difference.
	 */
	public static <E> SetDifference<E> compare(final Set<E> left, final Set<E> right) {
		return compare(checkNotNull(left, "left"),  checkNotNull(right, "right"), DefaultEquivalence.<E>getDefaultEquivalence());
	}
	
	/**
	 * Returns with the {@link SetDifference difference} of the two sets using the given {@link Equivalence equivalence} 
	 * among the set elements. 
	 * @param left the left set.
	 * @param right the other set.
	 * @param equivalence equivalence for comparing set elements between each others. 
	 * @return the difference.
	 */
	public static <E> SetDifference<E> compare(final Set<E> left, final Set<E> right, final Equivalence<E> equivalence) {
		
		checkNotNull(left, "left");
		checkNotNull(right, "right");
		checkNotNull(equivalence, "equivalence");
		
		final Function<Wrapper<E>, E> unwrapFunction = new Function<Wrapper<E>, E>() {
			@Override public E apply(final Wrapper<E> wrapper) {
				return wrapper.get();
			}
		};
		
		final Function<E, Wrapper<E>> wrapFunction = new Function<E, Wrapper<E>>() {
			@Override public Wrapper<E> apply(final E element) {
				return equivalence.wrap(element);
			}
		};

		final Set<Wrapper<E>> leftCopy = copyOf(transform(left, wrapFunction));
		final Set<Wrapper<E>> rightCopy = copyOf(transform(right, wrapFunction));
		
		//TODO check identical equality
		//TODO consider empty left and/or right
		
		final AtomicReference<Set<Wrapper<E>>> leftDiff = new AtomicReference<>();
		final AtomicReference<Set<Wrapper<E>>> rightDiff = new AtomicReference<>();
		final AtomicReference<Set<Wrapper<E>>> intersection = new AtomicReference<>();
		
		final CountDownLatch latch = new CountDownLatch(3);
		
		new Thread(new Runnable() {
			public void run() {
				try {
					leftDiff.set(difference(leftCopy, rightCopy));
				} finally {
					latch.countDown();
				}
			}
		}, "Calculate-set-left-diff-thread").start();
		
		new Thread(new Runnable() {
			public void run() {
				try {
					rightDiff.set(difference(rightCopy, leftCopy));
				} finally {
					latch.countDown();
				}
			}
		}, "Calculate-set-right-diff-thread").start();;
		
		new Thread(new Runnable() {
			public void run() {
				try {
					intersection.set(intersection(leftCopy, rightCopy));
				} finally {
					latch.countDown();
				}
			}
		}, "Calculate-set-intersection-thread").start();
		
		try {
			latch.await(15L, TimeUnit.MINUTES);
		} catch (final InterruptedException e) {
			throw new RuntimeException("Failed to calculate set difference.", e);
		}
		
		final Function<AtomicReference<Set<Wrapper<E>>>, Set<E>> toSetFunction = new Function<AtomicReference<Set<Wrapper<E>>>, Set<E>>() {
			@Override public Set<E> apply(final AtomicReference<Set<Wrapper<E>>> input) {
				return newHashSet(transform(input.get(), unwrapFunction));
			}
		};
		
		return new SetDifferenceImpl<E>(
				toSetFunction.apply(leftDiff), 
				toSetFunction.apply(rightDiff),
				toSetFunction.apply(intersection));
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
}