/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
 * @since 7.6
 */
public class ApiFileUploadConfig {
	
	private static final int MB_IN_BYTE = 1024 * 1024;
	
	private static final long UNLIMITED = -1;
	private static final int DEFAULT_MAX_IN_MEMORY_SIZE = 1024;

	private long maxFileSize;
	private long maxRequestSize;
	private int maxInMemorySize;
	
	public ApiFileUploadConfig(final long maxFileSize, final long maxRequestSize, final int maxInMemorySize) {
		this.maxFileSize = maxFileSize == 0 ? UNLIMITED : MB_IN_BYTE * maxFileSize; 
		this.maxRequestSize = maxRequestSize == 0 ? UNLIMITED : MB_IN_BYTE * maxRequestSize; 
		this.maxInMemorySize = maxInMemorySize == 0 ? DEFAULT_MAX_IN_MEMORY_SIZE : MB_IN_BYTE * maxInMemorySize; 
	}

	public long getMaxFileSize() {
		return maxFileSize;
	}
	
	public long getMaxRequestSize() {
		return maxRequestSize;
	}
	
	public int getMaxInMemorySize() {
		return maxInMemorySize;
	}
}
