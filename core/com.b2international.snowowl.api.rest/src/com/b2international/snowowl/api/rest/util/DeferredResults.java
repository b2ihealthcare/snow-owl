/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.api.rest.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import com.b2international.commons.collections.Procedure;
import com.b2international.snowowl.core.events.util.Promise;

/**
 * @since 4.5
 */
public class DeferredResults {

	private static final Logger LOG = LoggerFactory.getLogger(DeferredResults.class);
	
	private static final class ResultSetter<T> extends Procedure<T> {
		private final DeferredResult<T> deferredResult;
	
		private ResultSetter(DeferredResult<T> deferredResult) {
			this.deferredResult = deferredResult;
		}
	
		@Override
		protected void doApply(T input) {
			if (deferredResult.isSetOrExpired()) {
				LOG.warn("Deferred result is already set or expired, could not deliver result {}.", input);
			} else { 
				deferredResult.setResult(input);
			}
		}
	}
	
	private static final class PredefinedValueSetter<C, T> extends Procedure<C> {
		private final DeferredResult<T> deferredResult;
		private final T value;
		
		private PredefinedValueSetter(DeferredResult<T> deferredResult, T value) {
			this.deferredResult = deferredResult;
			this.value = value;
		}
		
		@Override
		protected void doApply(C input) {
			if (deferredResult.isSetOrExpired()) {
				LOG.warn("Deferred result is already set or expired, could not deliver value {} for result {}.", value, input);
			} else { 
				deferredResult.setResult(value);
			}
		}
	}

	private static final class ThrowableSetter extends Procedure<Throwable> {
		private final DeferredResult<?> deferredResult;

		private ThrowableSetter(DeferredResult<?> deferredResult) {
			this.deferredResult = deferredResult;
		}

		@Override
		protected void doApply(Throwable err) {
			if (deferredResult.isSetOrExpired()) {
				LOG.warn("Deferred result is already set or expired, could not deliver Throwable.", err);
			} else {
				deferredResult.setErrorResult(err);
			}
		}
	}

	private DeferredResults() {
		throw new UnsupportedOperationException("This class is not supposed to be instantiated.");
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
		promise.then(new ResultSetter<T>(result)).fail(new ThrowableSetter(result));
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
		promise.then(new PredefinedValueSetter<C, T>(result, response)).fail(new ThrowableSetter(result));
		return result;
	}

}
