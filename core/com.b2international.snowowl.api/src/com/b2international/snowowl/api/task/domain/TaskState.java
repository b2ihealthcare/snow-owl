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
 * Enumerates possible task states.
 */
public enum TaskState {

	/**
	 * Parent branch for this task has moved forward since this task was created; synchronization is necessary before
	 * promoting.
	 */
	NOT_SYNCHRONIZED, 

	/**
	 * No changes on the parent branch since task creation or last synchronization. Promotion of changes on this task
	 * is allowed. 
	 */
	SYNCHRONIZED, 

	/**
	 * Task promoted and closed for further modifications.
	 */
	PROMOTED;
}
