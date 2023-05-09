/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.commons.metric;

import java.util.Collections;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Simple interface with metric registration and retrieval capabilities. Can be used to adapt to any metric collection framework or library.
 * 
 * @since 8.11.0
 */
public interface Metrics {

	Metrics NOOP = new Metrics() {

		@Override
		public <T> void withMetric(String metricKey, T value, BiFunction<T, T, T> merge) {
		}

		@Override
		public Map<String, Object> getMeasurements() {
			return Collections.emptyMap();
		}
		
	};
	
	default void withIntegerMetric(String metricKey, Integer value) {
		withMetric(metricKey, value, Integer::sum);
	}
	
	default void withLongMetric(String metricKey, Long value) {
		withMetric(metricKey, value, Long::sum);
	}
	
	<T> void withMetric(String metricKey, T value, BiFunction<T, T, T> merge);
	
	Map<String, Object> getMeasurements();
	
}
