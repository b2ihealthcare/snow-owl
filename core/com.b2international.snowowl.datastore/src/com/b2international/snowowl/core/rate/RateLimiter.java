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

import java.time.Duration;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.MapMaker;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.Refill;

/**
 * @since 7.2
 */
public final class RateLimiter {

	private final ApiConfiguration configuration;
	private final ConcurrentMap<String, Bucket> bucketByUser = new MapMaker().makeMap();

	public RateLimiter(ApiConfiguration configuration) {
		this.configuration = configuration;
	}

	/**
	 * Consume 1 request
	 * 
	 * @param username
	 * @return {@link RateLimitConsumption} state after the consumption
	 */
	public RateLimitConsumption consume(String username) {
		Bucket bucket = bucketByUser.get(username);
		if (bucket == null) {
			bucket = createNewBucket();
			bucketByUser.put(username, bucket);
		}
		
		final ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
		return new RateLimitConsumption(probe.isConsumed(), probe.getRemainingTokens(), TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill()));
	}

	private Bucket createNewBucket() {
		long overdraft = 50;
		Refill refill = Refill.greedy(10, Duration.ofSeconds(1));
		Bandwidth limit = Bandwidth.classic(overdraft, refill);
		return Bucket4j.builder().addLimit(limit).build();
	}

}
