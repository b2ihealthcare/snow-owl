/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.config;

import java.util.concurrent.TimeUnit;

import javax.validation.constraints.Min;

/**
 * @since 7.18.0
 */
public class JobConfiguration {

	private static final long DEFAULT_JOB_CLEANUP_INTERVAL = TimeUnit.MINUTES.toMillis(1L);
	private static final long DEFAULT_STALE_JOB_CLEANUP_THRESHOLD = TimeUnit.DAYS.toMillis(30L);

	@Min(0)
	private long jobCleanupInterval = DEFAULT_JOB_CLEANUP_INTERVAL;

	@Min(0)
	private long staleJobCleanupThreshold = DEFAULT_STALE_JOB_CLEANUP_THRESHOLD;

	/**
	 * The interval for executing a remote job cleanup task in milliseconds, defaults to 1 minute
	 */
	public long getJobCleanupInterval() {
		return jobCleanupInterval;
	}

	/**
	 * The threshold to identify finished, failed or canceled jobs older than this value to use during clean up, defaults to 30 days
	 */
	public long getStaleJobCleanupThreshold() {
		return staleJobCleanupThreshold;
	}

	public void setJobCleanupInterval(final long jobCleanupInterval) {
		this.jobCleanupInterval = jobCleanupInterval;
	}

	public void setStaleJobCleanupThreshold(final long staleJobCleanupThreshold) {
		this.staleJobCleanupThreshold = staleJobCleanupThreshold;
	}

}
