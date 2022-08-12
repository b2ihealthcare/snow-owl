/*
 * Copyright 2022 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.core.domain.DelegatingContext;

/**
 * Request scoped execution context. Can be used to register request scoped services, caches, etc.
 * 
 * @since 8.5
 * @param <C>
 * @param <R>
 */
public final class RequestContext extends DelegatingContext {

	private Map<String, Object> metrics;
	
	public RequestContext(ServiceProvider delegate) {
		super(delegate);
		bind(RequestContext.class, this);
	}
	
	public void withMetric(String metricKey, Object value) {
		if (this.metrics == null) {
			this.metrics = new HashMap<>(2);
		}
		this.metrics.put(metricKey, value);
	}
	
	public Map<String, Object> getMetrics() {
		return metrics;
	}

}
