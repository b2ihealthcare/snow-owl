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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.google.common.collect.MapMaker;

/**
 * @since 5.0
 */
public final class DefaultMetrics implements Metrics {

	private final ConcurrentMap<String, Metric<?>> metrics = new MapMaker().makeMap();
	private final Map<String, Long> externalValues = newHashMap();
	
	@Override
	public Timer timer(String name) {
		return register(name, new StopwatchTimer());
	}

	private <T extends Metric<C>, C> T register(String name, T metric) {
		final T alreadyRegistered = (T) metrics.putIfAbsent(name, metric);
		return alreadyRegistered == null ? metric : alreadyRegistered;
	}

	@JsonAnyGetter
	public Map<String, Object> getMeasuredValues() {
		final Map<String, Object> measuredValues = newHashMapWithExpectedSize(metrics.keySet().size());
		for (final String name : metrics.keySet()) {
			measuredValues.put(name, metrics.get(name).getValue());
		}
		for (final String external : externalValues.keySet()) {
			measuredValues.put(external, externalValues.get(external));
		}
		return measuredValues;
	}

	@Override
	public void setExternalValue(String name, long value) {
		checkArgument(!metrics.containsKey(name), "Metric '%s' should be external, but it is managed by this registry.", name);
		if (value != Metrics.SKIP) {
			externalValues.put(name, value);
		}
	}

}
