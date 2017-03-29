/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.api.info.domain;

import com.b2international.snowowl.core.Repository;

/**
 * Captures information regarding the state of a given Repository. 
 * @since 5.8
 */
public interface IRepositoryInfo {

	/**
	 * Returns the repository identifier for which this state belongs to.
	 * 
	 * @return the repository identifier
	 */
	String getRepositoryId();
	
	/**
	 * Returns the latest commit timestamp from the database
	 * @return the head commit timestamp for the database
	 */
	long getHeadTimestampForDatabase();
	
	/**
	 * Returns the latest commit timestamp from the index
	 * @return the head commit timestamp for the index
	 */
	long getHeadTimestampForIndex();
	
	/**
	 * Returns the current state for the {@link Repository}.
	 * @return
	 */
	Repository.Health getHealth();
}
