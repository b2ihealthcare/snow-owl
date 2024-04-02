/*
 * Copyright 2024 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.rate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Min;

/**
 * @since 9.2
 */
public class RateLimitConfig {

	private static final long RATE_LIMITING_DISABLED = 0L;

	/**
	 * Capacity is the maximum number of tokens a single user can have. Defaults to zero which disables rate-limiting. Increase to any positive number to enable it.
	 */
	@Min(0)
	private long capacity = RATE_LIMITING_DISABLED;
	
	/**
	 * Refill rate describes the number of tokens that will be refilled after one second in a given user's token pool. Defaults to refill 1 token per second.
	 */
	@Min(1)
	private long refillRate = 1L;
	
	public long getCapacity() {
		return capacity;
	}
	
	public void setCapacity(long capacity) {
		this.capacity = capacity;
	}
	
	@JsonProperty("overdraft")
	/* package */ void setOverdraft(long overdraft) {
		setCapacity(overdraft);
	}
	
	public long getRefillRate() {
		return refillRate;
	}
	
	public void setRefillRate(long refillRate) {
		this.refillRate = refillRate;
	}

	@JsonIgnore
	public boolean isEnabled() {
		return getCapacity() > RATE_LIMITING_DISABLED;
	}
	
}
