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
package com.b2international.commons.concurrent;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.b2international.commons.collections.Procedure;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterables;

/**
 * Collection of collection related concurrent utility methods.
 * 
 */
public class ConcurrentCollectionUtils {
	
	/**
	 * Shared {@link ExecutorService} instance to limit the number of created threads.
	 */
	private static final ExecutorService SHARED_EXECUTOR_SERVICE = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	
	private ConcurrentCollectionUtils() {} // prevent instantiation
	
	private static final class PredicateCallable<T> implements Callable<T> {
		private final Predicate<T> predicate;
		private final T value;

		private PredicateCallable(final Predicate<T> predicate, final T value) {
			this.predicate = predicate;
			this.value = value;
		}

		@Override
		public T call() throws Exception {
			return predicate.apply(value) ? value : null;
		}
	}
	
	private static final class FunctionCallable<F, T> implements Callable<T> {
		private final Function<F, T> function;
		private final F value;

		private FunctionCallable(final Function<F, T> function, final F value) {
			this.function = function;
			this.value = value;
		}
		
		@Override
		public T call() throws Exception {
			return function.apply(value);
		}
	}

	/**
	 * Applies the specified predicate concurrently to each item of the source iterator, 
	 * retaining the original order of the elements. The underlying {@link Iterator} 
	 * implementation reads ahead in the source iterator and distributes the task of 
	 * evaluating the predicate to a thread pool, therefore the specified predicate 
	 * must be thread safe.
	 * 
	 * @param <T>
	 * @param sourceIterator the unfiltered iterator to use
	 * @param predicate the predicate to use as a filter
	 * @return the filtered iterator
	 */
	public static <T> Iterator<T> filter(final Iterator<T> sourceIterator, final Predicate<T> predicate) {
		Preconditions.checkNotNull(sourceIterator, "Source iterator cannot be null.");
		Preconditions.checkNotNull(predicate, "Filter predicate cannot be null.");

		return new AbstractIterator<T>() {
			private static final int READ_AHEAD_FACTOR = 2;
			
			private final int availableProcessors = Runtime.getRuntime().availableProcessors();
			private final int prefetchBatchSize = availableProcessors * READ_AHEAD_FACTOR;

			private final Queue<Future<T>> futureQueue = new ConcurrentLinkedQueue<Future<T>>();
			
			@Override
			protected T computeNext() {
				return nextFilteredElement();
			}

			private T nextFilteredElement() {
				try {
					while(readAhead() || !futureQueue.isEmpty()) {
						final Future<T> future = futureQueue.poll();
						final T futureValue = future.get();
						if (futureValue != null)
							return futureValue;
					}
					return endOfData();
				} catch (final InterruptedException e) {
					throw new RuntimeException("Concurrent filtered iterator interrupted.", e);
				} catch (final ExecutionException e) {
					throw new RuntimeException("Error in concurrent filtered iterator.", e);
				}
			}
			
			private boolean readAhead() {
				final int futureQueueSize = futureQueue.size();
				
				for (int i=0; i< prefetchBatchSize - futureQueueSize; i++) {
					if (sourceIterator.hasNext()) {
						final T sourceElement = sourceIterator.next();
						final Future<T> future = SHARED_EXECUTOR_SERVICE.submit(new PredicateCallable<T>(predicate, sourceElement));
						futureQueue.add(future);
					} else {
						return false;
					}
				}
				
				return true;
			}
		};
	}
	
	/**
	 * Applies the specified function concurrently to each item of the source iterator in a non sequential manner. 
	 * The underlying {@link Iterator} implementation reads ahead in the source iterator and distributes the task of 
	 * evaluating the predicate to a thread pool, therefore the specified function 
	 * must be thread safe.
	 * Non-sequentiality means that original ordering of the elements are not enforced, as different items in the threadpool take different amount of time to complete. 
	 * This implementation takes the first element from the threadpool that has finished processing, instead of blocking execution until the next-in-line element finishes processing.
	 * 
	 * @param <T>
	 * @param sourceIterator the source iterator to use
	 * @param function the function to use
	 * @return the iterator with transformed elements
	 */
	public static <F, T> Iterator<T> nonSequentialTransform(final Iterator<F> sourceIterator, final Function<F, T> function) {
		Preconditions.checkNotNull(sourceIterator, "Source iterator must not be null.");
		Preconditions.checkNotNull(function, "Function must not be null.");
		
		return new AbstractIterator<T>() {
			private static final int READ_AHEAD_FACTOR = 2;
			
			private final int availableProcessors = Runtime.getRuntime().availableProcessors();
			private final int prefetchBatchSize = availableProcessors * READ_AHEAD_FACTOR;
			private final Predicate<Future<T>> doneFuturePredicate = new DoneFuturePredicate<T>();
			
			private final Queue<Future<T>> futureQueue = new ConcurrentLinkedQueue<Future<T>>();
			
			@Override
			protected T computeNext() {
				return nextFilteredElement();
			}
			
			private T nextFilteredElement() {
				try {
					while(readAhead() || !futureQueue.isEmpty()) {
						Future<T> future = Iterables.find(futureQueue, doneFuturePredicate, futureQueue.peek());
						final T futureValue = future.get();
						futureQueue.remove(future);
						return futureValue;
					}
					return endOfData();
				} catch (final InterruptedException e) {
					throw new RuntimeException("Concurrent transformed iterator interrupted.", e);
				} catch (final ExecutionException e) {
					throw new RuntimeException("Error in concurrent transformed iterator. Message: " + e.getMessage() + " \n rootCause: " + Throwables.getRootCause(e).getMessage() , e);
				}
			}
			
			private boolean readAhead() {
				final int futureQueueSize = futureQueue.size();
				
				for (int i=0; i< prefetchBatchSize - futureQueueSize; i++) {
					if (sourceIterator.hasNext()) {
						final F sourceElement = sourceIterator.next();
						final Future<T> future = SHARED_EXECUTOR_SERVICE.submit(new FunctionCallable<F, T>(function, sourceElement));
						futureQueue.add(future);
					} else {
						return false;
					}
				}
				
				return true;
			}
		};
	}
	/**
	 * Applies the specified function concurrently to each item of the source iterator, 
	 * retaining the original order of the elements. The underlying {@link Iterator} 
	 * implementation reads ahead in the source iterator and distributes the task of 
	 * evaluating the predicate to a thread pool, therefore the specified function 
	 * must be thread safe.
	 * 
	 * @param <T>
	 * @param sourceIterator the source iterator to use
	 * @param function the function to use
	 * @return the iterator with transformed elements
	 */
	public static <F, T> Iterator<T> transform(final Iterator<F> sourceIterator, final Function<F, T> function) {
		return transform(sourceIterator, Runtime.getRuntime().availableProcessors(), function);
	}
	/**
	 * Applies the specified function concurrently to each item of the source iterator, 
	 * retaining the original order of the elements. The underlying {@link Iterator} 
	 * implementation reads ahead in the source iterator and distributes the task of 
	 * evaluating the predicate to a thread pool, therefore the specified function 
	 * must be thread safe.
	 * 
	 * @param <T>
	 * @param sourceIterator the source iterator to use
	 * @param function the function to use
	 * @return the iterator with transformed elements
	 */
	public static <F, T> Iterator<T> transform(final Iterator<F> sourceIterator, final int concurrencyLevel, final Function<F, T> function) {
		Preconditions.checkNotNull(sourceIterator, "Source iterator must not be null.");
		Preconditions.checkNotNull(function, "Function must not be null.");
		
		return new AbstractIterator<T>() {
			private static final int READ_AHEAD_FACTOR = 2;
			
			private final int prefetchBatchSize = concurrencyLevel * READ_AHEAD_FACTOR;
			
			private final Queue<Future<T>> futureQueue = new ConcurrentLinkedQueue<Future<T>>();
			
			@Override
			protected T computeNext() {
				return nextFilteredElement();
			}
			
			private T nextFilteredElement() {
				try {
					while(readAhead() || !futureQueue.isEmpty()) {
						final Future<T> future = futureQueue.poll();
						final T futureValue = future.get();
						return futureValue;
					}
					return endOfData();
				} catch (final InterruptedException e) {
					throw new RuntimeException("Concurrent transformed iterator interrupted.", e);
				} catch (final ExecutionException e) {
					throw new RuntimeException("Error in concurrent transformed iterator.", e);
				}
			}
			
			private boolean readAhead() {
				final int futureQueueSize = futureQueue.size();
				
				for (int i=0; i< prefetchBatchSize - futureQueueSize; i++) {
					if (sourceIterator.hasNext()) {
						final F sourceElement = sourceIterator.next();
						final Future<T> future = SHARED_EXECUTOR_SERVICE.submit(new FunctionCallable<F, T>(function, sourceElement));
						futureQueue.add(future);
					} else {
						return false;
					}
				}
				
				return true;
			}
		};
	}

	/**
	 * Concurrently applies the closure on each element of the given iterable. This method returns to the caller when the given closure is
	 * applied to all element, till then synchronously blocks. The number of executor threads is specified via
	 * {@link Runtime#availableProcessors()}.
	 * <p>Calling this method is similar to GPars' execution
	 * <pre>
	 * GParsPool.withPool $availableProcessoros, { it ->
	 *     //custom logic would go there
	 * }
	 * </pre>
	 * @param subjects the subject iterable. 
	 * @param nThreads - the number of threads to use for concurrency
	 * @param closure the procedure to apply on each element.
	 */
	public static <T> void forEach(final Iterable<? extends T> subjects, final Procedure<? super T> closure) {
		forEach(subjects, Runtime.getRuntime().availableProcessors(), closure);
	}
	
	/**
	 * Concurrently applies the closure on each element of the given iterable. This method returns to the caller when the given closure is
	 * applied to all element, till then synchronously blocks. The number of executor threads is specified via
	 * the given nThreads parameter.
	 * <p>Calling this method is similar to GPars' execution
	 * <pre>
	 * GParsPool.withPool $availableProcessoros, { it ->
	 *     //custom logic would go there
	 * }
	 * </pre>
	 * @param subjects the subject iterable. 
	 * @param nThreads - the number of threads to use for concurrency
	 * @param closure the procedure to apply on each element.
	 */
	public static <T> void forEach(final Iterable<? extends T> subjects, final int nThreads, final Procedure<? super T> closure) {
		checkNotNull(subjects, "Subject iterables argument cannot be null.");
		checkNotNull(closure, "Closure function argument cannot be null.");
		checkArgument(nThreads >= 1, "Number of threads cannot be zero or less.");
		
		ExecutorService service = null;
		
		try {
			
			service = Executors.newFixedThreadPool(nThreads);
			
			@SuppressWarnings("unchecked") final Future<Void>[] $ = new Future[Iterables.size(subjects)];
			
			int i = 0;
			for (final T subject : subjects) {
				
				$[i++] = service.submit(new Callable<Void>() {
					@Override public Void call() throws Exception {
						closure.apply(subject);
						return com.b2international.commons.Void.VOID;
					}
					
				});
				
			}
			
			if (null != service) {
				
				service.shutdown();
				service = null;
				
			}
			
			for (final Future<Void> f : $) {
				f.get();
			}
			
			
		} catch (final InterruptedException e) {
			
			Thread.interrupted();
			throw new RuntimeException(e);
			
		} catch (final ExecutionException e) {
			
			throw new RuntimeException(e);
			
		} finally {
			
			if (null != service) {
				
				service.shutdown();
				service = null;
				
			}
			
		}
		
	}
	
	public static void main(final String[] args) {
		
//		forEach(Range.closed(1, 1000000).asSet(DiscreteDomain<Integer>), new Procedure<Integer>() {
//			@Override protected void _apply(final Integer input) {
//				System.out.println(Thread.currentThread() + "\t" + input);
//			}
//		});
		
	}
	
}