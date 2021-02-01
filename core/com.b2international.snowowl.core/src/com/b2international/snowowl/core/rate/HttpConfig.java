/*
 * Copyright 2020-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @since 7.6
 */
public class HttpConfig {
	
	@NotEmpty
	private String maxFileSize = "1gb";
	@NotEmpty
	private String maxRequestSize = "1gb";
	@NotEmpty
	private String maxInMemorySize = "1mb";
	
	@NotEmpty
	private String requestTimeout = "300s";
	
	public String getMaxFileSize() {
		return maxFileSize;
	}
	
	public String getMaxInMemorySize() {
		return maxInMemorySize;
	}
	
	public String getMaxRequestSize() {
		return maxRequestSize;
	}
	
	public String getRequestTimeout() {
		return requestTimeout;
	}
	
	public void setMaxFileSize(String maxFileSize) {
		this.maxFileSize = maxFileSize;
	}
	
	public void setMaxInMemorySize(String maxInMemorySize) {
		this.maxInMemorySize = maxInMemorySize;
	}
	
	public void setMaxRequestSize(String maxRequestSize) {
		this.maxRequestSize = maxRequestSize;
	}
	
	public void setRequestTimeout(String requestTimeout) {
		this.requestTimeout = requestTimeout;
	}

	@JsonIgnore
	public long getMaxFileSizeBytes() {
		return ByteSizeValue.parseBytesSizeValue(getMaxFileSize(), "api.http.maxFileSize").getBytes();
	}

	@JsonIgnore
	public long getMaxRequestSizeBytes() {
		return ByteSizeValue.parseBytesSizeValue(getMaxRequestSize(), "api.http.maxRequestSize").getBytes();
	}

	@JsonIgnore
	public int getMaxInMemorySizeBytes() {
		return ByteSizeValue.parseBytesSizeValue(getMaxInMemorySize(), "api.http.maxInMemorySize").bytesAsInt();
	}

	@JsonIgnore
	public long getRequestTimeoutInMillis() {
		return TimeValue.parseTimeValue(getRequestTimeout(), "requestTimeout").millis();
	}

}
