/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import com.b2international.commons.CompositeClassLoader;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.eventbus.IHandler;
import com.b2international.snowowl.eventbus.IMessage;

/**
 * @since 5.0
 */
public final class AsyncRequest<R> {

	private final Request<ServiceProvider, R> request;
	
	public AsyncRequest(Request<ServiceProvider, R> request) {
		this.request = request;
	}

	/**
	 * Executes the asynchronous request using the event bus passed in.
	 * @param bus
	 * @return {@link Promise}
	 */
	public Promise<R> execute(IEventBus bus) {
		final Promise<R> promise = new Promise<>();
		final Class<R> responseType = request.getReturnType();
		final CompositeClassLoader classLoader = new CompositeClassLoader();
		classLoader.add(request.getClassLoader());
		request.getNestedRequests().stream().map(Request::getClassLoader).forEach(classLoader::add);
		request.getNestedRequests().stream().map(r -> r.getClass().getClassLoader()).forEach(classLoader::add);
		bus.send(Request.ADDRESS, request, Request.TAG, Collections.emptyMap(), new IHandler<IMessage>() {
			@Override
			public void handle(IMessage message) {
				try {
					if (message.isSucceeded()) {
						promise.resolve(message.body(responseType, classLoader), message.headers());
					} else {
						promise.reject(message.body(Throwable.class, AsyncRequest.class.getClassLoader()));
					}
				} catch (Throwable e) {
					promise.reject(e);
				}
			}
		});
		return promise;
	}
	
	/**
	 * @return the underlying request to be sent asynchronously.
	 */
	public Request<ServiceProvider, R> getRequest() {
		return request;
	}
	
	/**
	 * Execute the request and synchronously wait until it responds.
	 * @return the response
	 */
	public R get(ServiceProvider context) {
		return execute(context.service(IEventBus.class)).getSync();
	}
	
	/**
	 * Execute the request and synchronously wait until it responds or times out after the given milliseconds.
	 * @param timeout - timeout value in milliseconds
	 * @return the response
	 */
	public R get(ServiceProvider context, long timeout) {
		return get(context, timeout, TimeUnit.MILLISECONDS);
	}

	/**
	 * Execute the request and synchronously wait until it responds or times out after the given timeout config.
	 * @param timeout - timeout value
	 * @param unit - the unit for the timeout value
	 * @return the response
	 */
	private R get(ServiceProvider context, long timeout, TimeUnit unit) {
		return execute(context.service(IEventBus.class)).getSync(timeout, unit);
	}

}