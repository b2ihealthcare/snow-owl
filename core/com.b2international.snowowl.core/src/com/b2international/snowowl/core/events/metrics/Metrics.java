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

/**
 * @since 5.0
 */
public interface Metrics {

	Metrics NOOP = new Metrics() {
		@Override
		public Timer timer(String name) {
			return Timer.NOOP;
		}

		@Override
		public void setExternalValue(String name, long value) {
		}
	};

	/**
	 * Constant value for skipping externally measured metrics when serializing this {@link Metrics}.
	 */
	long SKIP = -1L;

	/**
	 * Returns a timer to measure elapsed time.
	 * 
	 * @param name
	 * @return
	 */
	Timer timer(String name);

	/**
	 * Sets an externally measured metric value with the given name and value in this {@link Metrics registry}.
	 * 
	 * @param name
	 * @param value
	 */
	void setExternalValue(String name, long value);

}
