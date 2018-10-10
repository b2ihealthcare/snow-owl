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

import io.micrometer.core.instrument.MeterRegistry;

/**
 * {@link ThreadLocal} based registry for request scoped {@link Metrics} instances.
 * 
 * @since 5.0
 */
public final class MonitoringThreadLocal {

	private static final ThreadLocal<MeterRegistry> REGISTRY = new ThreadLocal<MeterRegistry>() {
		@Override
		protected MeterRegistry initialValue() {
			return null;
		}
	};
	
	public static void set(MeterRegistry metrics) {
		REGISTRY.set(metrics);
	}
	
	public static MeterRegistry get() {
		return REGISTRY.get();
	}
	
	public static void release() {
		REGISTRY.remove();
	}
	
}
