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

import static com.google.common.base.Preconditions.checkNotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.domain.DelegatingServiceProvider;
import com.b2international.snowowl.core.events.DelegatingRequest;
import com.b2international.snowowl.core.events.Request;

/**
 * @since 4.5
 */
public final class MeteredRequest<R> extends DelegatingRequest<ServiceProvider, ServiceProvider, R> {

	private static final Logger LOG = LoggerFactory.getLogger("request");
	private final Metrics metrics;

	MeteredRequest(Metrics metrics, Request<ServiceProvider, R> next) {
		super(next);
		this.metrics = checkNotNull(metrics, "metrics");
	}
	
	@Override
	public R execute(ServiceProvider context) {
		final Timer responseTimer = metrics.timer("responseTime");
		responseTimer.start();
		try {
			return next(DelegatingServiceProvider
					.basedOn(context)
					.bind(Metrics.class, metrics)
					.build());
		} finally {
			responseTimer.stop();
			LOG.info(getMessage());
		}
	}

	private String getMessage() {
		try {
			return toString();
		} catch (Throwable e) {
			return "Unable to get request description: " + e.getMessage();
		}
	}
	
	@Override
	public String toString() {
		if (metrics == Metrics.NOOP) {
			return super.toString();
		} else {
			return String.format("{req:%s, metrics:%s}", super.toString(), metrics);
		}
	}

}
