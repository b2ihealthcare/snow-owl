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

import com.google.common.base.Stopwatch;

/**
 * @since 4.5
 */
public final class StopwatchResponseTimeMeter implements RequestMeter {

	private final Stopwatch watch;
	private final Logger log;
	
	public StopwatchResponseTimeMeter(Logger log) {
		this.log = checkNotNull(log, "log");
		this.watch = Stopwatch.createUnstarted();
	}
	
	@Override
	public void start(String message) {
		watch.start();
	}

	@Override
	public void stop(String message) {
		watch.stop();
		log.info("{} - {}", message, watch);
	}

}
