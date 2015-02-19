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

import java.text.MessageFormat;

import com.b2international.snowowl.datastore.editor.notification.NotificationMessage;

/**
 * 
 */
public class TaskClosedNotificationMessage extends NotificationMessage<TaskDetails> implements ITaskNotificationMessage {

	private static final long serialVersionUID = 1L;

	public TaskClosedNotificationMessage(final String taskId, final String closingUserId) {
		super(new TaskDetails(taskId, closingUserId), MessageFormat.format("Task closed by user {0}. "
				+ "Snow Owl will now deactivate this task. "
				+ "Any additional modifications will be lost.", closingUserId));
	}
	
	/* 
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.tasks.ITaskNotificationMessage#getTaskId()
	 */
	@Override
	public String getTaskId() {
		return getData().getTaskId();
	}
	
	public String getInitiatingUserId() {
		return getData().getInitiator();
	}
}