/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.branch;

/**
 * Represents an object capable of providing timestamp(s).
 * 
 * @since 4.1
 */
public interface TimestampProvider {

	/**
	 * Provides a timestamp. Timestamps may be repeated on multiple invocations. 
	 * 
	 * @return
	 */
	long getTimestamp();
	
	/**
	 * @since 7.0
	 */
	class Default implements TimestampProvider {
		
		private long lastIssuedTimeStamp = -1L;
		
		@Override
		public synchronized long getTimestamp() {
			long currentTimeMillis = System.currentTimeMillis();
			if (lastIssuedTimeStamp != currentTimeMillis) {
				lastIssuedTimeStamp = currentTimeMillis;
			} else {
				lastIssuedTimeStamp = currentTimeMillis + 1;
			}
			return lastIssuedTimeStamp;
		}
		
	}
	
}
