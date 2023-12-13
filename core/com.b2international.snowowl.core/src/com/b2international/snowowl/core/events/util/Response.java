/*
 * Copyright 2019-2023 B2i Healthcare, https://b2ihealthcare.com
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.ImmutableMap;

/**
 * @since 7.2
 */
public final class Response<T> {

	private final Map<String, String> headers;
	private final T body;

	private Response(T body, Map<String, String> headers) {
		this.body = body;
		this.headers = ImmutableMap.copyOf(headers);
	}
	
	public Map<String, String> getHeaders() {
		return headers;
	}
	
	public String getHeader(String name) {
		return headers.get(name);
	}
	
	public T getBody() {
		return body;
	}
	
	public Response<T> withHeader(String key, String value) {
		final Map<String, String> newHeaders = new HashMap<>(this.headers);
		newHeaders.put(key, value);
		return of(this.body, newHeaders);
	}
	
	public Response<T> withHeaders(Map<String, String> headers) {
		return of(this.body, headers);
	}
	
	public static <T> Response<T> of(T body, Map<String, String> headers) {
		return new Response<T>(body, headers);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(body, headers);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Response<?> other = (Response<?>) obj;
		return Objects.equals(body, other.body) && Objects.equals(headers, other.headers);
	}

}
