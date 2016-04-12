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
package com.b2international.snowowl.core.domain;

import com.b2international.snowowl.core.events.Request;

/**
 * Points to a versioned component storage space of a code system, on a particular branch.
 * @deprecated - will be removed in 4.7, use {@link Request} API instead 
 */
public interface IStorageRef {

	/**
	 * @return the repository identifier
	 */
	String getRepositoryId();

	/**
	 * Returns the branch path eg. "{@code MAIN/projectA/task1}".
	 * 
	 * @return the branch path
	 */
	String getBranchPath();

}
