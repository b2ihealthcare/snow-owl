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

import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;

/**
 * @since 5.0
 */
public final class StopwatchTimer implements Timer {

	private final Stopwatch watch = Stopwatch.createUnstarted();
	private final TimeUnit unit;
	
	StopwatchTimer() {
		this(TimeUnit.MILLISECONDS);
	}
	
	StopwatchTimer(TimeUnit unit) {
		this.unit = unit;
	}
	
	@Override
	public void start() {
		watch.start();
	}

	@Override
	public void stop() {
		watch.stop();
	}
	
	@Override
	public Long getValue() {
		return watch.elapsed(unit);
	}

}
