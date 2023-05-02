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

	private static final int DEFAULT_PURGE_THRESHOLD = 10;
	private static final long DEFAULT_STALE_JOB_AGE = TimeUnit.DAYS.toMillis(30L);

	@Min(1)
	private int purgeThreshold = DEFAULT_PURGE_THRESHOLD;

	@Min(0)
	private long staleJobAge = DEFAULT_STALE_JOB_AGE;

	/**
	 * The number of completed (FINISHED, FAILED, CANCELED) jobs which triggers a purge in the job index
	 */
	public int getPurgeThreshold() {
		return purgeThreshold;
	}

	/**
	 * The duration to determine if a job can be considered stale. Used in relation with the finish date of a given job.
	 */
	public long getStaleJobAge() {
		return staleJobAge;
	}

	public void setPurgeThreshold(final int purgeThreshold) {
		this.purgeThreshold = purgeThreshold;
	}

	public void setStaleJobAge(final long staleJobAge) {
		this.staleJobAge = staleJobAge;
	}

}
