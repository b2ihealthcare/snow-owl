/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.events;

import static com.google.common.base.Preconditions.checkNotNull;

import com.b2international.snowowl.core.ServiceProvider;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * Delegates execution of a {@link Request}. Can be used to decorate/wrap the incoming context within another context to meet the requirements of the
 * next {@link Request} in the execution chain, or you can use it to decorate the execution of the request chain with additional functionality like
 * logging, execution time measurement, authorization, authentication, etc.
 * 
 * @since 4.5
 * @param <C>
 *            - the required context type for this {@link Request}
 * @param <T>
 *            - the required context type of the delegate {@link Request}
 * @param <R>
 *            - the type of the result
 */
public abstract class DelegatingRequest<C extends ServiceProvider, T extends ServiceProvider, R> implements Request<C, R> {

	private static final long serialVersionUID = 1L;
	
	private final Request<T, R> next;

	protected DelegatingRequest(Request<T, R> next) {
		this.next = checkNotNull(next, "next");
	}

	/**
	 * Execute the wrapped next {@link Request}.
	 * 
	 * @param context
	 *            - a context suitable for the next {@link Request}
	 * @return
	 */
	protected final R next(T context) {
		return next.execute(context);
	}
	
	@Override
	@JsonIgnore
	public String getType() {
		return getClass().getSimpleName();
	}
	
	@JsonProperty
	@JsonUnwrapped
	public final Request<T, R> next() {
		return next;
	}

	@Override
	public final Class<R> getReturnType() {
		return next.getReturnType();
	}

}
