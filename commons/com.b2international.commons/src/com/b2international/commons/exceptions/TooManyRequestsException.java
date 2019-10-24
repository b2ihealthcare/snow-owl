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
package com.b2international.commons.exceptions;

import java.util.Collections;
import java.util.Map;

/**
 * @since 7.2
 */
public final class TooManyRequestsException extends ApiException {
	
	public final static long NO_SECONDS_TO_WAIT_AVAILABLE = -1L;
	private final long secondsToWait;
	
	public TooManyRequestsException() {
		this(-1L);
	}
	
	public TooManyRequestsException(long secondsToWait) {
		super("Too many requests");
		this.secondsToWait = secondsToWait;
	}

	@Override
	protected Integer getStatus() {
		return 429;
	}
	
	public long getSecondsToWait() {
		return secondsToWait;
	}
	
	@Override
	protected Map<String, Object> getAdditionalInfo() {
		return Collections.singletonMap("secondsToWait", getSecondsToWait());
	}

}
