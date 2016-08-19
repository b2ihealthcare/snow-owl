/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.events.metrics;

import com.b2international.snowowl.core.events.Request;

/**
 * Measures a value during the execution of a {@link Request}.
 * 
 * @since 4.5
 */
public interface RequestMeter {

	/**
	 * Marks the start of a request.
	 * 
	 * @param description
	 *            - the description of the request
	 */
	void start(String description);

	/**
	 * Marks the end of a request.
	 * 
	 * @param description
	 *            - the description of the request
	 */
	void stop(String description);

}
