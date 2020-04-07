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
final class Bucket4jRateLimiter implements RateLimiter {

	private final ApiConfiguration configuration;
	private final ConcurrentMap<String, Bucket> bucketByUser;

	public Bucket4jRateLimiter(ApiConfiguration configuration) {
		this.configuration = configuration;
		this.bucketByUser = new MapMaker().makeMap();
	}

	@Override
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
		long overdraft = configuration.getOverdraft();
		Refill refill = Refill.greedy(configuration.getRefillRate(), Duration.ofSeconds(1));
		Bandwidth limit = Bandwidth.classic(overdraft, refill);
		return Bucket4j.builder().addLimit(limit).build();
	}

}
