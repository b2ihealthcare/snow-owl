/*
 * Copyright 2022 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 8.8.0
 */
public final class DeprecationLogger {

	private static final Logger LOG = LoggerFactory.getLogger("deprecation");
	
	/**
	 * Log a deprecation warning message to the log files regarding a deprecated feature/functionality.
	 * 
	 * @param message - the message describing the deprecated functionality
	 * @param params - optional 
	 */
	public void log(String message, Object...params) {
		LOG.warn(message, params);
	}
	
}
