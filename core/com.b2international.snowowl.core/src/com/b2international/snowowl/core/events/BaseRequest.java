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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.exceptions.RequestTimeoutException;
import com.b2international.snowowl.eventbus.IEventBus;

/**
 * @since 4.5
 *
 * @param <C>
 * @param <B>
 */
public abstract class BaseRequest<C extends ServiceProvider, B> extends BaseEvent implements Request<C, B> {

	@Override
	public final Promise<B> execute(IEventBus bus) {
		return send(bus, getReturnType());
	}

	@Override
	public final B executeSync(IEventBus bus) {
		try {
			return execute(bus).get();
		} catch (InterruptedException e) {
			throw new SnowowlRuntimeException(e);
		} catch (ExecutionException e) {
			final Throwable cause = e.getCause();
			if (cause instanceof RuntimeException) {
				throw (RuntimeException) cause;
			}
			throw new SnowowlRuntimeException(cause);
		}
	}

	@Override
	public final B executeSync(IEventBus bus, long timeout) {
		try {
			return execute(bus).get(timeout, TimeUnit.MILLISECONDS);
		} catch (TimeoutException e) {
			throw new RequestTimeoutException(e);
		} catch (InterruptedException e) {
			throw new SnowowlRuntimeException(e);
		} catch (ExecutionException e) {
			final Throwable cause = e.getCause();
			if (cause instanceof RuntimeException) {
				throw (RuntimeException) cause;
			}
			throw new SnowowlRuntimeException(cause);
		}
	}

	@Override
	protected String getAddress() {
		throw new UnsupportedOperationException("This request "+ getClass().getName() +" cannot be sent over the bus on its own, wrap it into a RepositoryRequest");
	}
	
	/**
	 * Returns the class of the actual return type.
	 * 
	 * @return
	 */
	protected abstract Class<B> getReturnType();

}
