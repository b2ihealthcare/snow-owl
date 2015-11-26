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
package com.b2international.snowowl.core.events.metrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.Request;

/**
 * Class capable of decorating request's in {@link MeteredRequest} with meters selected for the given request. Currently only one meter is registered
 * on the incoming {@link Request}, the {@link StopwatchResponseTimeMeter}.
 * 
 * @since 4.5
 */
public final class RequestMetrics implements Metrics {

	private final static Logger LOG = LoggerFactory.getLogger("request");

	@Override
	public <C extends ServiceProvider, R> Request<C, R> measure(Request<C, R> req) {
		return new MeteredRequest<>(new StopwatchResponseTimeMeter(LOG), req);
	}

}
