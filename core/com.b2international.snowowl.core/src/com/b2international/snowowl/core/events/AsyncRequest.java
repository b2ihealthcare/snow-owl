/*
 * Copyright 2011-2024 B2i Healthcare, https://b2ihealthcare.com
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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import com.b2international.commons.exceptions.UnauthorizedException;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.identity.User;
import com.b2international.snowowl.core.jobs.JobRequests;
import com.b2international.snowowl.core.jobs.ScheduleJobRequestBuilder;
import com.b2international.snowowl.core.util.PlatformUtil;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.eventbus.IHandler;
import com.b2international.snowowl.eventbus.IMessage;
import com.google.common.collect.ImmutableMap;

/**
 * @since 5.0
 */
public final class AsyncRequest<R> {

	private final Request<ServiceProvider, R> request;
	
	private Map<String, String> requestHeaders;
	
	public AsyncRequest(Request<ServiceProvider, R> request) {
		this.request = request;
	}
	
	public AsyncRequest<R> withHeader(String headerName, String headerValue) {
		if (this.requestHeaders == null) {
			this.requestHeaders = new HashMap<>(2);
		}
		this.requestHeaders.put(headerName, headerValue);
		return this;
	}
	
	public AsyncRequest<R> withHeaders(Map<String, String> requestHeaders) {
		this.requestHeaders = requestHeaders == null ? null : ImmutableMap.copyOf(requestHeaders);
		return this;
	}

	public AsyncRequest<R> withContext(ServiceProvider context) {
		User user = context.optionalService(User.class).orElse(null);
		if (user != null) {
			return withContext(Map.of(User.class, user));	
		} else {
			return this;
		}
	}
	
	public AsyncRequest<R> withContext(Map<Class<?>, Object> context) {
		return context == null ? this : new AsyncRequest<>(new RequestWithContext<ServiceProvider, R>(request, context));
	}
	
	public Promise<R> executeAsAdmin(ServiceProvider context) {
		return withContext(Map.of(User.class, User.SYSTEM)).execute(context.service(IEventBus.class));
	}
	
	public Promise<R> executeWithContext(ServiceProvider context) {
		return withContext(context).execute(context.service(IEventBus.class));
	}
	
	/**
	 * Executes the asynchronous request using the event bus passed in.
	 * @param bus
	 * @return {@link Promise}
	 */
	public Promise<R> execute(IEventBus bus) {
		final Promise<R> promise = new Promise<>();
		final Class<R> responseType = request.getReturnType();
		final Exception callerStacktrace = PlatformUtil.isDevVersion() ? new Exception() : null; 
		bus.send(Request.ADDRESS, request, Request.TAG, requestHeaders, new IHandler<IMessage>() {
			@Override
			public void handle(IMessage message) {
				try {
					if (message.isSucceeded()) {
						promise.resolve(message.body(responseType), message.headers());
					} else {
						Throwable t = message.body(Throwable.class);
						if (t instanceof UnauthorizedException && callerStacktrace != null) {
							t.addSuppressed(callerStacktrace);
						}
						promise.reject(t);
					}
				} catch (Throwable e) {
					if (e instanceof UnauthorizedException && callerStacktrace != null) {
						e.addSuppressed(callerStacktrace);
					}
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
	
	/**
	 * Executes the fully built asynchronous request synchronously on the given {@link ServiceProvider context}.
	 * @param context
	 * @return
	 */
	public R execute(ServiceProvider context) {
		return getRequest().execute(context);
	}

	/**
	 * Helper to run a fully prepared async request inside a job and store its result.
	 *  
	 * @return a {@link ScheduleJobRequestBuilder} with only the request part configured to this {@link AsyncRequest}
	 */
	public ScheduleJobRequestBuilder runAsJob() {
		return JobRequests.prepareSchedule()
				.setRequest(this);
	}
	
	/**
	 * Wraps the this {@link AsyncRequest}'s {@link #getRequest()} into a {@link ScheduleJobRequestBuilder} and prepares for execution.
	 *  
	 * @param description - the description to use for the job
	 * @return the prepared {@link AsyncRequest} that will schedule the request as a job and return the job ID as a result
	 */
	public AsyncRequest<String> runAsJob(String description) {
		return runAsJob()
				.setDescription(description)
				.buildAsync();
	}
	
	/**
	 * Wraps the this {@link AsyncRequest}'s {@link #getRequest()} into a {@link ScheduleJobRequestBuilder} and prepares for execution.
	 *  
	 * @param jobKey - the id to use for job identification
	 * @param description - the description to use for the job
	 * @return the prepared {@link AsyncRequest} that will schedule the request as a job and return the job ID as a result
	 */
	public AsyncRequest<String> runAsJob(String jobKey, String description) {
		return runAsJob()
				.setKey(jobKey)
				.setDescription(description)
				.buildAsync();
	}
	
	/**
	 * Wraps the this {@link AsyncRequest}'s {@link #getRequest()} into a {@link ScheduleJobRequestBuilder} and prepares for execution.
	 * The restart flag is enabled for the job scheduling, so it will remove any existing jobs with the same key.
	 *  
	 * @param jobKey - the id to use for job identification
	 * @param description - the description to use for the job
	 * @return the prepared {@link AsyncRequest} that will schedule the request as a job and return the job ID as a result
	 */
	public AsyncRequest<String> runAsJobWithRestart(String jobKey, String description) {
		return runAsJob()
				.setKey(jobKey)
				.setDescription(description)
				.setRestart(true)
				.buildAsync();
	}

	/**
	 * A simple poller implementation that reuses this prepared {@link AsyncRequest} until a certain condition is met in the response object.
	 * 
	 * @param bus - the bus to use for request execution
	 * @param pollIntervalMillis - the polling interval between retries 
	 * @param canFinish
	 * @return
	 */
	public Promise<R> retryUntil(IEventBus bus, long pollIntervalMillis, Predicate<R> canFinish) {
		return execute(bus).thenWith(result -> {
			if (canFinish.test(result)) {
				return Promise.immediate(result);
			} else {
				try {
					Thread.sleep(pollIntervalMillis);
				} catch (InterruptedException e) {
					throw new SnowowlRuntimeException(e);
				}
				return retryUntil(bus, pollIntervalMillis, canFinish);
			}
		});
	}
	
}