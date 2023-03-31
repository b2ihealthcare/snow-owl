/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.rest.rate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.fail;

import java.time.LocalDateTime;

import org.elasticsearch.core.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.b2international.commons.exceptions.TooManyRequestsException;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.Resources;
import com.b2international.snowowl.core.events.util.Response;
import com.b2international.snowowl.core.rate.ApiConfiguration;
import com.b2international.snowowl.core.rate.ApiPlugin;
import com.b2international.snowowl.core.rate.RateLimitConsumption;
import com.b2international.snowowl.core.rate.RateLimiter;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.test.commons.Services;
import com.b2international.snowowl.test.commons.rest.ResourceApiAssert;
import com.b2international.snowowl.test.commons.rest.RestExtensions;

/**
 * @since 8.10.0
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RateLimitTest {

	private RateLimiter rateLimiter;

	@Before
	public void setup() throws Exception {
		// inject rate limit feature into the system until we run the rate limit tests
		Environment env = ApplicationContext.getServiceForClass(Environment.class);
		ApiConfiguration apiConfig = new ApiConfiguration();
		// this means that the user is able to perform 2 requests at a time, with one second refill rate (default value, but just in case fix it here as well)
		apiConfig.setOverdraft(2L);
		apiConfig.setRefillRate(1L);
		new ApiPlugin().initRateLimiter(env, apiConfig);
		this.rateLimiter = env.service(RateLimiter.class);
		
	}
	
	@After
	public void after() throws Exception {
		// revert back to noop rate limiter
		ApplicationContext.getServiceForClass(Environment.class).services().registerService(RateLimiter.class, RateLimiter.NOOP);
		rateLimiter = null;
	}
	
	@Test
	public void rateLimitServiceTest() throws Exception {
		// perform three consumptions at the same time
		RateLimitConsumption firstRequest = rateLimiter.consume(RestExtensions.USER);
		RateLimitConsumption secondRequest = rateLimiter.consume(RestExtensions.USER);
		RateLimitConsumption thirdRequest = rateLimiter.consume(RestExtensions.USER);
		// first two should succeed, third should fail to consume token
		assertThat(firstRequest.isConsumed()).isTrue();
		assertThat(secondRequest.isConsumed()).isTrue();
		assertThat(thirdRequest.isConsumed()).isFalse();
	}
	
	@Test
	public void rateLimitTest_JavaAPI() throws Exception {
		Response<Resources> response = ResourceRequests.prepareSearch()
			.setLimit(0)
			.buildAsync()
			.execute(Services.bus())
			.getSyncResponse();
		
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getHeaders()).containsEntry("X-Rate-Limit-Remaining", "1");
	}
	
	@Test
	public void rateLimitTest_RestAPI() throws Exception {
		ResourceApiAssert.assertResourceSearch(Map.of("limit", 0))
			.statusCode(200)
			.header("X-Rate-Limit-Remaining", "1");
	}
	
	@Test
	public void rateLimitTest_JavaAPI_RateLimited() throws Exception {
		// assume user consumed all available tokens from the bucket
		rateLimiter.consume(RestExtensions.USER);
		rateLimiter.consume(RestExtensions.USER);
		
		try {
			ResourceRequests.prepareSearch()
				.setLimit(0)
				.buildAsync()
				.execute(Services.bus())
				.getSyncResponse();
			fail("Second request should throw a too many requests exception");
		} catch (TooManyRequestsException e) {
			// based on the fact that we have to 
			assertThat(e.getSecondsToWait())
				.isBetween(0L, 1L);
			assertThat(e.getMessage())
				.isEqualTo("Too many requests");
		}
	}
	
	@Test
	public void rateLimitTest_RestAPI_RateLimited() throws Exception {
		// warm up request to initialize everything in the system before checking rate limits
		ResourceApiAssert.assertResourceSearch(Map.of("limit", 0));
		
		// assume user consumed all available tokens from the bucket
		rateLimiter.consume(RestExtensions.USER);
		rateLimiter.consume(RestExtensions.USER);
				
		System.err.println("Sending resource request..." + LocalDateTime.now());
		ResourceApiAssert.assertResourceSearch(Map.of("limit", 0))
			.statusCode(429)
			.header("X-Rate-Limit-Retry-After-Seconds", anyOf(equalTo("0"), equalTo("1")));
	}
	
}
