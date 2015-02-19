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
package com.b2international.snowowl.datastore.tasks;

import java.io.Serializable;

/**
 * Carries the task identifier as well as the identifier of the user who initiated the action.
 * 
 * @see TaskNotificationMessage
 */
public class TaskDetails implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final String taskId;
	private final String initiator;
	
	public TaskDetails(final String taskId, final String initiator) {
		this.taskId = taskId;
		this.initiator = initiator;
	}
	
	public String getTaskId() {
		return taskId;
	}

	
	public String getInitiator() {
		return initiator;
	}
}