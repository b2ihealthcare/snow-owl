/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.monitoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.DelegatingRequest;
import com.b2international.snowowl.core.events.Request;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Timer.Sample;

/**
 * @since 4.5
 */
public final class MonitoredRequest<R> extends DelegatingRequest<ServiceProvider, ServiceProvider, R> {

	private static final Logger LOG = LoggerFactory.getLogger("request");
	
	public MonitoredRequest(Request<ServiceProvider, R> next) {
		super(next);
	}
	
	@Override
	public R execute(ServiceProvider context) {
		final MeterRegistry registry = context.service(MeterRegistry.class);
		final Sample responseTimeSample = Timer.start(registry);
		try {
			return next(context);
		} finally {
			responseTimeSample.stop(registry.timer("responseTime"));
			LOG.info(getMessage(context));
		}
	}

	private String getMessage(ServiceProvider context) {
		try {
			final ObjectMapper mapper = context.service(ObjectMapper.class);
			return mapper.writeValueAsString(next());
		} catch (Throwable e) {
			return "Unable to get request description: " + e.getMessage();
		}
	}
	
}
