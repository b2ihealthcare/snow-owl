/*
 * Copyright 2019-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 7.2
 */
public class ApiConfiguration {

	@Valid
	private RateLimitConfig rateLimit = new RateLimitConfig();
	
	@Valid
	private HttpConfig http = new HttpConfig();
	
	public HttpConfig getHttp() {
		return http;
	}
	
	public void setHttp(HttpConfig http) {
		this.http = http;
	}
	
	public RateLimitConfig getRateLimit() {
		return rateLimit;
	}
	
	public void setRateLimit(RateLimitConfig rateLimit) {
		this.rateLimit = rateLimit;
	}
	
	// backward compatible configuration methods
	@JsonProperty
	/*package*/ void setOverdraft(long overdraft) {
		rateLimit.setOverdraft(overdraft);
	}
	
	@JsonProperty
	/*package*/ void setRefillRate(long refillRate) {
		rateLimit.setRefillRate(refillRate);
	}

}
