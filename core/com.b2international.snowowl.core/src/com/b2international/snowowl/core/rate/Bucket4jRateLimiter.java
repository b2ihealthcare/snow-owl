/*
 * Copyright 2019-2024 B2i Healthcare, https://b2ihealthcare.com
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

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;

/**
 * For more information see Bucket4j reference: https://bucket4j.com/8.10.1/toc.html#bandwidth
 * 
 * @since 7.2
 */
final class Bucket4jRateLimiter implements RateLimiter {

	private final RateLimitConfig configuration;
	private final ConcurrentMap<String, Bucket> bucketByUser;

	public Bucket4jRateLimiter(RateLimitConfig configuration) {
		this.configuration = configuration;
		this.bucketByUser = new MapMaker().makeMap();
	}

	@Override
	public RateLimitConsumption consume(String username) {
		final Bucket bucket = bucketByUser.computeIfAbsent(username, this::createNewBucket);
		final ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
		return new RateLimitConsumption(probe.isConsumed(), probe.getRemainingTokens(), TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill()));
	}

	/*
	 * userId argument is currently unused, tempting to assign it to the Bandwidth as optional identifier via #id(...), but that only increased memory consumption without any actual gain
	 */
	private Bucket createNewBucket(String userId) {
		return Bucket.builder()
				.addLimit(bandwidth -> 
					bandwidth
						.capacity(configuration.getCapacity())
						.refillGreedy(configuration.getRefillRate(), Duration.ofSeconds(1))
				)
				.build();
	}

}
