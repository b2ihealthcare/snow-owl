/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.b2international.snowowl.datastore.config.ConnectionPoolConfiguration;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 6.6
 */
public class ValidationConfiguration extends ConnectionPoolConfiguration {

	public static final int DEFAULT_NUMBER_OF_VALIDATION_THREADS = Math.max(2, Runtime.getRuntime().availableProcessors() / 2); 
	public static final String IS_UNPUBLISHED_ONLY = "isUnpublishedOnly";
	private static final int DEFAULT_MAX_CONCURRENT_EXPENSIVE_JOBS = 1;
	private static final int DEFAULT_MAX_CONCURRENT_NORMAL_JOBS = 4;
	
	@Min(1)
	@Max(8)
	private  int numberOfValidationThreads = DEFAULT_NUMBER_OF_VALIDATION_THREADS;
	
	@Min(1)
	@Max(5)
	private int maxConcurrentExpensiveJobs = DEFAULT_MAX_CONCURRENT_EXPENSIVE_JOBS;
	
	
	@Min(1)
	@Max(5)
	private int maxConcurrentNormalJobs = DEFAULT_MAX_CONCURRENT_NORMAL_JOBS;
	
	public void setNumberOfValidationThreads(int numberOfValidationThreads) {
		this.numberOfValidationThreads = numberOfValidationThreads;
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
	 * @return numberOfValidationThreads
	 */
	@JsonProperty("numberOfValidationThreads")
	public int getNumberOfValidationThreads() {
		return numberOfValidationThreads;
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
