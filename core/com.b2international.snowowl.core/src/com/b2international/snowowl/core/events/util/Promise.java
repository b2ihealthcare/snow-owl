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
package com.b2international.snowowl.core.events.util;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import com.b2international.commons.collections.Procedure;
import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.util.concurrent.AbstractFuture;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * @since 4.1
 * @param <T>
 *            - the type of the return value
 */
public final class Promise<T> extends AbstractFuture<T> {

	/**
	 * @param func - the function to wrap into a {@link Promise}
	 * @return
	 * @since 4.6
	 */
	@Beta
	public static <T> Promise<T> wrap(final Callable<T> func) {
		final ListeningExecutorService executor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(1));
		final ListenableFuture<T> submit = executor.submit(func);
		executor.shutdown();
		return wrap(submit); 
	}
	
	/**
	 * Define what to do when the promise becomes resolved.
	 * 
	 * @param then
	 * @return
	 */
	public final Promise<T> fail(final Procedure<Throwable> fail) {
		Futures.addCallback(this, new FutureCallback<T>() {

			@Override
			public void onSuccess(T result) {
			}

			@Override
			public void onFailure(Throwable t) {
				fail.apply(t);
			}

		});
		return null;
	}

	/**
	 * Define what to do when the promise becomes rejected.
	 * 
	 * @param fail
	 * @return
	 */
	public final <U> Promise<U> then(final Function<T, U> func) {
		final Promise<U> transformed = new Promise<>();
		Futures.addCallback(this, new FutureCallback<T>() {
			@Override
			public void onSuccess(T result) {
				try {
					transformed.resolve(func.apply(result));
				} catch (Throwable t) {
					onFailure(t);
				}
			}

			@Override
			public void onFailure(Throwable t) {
				transformed.reject(t);
			}
		});
		return transformed;
	}

	/**
	 * Resolves the promise by sending the given result object to all then listeners.
	 * 
	 * @param t
	 *            - the resolution of this promise
	 */
	public final void resolve(T t) {
		set(t);
	}

	/**
	 * Rejects the promise by sending the {@link Throwable} to all failure listeners.
	 * 
	 * @param throwable
	 */
	public final void reject(Throwable throwable) {
		setException(throwable);
	}

	public static Promise<List<Object>> all(Promise<?>...promises) {
		return Promise.wrap(Futures.allAsList(promises));
	}
	
	private static final <T> Promise<T> wrap(ListenableFuture<T> future) {
		final Promise<T> promise = new Promise<>();
		Futures.addCallback(future, new FutureCallback<T>() {
			@Override
			public void onSuccess(T result) {
				promise.resolve(result);
			}
			@Override
			public void onFailure(Throwable t) {
				promise.reject(t);
			}
		});
		return promise;
	}
	
	public static final <T> Promise<T> immediate(T value) {
		final Promise<T> promise = new Promise<>();
		promise.resolve(value);
		return promise;
	}
	
}
