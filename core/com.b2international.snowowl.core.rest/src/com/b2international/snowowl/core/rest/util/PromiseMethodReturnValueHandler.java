/*
 * Copyright 2019-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.method.support.AsyncHandlerMethodReturnValueHandler;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.events.util.Response;

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
				.thenRespond(promiseResponse -> setDeferredResult(result, promiseResponse))
				.fail(err -> {
					if (result.isSetOrExpired()) {
						LOG.warn("Deferred result is already set or expired, could not deliver Throwable {}.", err);
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

	private Response<?> setDeferredResult(DeferredResult<ResponseEntity<?>> result, Response<?> promiseResponse) {
		if (result.isSetOrExpired()) {
			LOG.warn("Deferred result is already set or expired, could not deliver result {}.", promiseResponse);
		} else { 
			final Object body = promiseResponse.getBody();
			final ResponseEntity<?> response;
			if (body instanceof ResponseEntity<?> b) {
				// return a custom ResponseEntity, copy it and apply headers returned from the system
				HttpHeaders headers = b.getHeaders();
				// append headers returned from system
				promiseResponse.getHeaders().forEach((headerName, headerValue) -> {
					headers.set(headerName, headerValue);
				});
				response = new ResponseEntity<>(b.getBody(), headers, b.getStatusCode());
				
			} else {
				// returning a standard object as reponse, use HTTP 200 OK
				BodyBuilder responseBuilder = ResponseEntity.ok();
				// append headers returned from system
				promiseResponse.getHeaders().forEach((headerName, headerValue) -> {
					responseBuilder.header(headerName, headerValue);
				});
				response = responseBuilder
						.body(body);
			}
			result.setResult(response);
		}
		return null;
	}

	@Override
	public boolean supportsReturnType(MethodParameter returnType) {
		Class<?> type = returnType.getParameterType();
		return Promise.class.isAssignableFrom(type);
	}

}
