/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Collections;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

/**
 * @since 7.2
 */
public final class RequestHeaders {

	private final Map<String, String> headers;

	public RequestHeaders(Map<String, String> headers) {
		this.headers = headers == null ? Collections.emptyMap() : ImmutableMap.copyOf(headers);
	}
	
	public String header(String name) {
		return headers.get(name);
	}
	
	public Map<String, String> headers() {
		return headers;
	}
	
}
