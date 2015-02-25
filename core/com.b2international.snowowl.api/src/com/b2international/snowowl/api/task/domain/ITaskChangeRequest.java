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

/**
 * Encapsulates a request to change the state of an existing editing task.
 */
public interface ITaskChangeRequest {

	/**
	 * Returns the desired new state of the task.
	 * <p>
	 * The following state transitions are allowed:
	 * <ul>
	 * <li>{@link TaskState#NOT_SYNCHRONIZED NOT_SYNCHRONIZED} &rarr; {@link TaskState#SYNCHRONIZED SYNCHRONIZED} (synchronize changes)
	 * <li>{@link TaskState#SYNCHRONIZED SYNCHRONIZED} &rarr; {@link TaskState#PROMOTED PROMOTED} (promote changes)
	 * </ul>
	 * 
	 * @return the desired new state of the task
	 */
	TaskState getState();
}
