/*
 * Copyright 2011-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.exceptions.ApiException;
import com.b2international.commons.exceptions.RequestTimeoutException;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.google.common.util.concurrent.*;

import io.reactivex.Observable;
import io.reactivex.Observer;

/**
 * @since 4.1
 * @param <T>
 *            - the type of the return value
 */
public final class Promise<T> extends Observable<T> {

	final SettableFuture<Response<T>> delegate = SettableFuture.create();
	
	// optionally attachable headers to pass down the promise chain to all listeners
	private Map<String, String> headers;
	
	/**
	 * @return the response body when this Promise becomes resolved.
	 * @since 4.6
	 */
	public T getSync() {
		return getSyncResponse().getBody();
	}
	
	/**
	 * @param timeout
	 * @param unit
	 * @return the response body when this Promise becomes resolved or throw an error if the specified timeout expires.
	 * @since 4.6
	 */
	public T getSync(final long timeout, final TimeUnit unit) {
		return getSyncResponse(timeout, unit).getBody();
	}
	
	/**
	 * @return the response when this Promise becomes resolved.
	 * @since 8.10
	 */
	public Response<T> getSyncResponse() {
		try {
			return delegate.get();
		} catch (final InterruptedException e) {
			throw new SnowowlRuntimeException(e);
		} catch (final ExecutionException e) {
			final Throwable cause = e.getCause();
			if (cause instanceof ApiException) {
				throw (ApiException) cause;
			} else if (cause instanceof RuntimeException) {
				throw (RuntimeException) cause;
			} else {
				throw new SnowowlRuntimeException(cause);
			}
		}
	}
	
	/**
	 * @param timeout
	 * @param unit
	 * @return the response when this Promise becomes resolved or throw an error if the specified timeout expires.
	 * @since 8.10
	 */
	public Response<T> getSyncResponse(final long timeout, final TimeUnit unit) {
		try {
			return delegate.get(timeout, unit);
		} catch (final TimeoutException e) {
			throw new RequestTimeoutException("Request timeout", e);
		} catch (final InterruptedException e) {
			throw new SnowowlRuntimeException(e);
		} catch (final ExecutionException e) {
			final Throwable cause = e.getCause();
			if (cause instanceof ApiException) {
				throw (ApiException) cause;
			} if (cause instanceof RuntimeException) {
				throw (RuntimeException) cause;
			} else {
				throw new SnowowlRuntimeException(cause);
			}
		}
	}
	
	/**
	 * Define what to do when the promise becomes rejected.
	 * 
	 * @param fail
	 * @return
	 */
	public final Promise<T> fail(final Function<Throwable, T> fail) {
		final Promise<T> promise = new Promise<>();
		Futures.addCallback(delegate, new FutureCallback<Response<T>>() {

			@Override
			public void onSuccess(final Response<T> result) {
				promise.resolve(result.getBody());
			}

			@Override
			public void onFailure(final Throwable t) {
				try {
					promise.resolve(fail.apply(t));
				} catch (final Throwable e) {
					promise.reject(e);
				}
			}

		}, MoreExecutors.directExecutor());
		return promise;
	}
	
	/**
	 * Define what to do when the promise becomes rejected. The given {@link Function} should return another {@link Promise} which will be used to evaluate this {@link Promise}.
	 * @param fail
	 * @return
	 */
	public final Promise<T> failWith(final Function<Throwable, Promise<T>> fail) {
		final Promise<T> promise = new Promise<>();
		Futures.addCallback(delegate, new FutureCallback<Response<T>>() {

			@Override
			public void onSuccess(final Response<T> result) {
				promise.resolve(result.getBody());
			}

			@Override
			public void onFailure(final Throwable t) {
				try {
					promise.resolveWith(fail.apply(t));
				} catch (final Throwable e) {
					promise.reject(e);
				}
			}

		}, MoreExecutors.directExecutor());
		return promise;
	}

	/**
	 * Define what to do when the promise becomes resolved.
	 * Transforms this promise type T into a promise of type U
	 * with the function passed in.  The new promise will be available at the time
	 * when this promise is available.
	 * 
	 * @param then
	 * @return promise with type T
	 */
	public final <U> Promise<U> then(final Function<T, U> then) {
		final Promise<U> transformed = new Promise<>();
		Futures.addCallback(delegate, new FutureCallback<Response<T>>() {
			@Override
			public void onSuccess(final Response<T> result) {
				try {
					transformed.resolve(then.apply(result.getBody()));
				} catch (final Throwable t) {
					onFailure(t);
				}
			}

			@Override
			public void onFailure(final Throwable t) {
				transformed.reject(t);
			}
		}, MoreExecutors.directExecutor());
		return transformed;
	}
	
	/**
	 * Define what to do when the promise becomes resolved.
	 * Transforms this promise type T into and replaces it to a new promise of type U
	 * with the function passed in.  The new promise will be available at the earliest when the original 
	 * promise is ready.
	 * 
	 * @param then
	 * @return promise with type T
	 */
	public final <U> Promise<U> thenWith(final Function<T, Promise<U>> then) {
		final Promise<U> transformed = new Promise<>();
		Futures.addCallback(delegate, new FutureCallback<Response<T>>() {
			@Override
			public void onSuccess(final Response<T> result) {
				try {
					transformed.resolveWith(then.apply(result.getBody()));
				} catch (final Throwable t) {
					onFailure(t);
				}
			}

			@Override
			public void onFailure(final Throwable t) {
				transformed.reject(t);
			}
		}, MoreExecutors.directExecutor());
		return transformed;
	}
	
	public final <U> Promise<U> thenRespond(final Function<Response<T>, Response<U>> then) {
		final Promise<U> transformed = new Promise<>();
		Futures.addCallback(delegate, new FutureCallback<Response<T>>() {
			@Override
			public void onSuccess(final Response<T> result) {
				try {
					transformed.resolveResponse(then.apply(result));
				} catch (final Throwable t) {
					onFailure(t);
				}
			}

			@Override
			public void onFailure(final Throwable t) {
				transformed.reject(t);
			}
		}, MoreExecutors.directExecutor());
		return transformed;
	}
	
	public final <U> Promise<U> thenRespondWith(final Function<Response<T>, Promise<U>> then) {
		final Promise<U> transformed = new Promise<>();
		Futures.addCallback(delegate, new FutureCallback<Response<T>>() {
			@Override
			public void onSuccess(final Response<T> result) {
				try {
					transformed.resolveWith(then.apply(result));
				} catch (final Throwable t) {
					onFailure(t);
				}
			}

			@Override
			public void onFailure(final Throwable t) {
				transformed.reject(t);
			}
		}, MoreExecutors.directExecutor());
		return transformed;
	}

	/**
	 * Resolves the promise by sending the given result object to all then listeners.
	 * 
	 * @param result
	 *            - the resolution of this promise
	 */
	public final void resolve(T result) {
		resolve(result, Collections.emptyMap());
	}
	
	/**
	 * Resolves the promise by sending the given result object to all then listeners along with a set of headers.
	 * 
	 * @param result
	 *            - the resolution of this promise
	 * @param headers
	 */
	public final void resolve(T result, final Map<String, String> headers) {
		resolveResponse(Response.of(result, headers));
	}
	
	public final void resolveResponse(Response<T> response) {
		if (CompareUtils.isEmpty(response.getHeaders()) && !CompareUtils.isEmpty(this.headers)) {
			delegate.set(Response.of(response.getBody(), this.headers));
		} else {
			delegate.set(response);
		}
	}
	
	final void resolveWith(final Promise<T> t) {
		t
			.thenRespond(response -> {
				resolveResponse(response);
				return null;
			})
			.fail(error -> {
				reject(error);
				return null;
			});
	}
	
	/**
	 * Rejects the promise by sending the {@link Throwable} to all failure listeners.
	 * 
	 * @param throwable
	 */
	public final void reject(final Throwable throwable) {
		delegate.setException(throwable);
	}

	/**
	 * @param func - the function to wrap into a {@link Promise}
	 * @return
	 * @since 4.6
	 */
	public static <T> Promise<T> wrap(final Callable<T> func) {
		final ListeningExecutorService executor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(1));
		final ListenableFuture<T> submit = executor.submit(func);
		executor.shutdown();
		return wrap(submit); 
	}
	
	/**
	 * @param promises
	 * @return
	 * @since 4.6
	 */
	public static Promise<List<Object>> all(final Collection<? extends Promise<?>> promises) {
		return Promise.wrap(Futures.allAsList(promises.stream().map(p -> p.delegate).collect(Collectors.toList())))
				.then(responses -> {
					return responses.stream().map(Response::getBody).collect(Collectors.toList());
				});
	}
	
	/**
	 * @param promises
	 * @return
	 * @since 4.6
	 */
	public static Promise<List<Object>> all(final Promise<?>...promises) {
		return Promise.wrap(Futures.allAsList(Stream.of(promises).map(p -> p.delegate).collect(Collectors.toList())))
				.then(responses -> {
					return responses.stream().map(Response::getBody).collect(Collectors.toList());
				});
	}
	
	/**
	 * Wraps a {@link ListenableFuture} into a {@link Promise} to easily add listener callbacks to it.
	 * @param future - the future to wrap
	 * @return
	 * @since 6.0
	 */
	public static final <T> Promise<T> wrap(final ListenableFuture<T> future) {
		final Promise<T> promise = new Promise<>();
		Futures.addCallback(future, new FutureCallback<T>() {
			@Override
			public void onSuccess(final T result) {
				promise.resolve(result);
			}
			@Override
			public void onFailure(final Throwable t) {
				promise.reject(t);
			}
		}, MoreExecutors.directExecutor());
		return promise;
	}
	
	/**
	 * Provides a promise object with type T that is available immediately.
	 * 
	 * @param value
	 * @return
	 * @since 4.6
	 */
	public static final <T> Promise<T> immediate(final T value) {
		final Promise<T> promise = new Promise<>();
		promise.resolve(value);
		return promise;
	}
	
	/**
	 * Provides a promise object with type T that is available immediately. It also allows specifying additional headers to be associated with the {@link Promise}.
	 * 
	 * @param value
	 * @return
	 * @since 8.10
	 */
	public static final <T> Promise<T> immediateResponse(final T value, final Map<String, String> headers) {
		final Promise<T> promise = new Promise<>();
		promise.resolve(value, headers);
		return promise;
	}
	
	/**
	 * Returns a {@link Promise} that will always fail with the given {@link Throwable}.
	 * @param throwable
	 * @return
	 */
	public static final <T> Promise<T> fail(final Throwable throwable) {
		final Promise<T> promise = new Promise<>();
		promise.reject(throwable);
		return promise;
	}

	@Override
	protected void subscribeActual(final Observer<? super T> subscriber) {
		then(result -> {
			subscriber.onNext(result);
			subscriber.onComplete();
			return null;
		}).fail(throwable -> {
			subscriber.onError(throwable);
			subscriber.onComplete();
			return null;
		});
	}

	public boolean isDone() {
		return delegate.isDone();
	}

	public boolean isCancelled() {
		return delegate.isCancelled();
	}

}
