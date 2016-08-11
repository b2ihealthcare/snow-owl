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

import static com.google.common.collect.Maps.newHashMapWithExpectedSize;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.google.common.collect.MapMaker;

/**
 * @since 5.0
 */
public final class DefaultMetrics implements Metrics {

	private final ConcurrentMap<String, Metric<?>> metrics = new MapMaker().makeMap();
	
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
		final Iterator<String> it = metrics.keySet().iterator();
		while (it.hasNext()) {
			final String name = it.next();
			measuredValues.put(name, metrics.get(name).getValue());
		}
		return measuredValues;
	}

}
