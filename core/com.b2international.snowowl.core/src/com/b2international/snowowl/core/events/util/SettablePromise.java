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

import com.b2international.commons.collections.Procedure;
import com.google.common.base.Function;
import com.google.common.util.concurrent.AbstractFuture;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;

/**
 * @since 4.1
 */
class SettablePromise<T> extends AbstractFuture<T> implements Promise<T> {

	@Override
	public Promise<T> fail(final Procedure<Throwable> fail) {
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

	@Override
	public <U> Promise<U> then(final Function<T, U> func) {
		final SettablePromise<U> transformed = new SettablePromise<>();
		Futures.addCallback(this, new FutureCallback<T>() {
			@Override
			public void onSuccess(T result) {
				try {
					transformed.set(func.apply(result));
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
	protected void resolve(T t) {
		set(t);
	}

	/**
	 * Rejects the promise by sending the {@link Throwable} to all failure listeners.
	 * 
	 * @param throwable
	 */
	protected void reject(Throwable throwable) {
		setException(throwable);
	}
}
