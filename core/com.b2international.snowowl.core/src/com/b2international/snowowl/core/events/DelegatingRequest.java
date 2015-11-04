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
package com.b2international.snowowl.core.events;

import static com.google.common.base.Preconditions.checkNotNull;

import com.b2international.commons.ClassUtils;
import com.b2international.snowowl.core.ServiceProvider;

/**
 * @since 4.5
 * @param <C>
 *            - the required context type for this {@link Request}
 * @param <T>
 *            - the required context type of the next {@link Request}
 * @param <B>
 *            - the type of the result
 */
public abstract class DelegatingRequest<C extends ServiceProvider, T extends ServiceProvider, B> extends BaseRequest<C, B> {

	private Request<T, B> next;

	protected DelegatingRequest(Request<T, B> next) {
		this.next = checkNotNull(next, "next");
	}

	/**
	 * Execute the wrapped next {@link Request}.
	 * 
	 * @param context
	 *            - a context suitable for the next {@link Request}
	 * @return
	 */
	protected final B next(T context) {
		return next.execute(context);
	}
	
	protected final Request<T, B> next() {
		return next;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected final Class<B> getReturnType() {
		return ClassUtils.checkAndCast(next, BaseRequest.class).getReturnType();
	}
	
}
