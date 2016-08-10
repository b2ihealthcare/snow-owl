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

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.MapMaker;

/**
 * @since 4.5
 */
public final class DefaultMetrics implements Metrics {

	private final ConcurrentMap<String, Metric> metrics = new MapMaker().makeMap();
	
	@Override
	public Timer timer(String name) {
		return register(name, new StopwatchTimer());
	}

	private <T extends Metric> T register(String name, T metric) {
		final T alreadyRegistered = (T) metrics.putIfAbsent(name, metric);
		return alreadyRegistered == null ? metric : alreadyRegistered;
	}
	
	@Override
	public String toString() {
		return toJson(metrics);
	}

	private String toJson(Map<String, Metric> metrics) {
		final StringBuilder builder = new StringBuilder();
		builder.append("{");
		for (String name : metrics.keySet()) {
			builder.append(name).append(":").append(metrics.get(name));
		}
		builder.append("}");
		return builder.toString();
	}
	
}
