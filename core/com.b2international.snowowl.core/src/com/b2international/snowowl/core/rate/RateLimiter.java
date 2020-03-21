/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

/**
 * @since 7.2
 */
@FunctionalInterface
public interface RateLimiter {

	RateLimitConsumption INF = new RateLimitConsumption(true, Integer.MAX_VALUE, 0);
	
	/**
	 * No-operation rate limiter to always consume the incoming requests without rejecting any of them.
	 */
	RateLimiter NOOP = username -> INF;

	/**
	 * Consume 1 request from the pool of available requests associated for the user.
	 * 
	 * @param username
	 * @return {@link RateLimitConsumption} state after the consumption
	 */
	RateLimitConsumption consume(String username);
	
}
