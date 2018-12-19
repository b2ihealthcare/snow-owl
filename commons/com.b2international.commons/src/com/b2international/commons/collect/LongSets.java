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
package com.b2international.commons.collect;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Sets.newHashSetWithExpectedSize;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.AbstractLongIterator;
import com.b2international.collections.longs.LongCollection;
import com.b2international.collections.longs.LongCollections;
import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongSet;
import com.b2international.commons.CompareUtils;
import com.b2international.commons.StopWatch;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.primitives.Longs;

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
	 * Returns the number of elements remaining in {@link LongIterator iterator}. 
	 * The iterator will be left exhausted: its {@link LongIterator#hasNext()} method will return {@code false}.
	 * @param itr the iterator.
 	 * @return the number of remaining elements.
	 */
	private static int size(final LongIterator itr) {
		checkNotNull(itr, "Long iterator argument cannot be null.");
		int $ = 0;
		while (itr.hasNext()) {
			itr.next();
			$++;
		}
		return $;
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
	 * Returns with a list containing all long values.
	 * @param collection the collection of primitive longs
	 * @return a list of long values.
	 */
	public static List<Long> toList(final LongCollection collection) {
		checkNotNull(collection, "Long collection argument cannot be null.");
		collection.trimToSize();
		return Longs.asList(collection.toArray());
	}
	
	/**
	 * Returns with a set containing all long values.
	 * @param collection the collection of primitive longs
	 * @return a set of long values.
	 */
	public static Set<Long> toSet(final LongCollection collection) {
		checkNotNull(collection, "Long collection argument cannot be null.");
		collection.trimToSize();
		final Set<Long> $ = newHashSetWithExpectedSize(collection.size());
		for (final LongIterator itr = collection.iterator(); itr.hasNext(); /* */) {
			$.add(itr.next());
		}
		return $;
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
	 * Creates and returns with a new mutable {@link LongOpenHashSet} instance containing the 
	 * elements in indeterministic order.  
	 * @param itr the elements that the new set instance should contain.
	 * @return the new set instance containing each element of the iterator without duplicates. 
	 */
	public static LongSet newLongSet(final LongIterator itr) {
		
		final LongSet $ = PrimitiveSets.newLongOpenHashSet();
		while (itr.hasNext()) {
			
			$.add(itr.next());
			
		}
		
		return $;
		
	}
	
	/**
	 * Evaluates the procedure argument on each element of the given iterator.
	 * @param iterator the iterator which elements will be evaluated against the procedure.
	 * @param procedure the procedure to apply on each element of the given iterator of primitive long numbers.
	 */
	public static void forEach(final LongIterator itr, final LongCollectionProcedure procedure) {
		checkNotNull(procedure, "procedure");
		while (itr.hasNext()) {
			procedure.apply(itr.next());
		}
	}
	
	/**
	 * Evaluates the procedure argument on each element of the given collection.
	 * @param collection the collection which each element will be evaluated against the procedure.
	 * @param procedure the procedure to apply on each element of the given collection of primitive long elements.
	 */
	public static void forEach(final LongCollection collection, final LongCollectionProcedure procedure) {
		forEach(collection.iterator(), procedure);
	}
	
	/**
	 * Just as {@link #forEach(LongIterator, LongCollectionProcedure)} but parallel evaluates the procedure argument 
	 * on each element of the given iterator.
	 * @param iterator the iterator which elements will be evaluated against the procedure.
	 * @param procedure the procedure to apply on each element of the given iterator of primitive long numbers.
	 */
	public static void parallelForEach(final LongIterator iterator, final LongCollectionProcedure procedure) {
		checkNotNull(procedure, "procedure");
		
		final LongListIteratorWrapper itr = new LongListIteratorWrapper(iterator); 
		final int nThreads = Runtime.getRuntime().availableProcessors();
		final ExecutorService service = Executors.newFixedThreadPool(nThreads);
		
		try {
			
			@SuppressWarnings("unchecked") final Future<Void>[] $ = new Future[size(itr)];
			itr.reset();
			
			final AtomicInteger i = new AtomicInteger();
			forEach(itr, new LongCollectionProcedure() {
				
				@Override
				public void apply(final long input) {
					$[i.getAndIncrement()] = service.submit(() -> {
						procedure.apply(input);
						return com.b2international.commons.Void.VOID;
					});
				}
			});
			
			for (final Future<Void> f : $) {
				f.get();
			}
			
		} catch (final InterruptedException e) {
			Thread.interrupted();
			throw new RuntimeException(e);
		} catch (final ExecutionException e) {
			throw new RuntimeException(e);
		} finally {
			service.shutdown();
		}
		
	}
	
	/**
	 * Just as {@link #forEach(LongCollection, LongCollectionProcedure)} but parallel evaluates the procedure argument 
	 * on each element of the given collection.
	 * @param collection the collection which each element will be evaluated against the procedure.
	 * @param procedure the procedure to apply on each element of the given iterator of primitive long numbers.
	 */
	public static void parallelForEach(final LongCollection collection, final LongCollectionProcedure procedure) {
		parallelForEach(collection.iterator(), procedure);
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
		final Collection<T> toCollection = (Collection<T>) (fromCollection instanceof LongSet ? newHashSet() : newArrayList());
		
		for (final LongIterator itr = fromCollection.iterator(); itr.hasNext();  /**/) {
			toCollection.add(function.apply(itr.next()));
		}

		return toCollection;
	}
	
	/**
	 * Combines the {@link LongIterator iterator} of the given collections into a single one.
	 * @param collection the first collection.
	 * @param others the other ones.
	 * @return the combined iterator.
	 */
	public static LongIterator iterator(final LongCollection collection, final LongCollection... others) {
		return concat(Iterables.transform(Lists.asList(checkNotNull(collection, "collection"), others), new Function<LongCollection, LongIterator>() {
			@Override public LongIterator apply(final LongCollection longCollection) {
				return longCollection.iterator();
			}
		}).iterator());
	}
	
	/**
	 * Concatenates the given long iterators into a single iterators.
	 * @param itr the first iterator.
	 * @param others the other ones.
	 * @return the concatenated iterators.
	 */
	public static LongIterator concat(final LongIterator itr, final LongIterator... others) {
		return concat(Lists.asList(checkNotNull(itr, "itr"), others).iterator());
	}
	
	/**
	 * Concatenates the given {@link LongIterator iterators} into a single iterators.
	 * @param iterators the iterators to combine.
	 * @return the concatenated iterators.
	 */
	public static LongIterator concat(final Iterator<LongIterator> iterators) {
		
		return new LongIterator() {
		
			private LongIterator current = LongCollections.emptyIterator();
			private LongIterator removeFrom;
			
			@Override
			public void remove() {
				checkState(removeFrom != null);
				removeFrom.remove();
				removeFrom = null;
			}
			
			@Override
			public long next() {
		        if (!hasNext()) {
		            throw new NoSuchElementException();
		          }
		          removeFrom = current;
		          return current.next();
			}
			
			@Override
			public boolean hasNext() {
				boolean currentHasNext;
				while (!(currentHasNext = checkNotNull(current).hasNext()) && iterators.hasNext()) {
					current = iterators.next();
				}
				return currentHasNext;
			}
		};
		
	}
	
	/**
	 * Returns the elements of {@code unfiltered} that satisfy the given {@link LongPredicate predicate}. 
	 * @param unfiltered the set to filter.
	 * @param predicate the predicate.
	 * @return a modifiable filtered collection based on the {@code unfiltered} one.
	 */
	public static LongSet filter(final LongSet unfiltered, final LongPredicate predicate) {
		checkNotNull(unfiltered, "unfiltered");
		checkNotNull(predicate, "predicate");
		final LongSet copy = PrimitiveSets.newLongOpenHashSet(unfiltered);
		for (final LongIterator itr = copy.iterator(); itr.hasNext(); /* */) {
			final long value = itr.next();
			if (!predicate.apply(value)) {
				itr.remove();
			}
		} 
		return copy;
	}

	private LongSets() { /*suppress instantiation*/
	}

	/**
	 * Procedure acting as a {@link LongFunction function} and providing no return value.
	 */
	public static interface LongCollectionProcedure {
		
		/**
		 * Applies the current function to the primitive long {@code input}.
		 * @param input the input primitive long to apply on the current procedure.
		 */
		void apply(final long input);
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
	
	/**
	 * Determines a {@code true} or {@code false} value for a given primitive long input.
	 */
	public static interface LongPredicate {

		/**
		 * Returns the result of applying this predicate to {@code input} primitive long.
		 */
		boolean apply(final long input);

		/**Predicate always producing {@code true} result.*/
		public static final LongPredicate ALL_PREDICATE = new LongPredicate() {
			@Override public boolean apply(final long input) {
				return true;
			}
		};
		
	}
	
	/**
	 * Predicate that evaluates to {@code true} if the primitive long being tested is a member of 
	 * any of the underlying collections.
	 * 
	 */
	/*default*/ static final class InLongPredicate implements LongPredicate {
		
		private final Iterable<? extends LongCollection> collections;

		/*default*/ InLongPredicate(final Iterable<? extends LongCollection> collections) {
			this.collections = checkNotNull(collections, "collections");
		}

		@Override
		public boolean apply(final long input) {
			for (final LongCollection collection : collections) {
				if (collection.contains(input)) {
					return true;
				}
			}				
			
			return false;
		}
		
	}
	
	/**
	 * Predicates that negates evaluation of the delegate predicate.
	 *
	 */
	/*default*/ static final class NotLongPredicate implements LongPredicate {

		private final LongPredicate predicate;

		/*default*/ NotLongPredicate(final LongPredicate predicate) {
			this.predicate = checkNotNull(predicate, "predicate");
		}
		
		@Override
		public boolean apply(final long input) {
			return !predicate.apply(input);
		}
		
		
	}

	/**
	 * Creates and returns with a new {@link LongPredicate} instance that evaluates to {@code true} if the primitive
	 * long being tested is a member of any of the underlying long collections.
	 * @param collections the collections to check against the membership.
	 * @return the predicate.
	 */
	public static LongPredicate in(final Iterable<? extends LongCollection> collections) {
		return new InLongPredicate(checkNotNull(collections, "collections"));
	}

	
	/**
	 * Creates and returns with a new {@link LongPredicate} instance that evaluates to {@code true} if the primitive
	 * long being tested is a member of any of the underlying long collections.
	 * @param collection the collection.
	 * @param others the others.
	 * @return the predicate.
	 */
	public static LongPredicate in(final LongCollection collection, final LongCollection... others) {
		return new InLongPredicate(Lists.asList(checkNotNull(collection, "collection"), checkNotNull(others, "others")));
	}
	
	/**
	 * Creates a new predicate instance that always negates the argument one.
	 * @param predicate the predicate to negate.
	 * @return a new NOT predicate.
	 */
	public static LongPredicate not(final LongPredicate predicate) {
		return new NotLongPredicate(checkNotNull(predicate, "predicate"));
	}
	
	public static void main(final String[] args) {
		
		System.out.println("**********************************************");
		System.out.println("difference");
		System.out.println("**********************************************");
		
		HashSet<Long> set1 = Sets.newHashSet();
		HashSet<Long> set2 = Sets.newHashSet();
		for (int i = 1; i < 12; i++) {
			set1.add((long) i);
		}
		
		for (int i = 5; i < 20; i++) {
			set2.add((long) i);
		}
		
		System.out.println("reference " + Sets.difference(set1, set2));
		System.out.println("reference " + Sets.difference(set2, set1));
		
		LongSet longset1 = PrimitiveSets.newLongOpenHashSet(Longs.toArray(set1));
		LongSet longset2 = PrimitiveSets.newLongOpenHashSet(Longs.toArray(set2));
		LongSet diff1 = LongSets.difference(longset1, longset2);
		LongSet diff2 = LongSets.difference(longset2, longset1);
		System.out.println("assert " + diff1);
		System.out.println("assert " + diff2);
		
		System.out.println("*******************************************");
		
		set1 = Sets.newHashSet();
		set2 = Sets.newHashSet();
		for (int i = 1; i < 3500000; i++) {
			set1.add((long) i);
		}
		
		for (int i = 500000; i < 4000000; i++) {
			set2.add((long) i);
		}
		
		long t = System.currentTimeMillis();
		
		System.out.println(Sets.difference(set1, set2).size());
		System.out.println(Sets.difference(set2, set1).size());
		
		StopWatch.time("diff sets", t);
		longset1 = PrimitiveSets.newLongOpenHashSet(Longs.toArray(set1));
		longset2 = PrimitiveSets.newLongOpenHashSet(Longs.toArray(set2));
		
		t = System.currentTimeMillis();
		diff1 = LongSets.difference(longset1, longset2);
		diff2 = LongSets.difference(longset2, longset1);
		System.out.println(diff1.size());
		System.out.println(diff2.size());
		StopWatch.time("diff longsets", t);
		
		
		System.out.println("**********************************************");
		System.out.println("intersection");
		System.out.println("**********************************************");
		
		set1 = Sets.newHashSet();
		set2 = Sets.newHashSet();
		for (int i = 1; i < 12; i++) {
			set1.add((long) i);
		}
		
		for (int i = 5; i < 20; i++) {
			set2.add((long) i);
		}
		
		System.out.println("reference " + Sets.intersection(set1, set2));
		System.out.println("reference " + Sets.intersection(set2, set1));
		
		longset1 = PrimitiveSets.newLongOpenHashSet(Longs.toArray(set1));
		longset2 = PrimitiveSets.newLongOpenHashSet(Longs.toArray(set2));
		LongSet intersection1 = LongSets.intersection(longset1, longset2);
		LongSet intersection2 = LongSets.intersection(longset2, longset1);
		System.out.println("assert " + intersection1);
		System.out.println("assser " + intersection2);
		
		System.out.println("*******************************************");
		
		set1 = Sets.newHashSet();
		set2 = Sets.newHashSet();
		for (int i = 1; i < 3500000; i++) {
			set1.add((long) i);
		}
		
		for (int i = 500000; i < 4000000; i++) {
			set2.add((long) i);
		}
		
		t = System.currentTimeMillis();
		
		System.out.println(Sets.intersection(set1, set2).size());
		System.out.println(Sets.intersection(set2, set1).size());
		
		StopWatch.time("intersection sets", t);
		longset1 = PrimitiveSets.newLongOpenHashSet(Longs.toArray(set1));
		longset2 = PrimitiveSets.newLongOpenHashSet(Longs.toArray(set2));
		
		t = System.currentTimeMillis();
		intersection1 = LongSets.intersection(longset1, longset2);
		intersection2 = LongSets.intersection(longset2, longset1);
		System.out.println(intersection1.size());
		System.out.println(intersection2.size());
		StopWatch.time("intersection longsets", t);
		
		
		
		
	}

}