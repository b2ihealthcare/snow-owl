/*
 * Copyright 2018-2024 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.internal.validation;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * @since 6.6
 */
public class ValidationConfiguration {

	// configuration keys
	public static final String IS_UNPUBLISHED_ONLY = "isUnpublishedOnly";
	public static final String USE_FSN = "useFsn";
	public static final String LOCALES = "extendedLocales";
	public static final String MODULES = "modules";
	
	private static final int DEFAULT_MAX_CONCURRENT_EXPENSIVE_JOBS = 1;
	private static final int DEFAULT_MAX_CONCURRENT_NORMAL_JOBS = 4;
	
	@Min(1)
	@Max(99)
	private Integer workerPoolSize;
	
	@Min(1)
	@Max(5)
	private int maxConcurrentExpensiveJobs = DEFAULT_MAX_CONCURRENT_EXPENSIVE_JOBS;
	
	@Min(1)
	@Max(5)
	private int maxConcurrentNormalJobs = DEFAULT_MAX_CONCURRENT_NORMAL_JOBS;
	
	// Accept previous configuration key for backwards compatibility
	@Deprecated
	@JsonProperty(value = "numberOfValidationThreads", access = JsonProperty.Access.WRITE_ONLY)
	public void setNumberOfValidationThreads(int numberOfValidationThreads) {
		this.workerPoolSize = numberOfValidationThreads;
	}
	
	public void setWorkerPoolSize(int workerPoolSize) {
		this.workerPoolSize = workerPoolSize;
	}
	
	public void setMaxConcurrentExpensiveJobs(int maxConcurrentExpensiveJobs) {
		this.maxConcurrentExpensiveJobs = maxConcurrentExpensiveJobs;
	}
	
	public void setMaxConcurrentNormalJobs(int maxConcurrentNormalJobs) {
		this.maxConcurrentNormalJobs = maxConcurrentNormalJobs;
	}
	
	/**
	 * The number of validations jobs that can be run asynchronously.
	 * 
	 * @return workerPoolSize
	 */
	@JsonProperty("workerPoolSize")
	public Integer getWorkerPoolSize() {
		return workerPoolSize;
	}
	
	/**
	 * The number of expensive validation jobs that can be run asynchronously.
	 * 
	 * @return maxConcurrentExpensiveJobs
	 */
	@JsonProperty("maxConcurrentExpensiveValidations")
	public int getMaxConcurrentExpensiveJobs() {
		return maxConcurrentExpensiveJobs;
	}
	
	/**
	 * The number of normal validation jobs that can be run asynchronously.
	 * 
	 * @return maxConcurrentNormalJobs
	 */
	@JsonProperty("maxConcurrentNormalValidations")
	public int getMaxConcurrentNormalJobs() {
		return maxConcurrentNormalJobs;
	}
}
