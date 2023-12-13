/*
 * Copyright 2022-2023 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import com.b2international.commons.metric.Metrics;
import com.b2international.snowowl.core.domain.DelegatingContext;
import com.b2international.snowowl.core.events.util.RequestHeaders;
import com.b2international.snowowl.core.events.util.ResponseHeaders;

/**
 * Request scoped execution context. Can be used to register request scoped services, caches, etc.
 * 
 * @since 8.5
 * @param <C>
 * @param <R>
 */
public final class RequestContext extends DelegatingContext implements Metrics {

	private Map<String, Object> metrics;
	
	public RequestContext(ServiceProvider delegate, Map<String, String> requestHeaders) {
		super(delegate);
		bind(RequestContext.class, this);
		bind(Metrics.class, this);
		bind(RequestHeaders.class, new RequestHeaders(requestHeaders));
		bind(ResponseHeaders.class, new ResponseHeaders());
	}
	
	@Override
	public <T> void withMetric(String metricKey, T value, BiFunction<T, T, T> merge) {
		if (this.metrics == null) {
			this.metrics = new HashMap<>(2);
		}
		this.metrics.merge(metricKey, value, (BiFunction<? super Object, ? super Object, ? extends Object>) merge);
	}
	
	@Override
	public Map<String, Object> getMeasurements() {
		return metrics;
	}

	public Map<String, String> requestHeaders() {
		return service(RequestHeaders.class).headers();
	}
	
	public Map<String, String> responseHeaders() {
		return service(ResponseHeaders.class).headers();
	}
	
}
