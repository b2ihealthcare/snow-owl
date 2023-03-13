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

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 7.2
 */
public class ApiConfiguration {

	public static final String ETAG_HEADER = "ETag";
	public static final String IF_NONE_MATCH_HEADER = "If-None-Match";
	public static final String CACHE_CONTROL_HEADER = "Cache-Control";

	@Valid
	private RateLimitConfig rateLimit = new RateLimitConfig();

	@Valid
	private HttpConfig http = new HttpConfig();

	/**
	 * The default value of this configuration is the desired Cache-Control header value to set when responding to any GET requests that is targeting
	 * a resource's content branch. Since objects in our system are stored in a revision-controlled way (along with authorization information) we are
	 * unable to let downstream HTTP caches to cache our content without enforcing revalidation of it immediately (to prevent data leakage). Thus the
	 * default value of this Cache-Control allows well crafted HTTP proxies to store the response in their cache, but immediately set it to stale
	 * (max-age=0) and enforce revalidation of that stale content (must-revalidate). This ensures that we use downstream caches as much as possible by
	 * preventing data leakage and stale responses.
	 * 
	 * <p>
	 * <h3>Revalidation</h3> When a browser or proxy HTTP cache would try to use the cached response with this header they must first send a request
	 * to the server to check whether the cached response can be used or not. All the content served by Snow Owl will have an ETag header set so that
	 * during revalidation the client can send the ETag value in the If-None-Match header for verification. If the ETag value is still valid the cache
	 * response can be used (HTTP 304 Not Modified will be sent back as response), if not the server will generate a new response and send it back to
	 * update the cached value.
	 * </p>
	 * 
	 */
	@NotEmpty
	private String cacheControl = "s-maxage=0,max-age=0,must-revalidate";

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

	@JsonProperty("cache_control")
	public String getCacheControl() {
		return cacheControl;
	}

	@JsonProperty("cache_control")
	public void setCacheControl(String cacheControl) {
		this.cacheControl = cacheControl;
	}

	// backward compatible configuration methods
	@JsonProperty
	/* package */ void setOverdraft(long overdraft) {
		rateLimit.setOverdraft(overdraft);
	}

	@JsonProperty
	/* package */ void setRefillRate(long refillRate) {
		rateLimit.setRefillRate(refillRate);
	}

}
