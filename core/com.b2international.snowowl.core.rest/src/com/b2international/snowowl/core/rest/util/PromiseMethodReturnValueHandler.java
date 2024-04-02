/*
 * Copyright 2019-2024 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.rest.util;

import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.method.support.AsyncHandlerMethodReturnValueHandler;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.events.util.Response;
import com.google.common.collect.Iterables;

import jakarta.servlet.http.HttpServletResponse;

/**
 * @since 7.2
 */
public class PromiseMethodReturnValueHandler implements HandlerMethodReturnValueHandler, AsyncHandlerMethodReturnValueHandler {

	private static final Logger LOG = LoggerFactory.getLogger(PromiseMethodReturnValueHandler.class);
	
	@Override
	public boolean isAsyncReturnValue(Object returnValue, MethodParameter returnType) {
		return returnValue instanceof Promise;
	}
	
	@Override
	public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
		if (returnValue == null) {
			mavContainer.setRequestHandled(true);
			return;
		}
		
		if (returnValue instanceof Promise<?>) {
			final Promise<?> promise = (Promise<?>) returnValue;
			final DeferredResult<ResponseEntity<?>> result = new DeferredResult<>();
			promise
				.thenRespond(promiseResponse -> setDeferredResult(result, promiseResponse, webRequest))
				.fail(err -> {
					if (result.isSetOrExpired()) {
						LOG.warn("Deferred result is already set or expired, could not deliver Throwable.", err);
					} else {
						result.setErrorResult(err);
					}
					return null;
				});
			WebAsyncUtils.getAsyncManager(webRequest).startDeferredResultProcessing(result, mavContainer);			
		} else {
			// Should not happen...
			throw new IllegalStateException("Unexpected return value type: " + returnValue);
		}
	}

	private Response<?> setDeferredResult(DeferredResult<ResponseEntity<?>> result, Response<?> promiseResponse, NativeWebRequest webRequest) {
		if (result.isSetOrExpired()) {
			LOG.warn("Deferred result is already set or expired, could not deliver result {}.", promiseResponse);
		} else {
			final Object body = promiseResponse.getBody();
			final ResponseEntity<?> response;

			final Stream<Map.Entry<String, String>> responseHeaders;
			
			if (body instanceof ResponseEntity<?> b) {
				// returning a standard object as response, with the given status code and without the headers to prevent header duplication
				response = ResponseEntity.status(b.getStatusCode()).body(b.getBody());
				responseHeaders = Stream.concat(promiseResponse.getHeaders().entrySet().stream(), b.getHeaders().isEmpty() ? Stream.empty() : flattenHttpHeaders(b));
			} else {
				// returning a standard object as response, use HTTP 200 OK
				response = ResponseEntity.ok().body(body);
				responseHeaders = promiseResponse.getHeaders().entrySet().stream();
			}
			
			// append headers returned from system directly into the HTTP Response
			// see Spring Security issue not being able to properly prevent duplicate caching headers
			// https://github.com/spring-projects/spring-security/issues/12865 
			responseHeaders.forEach((entry) -> {
				// XXX using set header here, for most of our use cases we only need a single response header, so overwrite anything that has been injected by Spring earlier
				webRequest.getNativeResponse(HttpServletResponse.class).setHeader(entry.getKey(), entry.getValue());
			});
			
			result.setResult(response);
		}
		return null;
	}

	private Stream<Entry<String, String>> flattenHttpHeaders(ResponseEntity<?> b) {
		return b.getHeaders()
				.entrySet()
				.stream()
				.map(entry -> {
					// raise an error if we'd like to set a multi-valued HTTP response header
					if (entry.getValue().size() > 1) {
						throw new SnowowlRuntimeException("Multi-valued response headers are not supported yet");
					}
					return Map.entry(entry.getKey(), Iterables.getFirst(entry.getValue(), null));
				});
	}

	@Override
	public boolean supportsReturnType(MethodParameter returnType) {
		Class<?> type = returnType.getParameterType();
		return Promise.class.isAssignableFrom(type);
	}

}
