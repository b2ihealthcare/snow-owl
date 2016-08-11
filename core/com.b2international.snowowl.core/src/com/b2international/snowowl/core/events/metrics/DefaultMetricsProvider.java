/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.events.metrics;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.Request;

/**
 * Class capable of decorating request's in {@link MeteredRequest} which offers {@link Metric}s for the entire execution of the request. By default it
 * registers a single {@link Timer} to measure the execution of the request.
 * 
 * @since 5.0
 */
public final class DefaultMetricsProvider implements MetricsProvider {

	@Override
	public <R> Request<ServiceProvider, R> measure(Request<ServiceProvider, R> req) {
		return new MeteredRequest<>(new DefaultMetrics(), req);
	}

}
