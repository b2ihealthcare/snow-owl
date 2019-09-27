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
public class RateLimitConsumption {

	private final boolean consumed;
	private final long remainingTokens;
	private final long secondsToWait;
	
	public RateLimitConsumption(boolean consumed, long remainingTokens, long secondsToWait) {
		this.consumed = consumed;
		this.remainingTokens = remainingTokens;
		this.secondsToWait = secondsToWait;
	}
	
	public boolean isConsumed() {
		return consumed;
	}
	
	public long getRemainingTokens() {
		return remainingTokens;
	}
	
	public long getSecondsToWait() {
		return secondsToWait;
	}
	
}
