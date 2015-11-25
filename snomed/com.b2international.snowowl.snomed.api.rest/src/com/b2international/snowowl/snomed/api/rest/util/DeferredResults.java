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
package com.b2international.snowowl.snomed.api.rest.util;

import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import com.b2international.commons.collections.Procedure;
import com.b2international.snowowl.core.events.util.Promise;

/**
 * @since 4.5
 */
public class DeferredResults {

	private DeferredResults() {
	}

	/**
	 * Wraps a {@link Promise} into Spring's {@link DeferredResult}.
	 * 
	 * @param promise
	 *            - the promise to wrap
	 * @return - the {@link DeferredResult}
	 */
	public static <T> DeferredResult<T> wrap(Promise<T> promise) {
		final DeferredResult<T> result = new DeferredResult<>();
		promise.then(new Procedure<T>() {
			@Override
			protected void doApply(T input) {
				result.setResult(input);
			}
		}).fail(new Procedure<Throwable>() {
			@Override
			protected void doApply(Throwable err) {
				result.setErrorResult(err);
			}
		});
		return result;
	}

	/**
	 * Wraps a {@link Promise} into Spring's {@link DeferredResult} and resolve it with the given {@link ResponseEntity response}.
	 * 
	 * @param promise
	 *            - the promise to wait for
	 * @param response
	 *            - the response to send back when the promise has been resolved
	 * @return
	 */
	public static <T extends ResponseEntity<B>, B, C> DeferredResult<T> wrap(Promise<C> promise, final T response) {
		final DeferredResult<T> result = new DeferredResult<>();
		promise.then(new Procedure<C>() {
			@Override
			protected void doApply(C input) {
				result.setResult(response);
			}
		}).fail(new Procedure<Throwable>() {
			@Override
			protected void doApply(Throwable err) {
				result.setErrorResult(err);
			}
		});
		return result;
	}

}
