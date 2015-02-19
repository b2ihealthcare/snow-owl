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
package com.b2international.snowowl.api.task.domain;

import java.util.Date;

/**
 * Captures information about an editing task.
 * 
 */
public interface ITask {

	/**
	 * Returns the task's description.
	 * @return the task description, eg. "{@code Inactivate relationship on Clinical finding}"
	 */
	String getDescription();
	
	/**
	 * Returns the identifier of this task, typically assigned by an external system.
	 * @return the identifier of this task, eg. "{@code 1245}"
	 */
	String getTaskId();

	/**
	 * Returns the creation or last synchronization time of this task. 
	 * @return the creation or last synchronization time of this task, eg. "{@code 2014-05-09T08:03:55Z}"
	 */
	Date getBaseTimestamp();

	/**
	 * Returns the last update time for this task. 
	 * @return the last update time for this task, eg. "{@code 2014-05-09T11:17:31Z}"
	 */
	Date getLastUpdatedTimestamp();

	/**
	 * Returns the task's current state.
	 * 
	 * @return
	 */
	TaskState getState();
	
}