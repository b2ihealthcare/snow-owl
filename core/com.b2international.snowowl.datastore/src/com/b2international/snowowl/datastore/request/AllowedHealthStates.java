/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.request;

import com.b2international.snowowl.core.Repository;
import com.b2international.snowowl.core.RepositoryInfo;

/**
 * @since 5.8
 */
public interface AllowedHealthStates {

	/**
	 * Returns an array of {@link Repository.Health Health} statuses in which the delegate request is allowed to execute.
	 * By Default the array contains only {@link Repository.Health#GREEN}. Which can be overriden in {@link RepositoryRequestBuilder} implementations 
	 * 	eg.: to allow maintenance related requests to be executed against a non-green {@link Repository}.
	 * @return an array of {@link Repository.Health} statuses.
	 */
	default Repository.Health[] allowedHealthstates() {
		return new Repository.Health[] { RepositoryInfo.Health.GREEN, RepositoryInfo.Health.YELLOW };
	}
	
}
